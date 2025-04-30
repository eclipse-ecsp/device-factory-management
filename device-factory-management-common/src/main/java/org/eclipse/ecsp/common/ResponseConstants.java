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

package org.eclipse.ecsp.common;

/**
 * This class contains constants for various response messages used in the application.
 * These constants are used to provide standardized response messages for different scenarios.
 * Each constant represents a specific response message.
 */
public class ResponseConstants {

    public static final String MISSING_USERID_RESPONSE = "Missing 'user-id' in http request header";
    public static final String MISSING_USERID_MSG = "Missing header 'user-id' for method parameter type";
    public static final String INVALID_DATE_FORMAT_RESPONSE = "Invalid date format passed. Valid format is yyyy/MM/dd";
    public static final String INVALID_DATE_FORMAT_MSG = "Invalid date format passed";
    public static final String INVALID_PAYLOAD_NULL_RESPONSE = "Invalid payload data. Contains null values for fields.";
    public static final String INVALID_PAYLOAD_RESPONSE = "Invalid payload data";
    public static final String DUPLICATE_ENTRY_RESPONSE =
        "One or more of the input factory data is already present in the DB. Duplicate entry.";
    public static final String INVALID_REPLACE_REQUEST_DATA = "Invalid Device replace request data.";
    public static final String INVALID_CURRENT_FACTORY_DATA_FOR_REPLACE =
        "No data is found in inventory for the passed current value";
    public static final String INVALID_CURRENT_FACTORY_DATA = "No data is found in inventory for the requested inputs";
    public static final String INVALID_DEVICE_REPLACEMENT_CURRENT_DATA_STATE = "Must be in either Faulty/Stolen state";
    public static final String INVALID_DEVICE_REPLACEMENT_REPLACE_DATA_STATE = "Must be in Provisioned state";
    public static final String INVALID_INACTIVATED_DEVICE_FOR_REPLACEMENT =
        "Passed current factory data is not activated before. You can replace only an activated device";
    public static final String SERIALNUMBER_IMEI_MANDATORY = "Either Serial number or IMEI is mandatory";
    public static final String WRONG_PAGE_VALUE_TYPE_MESSAGE = "Page should be unsigned number";
    public static final String WRONG_PAGE_SIZE_MESSAGE = "Page size must be between %d and %d";
    public static final String WRONG_QUERY_ASC_DESC_VALUE = "Invalid asc or desc value";
    public static final String WRONG_ISDETAILSREQUIRED_VALUE =
        "isdetailsrequired field is mandatory and should have Boolean type.";
    public static final String INVALID_SORTING_ORDER_VALUE =
        "sortingorder field is mandatory.If non empty then should have asc/desc type.";
    public static final String INVALID_CONTAINS_LIKE_FIELD =
        "containslikefields is mandatory.If non empty then should be allowed one.";
    public static final String INVALID_CONTAINS_LIKE_VALUE = "Invalid containslikevalue is passed";
    public static final String INVALID_RANGE_FIELD_RESPONSE =
        "rangefields and rangevalues should be of same length and allowed.";
    public static final String INVALID_SORTBY_FIELD_RESPONSE =
        "sortby field and value should be of same length and allowed fields.";
    public static final String INVALID_RANGE_VALUE_RESPONSE =
        "rangevalues is not in proper format.It should be separated by underscore(_)";
    public static final String ZERO_PAGE_VALUE_TYPE_MESSAGE = "Page should be greater than zero";
    public static final String INVALID_IMEI = "Invalid IMEI";
    public static final String INVALID_SERIAL = "Invalid Serial Number";
    public static final String INVALID_INPUT = "No data for the given input";
    public static final String INVALID_IMEI_LENGTH_ERROR_MSG = "IMEI value have to be minimum 3 digits for search";
    public static final String INVALID_IMEI_ERROR_MSG = "IMEI must be numeric";
    public static final String INVALID_SERIAL_NUMBER = "Invalid serial number";
    public static final String INVALID_SERIAL_NUMBER_LENGTH_ERROR_MSG =
        "Serial number value must to be minimum 3 digits for search";
    public static final String INVALID_SERIAL_NUMBER_ERROR_MSG = "Serial number must be alphanumeric";
    public static final String INVALID_IMEI_SERIAL_NUMBER_COMBINATION = "Invalid IMEI and serial number combination";
    public static final String DEVICE_NOT_FOUND_FOR_CONTAINS_FIELD = "No device is present with containing values: ";
    public static final String DEVICE_NOT_FOUND_FOR_IMEI_MSG = "Device not found for imei: ";
    public static final String DEVICE_NOT_FOUND_FOR_RANGE_FIELD = "No device is present with range values: ";
    public static final String INVALID_DATE = "Invalid Date format";
    public static final String DEVICE_STATE_RETRIEVAL_SUCCESS_MSG = "Device states retrieved successfully";
    public static final String DEVICE_DETAILS_SORT_BY_FIELD_VALIDATION_ERR_MSG = "Incorrect sortby field value. "
        + "Use one of the following values: "
        + "imei|serialNumber|model|iccid|ssid|bssid|msisdn|imsi|factoryAdmin|state|packageSerialNumber|recordDate"
        + "|createdDate";
    public static final String DEVICE_DETAILS_SORT_BY_FIELD_VALIDATION_BY_DEVICE_ID_ERR_MSG =
        "Incorrect sortby field value. "
            + "Use one of the following values: "
            +
            "imei|serialNumber|model|iccid|ssid|bssid|msisdn|imsi|factoryAdmin|state|packageSerialNumber|recordDate"
            + "|createdDate|deviceId";
    public static final String DEVICE_STATE_SORT_BY_FIELD_VALIDATION_ERR_MSG = "Incorrect sortby field value. "
        + "Use one of the following values: "
        + "state|stateTimestamp|manufacturingDate|imei|serialNumber|iccid|ssid|bssid|msisdn|imsi|factoryAdmin|"
        + "packageSerialNumber|recordDate";
    public static final String ORDER_BY_FIELD_VALIDATION_ERR_MSG = "orderby field should have either asc or desc type";
    public static final String SINCE_GREATER_THAN_UNTIL = "until has to be greater than since";
    public static final String INVALID_REPORT_FORMAT = "Invalid report format, only .pdf allowed";
    public static final String INVALID_TYPE = "Invalid type. Supported types: [ACTIVEDONGLE]";
    public static final String UNTIL_GREATER_THAN_CURR_TIME = "Until or Since cannot be greater than current timestamp";
    public static final String INVALID_NUMBER_OF_PARAMS =
        "Please provide any one of imei , SerialNumber , DeviceId or State and perform the search again";
    public static final String INVALID_NUMBER_OF_PARAMS_WITH_VIN =
        "Please provide any one of imei , SerialNumber , DeviceId or vin and perform the search again";
    public static final String INVALID_DEVICE_ID_LENGTH_ERROR_MSG =
        "Invalid device id, length must be atleast of 3 characters";
    public static final String INVALID_VIN_LENGTH_ERROR_MSG = "Invalid VIN, length must be of 17 characters";
    public static final String INVALID_DEVICE_ID_ERROR_MSG = "Device Id must be alphanumeric";
    public static final String INVALID_BOTSERIAL_NUMBER =
        "Invalid input. Enter valid serialNumber with prefix BOTSERIAL";
    public static final String INVALID_DEVICE_DELETE_STATE =
        "Cannot process device info delete request, device not in PROVISIONED or PROVISIONED_ALIVE state.";

    /**
     * Private constructor to prevent instantiation of the class.
     */
    private ResponseConstants() {

    }
}
