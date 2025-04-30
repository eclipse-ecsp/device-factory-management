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

package org.eclipse.ecsp.service.create.device;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.common.exception.ApiTechnicalException;
import org.eclipse.ecsp.common.exception.ApiValidationFailedException;
import org.eclipse.ecsp.common.util.HcpRestClientLibrary;
import org.eclipse.ecsp.common.util.Utils;
import org.eclipse.ecsp.config.DeviceInfoQueryProperty;
import org.eclipse.ecsp.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.DeviceTypeMandatoryParam;
import org.eclipse.ecsp.dto.DeviceTypeMandatoryParams;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataDto;
import org.eclipse.ecsp.service.SpringAuthTokenGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;

import static org.eclipse.ecsp.common.enums.ApiMessageEnum.DEVICE_ALREADY_EXIST_BY_SERIAL_NUMBER;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.DEVICE_TYPE_MANDATORY_PARAMETERS_EMPTY;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.GENERAL_ERROR;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.INVALID_DEVICE_TYPE;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.MISSING_MANDATORY_REQUEST_PARAMS;

/**
 * This abstract class provides a base implementation for the {@link IdeviceFactoryDataService} interface.
 * It contains common functionality and utility methods used by concrete implementations.
 */
public abstract class AbstractDeviceFactoryDataService implements IdeviceFactoryDataService {
    public static final String BEARER_KEY = "Bearer ";
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractDeviceFactoryDataService.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Resource(name = "envConfig")
    protected EnvConfig<DeviceInfoQueryProperty> config;
    @Autowired
    protected HcpRestClientLibrary restClientLibrary;
    @Autowired
    @Lazy
    protected SpringAuthTokenGenerator springAuthTokenGenerator;
    @Autowired
    private DeviceInfoFactoryDataDao deviceInfoFactoryDataDao;

    /**
     * Checks if a device with the given serial number already exists in the system.
     * If a device with the same serial number is found, an exception is thrown.
     *
     * @param serialNumber The serial number of the device to check.
     * @throws ApiValidationFailedException If a device with the same serial number already exists.
     */
    protected void checkForDuplicateSerialNumber(String serialNumber) {
        LinkedHashMap<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("serial_number", serialNumber);
        DeviceInfoFactoryData factoryData = deviceInfoFactoryDataDao.fetchDeviceInfoFactoryData(orderedMap);
        LOGGER.debug("## factoryData for serial number: {}", factoryData);
        if (factoryData != null) {
            throw new ApiValidationFailedException(DEVICE_ALREADY_EXIST_BY_SERIAL_NUMBER.getCode(),
                DEVICE_ALREADY_EXIST_BY_SERIAL_NUMBER.getMessage(),
                DEVICE_ALREADY_EXIST_BY_SERIAL_NUMBER.getGeneralMessage());
        }
    }

    /**
     * Validates if the device factory data request has all the mandatory parameters passed based on the device type.
     * If any mandatory parameter is missing or invalid, an exception is thrown.
     *
     * @param deviceFactoryDataDto      The device factory data DTO containing the request parameters.
     * @param deviceTypeMandatoryParams The device type mandatory parameters configuration.
     * @param allowedDeviceTypes        The allowed device types.
     * @throws ApiValidationFailedException If any mandatory parameter is missing or invalid.
     * @throws ApiTechnicalException        If the device type mandatory parameters are empty or not found.
     */
    protected void validateMandatoryParamsBasedOnDeviceType(DeviceFactoryDataDto deviceFactoryDataDto,
                                                            DeviceTypeMandatoryParams deviceTypeMandatoryParams,
                                                            String[] allowedDeviceTypes) {

        //Validate if device type is passed in request and is a valid device type
        if (!Utils.validateAllowedType(deviceFactoryDataDto.getDeviceType(), allowedDeviceTypes)) {
            LOGGER.debug("## device type is passed in request is null or is invalid");
            throw new ApiValidationFailedException(INVALID_DEVICE_TYPE.getCode(), INVALID_DEVICE_TYPE.getMessage(),
                INVALID_DEVICE_TYPE.getGeneralMessage());
        }

        //Convert request DTO to Map and the obtained map will have at least valid device type; hence map wont be empty
        LinkedHashMap<String, String> requestParams = MAPPER.convertValue(deviceFactoryDataDto, LinkedHashMap.class);
        LOGGER.debug("## requestParams map after converting request DTO to Map: {}", requestParams);

        //Retrieve mandatory params based on request device type
        DeviceTypeMandatoryParam deviceTypeMandatoryParam =
            deviceTypeMandatoryParams.getDeviceTypeMandatoryParams().stream()
                .filter(deviceTypeMandParam -> deviceTypeMandParam.getDeviceType()
                    .equalsIgnoreCase(deviceFactoryDataDto.getDeviceType()))
                .findAny().orElse(null);
        LOGGER.debug("## deviceTypeMandatoryParam based on device type passed in request: {}",
            deviceTypeMandatoryParam);
        if (deviceTypeMandatoryParam == null
            || CollectionUtils.isEmpty(deviceTypeMandatoryParam.getMandatoryParams())) {
            LOGGER.debug(
                "## no entries exist in system parameters or mandatory params retrieved from system parameters are"
                    + " empty; for the device type passed in the request");
            throw new ApiTechnicalException(DEVICE_TYPE_MANDATORY_PARAMETERS_EMPTY.getCode(),
                DEVICE_TYPE_MANDATORY_PARAMETERS_EMPTY.getMessage(),
                DEVICE_TYPE_MANDATORY_PARAMETERS_EMPTY.getGeneralMessage());
        }

        //Reduce the request map to contain only elements based on the mandatory params list obtained in previous step
        List<String> reducedRequestValues = requestParams.entrySet().stream()
            .filter(reqParam -> deviceTypeMandatoryParam.getMandatoryParams().contains(reqParam.getKey()))
            .map(reqParam -> ((reqParam.getValue() == null || reqParam.getValue().trim().isEmpty()) ? null :
                reqParam.getValue()))
            .toList();
        LOGGER.debug("## reducedRequestValues list based on mandatory params: {}", reducedRequestValues);
        if (CollectionUtils.isEmpty(reducedRequestValues)) {
            LOGGER.debug(
                "## mandatory params retrieved from system parameters doesnt contain any values or are different from"
                    + " the request params");
            throw new ApiTechnicalException(GENERAL_ERROR.getCode(),
                GENERAL_ERROR.getMessage(),
                GENERAL_ERROR.getGeneralMessage());
        }

        if (reducedRequestValues.contains(null)) {
            LOGGER.debug("## one or more mandatory params missing in the request json for the device type passed");
            throw new ApiValidationFailedException(MISSING_MANDATORY_REQUEST_PARAMS.getCode(),
                MISSING_MANDATORY_REQUEST_PARAMS.getMessage(),
                MISSING_MANDATORY_REQUEST_PARAMS.getGeneralMessage());
        }

    }
}
