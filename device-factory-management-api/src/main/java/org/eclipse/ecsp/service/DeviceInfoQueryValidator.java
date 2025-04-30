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
import org.eclipse.ecsp.common.ResponseConstants;
import org.eclipse.ecsp.common.SharedConstants;
import org.eclipse.ecsp.common.exception.ApiValidationFailedException;
import org.eclipse.ecsp.common.exception.DeviceNotFoundException;
import org.eclipse.ecsp.common.exception.InputParamValidationException;
import org.eclipse.ecsp.dao.DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum;
import org.eclipse.ecsp.dto.DeviceFilterDto;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.eclipse.ecsp.common.enums.ApiMessageEnum.INVALID_DEVICE_FILTER;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.INVALID_PAYLOAD_RESPONSE;

/**
 * The DeviceInfoQueryValidator class provides validation methods for various fields and parameters used in device
 * queries.
 * It also defines constants and mappings for sorting and filtering device information.
 */
public class DeviceInfoQueryValidator {
    /**
     * Device's "state" validator.
     */
    public static final Validator<String> STATE_VALIDATOR = s ->
        s.equals("PROVISIONED")
            || s.equals("PROVISIONED_ALIVE")
            || s.equals("FAULTY")
            || s.equals("STOLEN")
            || s.equals("ACTIVE")
            || s.equals("READY_TO_ACTIVATE");
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceInfoQueryValidator.class);
    private static final String SERIAL_NUMBER = "serial_number";
    private static final String MODEL = "model";
    private static final String ICCID = "iccid";
    private static final String BSSID = "bssid";
    private static final String MSISDN = "msisdn";
    private static final String FACTORY_ADMIN = "factory_admin";
    private static final String STATE = "state";
    private static final String PACKAGE_SERIAL_NUMBER = "package_serial_number";
    private static final String MANUFACTURING_DATE = "manufacturing_date";
    private static final String RECORD_DATE = "record_date";
    private static final String CREATED_DATE = "created_date";
    private static final String REQUEST_REGEX = "[\r\n]";
    /**
     * A mapping of device details column names to their corresponding sort keys.
     * This map is used to define the relationship between the column names in the
     * device details and the keys used for sorting operations.
     */
    public static Map<String, String> DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING = new LinkedHashMap<>();
    static HashMap<String, String> DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING_FOR_DEVICE_ID = new LinkedHashMap<>();
    static HashMap<String, String> DEVICE_STATE_SORT_BY_COLUMN_MAPPING = new LinkedHashMap<>();
    private static final int LENGTH_2 = 2;
    private static final int LENGTH_3 = 3;
    private static final int LENGTH_4 = 4;
    private static final int LENGTH_17 = 17;

    static {
        DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING.put("imei", "imei");
        DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING.put("serialNumber", SERIAL_NUMBER);
        DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING.put(MODEL, MODEL);
        DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING.put(ICCID, ICCID);
        DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING.put("ssid", "ssid");
        DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING.put(BSSID, BSSID);
        DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING.put(MSISDN, MSISDN);
        DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING.put("imsi", "imsi");
        DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING.put("factoryAdmin", FACTORY_ADMIN);
        DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING.put(STATE, STATE);
        DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING.put("packageSerialNumber", PACKAGE_SERIAL_NUMBER);
        DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING.put("manufacturingDate", MANUFACTURING_DATE);
        DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING.put("recordDate", RECORD_DATE);
        DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING.put("createdDate", CREATED_DATE);

        DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING_FOR_DEVICE_ID.put("deviceId", "harman_id");

        DEVICE_STATE_SORT_BY_COLUMN_MAPPING.put(STATE, STATE);
        DEVICE_STATE_SORT_BY_COLUMN_MAPPING.put("stateTimestamp", "created_timestamp");
        DEVICE_STATE_SORT_BY_COLUMN_MAPPING.put("manufacturingDate", MANUFACTURING_DATE);
        DEVICE_STATE_SORT_BY_COLUMN_MAPPING.put("imei", "imei");
        DEVICE_STATE_SORT_BY_COLUMN_MAPPING.put("serialNumber", SERIAL_NUMBER);
        DEVICE_STATE_SORT_BY_COLUMN_MAPPING.put(ICCID, ICCID);
        DEVICE_STATE_SORT_BY_COLUMN_MAPPING.put("ssid", "ssid");
        DEVICE_STATE_SORT_BY_COLUMN_MAPPING.put(BSSID, BSSID);
        DEVICE_STATE_SORT_BY_COLUMN_MAPPING.put(MSISDN, MSISDN);
        DEVICE_STATE_SORT_BY_COLUMN_MAPPING.put("imsi", "imsi");
        DEVICE_STATE_SORT_BY_COLUMN_MAPPING.put("factoryAdmin", FACTORY_ADMIN);
        DEVICE_STATE_SORT_BY_COLUMN_MAPPING.put("packageSerialNumber", PACKAGE_SERIAL_NUMBER);
        DEVICE_STATE_SORT_BY_COLUMN_MAPPING.put("recordDate", RECORD_DATE);
    }

    /**
     * Private constructor to prevent instantiation of the DeviceInfoQueryValidator class.
     */
    private DeviceInfoQueryValidator() {
        
    }

    /**
     * Validates the request data for sorting by a field.
     *
     * @param sortBy           the field to sort by
     * @param sortingOrder     the sorting order (asc or desc)
     * @param isdetailsrequired whether details are required or not
     * @throws DeviceNotFoundException if the request data is invalid
     */
    public static void validateSortByRequestData(String sortBy, String sortingOrder, String isdetailsrequired) {
        if (StringUtils.isNotEmpty(sortBy) && !AllowedSortBy.contains(sortBy)) {
            String sortByData = sortBy.replaceAll(REQUEST_REGEX, "");
            LOGGER.debug("Invalid sortBy value:{}", sortByData);
            throw new DeviceNotFoundException(ResponseConstants.INVALID_SORTBY_FIELD_RESPONSE);
        }
        if (StringUtils.isNotEmpty(sortingOrder)
            && !(sortingOrder.equalsIgnoreCase("asc") || sortingOrder.equalsIgnoreCase("desc"))) {
            String sortingOrderData = sortingOrder.replaceAll(REQUEST_REGEX, "");
            LOGGER.debug("Invalid sortingOrder value:{}", sortingOrderData);
            throw new DeviceNotFoundException(ResponseConstants.INVALID_SORTING_ORDER_VALUE);
        }
        if (!StringUtils.isNotEmpty(isdetailsrequired)
            || !(isdetailsrequired.equalsIgnoreCase("true") || isdetailsrequired.equalsIgnoreCase("false"))) {
            String isdetailsrequiredData = isdetailsrequired.replaceAll(REQUEST_REGEX, "");
            LOGGER.debug("Invalid isdetailsrequired value:{}", isdetailsrequiredData);
            throw new DeviceNotFoundException(ResponseConstants.WRONG_ISDETAILSREQUIRED_VALUE);
        }
    }

    /**
     * Validates the contains-like fields and values.
     *
     * @param containsLikeFieldList  the list of contains-like fields
     * @param containsLikeValueList  the list of contains-like values
     * @throws DeviceNotFoundException if the contains-like fields or values are invalid
     */
    static void validateContainsLike(List<String> containsLikeFieldList, List<String> containsLikeValueList) {
        if ((containsLikeFieldList == null && containsLikeValueList == null)
            || (containsLikeFieldList != null && containsLikeFieldList.isEmpty() && containsLikeValueList != null
            && containsLikeValueList.isEmpty())) {
            return;
        }
        if (containsLikeFieldList == null || containsLikeValueList == null
            || containsLikeFieldList.size() != containsLikeValueList.size()) {
            throw new DeviceNotFoundException(ResponseConstants.INVALID_CONTAINS_LIKE_FIELD);
        }
        for (String field : containsLikeFieldList) {
            if (!AllowedContainsLike.contains(field)) {
                throw new DeviceNotFoundException(ResponseConstants.INVALID_CONTAINS_LIKE_FIELD);
            }
        }
    }

    /**
     * Validates the range fields and values.
     *
     * @param rangeFieldList  the list of range fields
     * @param rangeValueList  the list of range values
     * @throws DeviceNotFoundException if the range fields or values are invalid
     */
    static void validateRangeField(List<String> rangeFieldList, List<String> rangeValueList) {
        if ((rangeFieldList == null && rangeValueList == null)
            || (rangeFieldList != null && rangeFieldList.isEmpty() && rangeValueList != null
            && rangeValueList.isEmpty())) {
            return;
        }
        if (rangeFieldList == null || rangeValueList == null || rangeFieldList.size() != rangeValueList.size()) {
            throw new DeviceNotFoundException(ResponseConstants.INVALID_RANGE_FIELD_RESPONSE);
        }
        validateAllowedRangeFields(rangeFieldList, rangeValueList);
    }

    /**
     * Validates the allowed range fields and range values.
     *
     * @param rangeFieldList  the list of range fields to be validated
     * @param rangeValueList  the list of range values to be validated
     * @throws DeviceNotFoundException if any of the range fields or range values are invalid
     */
    private static void validateAllowedRangeFields(List<String> rangeFieldList, List<String> rangeValueList) {
        if (rangeFieldList != null && !rangeFieldList.isEmpty()) {
            for (String field : rangeFieldList) {
                if (!AllowedRangeFields.contains(field)) {
                    throw new DeviceNotFoundException(ResponseConstants.INVALID_RANGE_FIELD_RESPONSE);
                }
            }
        }

        if (rangeValueList != null && !rangeValueList.isEmpty()) {
            for (String range : rangeValueList) {
                if (range.split(SharedConstants.UNDERSCORE).length != LENGTH_2) {
                    throw new DeviceNotFoundException(ResponseConstants.INVALID_RANGE_VALUE_RESPONSE);
                }
            }
        }
    }

    /**
     * Validates the IMEI (International Mobile Equipment Identity) number.
     *
     * @param imei the IMEI number to be validated
     * @return true if the IMEI is valid, false otherwise
     * @throws InputParamValidationException if the IMEI is invalid
     */
    static boolean validateImei(String imei) {
        if (StringUtils.isNotEmpty(imei)) {
            if (imei.length() < LENGTH_3) {
                throw new InputParamValidationException(ResponseConstants.INVALID_IMEI_LENGTH_ERROR_MSG);
            }
            // This is additional validation also we have added validation in
            // API Gateway component
            if (!StringUtils.isNumeric(imei)) {
                throw new InputParamValidationException(ResponseConstants.INVALID_IMEI_ERROR_MSG);
            }
        }
        return true;
    }

    /**
     * Validates the IMEI for a complete string.
     *
     * @param imei the IMEI to be validated
     * @throws IllegalArgumentException if the IMEI is not a numeric string
     */
    public static void validateImeiForCompleteString(String imei) {
        if (StringUtils.isNotEmpty(imei) && !StringUtils.isNumeric(imei)) {
            throw new IllegalArgumentException(ResponseConstants.INVALID_IMEI_ERROR_MSG);
        }
    }

    /**
     * Validates the given serial number.
     *
     * @param serialNumber the serial number to be validated
     * @return true if the serial number is valid, false otherwise
     * @throws IllegalArgumentException if the serial number is invalid
     */
    public static boolean validateSerialNumber(String serialNumber) {
        if (StringUtils.isNotEmpty(serialNumber)) {
            if (serialNumber.length() < LENGTH_3) {
                throw new IllegalArgumentException(ResponseConstants.INVALID_SERIAL_NUMBER_LENGTH_ERROR_MSG);
            }
            if (!StringUtils.isAlphanumeric(serialNumber)) {
                throw new IllegalArgumentException(ResponseConstants.INVALID_SERIAL_NUMBER_ERROR_MSG);
            }
        }
        return true;
    }

    /**
     * Validates the given device ID.
     *
     * @param deviceId the device ID to be validated
     * @throws IllegalArgumentException if the device ID is invalid
     */
    private static void validateDeviceId(String deviceId) {
        if (StringUtils.isNotEmpty(deviceId)) {
            if (deviceId.length() < LENGTH_3) {
                throw new IllegalArgumentException(ResponseConstants.INVALID_DEVICE_ID_LENGTH_ERROR_MSG);
            }
            if (!StringUtils.isAlphanumeric(deviceId)) {
                throw new IllegalArgumentException(ResponseConstants.INVALID_DEVICE_ID_ERROR_MSG);
            }
        }
    }

    /**
     * Validates the given serial number for completeness as a string.
     *
     * @param serialNumber the serial number to be validated
     * @throws IllegalArgumentException if the serial number is not alphanumeric
     */
    public static void validateSerialNumberForCompleteString(String serialNumber) {
        if (StringUtils.isNotEmpty(serialNumber) && !StringUtils.isAlphanumeric(serialNumber)) {
            throw new IllegalArgumentException(ResponseConstants.INVALID_SERIAL_NUMBER_ERROR_MSG);
        }
    }

    /**
     * Validates the sortBy and orderBy fields based on the given device details and input parameters.
     *
     * @param deviceDetails The type of device details to validate (DEVICE_STATE or DEVICE_DETAILS).
     * @param sortBy        The field to sort the results by.
     * @param orderBy       The order in which to sort the results (ASCENDING or DESCENDING).
     * @param inputParam    The input parameter type for device details (DEVICE_ID or other).
     * @throws InputParamValidationException If the sortBy or orderBy fields are invalid.
     */
    public static void validateSortByAndOrderByFields(DeviceInfoQueryFeatureType deviceDetails, String sortBy,
                                                      String orderBy,
                                                      DeviceDetailsInputTypeEnum inputParam) {
        switch (deviceDetails) {
            case DEVICE_STATE:
                if (StringUtils.isNotEmpty(sortBy) && !DEVICE_STATE_SORT_BY_COLUMN_MAPPING.containsKey(sortBy)) {
                    throw new InputParamValidationException(
                        ResponseConstants.DEVICE_STATE_SORT_BY_FIELD_VALIDATION_ERR_MSG);
                }
                break;
            case DEVICE_DETAILS:

                if (inputParam == DeviceDetailsInputTypeEnum.DEVICE_ID && StringUtils.isNotEmpty(sortBy)) {
                    if (!(DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING_FOR_DEVICE_ID.containsKey(sortBy)
                        || DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING.containsKey(sortBy))) {
                        throw new InputParamValidationException(
                            ResponseConstants.DEVICE_DETAILS_SORT_BY_FIELD_VALIDATION_BY_DEVICE_ID_ERR_MSG);
                    }
                } else if (StringUtils.isNotEmpty(sortBy)
                    && !DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING.containsKey(sortBy)) {
                    throw new InputParamValidationException(
                        ResponseConstants.DEVICE_DETAILS_SORT_BY_FIELD_VALIDATION_ERR_MSG);
                }
                break;
            default:
        }
        validateOrderByField(orderBy);
    }

    /**
     * Validates the orderBy field.
     *
     * @param orderBy the orderBy field to be validated
     * @throws InputParamValidationException if the orderBy field is not valid
     */
    public static void validateOrderByField(String orderBy) {
        if (StringUtils.isNotEmpty(orderBy)
            && !("asc".equalsIgnoreCase(orderBy) || "desc".equalsIgnoreCase(orderBy))) {
            throw new InputParamValidationException(ResponseConstants.ORDER_BY_FIELD_VALIDATION_ERR_MSG);
        }
    }

    /**
     * Validates and retrieves input parameters based on the given input types.
     *
     * @param inputType The input types to validate and retrieve parameters from.
     * @return A map containing the validated input parameters.
     * @throws InputParamValidationException If the input parameters are invalid.
     */
    public static Map<DeviceDetailsInputTypeEnum, String> validateAndGetInputParams(String... inputType) {
        LOGGER.info("## validateAndGetInputParams - START");
        /*
         * Maps with keys that are enum values should be replaced with EnumMap
         * When all the keys of a Map are values from the same enum, the Map can
         * be replaced with an EnumMap, which can be much more efficient than
         * other sets because the underlying data structure is a simple array.
         */

        String serialNumber = "";
        String deviceId = "";
        String vin = "";
        if (inputType.length > 1) {
            serialNumber = inputType[1];
        }
        if (inputType.length > LENGTH_2) {
            deviceId = inputType[LENGTH_2];
        }
        if (inputType.length > LENGTH_3) {
            vin = inputType[LENGTH_3];
        }
        String state = "";
        if (inputType.length > LENGTH_4) {
            state = inputType[LENGTH_4];
        }
        String imei = inputType[0];
        EnumMap<DeviceDetailsInputTypeEnum, String> input = new EnumMap<>(DeviceDetailsInputTypeEnum.class);
        input = verifyDetails(imei, serialNumber, deviceId, vin, state, input);
        if (!input.isEmpty()) {
            return input;
        }

        if (!StringUtils.isEmpty(imei)) {
            validateImei(imei);
            input.put(DeviceDetailsInputTypeEnum.IMEI, imei);
        }
        if (!StringUtils.isEmpty(serialNumber)) {
            validateSerialNumber(serialNumber);
            input.put(DeviceDetailsInputTypeEnum.SERIAL_NUMBER, serialNumber);
        }
        if (!StringUtils.isEmpty(deviceId)) {
            validateDeviceId(deviceId);
            input.put(DeviceDetailsInputTypeEnum.DEVICE_ID, deviceId);
        }
        if (!StringUtils.isEmpty(vin)) {
            validateVin(vin);
            input.put(DeviceDetailsInputTypeEnum.VIN, vin);
        }
        if (!StringUtils.isEmpty(state)) {
            input.put(DeviceDetailsInputTypeEnum.STATE, state);
        }
        // make sure we should have only one input from user
        if (input.size() > 1) {
            if (!StringUtils.isEmpty(vin)) {
                throw new InputParamValidationException(ResponseConstants.INVALID_NUMBER_OF_PARAMS_WITH_VIN);
            } else {
                throw new InputParamValidationException(ResponseConstants.INVALID_NUMBER_OF_PARAMS);
            }
        }
        LOGGER.info("## validateAndGetInputParams - END, input: {}", input);
        return input;
    }

    /**
     * Verifies the device details based on the provided inputs.
     *
     * @param imei          the IMEI number of the device
     * @param serialNumber  the serial number of the device
     * @param deviceId      the device ID
     * @param vin           the vehicle identification number
     * @param state         the state of the device
     * @param input         the input map containing device details
     * @return              the updated input map with device details
     */
    private static EnumMap<DeviceDetailsInputTypeEnum, String> verifyDetails(String imei, String serialNumber,
                                                                 String deviceId, String vin, String state,
                                                                 EnumMap<DeviceDetailsInputTypeEnum, String> input) {
        if (StringUtils.isEmpty(imei) && StringUtils.isEmpty(serialNumber) && StringUtils.isEmpty(deviceId)
            && StringUtils.isEmpty(vin)
            && StringUtils.isEmpty(state)) {
            input.put(DeviceDetailsInputTypeEnum.IMEI, imei);
            return input;
        }
        return input;
    }

    /**
     * Validates the VIN (Vehicle Identification Number).
     *
     * @param vin the VIN to be validated
     * @throws IllegalArgumentException if the VIN length is not equal to 17
     */
    private static void validateVin(String vin) {
        if (vin.length() != LENGTH_17) {
            throw new IllegalArgumentException(ResponseConstants.INVALID_VIN_LENGTH_ERROR_MSG);
        }
    }

    /**
     * Validates the device filter based on the provided DeviceFilterDto.
     *
     * @param filterDto The DeviceFilterDto containing the filter criteria.
     * @return true if the device filter is valid, false otherwise.
     * @throws ApiValidationFailedException if the device filter is invalid.
     */
    public static boolean validateDeviceFilter(DeviceFilterDto filterDto) {
        //validate imei
        List<String> imeiList = filterDto.getImei();
        boolean validationStatus = true;
        if (imeiList != null) {
            for (String imei : imeiList) {
                validationStatus &= validateImei(imei);
            }
        }
        //validate serial number
        if (validationStatus && filterDto.getSerialNumber() != null) {
            List<String> serialNumberList = filterDto.getSerialNumber();
            for (String serialNumber : serialNumberList) {
                validationStatus &= validateSerialNumber(serialNumber);
            }
        }
        //need to have validation for following attributes in future
        //validate model
        //validate iccid
        //validate ssid
        //validate msisdn
        //validate imsi
        //validate state
        if (!validationStatus) {
            throw new ApiValidationFailedException(INVALID_DEVICE_FILTER.getCode(), INVALID_DEVICE_FILTER.getMessage(),
                INVALID_DEVICE_FILTER.getGeneralMessage());
        }
        return true;
    }

    /**
     * Validates the device factory data.
     *
     * @param difd The device info factory data to be validated.
     * @return {@code true} if the device factory data is valid, {@code false} otherwise.
     * @throws ApiValidationFailedException if the device factory data is invalid.
     */
    public static boolean validateDeviceFactoryData(DeviceInfoFactoryData difd) {
        boolean validationStatus = true;
        if (difd.getImei() != null) {
            validationStatus = validateImei(difd.getImei());
        }
        if (difd.getSerialNumber() != null) {
            validationStatus = validateSerialNumber(difd.getSerialNumber());
        }
        if (difd.getState() != null) {
            validationStatus = STATE_VALIDATOR.validate(difd.getState());
        }
        //Validation for other attributes needs to be done post discussion with Product team
        if (!validationStatus) {
            throw new ApiValidationFailedException(INVALID_PAYLOAD_RESPONSE.getCode(),
                INVALID_PAYLOAD_RESPONSE.getMessage(),
                INVALID_PAYLOAD_RESPONSE.getGeneralMessage());
        }
        return true;
    }

    /**
     * Validates the given serial number for a complete bot string.
     *
     * @param serialNumber the serial number to be validated
     * @throws IllegalArgumentException if the serial number is empty, contains non-alphanumeric characters,
     *                                  or does not start with the bot device prefix
     */
    public static void validateSerialNumberForBotCompleteString(String serialNumber) {
        if (StringUtils.isNotEmpty(serialNumber)) {
            if (!StringUtils.isAlphanumeric(serialNumber)) {
                throw new IllegalArgumentException(ResponseConstants.INVALID_SERIAL_NUMBER_ERROR_MSG);
            } else if (!serialNumber.toUpperCase().startsWith(CommonConstants.BOT_DEVICE_PREFIX)) {
                throw new IllegalArgumentException(ResponseConstants.INVALID_BOTSERIAL_NUMBER);
            }
        }
    }

    /**
     * Represents the types of features that can be queried for device information.
     * The available feature types are DEVICE_DETAILS and DEVICE_STATE.
     */
    public enum DeviceInfoQueryFeatureType {
        DEVICE_DETAILS, DEVICE_STATE;
    }

    /**
     * Enum representing the allowed sorting options for device information queries.
     */
    private enum AllowedSortBy {
        IMEI("imei"),
        SERIAL_NUMBER(DeviceInfoQueryValidator.SERIAL_NUMBER),
        MODEL(DeviceInfoQueryValidator.MODEL),
        ID("ID"),
        ICCID(DeviceInfoQueryValidator.ICCID),
        SSID("ssid"),
        BSSID(DeviceInfoQueryValidator.BSSID),
        MSISDN(DeviceInfoQueryValidator.MSISDN),
        IMSI("imsi"),
        FACTORY_ADMIN(DeviceInfoQueryValidator.FACTORY_ADMIN),
        STATE(DeviceInfoQueryValidator.STATE),
        PACKAGE_SERIAL_NUMBER(DeviceInfoQueryValidator.PACKAGE_SERIAL_NUMBER),
        MANUFACTURING_DATE(DeviceInfoQueryValidator.MANUFACTURING_DATE),
        RECORD_DATE(DeviceInfoQueryValidator.RECORD_DATE),
        CREATED_DATE(DeviceInfoQueryValidator.CREATED_DATE);

        private static Set<String> values = new HashSet<>();

        static {
            for (AllowedSortBy allowed : AllowedSortBy.values()) {
                values.add(allowed.field);
            }
        }

        private String field;

        /**
         * Constructs a new AllowedSortBy object with the specified field.
         *
         * @param field the field to sort by
         */
        AllowedSortBy(String field) {
            this.field = field;
        }

        /**
         * Checks if the specified value is contained in the list of values.
         *
         * @param value the value to check
         * @return true if the value is contained in the list, false otherwise
         */
        public static boolean contains(String value) {
            return values.contains(value);
        }
    }

    /**
     * Enum representing the allowed fields for the "contains-like" query in the DeviceInfoQueryValidator class.
     */
    private enum AllowedContainsLike {
        MODEL(DeviceInfoQueryValidator.MODEL),
        IMEI("imei"),
        SERIAL_NUMBER(DeviceInfoQueryValidator.SERIAL_NUMBER),
        ICCID(DeviceInfoQueryValidator.ICCID),
        SSID("ssid"),
        BSSID(DeviceInfoQueryValidator.BSSID),
        MSISDN(DeviceInfoQueryValidator.MSISDN),
        IMSI("imsi"),
        FACTORY_ADMIN(DeviceInfoQueryValidator.FACTORY_ADMIN),
        STATE(DeviceInfoQueryValidator.STATE),
        IS_STOLEN("isstolen"),
        IS_FAULTY("isfaulty"),
        PACKAGE_SERIAL_NUMBER(DeviceInfoQueryValidator.PACKAGE_SERIAL_NUMBER);

        private static Set<String> values = new HashSet<>();

        static {
            for (AllowedContainsLike allowed : AllowedContainsLike.values()) {
                values.add(allowed.field);
            }
        }

        private String field;

        /**
         * Constructs a new AllowedContainsLike object with the specified field.
         *
         * @param field the field to be used for the AllowedContainsLike operation
         */
        AllowedContainsLike(String field) {
            this.field = field;
        }

        /**
         * Checks if the specified value is contained in the list of values.
         *
         * @param value the value to check
         * @return true if the value is contained in the list, false otherwise
         */
        public static boolean contains(String value) {
            return values.contains(value);
        }
    }

    /**
     * Enum representing the allowed range fields for device information queries.
     */
    private enum AllowedRangeFields {
        MANUFACTURING_DATE(DeviceInfoQueryValidator.MANUFACTURING_DATE),
        RECORD_DATE(DeviceInfoQueryValidator.RECORD_DATE),
        CREATED_DATE(DeviceInfoQueryValidator.CREATED_DATE);

        private static Set<String> values = new HashSet<>();

        static {
            for (AllowedRangeFields allowed : AllowedRangeFields.values()) {
                values.add(allowed.field);
            }
        }

        private String field;

        /**
         * Constructs a new AllowedRangeFields object with the specified field.
         *
         * @param field the field for the AllowedRangeFields object
         */
        AllowedRangeFields(String field) {
            this.field = field;
        }

        /**
         * Checks if the specified value is contained in the list of values.
         *
         * @param value the value to check
         * @return true if the value is contained in the list, false otherwise
         */
        public static boolean contains(String value) {
            return values.contains(value);
        }
    }

    /**
     * A functional interface for validating input of type T.
     *
     * @param <T> the type of input to be validated
     */
    @FunctionalInterface
    public interface Validator<T> {

        /**
         * Validates the input of type T.
         *
         * @param input the input to be validated
         * @return true if the input is valid, false otherwise
         */
        boolean validate(T input);
    }
}
