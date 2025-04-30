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

package org.eclipse.ecsp.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.ecsp.common.ApiResponse;
import org.eclipse.ecsp.common.ErrorUtils;
import org.eclipse.ecsp.common.HcpServicesBaseResponse;
import org.eclipse.ecsp.common.ResponseConstants;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.common.enums.ApiMessageEnum;
import org.eclipse.ecsp.common.exception.ApiTechnicalException;
import org.eclipse.ecsp.common.exception.ApiValidationFailedException;
import org.eclipse.ecsp.common.exception.DeleteDeviceException;
import org.eclipse.ecsp.common.exception.DeviceNotFoundException;
import org.eclipse.ecsp.common.exception.InputParamValidationException;
import org.eclipse.ecsp.common.exception.InvalidDeviceStateException;
import org.eclipse.ecsp.common.exception.PageParamResolverException;
import org.eclipse.ecsp.common.exception.SizeParamResolverException;
import org.eclipse.ecsp.config.DeviceInfoQueryProperty;
import org.eclipse.ecsp.dto.DeviceFilterDto;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryDataRequest;
import org.eclipse.ecsp.dto.DeviceInfoFactoryDataWithSubscription;
import org.eclipse.ecsp.dto.DeviceInfoPage;
import org.eclipse.ecsp.dto.DeviceInfoQueryDto;
import org.eclipse.ecsp.dto.DeviceInfoQuerySuccessResponse;
import org.eclipse.ecsp.dto.DeviceStateHistory;
import org.eclipse.ecsp.dto.DeviceUpdateRequest;
import org.eclipse.ecsp.dto.ErrorRest;
import org.eclipse.ecsp.dto.RestResponse;
import org.eclipse.ecsp.dto.SimpleResponseMessage;
import org.eclipse.ecsp.dto.StateChange;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataBaseDto;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataCreateDto;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataDto;
import org.eclipse.ecsp.dto.read.device.DeviceDetailsBaseDto;
import org.eclipse.ecsp.dto.read.device.DeviceDetailsDtoV1;
import org.eclipse.ecsp.dto.read.device.DeviceDetailsDtoV2;
import org.eclipse.ecsp.dto.read.device.DeviceDetailsDtoV4;
import org.eclipse.ecsp.dto.read.device.DeviceDetailsDtoV5;
import org.eclipse.ecsp.dto.validation.ValuesAllowed;
import org.eclipse.ecsp.security.Security;
import org.eclipse.ecsp.service.DeviceInfoQueryService;
import org.eclipse.ecsp.service.DeviceInfoQueryValidator;
import org.eclipse.ecsp.service.create.device.IdeviceFactoryDataService;
import org.eclipse.ecsp.service.read.device.IallDeviceDetailsService;
import org.eclipse.ecsp.service.read.device.IdeviceDetailsService;
import org.eclipse.ecsp.webutil.ExtendedApiResponse;
import org.eclipse.ecsp.webutil.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InvalidAttributeValueException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.eclipse.ecsp.common.enums.ApiMessageEnum.DEVICE_NOT_FOUND;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.DEVICE_RETRIEVE_SUCCESS;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.DEVICE_UPDATE_FAILED;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.DEVICE_UPDATE_SUCCESS;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.GENERAL_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

/**
 * The DeviceController class is responsible for handling HTTP requests related to devices.
 * It provides APIs for creating devices and retrieving device information.
 */
@RestController
@Validated
public class DeviceController {
    private static final String INTERNAL_SERVER_ERROR = "Internal server error";
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceController.class);
    private static final String SUCCESS = "Success";
    private static final String USER_ID = "user-id";
    private static final String REQUEST_REGEX = "[\r\n]";
    private static final String ERROR_MESSAGE = "## Error has occurred while creating device. ErrorMsg: {}";
    private static final String LOG_MESSAGE = "## Error has occurred while calling getAllFactoryData() errorMsg: {},";
    private static final String ERROR_LOG = " rootCause: {}, errorTrace: {}";
    private static final String DEVICE_RETRIEVAL_FAILED = "Device details retrieval failed";
    private static final String ROOT_CAUSE = ", Root Cause:";
    private static final String DEVICE_STATES_RETRIEVAL_FAILED = "Device states retrieval failed";
    private static final String REQUEST_ID = "req1";
    private static final String CODE = "HCP-001";
    private static final int STATUS_CODE_200 = 200;
    private static final int STATUS_CODE_400 = 400;
    private static final int STATUS_CODE_404 = 404;
    private static final int STATUS_CODE_500 = 500;

    @Resource(name = "envConfig")
    protected EnvConfig<DeviceInfoQueryProperty> config;

    @Autowired
    private DeviceInfoQueryService deviceInfoQueryService;

    @Autowired
    @Qualifier(value = "deviceFactoryDataService")
    private IdeviceFactoryDataService deviceFactoryDataService;

    @Autowired
    @Qualifier(value = "deviceDetailsServiceV1")
    private IdeviceDetailsService<DeviceDetailsBaseDto> deviceDetailsServiceV1;

    @Autowired
    @Qualifier(value = "allDeviceDetailsServiceV2")
    private IallDeviceDetailsService<DeviceDetailsBaseDto, DeviceInfoPage<List<DeviceInfoFactoryData>>>
        allDeviceDetailsServiceV2;

    @Autowired
    @Qualifier(value = "allDeviceDetailsServiceV4")
    private IallDeviceDetailsService<DeviceDetailsBaseDto, DeviceInfoQueryDto<DeviceInfoFactoryDataWithSubscription>>
        allDeviceDetailsServiceV4;

    @Autowired
    @Qualifier(value = "allDeviceDetailsServiceV5")
    private IallDeviceDetailsService<DeviceDetailsBaseDto, DeviceInfoQueryDto<DeviceInfoFactoryDataWithSubscription>>
        allDeviceDetailsServiceV5;

    @Autowired
    @Qualifier(value = "guestUserDeviceFactoryDataService")
    private IdeviceFactoryDataService guestUserDeviceFactoryDataService;

    @Autowired
    @Qualifier(value = "swmIntegrationDeviceFactoryDataService")
    private IdeviceFactoryDataService swmIntegrationDeviceFactoryDataService;

    /**
     * Replaces new line and carriage return characters in the given data with an empty string.
     *
     * @param data the data to be processed
     * @param <T> the type of the data
     * @return the processed data with new line and carriage return characters removed, or null if the input data is
     *      null
     */
    public static <T> String replaceNewLineAndCarriage(T data) {
        return data != null ? data.toString().replaceAll(REQUEST_REGEX, "") : null;
    }

    /**
     * Creates devices using the provided device factory data in version 1 format.
     *
     * @param deviceInfoFactoryData The array of device information factory data.
     * @param userId The user ID obtained from the request header.
     * @return A ResponseEntity containing the response object.
     */
    @PostMapping(value = "/v1/devices/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "POST /v1/devices/create", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "IgniteSystem"})
    public ResponseEntity<Object> addDevices(@RequestBody @Valid DeviceInfoFactoryDataRequest[] deviceInfoFactoryData,
                                             @RequestHeader(USER_ID) String userId) {
        String userIdForAddDevice = userId.replaceAll(REQUEST_REGEX, "");
        String requestData = Arrays.toString(deviceInfoFactoryData).replaceAll(REQUEST_REGEX, "");
        LOGGER.info("Invoking create devices API, UserId:{}, Request{}", userIdForAddDevice, requestData);
        deviceInfoQueryService.saveDeviceFactoryData(deviceInfoFactoryData, userId);
        LOGGER.info("End create devices API call.");
        return RestResponse.ok(new SimpleResponseMessage(SUCCESS));
    }

    /**
     * Creates devices using the provided device factory data in version 2 format.
     *
     * @param deviceFactoryDataDtos An array of DeviceFactoryDataDto objects containing the device factory data.
     * @param userId                The user ID obtained from the request header.
     * @return                      A ResponseEntity containing the API response.
     */
    @PostMapping(value = "/v2/devices/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "POST /v2/devices/create", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "IgniteSystem"})
    public ResponseEntity<ApiResponse<Object>> createDevicesV2(
        @RequestBody @Valid DeviceFactoryDataDto[] deviceFactoryDataDtos,
        @RequestHeader(USER_ID) String userId) {
        String requestUserId = userId.replaceAll(REQUEST_REGEX, "");
        LOGGER.info("## createDevices  Controller - START UserId: {}", requestUserId);
        ApiResponse<Object> apiResponse;
        try {
            boolean createDeviceStatus = createDevicesByType(deviceFactoryDataDtos, userId, "V2");
            if (createDeviceStatus) {
                apiResponse = new ApiResponse.Builder<>(
                    ApiMessageEnum.FACTORY_DATA_CREATE_SUCCESS.getCode(),
                    ApiMessageEnum.FACTORY_DATA_CREATE_SUCCESS.getMessage(),
                    OK).build();
            } else {
                apiResponse = new ApiResponse.Builder<>(ApiMessageEnum.GENERAL_ERROR.getCode(),
                    ApiMessageEnum.GENERAL_ERROR.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (ApiValidationFailedException e) {
            LOGGER.error(ERROR_MESSAGE, e.getErrorMessage());
            apiResponse = new ApiResponse.Builder<>(e.getCode(), e.getMessage(), BAD_REQUEST).build();
        } catch (Exception e) {
            apiResponse = parseException(e);
            if (apiResponse == null) {
                apiResponse = new ApiResponse.Builder<>(ApiMessageEnum.GENERAL_ERROR.getCode(),
                    ApiMessageEnum.GENERAL_ERROR.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            Map<Object, Object> deviceFactoryDataErrorMap = new LinkedHashMap<>();
            deviceFactoryDataErrorMap.put("deviceFactoryDataDtos", Arrays.toString(deviceFactoryDataDtos));
            deviceFactoryDataErrorMap.put(ErrorUtils.ERROR_CODE_KEY, ApiMessageEnum.GENERAL_ERROR.getCode());
            LOGGER.error("{}",
                ErrorUtils.buildError("## Error has occurred while creating device.", e, deviceFactoryDataErrorMap));
        }
        LOGGER.info("## createDevices Controller - END");
        return WebUtils.getResponseEntity(apiResponse);
    }

    /**
     * Creates devices using the provided device factory data in version 3.
     *
     * @param deviceFactoryDataDtos An array of DeviceFactoryDataDto objects containing the device factory data.
     * @param userId The user ID obtained from the request header.
     * @return A ResponseEntity containing the API response.
     */
    @PostMapping(value = "/v3/devices/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "POST /v3/devices/create", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "IgniteSystem"})
    public ResponseEntity<ApiResponse<Object>> createDevicesV3(
        @RequestBody @Valid DeviceFactoryDataDto[] deviceFactoryDataDtos,
        @RequestHeader(USER_ID) String userId) {
        String requestUserId = userId.replaceAll(REQUEST_REGEX, "");
        LOGGER.info("## v3 createDevices  Controller - START UserId: {}", requestUserId);
        ApiResponse<Object> apiResponse;
        try {
            boolean createDeviceStatus = createDevicesByType(deviceFactoryDataDtos, userId, "V3");
            if (createDeviceStatus) {
                apiResponse = new ApiResponse.Builder<>(
                    ApiMessageEnum.FACTORY_DATA_CREATE_SUCCESS.getCode(),
                    ApiMessageEnum.FACTORY_DATA_CREATE_SUCCESS.getMessage(),
                    OK).build();
            } else {
                apiResponse = new ApiResponse.Builder<>(ApiMessageEnum.GENERAL_ERROR.getCode(),
                    ApiMessageEnum.GENERAL_ERROR.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (ApiValidationFailedException e) {
            LOGGER.error(ERROR_MESSAGE, e.getErrorMessage());
            apiResponse = new ApiResponse.Builder<>(e.getCode(), e.getMessage(), BAD_REQUEST).build();
        } catch (ApiTechnicalException e) {
            LOGGER.error(ERROR_MESSAGE, e.getErrorMessage());
            apiResponse =
                new ApiResponse.Builder<>(e.getCode(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            apiResponse = parseException(e);
            if (apiResponse == null) {
                apiResponse = new ApiResponse.Builder<>(ApiMessageEnum.GENERAL_ERROR.getCode(),
                    ApiMessageEnum.GENERAL_ERROR.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            Map<Object, Object> deviceFactoryErrorMap = new LinkedHashMap<>();
            deviceFactoryErrorMap.put("deviceFactoryDataDtos", Arrays.toString(deviceFactoryDataDtos));
            deviceFactoryErrorMap.put(ErrorUtils.ERROR_CODE_KEY, ApiMessageEnum.GENERAL_ERROR.getCode());
            LOGGER.error("{}",
                ErrorUtils.buildError("## Error has occurred while creating device.", e, deviceFactoryErrorMap));
        }
        LOGGER.info("## createDevices Controller - END");
        return WebUtils.getResponseEntity(apiResponse);
    }

    /**
     * Creates devices based on the device factory data, user ID, and version.
     *
     * @param deviceFactoryDataDtos An array of device factory data objects.
     * @param userId               The ID of the user.
     * @param version              The version of the device.
     * @return                     A boolean indicating the status of device creation.
     * @throws Exception           If an error occurs during device creation.
     */
    private boolean createDevicesByType(DeviceFactoryDataDto[] deviceFactoryDataDtos, String userId, String version)
        throws Exception {
        DeviceFactoryDataBaseDto deviceFactoryDataBaseDto =
            new DeviceFactoryDataCreateDto(deviceFactoryDataDtos, userId, version);
        String deviceCreationType = config.getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        LOGGER.info("deviceCreationType: {}", deviceCreationType);
        boolean createDeviceStatus;
        switch (deviceCreationType) {
            case "swmIntegration":
                createDeviceStatus = swmIntegrationDeviceFactoryDataService.createDevice(deviceFactoryDataBaseDto);
                break;
            case "guestUser":
                createDeviceStatus = guestUserDeviceFactoryDataService.createDevice(deviceFactoryDataBaseDto);
                break;
            default:
                createDeviceStatus = deviceFactoryDataService.createDevice(deviceFactoryDataBaseDto);
                break;
        }
        return createDeviceStatus;
    }

    /**
     * Retrieves factory data for devices based on the provided parameters.
     *
     * @param imei                 the IMEI number of the device (optional)
     * @param serialnumber         the serial number of the device (optional)
     * @param ssid                 the SSID of the device (optional)
     * @param iccid                the ICCID of the device (optional)
     * @param msisdn               the MSISDN of the device (optional)
     * @param imsi                 the IMSI of the device (optional)
     * @param bssid                the BSSID of the device (optional)
     * @param packageserialnumber  the package serial number of the device (optional)
     * @return a ResponseEntity containing the factory data for the devices
     */
    @GetMapping(value = "/v1/devices/details")
    @Operation(summary = "GET /v1/devices/details", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "IgniteSystem"})
    public ResponseEntity<Object> findFactoryData(
            @RequestParam(value = "imei", required = false) 
            @Pattern(regexp = "^[0-9]*$", message = "IMEI should be number only") String imei,
            @RequestParam(value = "serialnumber", required = false)
            @Pattern(regexp = "^[0-9a-zA-Z]*$", 
                message = "serialnumber should be alphanumeric") String serialnumber,
            @RequestParam(value = "ssid", required = false) 
            @Pattern(regexp = "^[0-9a-zA-Z]*$", message = "ssid should be alphanumeric") String ssid,
            @RequestParam(value = "iccid", required = false) 
            @Pattern(regexp = "^[0-9a-zA-Z]*$", message = "iccid should be alphanumeric") String iccid,
            @RequestParam(value = "msisdn", required = false) 
            @Pattern(regexp = "^[0-9]*$", message = "msisdn should be number only") String msisdn,
            @RequestParam(value = "imsi", required = false) 
            @Pattern(regexp = "^[0-9a-zA-Z]*$", message = "imsi should be alphanumeric") String imsi,
            @RequestParam(value = "bssid", required = false) 
            @Pattern(regexp = "^[0-9a-zA-Z:]*$", message = "bssid should be alphanumeric") String bssid,
            @RequestParam(value = "packageserialnumber", required = false)
            @Pattern(regexp = "^[0-9a-zA-Z]*$", 
                message = "packageserialnumber should be alphanumeric") String packageserialnumber) {
        try {
            LOGGER.info("## findFactoryData Controller - START");
            DeviceDetailsBaseDto baseDto = new DeviceDetailsDtoV1(serialnumber, imei, ssid, iccid, msisdn, imsi, bssid,
                packageserialnumber);
            List<DeviceInfoFactoryData> factoryData = deviceDetailsServiceV1.findFactoryData(baseDto);
            LOGGER.info("## factoryData got from DB: {}", factoryData);
            LOGGER.info("## findFactoryData Controller - END");
            return RestResponse.ok(factoryData);
        } catch (DeviceNotFoundException e) {
            LOGGER.error(
                "## Error has occurred while calling find factory data. imei: {}, serialNumber: {}, errorMsg: {},"
                    + ERROR_LOG,
                replaceNewLineAndCarriage(imei), replaceNewLineAndCarriage(serialnumber),
                replaceNewLineAndCarriage(e.getCause()),
                replaceNewLineAndCarriage(ExceptionUtils.getRootCauseMessage(e)),
                e.toString().replaceAll(REQUEST_REGEX, ""));
            ErrorRest errorRest = new ErrorRest();
            errorRest.setMessage(e.getMessage());
            errorRest.setReference(null);
            return RestResponse.not_Found(errorRest);
        } catch (Exception e) {
            LOGGER.error(
                "## Error has occurred while find factory data. imei: {}, serialNumber: {}, errorMsg: {},"
                    + ERROR_LOG,
                replaceNewLineAndCarriage(imei), replaceNewLineAndCarriage(serialnumber),
                replaceNewLineAndCarriage(e.getCause()),
                replaceNewLineAndCarriage(ExceptionUtils.getRootCauseMessage(e)),
                e.toString().replaceAll(REQUEST_REGEX, ""));
            ErrorRest errorRest = new ErrorRest();
            errorRest.setMessage(e.getMessage());
            errorRest.setReference(null);
            return RestResponse.internal_Server_Error(errorRest);
        }
    }

    /**
     * Retrieves all factory data based on the provided parameters.
     *
     * @param isdetailsrequired   Indicates whether details are required.
     * @param size                The size of the data to retrieve (optional).
     * @param page                The page number of the data to retrieve (optional).
     * @param asc                 Sort the data in ascending order (optional).
     * @param desc                Sort the data in descending order (optional).
     * @param imei                The IMEI number of the device (optional).
     * @param serialNumber        The serial number of the device (optional).
     * @return                    The ResponseEntity containing the retrieved factory data.
     */
    @GetMapping(value = "v2/devices/details")
    @Operation(summary = "GET v2/devices/details", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) },
            parameters = { @Parameter(in = ParameterIn.QUERY, name = "isdetailsrequired",
                    schema = @Schema(allowableValues = { "true", "false" }), required = true)})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "IgniteSystem"})
    public ResponseEntity<Object> getAllFactoryData(
            @RequestParam(value = "isdetailsrequired", defaultValue = "true")
                                                        String isdetailsrequired,
            @RequestParam(value = "size", required = false, defaultValue = "") String size,
            @RequestParam(value = "page", required = false, defaultValue = "") String page,
            @RequestParam(value = "asc", required = false, defaultValue = "") String asc,
            @RequestParam(value = "desc", required = false, defaultValue = "") String desc,
            @RequestParam(value = "imei", required = false) 
            @Pattern(regexp = "^[0-9]*$", message = "IMEI should be number only") String imei,
            @RequestParam(value = "serialnumber", required = false)
            @Pattern(regexp = "^[0-9a-zA-Z]*$", message = "Serial number should be alphanumeric") String serialNumber) {
        ErrorRest errorRest = new ErrorRest();
        try {
            LOGGER.info("## findFactoryData Controller - START");
            DeviceDetailsBaseDto baseDto =
                new DeviceDetailsDtoV2(size, page, asc, desc, isdetailsrequired, imei, serialNumber);
            DeviceInfoPage<List<DeviceInfoFactoryData>> allFactoryData =
                allDeviceDetailsServiceV2.findAllFactoryData(baseDto);
            LOGGER.info("## all factoryData got from DB: {}", allFactoryData);
            LOGGER.info("## all findFactoryData Controller - END");
            return RestResponse.ok(allFactoryData);
        } catch (IllegalArgumentException | InputParamValidationException | PageParamResolverException
            | SizeParamResolverException e) {
            LOGGER.error(
                "## Error has occurred while calling findFactoryData() where  imei: {}, serialNumber: {}, errorMsg: {},"
                    + ERROR_LOG,
                imei != null ? imei.replaceAll(REQUEST_REGEX, "") : null,
                serialNumber != null ? serialNumber.replaceAll(REQUEST_REGEX, "") : null,
                e.getCause() != null ? e.getCause().toString().replaceAll(REQUEST_REGEX, "") : null,
                ExceptionUtils.getRootCauseMessage(e) != null
                    ? ExceptionUtils.getRootCauseMessage(e).replaceAll(REQUEST_REGEX, "") : null,
                e.toString().replaceAll(REQUEST_REGEX, ""));
            errorRest.setMessage(e.getMessage());
            return RestResponse.bad_Request(errorRest);
        } catch (DeviceNotFoundException e) {
            LOGGER.error(
                "## Error has occurred while calling findFactoryData() where imei: {}, serialNumber: {}, errorMsg: {},"
                    + " rootCause: {}, errorTrace: ",
                imei, serialNumber, e.getCause(), ExceptionUtils.getRootCauseMessage(e), e);
            errorRest.setMessage(e.getMessage());
            return RestResponse.not_Found(errorRest);
        } catch (Exception e) {
            LOGGER.error(
                "## Error has occurred while calling findFactoryData() where imei: {}, serialNumber: {}, errorMsg: {},"
                    + " rootCause: {}, errorTrace: ",
                imei, serialNumber, e.getCause(), ExceptionUtils.getRootCauseMessage(e), e);
            errorRest.setMessage(INTERNAL_SERVER_ERROR);
            return RestResponse.internal_Server_Error(errorRest);
        }
    }

    /**
     * Retrieves all factory data for devices.
     *
     * @param imei    The IMEI number of the device (optional).
     * @param size    The number of results to return per page (optional).
     * @param page    The page number of the results to return (optional).
     * @param sortby  The field to sort the results by (optional, default: "imei").
     * @param orderBy The order in which to sort the results ("asc" or "desc", optional, default: "desc").
     * @return A ResponseEntity containing the response data.
     */
    @GetMapping(value = "/v4/devices/details")
    @Operation(summary = "GET /v4/devices/details", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) })
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "IgniteSystem"})
    public ResponseEntity<HcpServicesBaseResponse> getAllFactoryData(
        @RequestParam(value = "imei", required = false) 
        @Pattern(regexp = "^[0-9]*$", message = "IMEI should be number only") String imei,
        @RequestParam(value = "size", required = false, defaultValue = "") String size,
        @RequestParam(value = "page", required = false, defaultValue = "") String page,
        @RequestParam(value = "sortby", required = false, defaultValue = "imei") String sortby,
        @ValuesAllowed(propName = "orderBy", values = { "asc", "desc" }) 
        @RequestParam(value = "orderby", required = false, defaultValue = "desc") String orderBy) {
        try {
            String requestImei = replaceNewLineAndCarriage(imei);
            String requestSize = replaceNewLineAndCarriage(size);
            String requestPage = replaceNewLineAndCarriage(page);
            String requestSortby = replaceNewLineAndCarriage(sortby);
            String requestOrderBy = replaceNewLineAndCarriage(orderBy);
            LOGGER.debug("getAllFactoryData Controller START - imei:{}, size:{}, page:{}, sortby:{}, orderby:{}",
                requestImei, requestSize, requestPage, requestSortby, requestOrderBy);
            DeviceDetailsBaseDto baseDto = new DeviceDetailsDtoV4(size, page, sortby, orderBy, imei);
            DeviceInfoQueryDto<DeviceInfoFactoryDataWithSubscription> allFactoryData = allDeviceDetailsServiceV4
                .findAllFactoryData(baseDto, imei);

            DeviceInfoQuerySuccessResponse<DeviceInfoFactoryDataWithSubscription> result =
                new DeviceInfoQuerySuccessResponse<>();
            String reason = "All Devices Retrieved based on given IMEI";
            String message = "Device details fetched based on either full/partial imei";
            setResult(allFactoryData, result, STATUS_CODE_200, REQUEST_ID, CODE, reason, message);
            return RestResponse.ok(result);
        } catch (PageParamResolverException | SizeParamResolverException | InputParamValidationException e) {
            if (StringUtils.isEmpty(imei)) {
                LOGGER.error("## Error has occurred while finding all factoryData. errorMsg: {},"
                    + ERROR_LOG, e.getMessage(), ExceptionUtils.getRootCauseMessage(e), e);
                return RestResponse.createFailureResponse(STATUS_CODE_400, "Device details retrieval failed ",
                    "Failed to retrieve  device details, Root Cause: " + e.getMessage());
            } else {
                LOGGER.error("## Error has occurred while calling findFactoryData() where imei: {}, errorMsg: {},"
                        + ERROR_LOG,
                    replaceNewLineAndCarriage(imei), replaceNewLineAndCarriage(e.getCause()),
                    replaceNewLineAndCarriage(ExceptionUtils.getRootCauseMessage(e)),
                    e.toString().replaceAll(REQUEST_REGEX, ""));
                return RestResponse.createFailureResponse(STATUS_CODE_400, "Device details retrieval failed ",
                    "Failed to retrieve device details for given IMEI : " + imei + ", Root Cause:  " + e.getMessage());
            }

        } catch (DeviceNotFoundException e) {
            if (StringUtils.isEmpty(imei)) {
                LOGGER.error(LOG_MESSAGE + ERROR_LOG, e.getCause(), ExceptionUtils.getRootCauseMessage(e), e);
                return RestResponse.createFailureResponse(STATUS_CODE_404, DEVICE_RETRIEVAL_FAILED,
                    "Failed to retrieve device details,  Root Cause: " + e.getMessage());
            } else {
                LOGGER.error("## Error has occurred while calling findFactoryData() where imei: {}, errorMsg: {},"
                        + ERROR_LOG, imei, e.getCause(), ExceptionUtils.getRootCauseMessage(e), e);
                return RestResponse.createFailureResponse(STATUS_CODE_404, DEVICE_RETRIEVAL_FAILED,
                    "Failed to retrieve device details for given  IMEI: " + imei + ROOT_CAUSE + e.getMessage());
            }
        } catch (Exception e) {
            if (StringUtils.isEmpty(imei)) {
                LOGGER.error(LOG_MESSAGE + ERROR_LOG, e.getCause(), ExceptionUtils.getRootCauseMessage(e), e);
                return RestResponse.createFailureResponse(STATUS_CODE_500, DEVICE_RETRIEVAL_FAILED,
                    "Failed to retrieve device details, Root Cause:  " + e.getMessage());
            } else {
                LOGGER.error(
                    "## Error has occurred while calling findFactoryData() imei: {}, errorMsg: {},"
                        + ERROR_LOG, imei, e.getCause(), ExceptionUtils.getRootCauseMessage(e), e);
                LOGGER.error("Error while fetching device details based on imei: {}, error:{}", imei, e);
                return RestResponse.createFailureResponse(STATUS_CODE_500, DEVICE_RETRIEVAL_FAILED,
                    "Failed to retrieve device details for given IMEI: " + imei + ROOT_CAUSE + e.getMessage());
            }
        }
    }

    /**
     * Sets the result of the device information query.
     *
     * @param allFactoryData The device information query data.
     * @param result The device information query success response.
     * @param statusCode The HTTP status code.
     * @param requestId The request ID.
     * @param code The response code.
     * @param reason The reason for the response.
     * @param message The response message.
     */
    private void setResult(DeviceInfoQueryDto<DeviceInfoFactoryDataWithSubscription> allFactoryData,
                           DeviceInfoQuerySuccessResponse<DeviceInfoFactoryDataWithSubscription> result,
                           int statusCode, String requestId, String code, String reason, String message) {
        result.setHttpStatusCode(statusCode);
        result.setRequestId(requestId);
        result.setCode(code);
        result.setReason(reason);
        result.setMessage(message);
        result.setRecordStats(allFactoryData.getRecordStats());
        result.setData(allFactoryData.getData());
    }

    /**
     * Retrieves all factory data with subscription status based on the provided parameters.
     *
     * @param imei         the IMEI number of the device (optional)
     * @param size         the size of the page (optional)
     * @param page         the page number (optional)
     * @param serialNumber the serial number of the device (optional)
     * @param deviceId     the ID of the device (optional)
     * @param vin          the VIN (Vehicle Identification Number) of the device (optional)
     * @param sortby       the field to sort by (optional, default value is "imei")
     * @param orderBy      the order of sorting (optional, default value is "desc")
     * @param state        the state of the device (optional)
     * @return the ResponseEntity containing the response with the device details
     */
    @GetMapping(value = "/v5/devices/details")
    @Operation(summary = "GET /v5/devices/details", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) })
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "IgniteSystem"})
    public ResponseEntity<HcpServicesBaseResponse> getAllFactoryDataWithSubscriptionStatus(
        @RequestParam(value = "imei", required = false) 
        @Pattern(regexp = "^[0-9]*$", message = "IMEI should be number only") String imei,
        @RequestParam(value = "size", required = false, defaultValue = "") String size,
        @RequestParam(value = "page", required = false, defaultValue = "") String page,
        @RequestParam(value = "serialnumber", required = false, defaultValue = "") String serialNumber,
        @RequestParam(value = "deviceid", required = false, defaultValue = "") String deviceId,
        @RequestParam(value = "vin", required = false, defaultValue = "") String vin,
        @RequestParam(value = "sortby", required = false, defaultValue = "imei") String sortby,
        @ValuesAllowed(propName = "orderBy", values = { "asc", "desc" }) 
        @RequestParam(value = "orderby", required = false, defaultValue = "desc") String orderBy,
        @RequestParam(value = "state", required = false, defaultValue = "") String state) {
        try {
            String requestImei = replaceNewLineAndCarriage(imei);
            String requestSerialNumber = replaceNewLineAndCarriage(serialNumber);
            String requestDeviceId = replaceNewLineAndCarriage(deviceId);
            String requestSize = replaceNewLineAndCarriage(size);
            String requestPage = replaceNewLineAndCarriage(page);
            String requestSortby = replaceNewLineAndCarriage(sortby);
            String requestOrderBy = replaceNewLineAndCarriage(orderBy);
            String requestVin = replaceNewLineAndCarriage(vin);
            String requestState = replaceNewLineAndCarriage(state);
            LOGGER.info(
                "## getAllFactoryDataWithSubscriptionStatus Controller START - imei:{}, serialNumber:{}, deviceId:{},"
                    + " size:{}, page:{}, sortby:{}, orderby:{}, vin:{}, state:{}",
                requestImei, requestSerialNumber, requestDeviceId, requestSize, requestPage, requestSortby,
                requestOrderBy, requestVin, requestState);
            DeviceDetailsBaseDto baseDto =
                new DeviceDetailsDtoV5(size, page, sortby, orderBy, imei, serialNumber, deviceId, state, vin);
            DeviceInfoQueryDto<DeviceInfoFactoryDataWithSubscription> dto =
                allDeviceDetailsServiceV5.findAllFactoryData(baseDto, imei, serialNumber, deviceId, vin, state);
            DeviceInfoQuerySuccessResponse<DeviceInfoFactoryDataWithSubscription> result =
                new DeviceInfoQuerySuccessResponse<>();
            String reason = "Device details retrieval";
            String message = "Device details fetched based on either full/partial imei or serialNumber or deviceId";
            setResult(dto, result, STATUS_CODE_200, REQUEST_ID, CODE, reason, message);
            return RestResponse.ok(result);
        } catch (PageParamResolverException | SizeParamResolverException | InputParamValidationException
            | IllegalArgumentException e) {
            LOGGER.error(LOG_MESSAGE + ERROR_LOG, e.getCause(), ExceptionUtils.getRootCauseMessage(e), e);
            return RestResponse.createFailureResponse(STATUS_CODE_400, DEVICE_RETRIEVAL_FAILED,
                "Failed to retrieve device details, Root Cause:  " + e.getMessage());
        } catch (DeviceNotFoundException e) {
            return checkIfEmpty(imei, serialNumber, deviceId, state, e);
        } catch (Exception e) {
            LOGGER.error(LOG_MESSAGE + ERROR_LOG, e.getCause(), ExceptionUtils.getRootCauseMessage(e), e);
            return RestResponse.createFailureResponse(STATUS_CODE_500, DEVICE_RETRIEVAL_FAILED,
                "Failed to retrieve device details, Root Cause: " + e.getMessage());
        }
    }

    /**
     * Checks if any of the input parameters (imei, serialNumber, deviceId, state) is empty and returns an appropriate
     * ResponseEntity with an error message if any of the parameters is empty. If none of the parameters is empty, it
     * returns a ResponseEntity with a generic error message.
     *
     * @param imei           The IMEI number of the device.
     * @param serialNumber   The serial number of the device.
     * @param deviceId       The ID of the device.
     * @param state          The state of the device.
     * @param e              The DeviceNotFoundException that occurred.
     * @return               A ResponseEntity containing an appropriate error message based on the empty parameter.
     */
    private ResponseEntity<HcpServicesBaseResponse> checkIfEmpty(String imei, String serialNumber, String deviceId,
                                                                 String state, DeviceNotFoundException e) {
        if (!StringUtils.isEmpty(imei)) {
            LOGGER.error("## Error has occurred while calling getAllFactoryData() imei: {},  errorMsg: {},"
                + ERROR_LOG, imei, e.getCause(), ExceptionUtils.getRootCauseMessage(e), e);
            return RestResponse.createFailureResponse(STATUS_CODE_404, DEVICE_RETRIEVAL_FAILED,
                "Failed to retrieve device details for given IMEI: " + imei + ROOT_CAUSE + e.getMessage());
        }
        if (!StringUtils.isEmpty(serialNumber)) {
            LOGGER.error("## Error has occurred while calling getAllFactoryData() serialNumber: {},  errorMsg: {},"
                + ERROR_LOG, serialNumber, e.getCause(), ExceptionUtils.getRootCauseMessage(e), e);
            return RestResponse.createFailureResponse(STATUS_CODE_404, DEVICE_RETRIEVAL_FAILED,
                "Failed to retrieve device details for given serialnumber: " + serialNumber + ROOT_CAUSE
                    + e.getMessage());
        }
        if (!StringUtils.isEmpty(deviceId)) {
            LOGGER.error("## Error has occurred while calling getAllFactoryData() deviceId: {},  errorMsg: {},"
                + ERROR_LOG, deviceId, e.getCause(), ExceptionUtils.getRootCauseMessage(e), e);
            return RestResponse.createFailureResponse(STATUS_CODE_404, DEVICE_RETRIEVAL_FAILED,
                "Failed to retrieve device details for given deviceid: " + deviceId + ROOT_CAUSE + e.getMessage());
        }
        if (!StringUtils.isEmpty(state)) {
            LOGGER.error("## Error has occurred while calling getAllFactoryData() state: {},  errorMsg: {},"
                + ERROR_LOG, state, e.getCause(), ExceptionUtils.getRootCauseMessage(e), e);
            return RestResponse.createFailureResponse(STATUS_CODE_404, DEVICE_RETRIEVAL_FAILED,
                "Failed to retrieve device details for given state: " + state + ROOT_CAUSE + e.getMessage());
        } else {
            LOGGER.error("## Error has occurred while calling getAllFactoryData() state: {},  errorMsg: {},"
                + ERROR_LOG, state, e.getCause(), ExceptionUtils.getRootCauseMessage(e), e);
            return RestResponse.createFailureResponse(STATUS_CODE_404, DEVICE_RETRIEVAL_FAILED,
                "Failed to retrieve device details, Root Cause: " + e.getMessage());
        }
    }

    /**
     * Parses the given exception and returns an ApiResponse object based on the type of exception.
     *
     * @param e The exception to be parsed.
     * @return An ApiResponse object representing the parsed exception.
     */
    private ApiResponse<Object> parseException(Exception e) {
        ApiResponse<Object> apiResponse = null;
        if (e instanceof ServletRequestBindingException) {
            String message = e.getMessage();
            if ((message != null ? message : StringUtils.EMPTY).contains(ResponseConstants.MISSING_USERID_MSG)) {
                apiResponse = new ApiResponse.Builder<>(
                    ApiMessageEnum.MISSING_USERID_RESPONSE.getCode(),
                    ApiMessageEnum.MISSING_USERID_RESPONSE.getMessage(),
                    BAD_REQUEST)
                    .build();
            }
        } else if (e instanceof HttpMessageNotReadableException) {
            apiResponse = new ApiResponse.Builder<>(
                ApiMessageEnum.INVALID_PAYLOAD_RESPONSE.getCode(), ApiMessageEnum.INVALID_PAYLOAD_RESPONSE.getMessage(),
                BAD_REQUEST)
                .build();
        } else if (e instanceof DuplicateKeyException) {
            apiResponse = new ApiResponse.Builder<>(
                ApiMessageEnum.DUPLICATE_ENTRY_RESPONSE.getCode(), ApiMessageEnum.DUPLICATE_ENTRY_RESPONSE.getMessage(),
                BAD_REQUEST)
                .build();
        } else if (e instanceof DataIntegrityViolationException) {
            apiResponse = new ApiResponse.Builder<>(
                ApiMessageEnum.INVALID_PAYLOAD_NULL_RESPONSE.getCode(),
                ApiMessageEnum.INVALID_PAYLOAD_NULL_RESPONSE.getMessage(),
                BAD_REQUEST)
                .build();
        } else if (e instanceof DeleteDeviceException) {
            apiResponse = new ApiResponse.Builder<>(
                ApiMessageEnum.INVALID_CURRENT_FACTORY_DATA.getCode(),
                ApiMessageEnum.INVALID_CURRENT_FACTORY_DATA.getMessage(),
                BAD_REQUEST)
                .build();
        } else if (e instanceof UncategorizedSQLException
            || e.getMessage().contains(ResponseConstants.INVALID_DATE_FORMAT_MSG)) {
            apiResponse = new ApiResponse.Builder<>(
                ApiMessageEnum.INVALID_DATE_FORMAT_RESPONSE.getCode(),
                ApiMessageEnum.INVALID_DATE_FORMAT_RESPONSE.getMessage(),
                BAD_REQUEST).build();
        } else {
            apiResponse = new ApiResponse.Builder<>(ApiMessageEnum.GENERAL_ERROR.getCode(),
                ApiMessageEnum.GENERAL_ERROR.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return apiResponse;
    }

    /**
     * Deletes a device based on the provided IMEI or serial number.
     *
     * @param imei        the IMEI of the device (optional)
     * @param serialnumber the serial number of the device (optional)
     * @param userId      the user ID obtained from the request header
     * @return a ResponseEntity containing the result of the deletion operation
     */
    @DeleteMapping(value = "/v1/devices/details")
    @Operation(summary = "DELETE /v1/devices/details", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "IgniteSystem"})
    public ResponseEntity<Object> deleteDevice(
                @RequestParam(value = "imei", required = false, defaultValue = "") String imei,
                @RequestParam(value = "serialnumber", required = false, defaultValue = "") 
                    String serialnumber, @RequestHeader(USER_ID) String userId) {
        LOGGER.info("Device deletion request initiated");
        ErrorRest errorRest = new ErrorRest();
        if (StringUtils.isEmpty(imei) && StringUtils.isEmpty(serialnumber)) {
            errorRest.setMessage("Either of imei or SerialNumber must be present in the request.");
            return RestResponse.bad_Request(errorRest);
        }
        try {
            deviceInfoQueryService.deleteDeviceFactoryData(imei, serialnumber);
        } catch (DeleteDeviceException e) {
            LOGGER.error("{}", ErrorUtils.buildError("## Error has occurred while deleting device.", e,
                getErrorMap(imei, serialnumber)));
            errorRest.setMessage(e.getMessage());
            return RestResponse.precondition_Failed(errorRest);
        } catch (IllegalArgumentException | InputParamValidationException e) {
            LOGGER.error("{}",
                ErrorUtils.buildError("## Error while deleting device details. Invalid input param value.", e,
                    getErrorMap(imei, serialnumber)));
            errorRest.setMessage(e.getMessage());
            return RestResponse.bad_Request(errorRest);
        } catch (DeviceNotFoundException e) {
            LOGGER.error("{}", ErrorUtils.buildError(
                "## Error while deleting device details. No data is found in inventory for the passed current value.",
                e, getErrorMap(imei, serialnumber)));
            errorRest.setMessage(e.getMessage());
            return RestResponse.not_Found(errorRest);
        } catch (Exception e) {
            LOGGER.error("{}",
                ErrorUtils.buildError("## Error while deleting device details.", e, getErrorMap(imei, serialnumber)));
            errorRest.setMessage(e.getMessage());
            return RestResponse.internal_Server_Error(errorRest);
        }
        LOGGER.info("Device deletion request completed successfully");
        return RestResponse.ok(new SimpleResponseMessage(SUCCESS));
    }

    /**
     * Changes the state of a device.
     *
     * @param stateChange The object containing the state change information.
     * @return A ResponseEntity containing the response object.
     * @throws InvalidAttributeValueException If the attribute value is invalid.
     */
    @PutMapping(value = "/v1/devices/state", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "PUT /v1/devices/state", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) })
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "IgniteSystem"})
    public ResponseEntity<Object> changeDeviceState(@RequestBody @Valid StateChange stateChange)
        throws InvalidAttributeValueException {
        String requestData = stateChange.toString().replaceAll(REQUEST_REGEX, "");
        LOGGER.info("## passed deviceInfo :{}", requestData);
        deviceInfoQueryService.changeDeviceState(stateChange);
        return RestResponse.ok(new SimpleResponseMessage(SUCCESS));
    }

    /**
     * Updates the device information with the provided device update request.
     *
     * @param deviceUpdateRequest The device update request containing the updated device information.
     * @param userId              The user ID of the requester.
     * @return A ResponseEntity containing the result of the device update operation.
     */
    @PutMapping(value = "/v1/devices/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "PUT /v1/devices/update", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "IgniteSystem"})
    public ResponseEntity<Object> updateDevice(@RequestBody @Valid DeviceUpdateRequest deviceUpdateRequest,
                                               @RequestHeader(USER_ID) String userId) {
        String requestData = userId.replaceAll(REQUEST_REGEX, "");
        LOGGER.info("userId :{}", requestData);
        ErrorRest errorRest = new ErrorRest();
        try {
            deviceUpdateRequest.getCurrentValue().setFactoryAdmin(userId);
            deviceUpdateRequest.getReplaceWith().setFactoryAdmin(userId);
            String deviceObjectRequestData = deviceUpdateRequest.toString().replaceAll(REQUEST_REGEX, "");
            LOGGER.info("RequestObject:{}", deviceObjectRequestData);
            deviceInfoQueryService.updateDeviceInfoFactoryData(deviceUpdateRequest);
        } catch (InvalidAttributeValueException | ParseException ex) {
            LOGGER.error("Invalid request format is provided", ex);
            errorRest.setMessage("Invalid request format is provided");
            return RestResponse.bad_Request(errorRest);
        } catch (DeviceNotFoundException dnfe) {
            LOGGER.error("Device not found error ", dnfe);
            errorRest.setMessage("DEVICE_NOT_FOUND");
            return RestResponse.not_Found(errorRest);
        } catch (DataAccessException dae) {
            LOGGER.error("Device access error - Some values for replacement is/are already in use", dae);
            errorRest.setMessage("Some values for replacement is/are already in use");
            return RestResponse.bad_Request(errorRest);
        } catch (ApiValidationFailedException e) {
            LOGGER.error("## Error has occurred while performing update. User: {}, ErrMsg: {}", userId,
                e.getErrorMessage());
            errorRest.setMessage(e.getMessage());
            return RestResponse.bad_Request(errorRest);
        } catch (InvalidDeviceStateException e) {
            LOGGER.error("## Errror has occurred while performing update. User: {}, ErrMsg: {}", userId,
                e.getMessage());
            errorRest.setMessage(e.getMessage());
            return RestResponse.bad_Request(errorRest);
        } catch (Exception e) {
            LOGGER.error("Device update error ", e);
            errorRest.setMessage("device updation is failed");
            return RestResponse.internal_Server_Error(errorRest);
        }
        LOGGER.info("Update is successful");
        return RestResponse.ok(new SimpleResponseMessage(SUCCESS));
    }

    /**
     * Updates the device information for the specified factory ID.
     *
     * @param deviceInfoFactoryData The updated device information.
     * @param factoryId The ID of the factory.
     * @return The response entity containing the updated device information.
     */
    @PatchMapping(value = "/v1/devices/{factoryId}")
    @Hidden
    public ResponseEntity<ExtendedApiResponse<Object>> updateDevice(
        @RequestBody DeviceInfoFactoryData deviceInfoFactoryData,
        @PathVariable(value = "factoryId") Long factoryId) {
        LOGGER.info("## updateDevice CONTROLLER - START ");
        ExtendedApiResponse<Object> apiResponse;
        String requestId = UUID.randomUUID().toString();
        try {
            deviceInfoFactoryData.setId(factoryId);
            int updateCount = deviceInfoQueryService.updateDevice(deviceInfoFactoryData);
            apiResponse = (updateCount == 0)
                ? new ExtendedApiResponse.Builder<>(requestId, OK, DEVICE_UPDATE_FAILED.getCode(),
                DEVICE_UPDATE_FAILED.getMessage(),
                DEVICE_UPDATE_FAILED.getGeneralMessage()).build() :
                new ExtendedApiResponse.Builder<>(requestId, OK, DEVICE_UPDATE_SUCCESS.getCode(),
                    DEVICE_UPDATE_SUCCESS.getMessage(),
                    DEVICE_UPDATE_SUCCESS.getGeneralMessage()).build();
        } catch (ApiValidationFailedException e) {
            LOGGER.error("## Error has occurred while updating device. ErrorMsg: {}", e.getErrorMessage());
            apiResponse = new ExtendedApiResponse.Builder<>(requestId, BAD_REQUEST, e.getCode(), e.getMessage(),
                e.getErrorMessage()).build();
        } catch (Exception e) {
            Map<Object, Object> factoryErrorMap = new HashMap<>();
            factoryErrorMap.put("FactoryId", factoryId);
            factoryErrorMap.put(ErrorUtils.ERROR_CODE_KEY, ApiMessageEnum.GENERAL_ERROR.getCode());
            LOGGER.error("{}", ErrorUtils.buildError("## Error while updating device attributes", e,
                factoryErrorMap));
            apiResponse =
                new ExtendedApiResponse.Builder<>(requestId, OK, GENERAL_ERROR.getCode(), GENERAL_ERROR.getMessage(),
                    GENERAL_ERROR.getGeneralMessage()).build();
        }
        return WebUtils.getResponseEntity(apiResponse);
    }

    /**
     * Retrieves all factory data for devices.
     *
     * @param httpServletRequest    the HttpServletRequest object
     * @param isdetailsrequired     the flag indicating if details are required
     * @param size                  the size of the page
     * @param page                  the page number
     * @param containsLikeFields    the fields to search for containing values
     * @param containsLikeValues    the values to search for in the fields
     * @param sortByField           the field to sort the results by
     * @param sortingOrder          the sorting order (ASC or DESC)
     * @param rangeFields           the fields to search for within a range
     * @param rangeValues           the values to search for within the range fields
     * @return                      a ResponseEntity containing the response object
     */
    @GetMapping(value = "v3/devices/details")
    @ResponseBody
    @Operation(summary = "GET v3/devices/details", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
    }, parameters = { @Parameter(in = ParameterIn.QUERY, name = "isdetailsrequired",
            schema = @Schema(allowableValues = { "true", "false" }), required = true), @Parameter(in =
            ParameterIn.QUERY, name = "sortingorder", schema = @Schema(allowableValues = { "asc", "desc" }),
            required = true)
    })
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "IgniteSystem"})
    public ResponseEntity<Object> getAllFactoryDataV3(
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "isdetailsrequired", defaultValue = "true") String isdetailsrequired,
            @RequestParam(value = "size", required = false, defaultValue = "") String size,
            @RequestParam(value = "page", required = false, defaultValue = "") String page,
            @RequestParam(value = "containslikefields", required = false, defaultValue = "") String containsLikeFields,
            @RequestParam(value = "containslikevalues", required = false, defaultValue = "") String containsLikeValues,
            @RequestParam(value = "sortby", required = false, defaultValue = "") String sortByField,
            @RequestParam(value = "sortingorder", defaultValue = "asc") String sortingOrder,
            @RequestParam(value = "rangefields", required = false, defaultValue = "") String rangeFields,
            @RequestParam(value = "rangevalues", required = false, defaultValue = "") String rangeValues) {
        try {
            LOGGER.info(
                "v3/devices/details got called with params isdetailsrequired:{},size:{},page:{},containslikefields:{},"
                    + "containslikevalues:{},sortbyField:{},sortingorder:{},rangefields:{},rangevalues:{}",
                isdetailsrequired != null ? isdetailsrequired.replaceAll(REQUEST_REGEX, "") : null,
                size != null ? size.replaceAll(REQUEST_REGEX, "") : null,
                page != null ? page.replaceAll(REQUEST_REGEX, "") : null,
                containsLikeFields != null ? containsLikeFields.replaceAll(REQUEST_REGEX, "") : null,
                containsLikeValues != null ? containsLikeValues.replaceAll(REQUEST_REGEX, "") : null,
                sortByField != null ? sortByField.replaceAll(REQUEST_REGEX, "") : null,
                sortByField != null ? sortingOrder.replaceAll(REQUEST_REGEX, "") : null,
                rangeFields != null ? rangeFields.replaceAll(REQUEST_REGEX, "") : null,
                rangeFields != null ? rangeValues.replaceAll(REQUEST_REGEX, "") : null);
            Map<String, String> requestParams = new HashMap<>();
            requestParams.put("isdetailsrequired", isdetailsrequired);
            requestParams.put("size", size);
            requestParams.put("page", page);
            requestParams.put("containslikefields", containsLikeFields);
            requestParams.put("containslikevalues", containsLikeValues);
            requestParams.put("sortbyparam", sortByField);
            requestParams.put("sortingorder", sortingOrder);
            requestParams.put("rangefields", rangeFields);
            requestParams.put("rangevalues", rangeValues);
            return RestResponse.ok(deviceInfoQueryService.getAllFactoryDataV3(requestParams));
        } catch (DeviceNotFoundException | PageParamResolverException | SizeParamResolverException ex) {
            ErrorRest errorRest = new ErrorRest();
            errorRest.setMessage(ex.getMessage());
            return RestResponse.bad_Request(errorRest);
        } catch (Exception e) {
            ErrorRest errorRest = new ErrorRest();
            errorRest.setMessage(e.getMessage());
            return RestResponse.internal_Server_Error(errorRest);
        }
    }

    /**
     * Retrieves all device states for a given IMEI.
     *
     * @param imei     The IMEI of the device.
     * @param size     The number of results to retrieve per page (optional).
     * @param page     The page number to retrieve (optional).
     * @param sortBy   The field to sort the results by (optional, default is "imei").
     * @param orderBy  The order in which to sort the results (optional, default is "desc").
     * @return A ResponseEntity containing the response with the device states.
     */
    @GetMapping(value = "v1/devices/{imei}/states")
    @ResponseBody
    @Operation(summary = "GET v1/devices/{imei}/states", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) },
            parameters = { @Parameter(in = ParameterIn.QUERY, name = "orderby",
                    schema = @Schema(allowableValues = { "asc", "desc" }))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = {"IgniteSystem"})
    public ResponseEntity<HcpServicesBaseResponse> findAllDeviceStates(
            @PathVariable("imei") String imei,
            @RequestParam(value = "size", required = false, defaultValue = "") String size,
            @RequestParam(value = "page", required = false, defaultValue = "") String page,
            @RequestParam(value = "sortby", required = false, defaultValue = "") String sortBy,
            @RequestParam(value = "orderby", required = false, defaultValue = "") String orderBy) {
        try {
            LOGGER.debug(
                "findAllDeviceStates -  imei:{}, size:{}, page:{}, sortBy:{}, orderBy:{}",
                imei != null ? imei.replaceAll(REQUEST_REGEX, "") : null,
                size != null ? size.replaceAll(REQUEST_REGEX, "") : null,
                page != null ? page.replaceAll(REQUEST_REGEX, "") : null,
                sortBy != null ? sortBy.replaceAll(REQUEST_REGEX, "") : null,
                orderBy != null ? orderBy.replaceAll(REQUEST_REGEX, "") : null);
            DeviceInfoQuerySuccessResponse<DeviceStateHistory> result =
                deviceInfoQueryService.findAllDeviceStates(imei, size, page, orderBy, sortBy);
            return RestResponse.ok(result);
        } catch (PageParamResolverException | SizeParamResolverException | InputParamValidationException ex) {
            LOGGER.error("Error while validating input params with imei:{}, error: {}", imei, ex.getMessage());
            return RestResponse.createFailureResponse(STATUS_CODE_400, DEVICE_STATES_RETRIEVAL_FAILED, "Failed to "
                + "retrieve device states for given IMEI: " + imei + ROOT_CAUSE + ex.getMessage());
        } catch (DeviceNotFoundException ex) {
            LOGGER.error("Error has occurred while retrieving device states with imei:{}, error:{}", imei,
                ex.getMessage());
            return RestResponse.createFailureResponse(STATUS_CODE_404, DEVICE_STATES_RETRIEVAL_FAILED,
                "Failed to retrieve device states for given IMEI: " + imei + ROOT_CAUSE + ex.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error has occurred while retrieving device states with imei:{}, error:{}", imei, e);
            return RestResponse.createFailureResponse(STATUS_CODE_500, DEVICE_STATES_RETRIEVAL_FAILED,
                "Failed to retrieve device states for given IMEI: " + imei + ROOT_CAUSE + e.getMessage());
        }
    }

    /**
     * Finds devices by applying a filter.
     *
     * @param deviceFilterDto The filter criteria for devices.
     * @return A ResponseEntity containing the API response with the filtered devices.
     */
    @PostMapping(value = "/v1/devices/filter")
    @Hidden
    public ResponseEntity<ExtendedApiResponse<Object>> findByFilter(@RequestBody DeviceFilterDto deviceFilterDto) {
        LOGGER.info("## findByFilter CONTROLLER - START ");
        ExtendedApiResponse<Object> apiResponse;
        String requestId = UUID.randomUUID().toString();
        try {
            List<DeviceInfoFactoryData> result = deviceInfoQueryService.filterDevice(deviceFilterDto);
            apiResponse = (result.isEmpty())
                ? new ExtendedApiResponse.Builder<>(requestId, NOT_FOUND, DEVICE_NOT_FOUND.getCode(),
                    DEVICE_NOT_FOUND.getMessage(),
                    DEVICE_NOT_FOUND.getGeneralMessage()).build() :
                new ExtendedApiResponse.Builder<>(requestId, OK, DEVICE_RETRIEVE_SUCCESS.getCode(),
                    DEVICE_RETRIEVE_SUCCESS.getMessage(),
                    DEVICE_RETRIEVE_SUCCESS.getGeneralMessage()).withData(result).build();
        } catch (Exception e) {
            Map<Object, Object> errorMap = new HashMap<>();
            errorMap.put(ErrorUtils.ERROR_CODE_KEY, ApiMessageEnum.GENERAL_ERROR.getCode());
            LOGGER.error("{}", ErrorUtils.buildError("## Error while finding device using filter.", e, errorMap));
            apiResponse =
                new ExtendedApiResponse.Builder<>(requestId, OK, GENERAL_ERROR.getCode(), GENERAL_ERROR.getMessage(),
                    GENERAL_ERROR.getGeneralMessage()).build();
        }
        return WebUtils.getResponseEntity(apiResponse);
    }


    /**
     * Returns a map containing error information based on the provided IMEI and serial number.
     *
     * @param imei          The IMEI (International Mobile Equipment Identity) number.
     * @param serialnumber  The serial number of the device.
     * @return              A map containing error information.
     */
    private Map<Object, Object> getErrorMap(String imei, String serialnumber) {
        Map<Object, Object> errorMap = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(imei)) {
            errorMap.put("imei", imei);
        } else if (StringUtils.isNotBlank(serialnumber)) {
            errorMap.put("serialNumber", serialnumber);
        }
        errorMap.put(ErrorUtils.ERROR_CODE_KEY, ApiMessageEnum.GENERAL_ERROR.getCode());
        return errorMap;
    }
}
