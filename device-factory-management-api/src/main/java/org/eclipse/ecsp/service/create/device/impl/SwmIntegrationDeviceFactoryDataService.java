/*
 *  *******************************************************************************
 *  Copyright (c) 2023-24 Harman International
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  *******************************************************************************
 */

package org.eclipse.ecsp.service.create.device.impl;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.common.CommonConstants;
import org.eclipse.ecsp.common.ErrorUtils;
import org.eclipse.ecsp.common.exception.ApiTechnicalException;
import org.eclipse.ecsp.common.exception.ApiValidationFailedException;
import org.eclipse.ecsp.config.DeviceInfoQueryProperty;
import org.eclipse.ecsp.dao.VinDetailsDao;
import org.eclipse.ecsp.dao.create.device.IdeviceFactoryDataDao;
import org.eclipse.ecsp.dto.DeviceTypeMandatoryParams;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataBaseDto;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataDto;
import org.eclipse.ecsp.dto.swm.SwmRequest;
import org.eclipse.ecsp.dto.swm.SwmVehiclesRequest;
import org.eclipse.ecsp.dto.swm.VehiclePost;
import org.eclipse.ecsp.service.SaasApiService;
import org.eclipse.ecsp.service.create.device.AbstractDeviceFactoryDataService;
import org.eclipse.ecsp.service.swm.IswmCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.ecsp.common.enums.ApiMessageEnum.DEVICE_ALREADY_EXIST_BY_VIN;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.DEVICE_TYPE_MANDATORY_PARAMETERS_EMPTY;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.GENERAL_ERROR;

/**
 * This class is responsible for managing the integration with the SWM (Service Workflow Manager) system
 * and creating devices based on the provided factory data.
 * It extends the AbstractDeviceFactoryDataService class.
 */
@Service
@Transactional
public class SwmIntegrationDeviceFactoryDataService extends AbstractDeviceFactoryDataService {
    private static final String COMMA = ", ";
    private static final String ZERO = "0";
    protected final VinDetailsDao vinDetailsDao;
    private final IswmCrudService<SwmRequest> swmService;
    private final IdeviceFactoryDataDao createDeviceDao;
    private static final int CAPACITY = 200;
    private static final int VALUE_9 = 9;

    @Autowired
    SaasApiService saasApiService;

    @Value("#{'${allowed_device_types}'.split(',')}")
    private String[] allowedTypes;

    /**
     * Constructs a new SwmIntegrationDeviceFactoryDataService with the specified dependencies.
     *
     * @param createDeviceDao The data access object for creating device factory data.
     * @param vinDetailsDao The data access object for retrieving VIN details.
     * @param swmService The service for interacting with the SwmRequest API.
     */
    @Autowired
    public SwmIntegrationDeviceFactoryDataService(
        @Qualifier(value = "swmIntegrationVehicleFactoryDataDao") IdeviceFactoryDataDao createDeviceDao,
        VinDetailsDao vinDetailsDao,
        IswmCrudService<SwmRequest> swmService) {
        this.createDeviceDao = createDeviceDao;
        this.vinDetailsDao = vinDetailsDao;
        this.swmService = swmService;
    }

    /**
     * Creates a device based on the provided device factory data.
     *
     * @param deviceFactoryDataBaseDto The data object containing the device factory data.
     * @return True if the device creation is successful, false otherwise.
     */
    @Override
    public boolean createDevice(DeviceFactoryDataBaseDto deviceFactoryDataBaseDto) {
        LOGGER.debug("## createDevice - START");
        boolean deviceCreateStatus = false;
        boolean isVersionV3 = false;

        DeviceTypeMandatoryParams deviceTypeMandatoryParams = null;
        deviceTypeMandatoryParams =
            validateAndGetDeviceTypeMandatoryParamsFromSystemParameter(deviceFactoryDataBaseDto);

        DeviceFactoryDataDto[] deviceFactoryDataDtos = deviceFactoryDataBaseDto.getDeviceFactoryDataDtos();
        String userId = deviceFactoryDataBaseDto.getUserId();
        StringBuilder swmCreationFailedSb = new StringBuilder(CAPACITY);
        String swmIntegrationEnabled = config.getStringValue(DeviceInfoQueryProperty.SWM_INTEGRATION_ENABLED);
        LOGGER.debug("## swmIntegrationEnabled: {}", swmIntegrationEnabled);
        for (DeviceFactoryDataDto deviceFactoryDataDto : deviceFactoryDataDtos) {
            if (deviceFactoryDataBaseDto.getVersion().equalsIgnoreCase("V3")) {
                isVersionV3 = true;
                LOGGER.debug("## validating Device Type : {}", deviceFactoryDataDto.getDeviceType());
                validateMandatoryParamsBasedOnDeviceType(deviceFactoryDataDto, deviceTypeMandatoryParams, allowedTypes);
            }

            if (deviceFactoryDataDto.getSerialNumber() != null) {
                checkForDuplicateSerialNumber(deviceFactoryDataDto.getSerialNumber());
            }

            if (deviceFactoryDataDto.getVin() != null) {
                checkForDuplicateVin(deviceFactoryDataDto.getVin());
            }

            if ("true".equals(swmIntegrationEnabled)) {
                deviceCreateStatus = createDeviceWithSwmIntegration(userId, deviceFactoryDataDto, isVersionV3);
                if (!deviceCreateStatus) {
                    swmCreationFailedSb.append(deviceFactoryDataDto.getVin()).append(COMMA);
                }
            } else {
                deviceCreateStatus = createDeviceOnly(userId, deviceFactoryDataDto);
            }
        }
        //Log failed SWM Integration by VIN
        if (swmCreationFailedSb.length() != 0) {
            LOGGER.debug("## Device creation failed for these vins: {}", swmCreationFailedSb);
        }
        return deviceCreateStatus;
    }

    /**
     * Creates a device without integrating with the SWM (Software Management) system.
     *
     * @param userId              the ID of the user creating the device
     * @param deviceFactoryDataDto the data of the device to be created
     * @return true if the device is created successfully, false otherwise
     */
    private boolean createDeviceOnly(String userId, DeviceFactoryDataDto deviceFactoryDataDto) {
        boolean createStatus = createDeviceDao.createDevice(deviceFactoryDataDto, userId);
        if (createStatus) {
            LOGGER.debug("## Device created successfully without swm integration, createStatus: true");
        } else {
            LOGGER.debug("## Failed to create Device  without swm integration, createStatus: false");
        }
        return createStatus;
    }

    /**
     * Creates a device with SWM integration.
     *
     * @param userId             the user ID
     * @param deviceFactoryDataDto the device factory data DTO
     * @param isVersionV3        a flag indicating if the version is V3
     * @return true if the device is created successfully with SWM integration, false otherwise
     */
    private boolean createDeviceWithSwmIntegration(String userId, DeviceFactoryDataDto deviceFactoryDataDto,
                                                   boolean isVersionV3) {
        boolean swmIntegrationStatus;
        try {
            if (isVersionV3 && (deviceFactoryDataDto.getVin() == null || deviceFactoryDataDto.getVin().isEmpty())) {
                swmIntegrationStatus = true;
                LOGGER.debug("## vin not passed in the request hence setting swmIntegrationStatus as : {}",
                    swmIntegrationStatus);
                LOGGER.info("## vin not passed in the request hence setting swmIntegrationStatus as : {}",
                    swmIntegrationStatus);
            } else {
                swmIntegrationStatus = swmService.createVehicle(createSwmRequest(deviceFactoryDataDto));
            }
            LOGGER.debug("## swmIntegrationStatus: {}", swmIntegrationStatus);
        } catch (Exception e) {
            LOGGER.error("{}", ErrorUtils.buildError("## Error has occurred creating vehicle with SWM", e,
                getErrorMap(deviceFactoryDataDto.getVin(), deviceFactoryDataDto.getSerialNumber())));
            swmIntegrationStatus = false;
        }
        if (swmIntegrationStatus) {
            try {
                boolean createStatus = createDeviceDao.createDevice(deviceFactoryDataDto, userId);
                if (createStatus) {
                    LOGGER.debug("##Device created successfully with swm integration, createStatus: true");
                } else {
                    LOGGER.debug("##Failed to create Device  with swm integration, createStatus: false");
                    //delete vehicle from swm using vin
                }
            } catch (Exception e) {
                //delete vehicle from swm using vin
            }
        }
        return swmIntegrationStatus;
    }

    /**
     * Checks if a VIN (Vehicle Identification Number) already exists in the database.
     * Throws an exception if a duplicate VIN is found.
     *
     * @param vin The VIN to check for duplicates.
     * @throws ApiValidationFailedException If a duplicate VIN is found.
     */
    private void checkForDuplicateVin(String vin) {
        if (vinDetailsDao.checkForVin(vin)) {
            throw new ApiValidationFailedException(DEVICE_ALREADY_EXIST_BY_VIN.getCode(),
                DEVICE_ALREADY_EXIST_BY_VIN.getMessage(),
                DEVICE_ALREADY_EXIST_BY_VIN.getGeneralMessage());
        }
    }

    /**
     * Creates a SwmVehiclesRequest object based on the provided DeviceFactoryDataDto.
     *
     * @param deviceFactoryDataDto The DeviceFactoryDataDto object containing the data for creating the
     *                             SwmVehiclesRequest.
     * @return The SwmVehiclesRequest object created based on the DeviceFactoryDataDto.
     */
    private SwmVehiclesRequest createSwmRequest(DeviceFactoryDataDto deviceFactoryDataDto) {
        LOGGER.debug("## createSwmRequest - START");
        String chassisNumber = deviceFactoryDataDto.getChassisNumber();
        String vin = deviceFactoryDataDto.getVin();
        VehiclePost vehicleData = new VehiclePost();
        vehicleData.setChassisNumber(StringUtils.isBlank(chassisNumber) ? vin : chassisNumber);
        vehicleData.setDomainId(config.getStringValue(DeviceInfoQueryProperty.SWM_DOMAIN_ID));
        vehicleData.setPlant(
            (StringUtils.isBlank(deviceFactoryDataDto.getPlant())) ? "Plant" : deviceFactoryDataDto.getPlant());
        vehicleData.setProductionWeek(getProductionWeek(deviceFactoryDataDto.getProductionWeek()));
        vehicleData.setVehicleModelId(deviceFactoryDataDto.getModel());
        vehicleData.setVin(vin);
        vehicleData.setFriendlyName(deviceFactoryDataDto.getFriendlyName());
        String vehicleModelYear = deviceFactoryDataDto.getVehicleModelYear();
        Map<String, String> specificAttributes = new HashMap<>();
        specificAttributes.put(CommonConstants.SWM_VEHICLE_MODEL_YEAR,
            StringUtils.isBlank(vehicleModelYear) ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) :
                vehicleModelYear);
        vehicleData.setSpecificAttributes(specificAttributes);

        List<VehiclePost> vehiclePostList = new ArrayList<>();
        vehiclePostList.add(vehicleData);
        SwmVehiclesRequest swmRequest = new SwmVehiclesRequest();
        swmRequest.setVehiclePost(vehiclePostList);
        LOGGER.debug("## createSwmRequest - END swmRequest: {}", swmRequest);
        return swmRequest;
    }

    /**
     * Returns the production week based on the given production week value.
     * If the production week value is blank, it calculates the current production week based on the current year and
     * week number.
     *
     * @param productionWeek the production week value
     * @return the production week
     */
    private String getProductionWeek(String productionWeek) {
        if (StringUtils.isBlank(productionWeek)) {
            Calendar cal = Calendar.getInstance();
            int currentYear = cal.get(Calendar.YEAR);
            int currentYearsWeekNumber = cal.get(Calendar.WEEK_OF_YEAR);
            if (currentYearsWeekNumber <= VALUE_9) {
                productionWeek = currentYear + ZERO + currentYearsWeekNumber;
            } else {
                productionWeek = currentYear + String.valueOf(currentYearsWeekNumber);
            }
        }
        return productionWeek;
    }

    /**
     * Validates and retrieves the device type mandatory parameters from the system parameter.
     *
     * @param deviceFactoryDataBaseDto The device factory data base DTO.
     * @return The device type mandatory parameters, or null if the version is not "V3".
     * @throws ApiTechnicalException If the device type mandatory parameters are empty or null.
     */
    private DeviceTypeMandatoryParams validateAndGetDeviceTypeMandatoryParamsFromSystemParameter(
        DeviceFactoryDataBaseDto deviceFactoryDataBaseDto) {
        DeviceTypeMandatoryParams deviceTypeMandatoryParams = null;
        if (deviceFactoryDataBaseDto.getVersion().equalsIgnoreCase("V3")) {
            deviceTypeMandatoryParams = saasApiService.getDeviceTypeMandatoryParamsFromSystemParameter();
            LOGGER.debug("fetched deviceTypeMandatoryParams list : {}", deviceTypeMandatoryParams);

            if (deviceTypeMandatoryParams == null
                || CollectionUtils.isEmpty(deviceTypeMandatoryParams.getDeviceTypeMandatoryParams())) {
                throw new ApiTechnicalException(DEVICE_TYPE_MANDATORY_PARAMETERS_EMPTY.getCode(),
                    DEVICE_TYPE_MANDATORY_PARAMETERS_EMPTY.getMessage(),
                    DEVICE_TYPE_MANDATORY_PARAMETERS_EMPTY.getGeneralMessage());
            }
        }
        return deviceTypeMandatoryParams;
    }

    /**
     * Returns a map containing error information for the given VIN and serial number.
     *
     * @param vin The VIN (Vehicle Identification Number) associated with the error.
     * @param serialnumber The serial number associated with the error.
     * @return A map containing error information, including the VIN, serial number, and error code.
     */
    private Map<Object, Object> getErrorMap(String vin, String serialnumber) {
        Map<Object, Object> errorMap = new LinkedHashMap<>();
        errorMap.put("vin", vin);
        errorMap.put("serialNumber", serialnumber);
        errorMap.put(ErrorUtils.ERROR_CODE_KEY, GENERAL_ERROR.getCode());
        return errorMap;
    }
}
