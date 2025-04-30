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

package org.eclipse.ecsp.service;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.common.CommonConstants;
import org.eclipse.ecsp.common.RecordStats;
import org.eclipse.ecsp.common.ResponseConstants;
import org.eclipse.ecsp.common.SharedConstants;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.common.enums.ApiMessageEnum;
import org.eclipse.ecsp.common.exception.ApiValidationFailedException;
import org.eclipse.ecsp.common.exception.DeleteDeviceException;
import org.eclipse.ecsp.common.exception.DeviceNotFoundException;
import org.eclipse.ecsp.common.exception.InvalidDeviceStateException;
import org.eclipse.ecsp.common.exception.InvalidDeviceStateTransitionException;
import org.eclipse.ecsp.common.exception.PageParamResolverException;
import org.eclipse.ecsp.common.exception.SizeParamResolverException;
import org.eclipse.ecsp.common.exception.UpdateDeviceException;
import org.eclipse.ecsp.common.rowmapper.DeviceDataMapper;
import org.eclipse.ecsp.common.util.SqlUtility;
import org.eclipse.ecsp.config.DeviceInfoQueryProperty;
import org.eclipse.ecsp.dao.DeviceInfoFactoryDao;
import org.eclipse.ecsp.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.dao.DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum;
import org.eclipse.ecsp.dao.HcpInfoDao;
import org.eclipse.ecsp.dao.VinDetailsDao;
import org.eclipse.ecsp.dto.DeviceData;
import org.eclipse.ecsp.dto.DeviceFilterDto;
import org.eclipse.ecsp.dto.DeviceInfoAggregateFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryDataRequest;
import org.eclipse.ecsp.dto.DeviceInfoPage;
import org.eclipse.ecsp.dto.DeviceInfoPage.HcpPageable;
import org.eclipse.ecsp.dto.DeviceInfoQuerySuccessResponse;
import org.eclipse.ecsp.dto.DeviceState;
import org.eclipse.ecsp.dto.DeviceStateHistory;
import org.eclipse.ecsp.dto.DeviceUpdateRequest;
import org.eclipse.ecsp.dto.HcpInfo;
import org.eclipse.ecsp.dto.StateChange;
import org.eclipse.ecsp.dto.swm.SwmRequest;
import org.eclipse.ecsp.dto.swm.SwmUpdateVehicleRequest;
import org.eclipse.ecsp.dto.swm.SwmVinRequest;
import org.eclipse.ecsp.service.swm.IswmCrudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.management.InvalidAttributeValueException;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.ecsp.dto.DeviceState.PROVISIONED;
import static org.eclipse.ecsp.service.DeviceInfoQueryValidator.validateDeviceFactoryData;
import static org.eclipse.ecsp.service.DeviceInfoQueryValidator.validateDeviceFilter;

/**
 * The DeviceInfoQueryService class is responsible for querying and manipulating device information.
 * It provides methods for retrieving, filtering, and deleting device data.
 */
@Component
public class DeviceInfoQueryService {
    private static final String DEVICE_INFO_FACTORY_DATA_FACTORY_ADMIN_COLUMN = "factory_admin";
    private static final String DEVICE_INFO_FACTORY_DATA_MANUFACTURING_DATE_COLUMN = "manufacturing_date";
    private static final String SWM_INTEGRATION = "swmIntegration";
    private static final String VEHICLE_MODEL_YEAR = "vehicleModelYear";
    private static final int DEFAULT_PAGE_VALUE = 1;
    private static final int DEFAULT_PAGE_SIZE_PARAM_VALUE = 20;
    private static final int MIN_PAGE_SIZE_PARAM = 1;
    private static final int MAX_PAGE_SIZE_PARAM = 5000;
    private static final String DEVICE_INFO_FACTORY_DATA_SERIAL_NUMBER_COLUMN = "serial_number";
    private static final String DEVICE_INFO_FACTORY_DATA_PACKAGE_SERIAL_NUMBER_COLUMN = "package_serial_number";
    private static final String DEVICE_INFO_FACTORY_DATA_PLATFORM_VERSION_COLUMN = "platform_version";
    private static final String DEVICE_INFO_FACTORY_DATA_RECORD_DATE_COLUMN = "record_date";
    private static final String DEVICE_INFO_FACTORY_DATA_CREATED_DATE_COLUMN = "created_date";
    private static final String DEVICE_INFO_FACTORY_DATA_ISSTOLEN_COLUMN = "isstolen";
    private static final String DEVICE_INFO_FACTORY_DATA_ISFAULTY_COLUMN = "isfaulty";
    private static final String DEVICE_INFO_FACTORY_DATA_ID_COLUMN = "\"ID\"";
    private static final String MANUFACTURING_DATE = "manufacturingdate";
    private static final String SERIAL_NUMBER = "serialnumber";
    private static final String PACKAGE_SERIAL_NUMBER1 = "packageserialnumber";
    private static final String PLATFORM_VERSION = "platformversion";
    private static final String RECORD_DATE = "recorddate";
    private static final String CREATED_DATE = "createddate";
    private static final String FACTORY_ADMIN = "factoryadmin";
    private static final String STOLEN = "stolen";
    private static final String FAULTY = "faulty";
    private static final String ID = "id";
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceInfoQueryService.class);
    private static final String PACKAGE_SERIAL_NUMBER = "package_serial_number";
    private static final String UPDATED = "UPDATED";
    private static final String REQUEST_REGEX = "[\r\n]";
    private static final int STATUS_CODE_200 = 200;
    private static final int VALUE_3 = 3;

    @Autowired
    protected EnvConfig<DeviceInfoQueryProperty> config;
    @Autowired
    private HcpInfoDao hcpInfoDao;
    @Autowired
    private DeviceInfoFactoryDataDao deviceInfoFactoryDataDao;
    @Autowired
    private DeviceInfoFactoryDao deviceInfoFactoryDao;
    @Autowired
    private IswmCrudService<SwmRequest> swmService;
    @Autowired
    private VinDetailsDao vinDetailsDao;

    /**
     * Retrieves the attribute map for the given object.
     *
     * @param object the object from which to retrieve the attributes
     * @param <T> the type of the object
     * @return a map containing the attributes of the object
     */
    private static <T> Map<String, Object> getAttributeMap(T object) {
        LOGGER.info("## getFilterAttributeMap - START");
        Map<String, Object> attributeMap = new LinkedHashMap<>();
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (isGetter(method)) {
                try {
                    //Extract method name after get prefix, here 3 it the length of get<methodName>
                    String attributeName = method.getName().substring(VALUE_3).toLowerCase();
                    String difdColumnName = getDeviceInfoFactoryDataColumnName(attributeName);
                    attributeMap.put(difdColumnName, method.invoke(object));
                } catch (RuntimeException ex) {
                    LOGGER.warn("## Unable to call get method of attribute. Error {}", method.getName());
                } catch (Exception e) {
                    LOGGER.warn("## Unable to call get method of attribute: {}", method.getName());
                }
            }
        }
        return attributeMap;
    }

    /**
     * Checks if a given method is a getter method.
     *
     * @param method The method to check.
     * @return {@code true} if the method is a getter method, {@code false} otherwise.
     */
    private static boolean isGetter(Method method) {
        return (method.getName().startsWith("get") || method.getName().startsWith("is"))
            && method.getParameterCount() == 0 && !method.getReturnType().equals(void.class);
    }

    /**
     * Returns the corresponding column name in the device info factory data table
     * for the given attribute name.
     *
     * @param attributeName the attribute name for which to retrieve the column name
     * @return the column name in the device info factory data table
     */
    private static String getDeviceInfoFactoryDataColumnName(String attributeName) {
        String difdColumnName;
        if (MANUFACTURING_DATE.equals(attributeName)) {
            difdColumnName = DEVICE_INFO_FACTORY_DATA_MANUFACTURING_DATE_COLUMN;
        } else if (SERIAL_NUMBER.equals(attributeName)) {
            difdColumnName = DEVICE_INFO_FACTORY_DATA_SERIAL_NUMBER_COLUMN;
        } else if (PACKAGE_SERIAL_NUMBER1.equals(attributeName)) {
            difdColumnName = DEVICE_INFO_FACTORY_DATA_PACKAGE_SERIAL_NUMBER_COLUMN;
        } else if (PLATFORM_VERSION.equals(attributeName)) {
            difdColumnName = DEVICE_INFO_FACTORY_DATA_PLATFORM_VERSION_COLUMN;
        } else if (RECORD_DATE.equals(attributeName)) {
            difdColumnName = DEVICE_INFO_FACTORY_DATA_RECORD_DATE_COLUMN;
        } else if (CREATED_DATE.equals(attributeName)) {
            difdColumnName = DEVICE_INFO_FACTORY_DATA_CREATED_DATE_COLUMN;
        } else if (FACTORY_ADMIN.equals(attributeName)) {
            difdColumnName = DEVICE_INFO_FACTORY_DATA_FACTORY_ADMIN_COLUMN;
        } else if (STOLEN.equals(attributeName)) {
            difdColumnName = DEVICE_INFO_FACTORY_DATA_ISSTOLEN_COLUMN;
        } else if (FAULTY.equals(attributeName)) {
            difdColumnName = DEVICE_INFO_FACTORY_DATA_ISFAULTY_COLUMN;
        } else if (ID.equals(attributeName)) {
            difdColumnName = DEVICE_INFO_FACTORY_DATA_ID_COLUMN;
        } else {
            difdColumnName = attributeName;
        }
        return difdColumnName;
    }

    /**
     * Deletes the device factory data based on the provided IMEI and serial number.
     *
     * @param imei The IMEI (International Mobile Equipment Identity) of the device.
     * @param serialnumber The serial number of the device.
     * @throws DeviceNotFoundException If the device factory data is not found.
     * @throws DeleteDeviceException If there is an error while deleting the device.
     */
    @Transactional
    public void deleteDeviceFactoryData(String imei, String serialnumber) {
        LOGGER.info("## deleteDeviceFactoryData - START imei: {}, serialNumber: {}",
            imei != null ? imei.replaceAll(REQUEST_REGEX, "") : null,
            serialnumber != null ? serialnumber.replaceAll(REQUEST_REGEX, "") : null);
        // validating imei and serialnumber
        DeviceInfoQueryValidator.validateImeiForCompleteString(imei);
        DeviceInfoQueryValidator.validateSerialNumberForCompleteString(serialnumber);
        DeviceInfoFactoryData queryData = new DeviceInfoFactoryData();
        queryData.setImei(imei);
        queryData.setSerialNumber(serialnumber);
        List<DeviceInfoFactoryData> factoryList = deviceInfoFactoryDataDao.constructAndFetchFactoryData(queryData);
        if (CollectionUtils.isEmpty(factoryList)) {
            throw new DeviceNotFoundException(ResponseConstants.INVALID_CURRENT_FACTORY_DATA);
        }
        final DeviceInfoFactoryData currentData = factoryList.get(0);
        try {
            String deviceCreationType = config.getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
            boolean swmIntegrationEnabled = config.getBooleanValue(DeviceInfoQueryProperty.SWM_INTEGRATION_ENABLED);
            String vin = null;
            if (swmIntegrationEnabled && SWM_INTEGRATION.equals(deviceCreationType)) {
                vin = deviceInfoFactoryDataDao.findVinEitherByImeiOrSerialNumber(imei, serialnumber);
                if (vin == null) {
                    throw new DeleteDeviceException("Cannot delete device due to vin does not exist in db");
                }
            }
            deviceInfoFactoryDataDao.deletefactoryData(queryData.getImei(), queryData.getSerialNumber(), currentData);
            LOGGER.debug("## deviceCreationType: {}", deviceCreationType);
            LOGGER.info("## swmIntegrationEnabled: {}", swmIntegrationEnabled);
            if (swmIntegrationEnabled && SWM_INTEGRATION.equals(deviceCreationType)) {
                performSwmVehicleDelete(vin);
            }
        } catch (DataIntegrityViolationException e) {
            throw new DeleteDeviceException("Cannot delete the device. Device has historical data.", e);

        } catch (Exception e) {
            throw new DeleteDeviceException("Cannot delete the device due to internal server error", e);
        }
        LOGGER.info("## deleteDeviceFactoryData - END");
    }

    /**
     * Performs the deletion of a vehicle from the SWM (Smart Waste Management) system.
     *
     * @param vin The VIN (Vehicle Identification Number) of the vehicle to be deleted.
     * @throws DeleteDeviceException If there is an error while deleting the device from the SWM system.
     */
    private void performSwmVehicleDelete(String vin) {
        LOGGER.debug("## performSwmVehicleDelete - START");
        try {
            SwmVinRequest swmVinRequest = new SwmVinRequest();
            swmVinRequest.setVin(vin);
            boolean deleteDeviceStatus = swmService.deleteVehicle(swmVinRequest);
            if (deleteDeviceStatus) {
                LOGGER.debug("## Device deleted from SWM successfully");
            } else {
                throw new DeleteDeviceException("Cannot delete device due to swm internal server error");
            }
        } catch (Exception e) {
            throw new DeleteDeviceException("Cannot delete device due to swm internal server error", e);
        }
        LOGGER.debug("## performSwmVehicleDelete - END");
    }

    /**
     * Changes the state of a device based on the provided StateChange object.
     *
     * @param stateChange The StateChange object containing the necessary information for the state change.
     * @throws InvalidAttributeValueException If either the factory id or the IMEI is missing, or if the state is null.
     * @throws InvalidDeviceStateTransitionException If the state transition is not allowed.
     */
    public void changeDeviceState(StateChange stateChange) throws InvalidAttributeValueException {
        if ((null == stateChange.getFactoryId()) && StringUtils.isEmpty(stateChange.getImei())) {
            LOGGER.error("Both factory id and imei are null");
            throw new InvalidAttributeValueException("Either of factory id or imei is mandatory");
        }
        if (null == stateChange.getState()) {
            LOGGER.error("state is null");
            throw new InvalidAttributeValueException("state is mandatory");
        }

        DeviceInfoFactoryData deviceInfoFactoryData = getDeviceInfoFactoryData(stateChange);
        // Getting existing device state
        if (deviceInfoFactoryData != null) {
            DeviceState existingState = getExistingDeviceState(deviceInfoFactoryData);

            // validating transition
            boolean stateChangeAllowed = existingState.isValidTransition(stateChange.getState());
            if (!stateChangeAllowed) {
                LOGGER.error("state change not allowed  from : {} , to : {} ", existingState, stateChange.getState());
                throw new InvalidDeviceStateTransitionException(
                    String.format("Device State Transition for factoryid :%s ,from %s to %s is not allowed ",
                        deviceInfoFactoryData.getId(), existingState, stateChange.getState()));
            }
            // Checking - to change state to active allowed
            isDeviceStateToActiveAllowed(stateChange, existingState);

            // changing device state and creating history entry
            deviceInfoFactoryDataDao.changeDeviceState(deviceInfoFactoryData.getId(), stateChange.getState().getValue(),
                UPDATED);
        }

    }

    /**
     * Retrieves the DeviceInfoFactoryData based on the provided StateChange object.
     *
     * @param stateChange The StateChange object containing the factory ID and IMEI.
     * @return The DeviceInfoFactoryData object corresponding to the factory ID and IMEI.
     * @throws InvalidParameterException If the DeviceInfoFactoryData is not found for the provided factory ID and IMEI.
     */
    private DeviceInfoFactoryData getDeviceInfoFactoryData(StateChange stateChange) {
        DeviceInfoFactoryData deviceInfoFactoryData = null;

        if ((null != stateChange.getFactoryId()) && (null != stateChange.getImei())) {
            deviceInfoFactoryData = deviceInfoFactoryDataDao
                .findByFactoryIdAndImei(stateChange.getFactoryId(), stateChange.getImei());
        } else if (null != stateChange.getFactoryId()) {
            deviceInfoFactoryData = deviceInfoFactoryDataDao
                .findByFactoryId(stateChange.getFactoryId());
        } else if (null != stateChange.getImei()) {
            deviceInfoFactoryData = deviceInfoFactoryDataDao
                .findByFactoryImei(stateChange.getImei());
        }

        if (null == deviceInfoFactoryData) {
            if ((null != stateChange.getFactoryId()) && (null != stateChange.getImei())) {
                LOGGER.error("DeviceInfo Factory Data not found for factory id :{} and imei :{}",
                    stateChange.getFactoryId(),
                    stateChange.getImei());
                throw new InvalidParameterException(
                    String.format("DeviceInfo Factory Data not found for factory id :%s and imei :%s",
                        stateChange.getFactoryId(),
                        stateChange.getImei()));
            } else if (null != stateChange.getFactoryId()) {
                LOGGER.error("DeviceInfo Factory Data not found for factory id :{} ", stateChange.getFactoryId());
                throw new InvalidParameterException(
                    String.format("DeviceInfo Factory Data not found for factory id :%s ", stateChange.getFactoryId()));
            } else if (null != stateChange.getImei()) {
                LOGGER.error("DeviceInfo Factory Data not found for factory imei :{} ", stateChange.getFactoryId());
                throw new InvalidParameterException(
                    String.format("DeviceInfo Factory Data not found for imei :%s ", stateChange.getImei()));
            }
        }
        return deviceInfoFactoryData;
    }

    /**
     * Checks if the device state transition to ACTIVE is allowed.
     *
     * @param stateChange   the state change object containing the new state
     * @param existingState the existing state of the device
     * @throws InvalidDeviceStateTransitionException if the device state transition is not allowed
     */
    private void isDeviceStateToActiveAllowed(StateChange stateChange, DeviceState existingState) {
        if (DeviceState.ACTIVE.getValue().equals(stateChange.getState().getValue())) {
            HcpInfo hcpInfo = hcpInfoDao.findByFactoryId(stateChange.getFactoryId());
            LOGGER.debug("hcpInfo :{} , for factoryid :{} ", hcpInfo, stateChange.getFactoryId());
            if (null == hcpInfo || StringUtils.isBlank(hcpInfo.getHarmanId())) {
                throw new InvalidDeviceStateTransitionException(String.format(
                    "Device State Transition for factoryid :%s, from %s to %s is not allowed as device was not already "
                        + "active", stateChange.getFactoryId(), existingState, stateChange.getState()));
            }
        }
    }

    /**
     * Retrieves the existing state of a device based on the provided device information factory data.
     *
     * @param deviceInfoFactoryData The device information factory data containing the device state.
     * @return The existing state of the device.
     * @throws InvalidDeviceStateException If the provided device state is not valid.
     */
    private DeviceState getExistingDeviceState(DeviceInfoFactoryData deviceInfoFactoryData) {
        LOGGER.debug("getting existing state for factory id :{}", deviceInfoFactoryData.getId());
        DeviceState existingState;
        try {
            if (Boolean.TRUE.equals(deviceInfoFactoryData.getFaulty())) {
                existingState = DeviceState.FAULTY;
            } else if (Boolean.TRUE.equals(deviceInfoFactoryData.getStolen())) {
                existingState = DeviceState.STOLEN;
            } else {
                existingState = DeviceState.valueOf(deviceInfoFactoryData.getState());
            }
        } catch (IllegalArgumentException ex) {
            throw new InvalidDeviceStateException(
                String.format("%s is not a valid existing  state", deviceInfoFactoryData.getState()));
        }
        return existingState;
    }

    /**
     * Updates the device information factory data based on the provided device update request.
     *
     * @param deviceUpdateRequest The device update request containing the current and replacement device data.
     * @throws InvalidAttributeValueException If the request object is null or if one or more required attribute values
     *      are missing.
     * @throws ParseException If there is an error parsing the device data.
     * @throws DeviceNotFoundException If no device is found in the DataInfoFactoryData for the given request.
     * @throws InvalidDeviceStateException If the device is not in a valid state to perform the action.
     */
    @Transactional
    public void updateDeviceInfoFactoryData(DeviceUpdateRequest deviceUpdateRequest)
        throws InvalidAttributeValueException, ParseException {
        DeviceDataMapper deviceDataMapper = new DeviceDataMapper();
        DeviceData currentValue = deviceUpdateRequest.getCurrentValue();
        LinkedHashMap<String, Object> currentValueOrderedMap =
            (LinkedHashMap<String, Object>) deviceDataMapper.getOrderedMap(currentValue);

        DeviceData replaceWith = deviceUpdateRequest.getReplaceWith();
        LinkedHashMap<String, Object> replaceWithOrderedMap =
            (LinkedHashMap<String, Object>) deviceDataMapper.getOrderedMap(replaceWith);

        if (currentValueOrderedMap == null || replaceWithOrderedMap == null) {
            throw new InvalidAttributeValueException("Request object (deviceUpdateRequest) is null");
        } else {
            // Before validating all attributes to be mandatory, preserve
            // package serial number entry from ordered map due to optional
            // field
            Object packageSerialNumberFromCurrentValueOrderedMap = currentValueOrderedMap.remove(PACKAGE_SERIAL_NUMBER);
            Object packageSerialNumberFromReplaceWithOrderedMap = replaceWithOrderedMap.remove(PACKAGE_SERIAL_NUMBER);

            LOGGER.debug("PackageSerialNumber From CurrentValue {}", packageSerialNumberFromCurrentValueOrderedMap);
            LOGGER.debug("PackageSerialNumber From ReplaceWith {}", packageSerialNumberFromReplaceWithOrderedMap);
            // checking for Mandatory attributes and its value except Package
            // Serial Number
            if (currentValueOrderedMap.containsValue(null) || replaceWithOrderedMap.containsValue(null)) {
                throw new InvalidAttributeValueException(
                    "One or more than one required attribute(s) value is missing either in \"currentValue\" or"
                        + " \"replaceWith\" input json");
            } else {
                /*
                 * Add preserved Package Serial Number into CurrentValue map if
                 * it is not null, and it will be use in query while fetching
                 * existing Factory Data
                 */
                if (packageSerialNumberFromCurrentValueOrderedMap != null) {
                    currentValueOrderedMap.put(PACKAGE_SERIAL_NUMBER, packageSerialNumberFromCurrentValueOrderedMap);
                }
                DeviceInfoFactoryData deviceInfoFactoryData =
                    deviceInfoFactoryDataDao.fetchDeviceInfoFactoryData(currentValueOrderedMap);
                if (deviceInfoFactoryData == null) {
                    throw new DeviceNotFoundException(
                        "No device found in DataInfoFactoryData for request: " + currentValueOrderedMap);
                } else if (!PROVISIONED.toString().equals(deviceInfoFactoryData.getState())) {
                    throw new InvalidDeviceStateException("Device is not in valid state to perform the action");
                } else {
                    LOGGER.info("Device is found:{}", deviceInfoFactoryData);

                    // Put back Package Serial Number entry into replaceWith
                    // ordered Map before update
                    replaceWithOrderedMap.put(PACKAGE_SERIAL_NUMBER, packageSerialNumberFromReplaceWithOrderedMap);

                    // Update Factory Data
                    Map<String, Object> conditionalOrderedMap = new LinkedHashMap<>();
                    conditionalOrderedMap.put(DeviceInfoFactoryDataDao.ID, deviceInfoFactoryData.getId());
                    deviceInfoFactoryDataDao.update(conditionalOrderedMap, replaceWithOrderedMap);

                    String deviceCreationType = config.getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
                    // vin validation and update: vin value from currentValue should exist
                    // in db and replaceWith should not exist in db

                    updateDeviceInfoFactoryForSwmIntegration(deviceCreationType, deviceInfoFactoryData, currentValue,
                        replaceWith);
                    LOGGER.info("Device update is successful");
                }
            }
        }
    }

    /**
     * Updates the device information factory for SWM integration.
     *
     * @param deviceCreationType The type of device creation.
     * @param deviceInfoFactoryData The device information factory data.
     * @param currentValue The current device data.
     * @param replaceWith The device data to replace with.
     * @throws InvalidAttributeValueException If the attribute value is invalid.
     */
    private void updateDeviceInfoFactoryForSwmIntegration(String deviceCreationType,
                                                          DeviceInfoFactoryData deviceInfoFactoryData,
                                                          DeviceData currentValue, DeviceData replaceWith)
        throws InvalidAttributeValueException {
        if (SWM_INTEGRATION.equals(deviceCreationType)) {
            if (!vinDetailsDao.checkForCurretVin(deviceInfoFactoryData.getId(), currentValue.getVin())) {
                throw new ApiValidationFailedException(ApiMessageEnum.DEVICE_DETAILS_NOT_FOUND.getCode(),
                    ApiMessageEnum.DEVICE_DETAILS_NOT_FOUND.getMessage(),
                    ApiMessageEnum.DEVICE_DETAILS_NOT_FOUND.getGeneralMessage());
            }
            if (vinDetailsDao.checkForVin(replaceWith.getVin())
                && !replaceWith.getVin().equals(currentValue.getVin())) {
                throw new ApiValidationFailedException(ApiMessageEnum.DEVICE_ALREADY_EXIST_BY_VIN.getCode(),
                    ApiMessageEnum.DEVICE_ALREADY_EXIST_BY_VIN.getMessage(),
                    ApiMessageEnum.DEVICE_ALREADY_EXIST_BY_VIN.getGeneralMessage());
            }
            if (!replaceWith.getVin().equals(currentValue.getVin())) {
                vinDetailsDao.updateVin(deviceInfoFactoryData.getId(), replaceWith.getVin());
            }

            boolean swmIntegrationEnabled = config.getBooleanValue(DeviceInfoQueryProperty.SWM_INTEGRATION_ENABLED);
            LOGGER.info("## deviceCreationType: {}", deviceCreationType);
            LOGGER.info("## swmIntegrationEnabled: {}", swmIntegrationEnabled);
            if (swmIntegrationEnabled) {
                performSwmVehicleUpdate(currentValue, replaceWith);
            }
        }
    }

    /**
     * Performs the SWM vehicle update for the given device data.
     *
     * @param currentValue The current device data.
     * @param replaceWith The device data to replace with.
     * @throws InvalidAttributeValueException If chassis number, production week, or replaceWith data is empty.
     * @throws UpdateDeviceException If there is an error updating the device in SWM.
     */
    private void performSwmVehicleUpdate(DeviceData currentValue, DeviceData replaceWith)
        throws InvalidAttributeValueException {
        String currentChassisNumber = currentValue.getChassisNumber();
        String currentProductionWeek = currentValue.getProductionWeek();
        String replaceWithChassisNumber = replaceWith.getChassisNumber();
        String replaceWithProductionWeek = replaceWith.getProductionWeek();
        LOGGER.debug("## currentChassisNumber: {}", currentChassisNumber);
        LOGGER.debug("## currentProductionWeek: {}", currentProductionWeek);
        LOGGER.debug("## replaceWithChassisNumber: {}", replaceWithChassisNumber);
        LOGGER.debug("## replaceWithProductionWeek: {}", replaceWithProductionWeek);
        if (!(StringUtils.isNotEmpty(currentChassisNumber)
            && StringUtils.isNotEmpty(currentProductionWeek)
            && StringUtils.isNotEmpty(replaceWithChassisNumber)
            && StringUtils.isNotEmpty(replaceWithProductionWeek))) {
            throw new InvalidAttributeValueException("Chassis number and production week is mandatory");
        }
        String vin = deviceInfoFactoryDataDao.findVinEitherByImeiOrSerialNumber(replaceWith.getImei(),
            replaceWith.getSerialNumber());
        if (vin != null) {
            SwmUpdateVehicleRequest swmUpdateVehicleRequest = new SwmUpdateVehicleRequest();
            swmUpdateVehicleRequest.setChassisNumber(replaceWith.getChassisNumber());
            swmUpdateVehicleRequest.setProductionWeek(replaceWith.getProductionWeek());
            swmUpdateVehicleRequest.setPlant(replaceWith.getPlant());
            swmUpdateVehicleRequest.setVin(replaceWith.getVin());
            Map<String, String> specificAttributesMap = new HashMap<>();
            specificAttributesMap.put(VEHICLE_MODEL_YEAR, replaceWith.getVehicleModelYear());
            swmUpdateVehicleRequest.setSpecificAttributes(specificAttributesMap);
            try {
                boolean deleteDeviceStatus = swmService.updateVehicle(swmUpdateVehicleRequest);
                if (deleteDeviceStatus) {
                    LOGGER.info("## Device deleted from SWM successfully");
                } else {
                    throw new UpdateDeviceException("Cannot update device due to swm internal server error");
                }
            } catch (Exception e) {
                throw new UpdateDeviceException("Cannot update device due to swm internal server error", e);
            }
        } else {
            throw new UpdateDeviceException("Cannot update device due vin does not exist in db!");
        }
    }

    /**
     * Retrieves all factory data based on the provided request parameters.
     *
     * @param requestParams The map of request parameters.
     * @return A page of DeviceInfoFactoryData objects containing the retrieved factory data.
     * @throws DeviceNotFoundException If no factory data is found for the specified contains or range fields.
     */
    public DeviceInfoPage<DeviceInfoFactoryData> getAllFactoryDataV3(Map<String, String> requestParams) {

        String containsLikeFields = getRequestParamsFromMap(requestParams, "containslikefields");
        String containsLikeValues = getRequestParamsFromMap(requestParams, "containslikevalues");
        String rangeFields = getRequestParamsFromMap(requestParams, "rangefields");
        String rangeValues = getRequestParamsFromMap(requestParams, "rangevalues");
        String sortBy = getRequestParamsFromMap(requestParams, "sortbyparam");
        String sortingOrder = getRequestParamsFromMap(requestParams, "sortingorder");
        String isdetailsrequired = getRequestParamsFromMap(requestParams, "isdetailsrequired");
        List<String> containsLikeFieldList = SqlUtility.getList(containsLikeFields, SharedConstants.COMMA);
        List<String> containsLikeValueList = SqlUtility.getList(containsLikeValues, SharedConstants.COMMA);
        List<String> rangeFieldList = SqlUtility.getList(rangeFields, SharedConstants.COMMA);
        List<String> rangeValueList = SqlUtility.getList(rangeValues, SharedConstants.COMMA);

        DeviceInfoQueryValidator.validateSortByRequestData(sortBy, sortingOrder, isdetailsrequired);
        DeviceInfoQueryValidator.validateContainsLike(containsLikeFieldList, containsLikeValueList);
        DeviceInfoQueryValidator.validateRangeField(rangeFieldList, rangeValueList);
        LOGGER.info("All validation done successfully");
        String pageValue = getRequestParamsFromMap(requestParams, "page");
        String sizeValue = getRequestParamsFromMap(requestParams, "size");
        int page = resolvePageArgument(pageValue);
        int size = resolveSizeArgument(sizeValue);

        Long total =
            deviceInfoFactoryDataDao.constructFetchTotalFactoryData(containsLikeFieldList, containsLikeValueList,
                rangeFieldList, rangeValueList);
        if (total == null || total <= 0) {
            if (!containsLikeValueList.isEmpty()) {
                throw new DeviceNotFoundException(
                    ResponseConstants.DEVICE_NOT_FOUND_FOR_CONTAINS_FIELD + containsLikeValueList.toString());
            }
            if (!rangeValueList.isEmpty()) {
                throw new DeviceNotFoundException(
                    ResponseConstants.DEVICE_NOT_FOUND_FOR_RANGE_FIELD + rangeValueList.toString());
            }
        }

        List<DeviceInfoFactoryData> factoryDataList;

        if (total != null && total > 0 && Boolean.TRUE.equals(Boolean.valueOf(isdetailsrequired))) {
            factoryDataList = deviceInfoFactoryDataDao.constructFetchFactoryData(requestParams, page, size);
        } else {
            total = 0L;
            factoryDataList = Collections.emptyList();
        }

        DeviceInfoAggregateFactoryData aggregateData = new DeviceInfoAggregateFactoryData();
        aggregateData.setStateCount(deviceInfoFactoryDataDao.constructFetchAgrigateDeviceState(containsLikeFieldList,
            containsLikeValueList, rangeFieldList, rangeValueList));

        if (Boolean.TRUE.equals(Boolean.valueOf(isdetailsrequired))) {
            return (new DeviceInfoPage<>(aggregateData, factoryDataList, new HcpPageable(page, size, total)));
        }
        aggregateData.setCount(total);
        return (new DeviceInfoPage<>(aggregateData, null, null));
    }

    /**
     * Resolves the page argument from the given parameter value.
     *
     * @param paramValue the parameter value to resolve
     * @return the resolved page argument
     * @throws PageParamResolverException if the parameter value is invalid
     */
    private int resolvePageArgument(String paramValue) {
        int page = DEFAULT_PAGE_VALUE;
        if (!StringUtils.isEmpty(paramValue)) {
            try {
                page = Integer.parseUnsignedInt(paramValue);
                if (page == 0) {
                    throw new PageParamResolverException(ResponseConstants.ZERO_PAGE_VALUE_TYPE_MESSAGE);
                }
            } catch (NumberFormatException e) {
                LOGGER.debug("Page should be unsigned number: paramValue{}, exception - {}.", paramValue,
                    e.getMessage());
                throw new PageParamResolverException(ResponseConstants.WRONG_PAGE_VALUE_TYPE_MESSAGE);
            }
        }
        return page;
    }

    /**
     * Resolves the size argument based on the provided parameter value.
     * If the parameter value is empty or not a valid unsigned integer, it throws a SizeParamResolverException.
     * If the size is not within the valid range, it throws a SizeParamResolverException.
     *
     * @param paramValue the parameter value to resolve
     * @return the resolved size argument
     * @throws SizeParamResolverException if the parameter value is not a valid unsigned integer or if the size is not
     *      within the valid range
     */
    private int resolveSizeArgument(String paramValue) {
        int size = DEFAULT_PAGE_SIZE_PARAM_VALUE;

        if (!StringUtils.isEmpty(paramValue)) {
            try {
                size = Integer.parseUnsignedInt(paramValue);
            } catch (NumberFormatException e) {
                LOGGER.debug("Page size should be unsigned number: paramValue{}, exception - {}.", paramValue,
                    e.getMessage());
                throw new SizeParamResolverException(String.format(ResponseConstants.WRONG_PAGE_SIZE_MESSAGE,
                    MIN_PAGE_SIZE_PARAM, MAX_PAGE_SIZE_PARAM));
            }
        }
        if (size < MIN_PAGE_SIZE_PARAM || size > MAX_PAGE_SIZE_PARAM) {
            LOGGER.debug("page size must be between {} and {}", MIN_PAGE_SIZE_PARAM, MAX_PAGE_SIZE_PARAM);
            throw new SizeParamResolverException(
                String.format(ResponseConstants.WRONG_PAGE_SIZE_MESSAGE, MIN_PAGE_SIZE_PARAM, MAX_PAGE_SIZE_PARAM));
        }
        return size;
    }

    /**
     * Retrieves all device states based on the provided parameters.
     *
     * @param imei         The IMEI number of the device.
     * @param sizeValue    The size value for pagination.
     * @param pageValue    The page value for pagination.
     * @param sortingOrder The sorting order for the device states.
     * @param sortByField  The field to sort the device states by.
     * @return A success response containing the list of device states.
     * @throws DeviceNotFoundException If the device with the given IMEI is not found.
     */
    public DeviceInfoQuerySuccessResponse<DeviceStateHistory> findAllDeviceStates(String imei, String sizeValue,
                                                                                  String pageValue, String sortingOrder,
                                                                                  String sortByField) {

        LOGGER.debug("inside findAllDeviceStates method");

        DeviceInfoQueryValidator.validateSortByAndOrderByFields(
            DeviceInfoQueryValidator.DeviceInfoQueryFeatureType.DEVICE_STATE, sortByField, sortingOrder,
            DeviceDetailsInputTypeEnum.IMEI);
        DeviceInfoQueryValidator.validateImei(imei);
        LOGGER.debug("All validation done successfully");
        List<DeviceStateHistory> deviceStates;
        int page = resolvePageArgument(pageValue);
        int size = resolveSizeArgument(sizeValue);
        Long total = deviceInfoFactoryDataDao.findTotalDeviceState(imei);
        // validating if total number of count is 0 and imei is not empty
        if (total == null || total <= 0) {
            throw new DeviceNotFoundException(ResponseConstants.DEVICE_NOT_FOUND_FOR_IMEI_MSG + imei);
        } else { // fetching complete state history details from DB along with pagination
            String sortByColumnName = DeviceInfoQueryValidator.DEVICE_STATE_SORT_BY_COLUMN_MAPPING.get(sortByField);
            deviceStates =
                deviceInfoFactoryDataDao.constructAndFetchDeviceStates(size, page, sortingOrder, sortByColumnName,
                    imei);
        }
        RecordStats<Object> recordStats = new RecordStats<>();
        // populating the success response with data
        recordStats.setPage(page);
        recordStats.setSize(size);
        recordStats.setTotal(total);
        DeviceInfoQuerySuccessResponse<DeviceStateHistory> successResponse = new DeviceInfoQuerySuccessResponse<>();
        successResponse.setCode("HCP-0001");
        successResponse.setData(deviceStates);
        successResponse.setHttpStatusCode(STATUS_CODE_200);
        successResponse.setMessage(ResponseConstants.DEVICE_STATE_RETRIEVAL_SUCCESS_MSG);
        successResponse.setReason("Device states retrieval");
        successResponse.setRecordStats(recordStats);
        successResponse.setRequestId("Req-0001");
        return successResponse;
    }

    /**
     * Saves the device factory data for the given device info data requests.
     *
     * @param deviceInfoDataRequest an array of DeviceInfoFactoryDataRequest objects containing the device factory data
     *                              to be saved
     * @param userId the ID of the user performing the save operation
     */
    public void saveDeviceFactoryData(DeviceInfoFactoryDataRequest[] deviceInfoDataRequest, String userId) {
        for (DeviceInfoFactoryDataRequest deviceFactoryData : deviceInfoDataRequest) {
            deviceInfoFactoryDao.insertData(deviceFactoryData, userId);
        }
    }

    /**
     * Filters the list of device information based on the provided filter criteria.
     *
     * @param filterDto The filter criteria to apply.
     * @return The list of filtered device information.
     */
    public List<DeviceInfoFactoryData> filterDevice(DeviceFilterDto filterDto) {
        LOGGER.info("## filterDevice SERVICE - START");
        validateDeviceFilter(filterDto);
        return deviceInfoFactoryDataDao.filterDevice(getAttributeMap(filterDto));
    }

    /**
     * Updates a device with the provided DeviceInfoFactoryData.
     *
     * @param difd The DeviceInfoFactoryData object containing the updated device information.
     * @return The number of devices updated.
     */
    public int updateDevice(DeviceInfoFactoryData difd) {
        LOGGER.info("## updateDevice SERVICE - START");
        validateDeviceFactoryData(difd);
        Map<String, Object> attributeMap = getAttributeMap(difd);
        Object id = attributeMap.remove(DEVICE_INFO_FACTORY_DATA_ID_COLUMN);
        Map<String, Object> conditionMap = new LinkedHashMap<>();
        conditionMap.put(DEVICE_INFO_FACTORY_DATA_ID_COLUMN, id);
        return deviceInfoFactoryDataDao.updateDevice(attributeMap, conditionMap);
    }

    /**
     * Retrieves the value associated with the specified key from the given requestParams map.
     *
     * @param requestParams The map containing the request parameters.
     * @param key The key whose associated value is to be retrieved.
     * @return The value associated with the specified key, or null if the key is not found or the map is empty.
     */
    private String getRequestParamsFromMap(Map<String, String> requestParams, String key) {
        if (requestParams != null && !requestParams.isEmpty() && requestParams.containsKey(key)) {
            return requestParams.get(key);
        }
        return null;
    }
}

