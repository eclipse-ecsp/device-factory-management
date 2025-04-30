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

package org.eclipse.ecsp.common.enums;

/**
 * This enum represents the API message codes, messages, and general messages.
 * Each enum constant contains a code, message, and general message associated with it.
 * The code represents a unique identifier for the message.
 * The message provides a specific description of the message.
 * The general message provides a general description of the message.
 */
public enum ApiMessageEnum {
    GENERAL_ERROR("dfd-777", "Internal server error", "Not successful. Something went wrong. Please contact admin."),
    SERIALNUMBER_IMEI_MANDATORY("dfd-001", "Validation failed", "Either Serial number or IMEI is mandatory."),
    INVALID_CURRENT_FACTORY_DATA("dfd-002", "Resource not found",
        "No data is found in inventory for the requested inputs."),
    FIND_FACTORY_DATA_SUCCESS("dfd-003", "Success", "Device factory data retrieved successfully."),
    FACTORY_DATA_CREATE_SUCCESS("dfd-004", "Success", "Device factory data created successfully."),
    INVALID_IMEI_SERIAL_NUMBER_COMBINATION("dfd-005", "Validation failed",
        "Invalid IMEI and serial number combination."),
    INVALID_IMEI("dfd-006", "Validation failed", "Invalid IMEI"),
    INVALID_SERIAL_NUMBER("dfd-007", "Validation failed", "Invalid serial number."),
    WRONG_QUERY_ASC_DESC_VALUE("dfd-008", "Validation failed", "Invalid asc or desc value."),
    WRONG_ISDETAILSREQUIRED_VALUE("dfd-009", "Validation failed",
        "isdetailsrequired field is mandatory and should have boolean type."),
    INVALID_IMEI_LENGTH_ERROR_MSG("dfd-010", "Validation failed", "IMEI value have to be minimum 3 digits for search."),
    INVALID_IMEI_ERROR_MSG("dfd-011", "Validation failed", "IMEI must be numeric."),
    INVALID_SERIAL_NUMBER_LENGTH_ERROR_MSG("dfd-012", "Validation failed",
        "Serial number value must to be minimum 3 digits for search."),
    INVALID_SERIAL_NUMBER_ERROR_MSG("dfd-013", "Validation failed", "Serial number must be alphanumeric."),
    ZERO_PAGE_VALUE_TYPE_MESSAGE("dfd-014", "Validation failed", "Page should be greater than zero."),
    WRONG_PAGE_VALUE_TYPE_MESSAGE("dfd-015", "Validation failed", "Page should be unsigned number."),
    WRONG_PAGE_SIZE_MESSAGE("dfd-016", "Validation failed", "Page size must be between 1 and 5000"),
    NOT_SUPPORTED_VERSION("dfd-017", "Not supported", "This version of api is not supported."),
    INVALID_NUMBER_OF_PARAMS_WITH_VIN("dfd-018", "Validation failed",
        "Please provide any one of imei, SerialNumber, DeviceId or vin and perform the search again."),
    INVALID_NUMBER_OF_PARAMS("dfd-019", "Validation failed",
        "Please provide any one of imei, SerialNumber, DeviceId or State and perform the search again"),
    INVALID_VIN_LENGTH_ERROR_MSG("dfd-020", "Validation failed", "Invalid VIN, length must be of 17 characters"),
    INVALID_SORTBY_FIELD_RESPONSE("dfd-021", "Validation failed",
        "sortby field and value should be of same length and allowed fields."),
    INVALID_SORTING_ORDER_VALUE("dfd-022", "Validation failed",
        "sorting order field is mandatory. If non empty then should have asc/desc type."),
    INVALID_CONTAINS_LIKE_FIELD("dfd-023", "Validation failed",
        "containslikefields is mandatory. If non empty then should be allowed one."),
    INVALID_RANGE_FIELD_RESPONSE("dfd-024", "Validation failed",
        "rangefields and rangevalues should be of same length and allowed."),
    INVALID_RANGE_VALUE_RESPONSE("dfd-025", "Validation failed",
        "range values is not in proper format. It should be separated by underscore(_)"),
    INVALID_DEVICE_ID_LENGTH_ERROR_MSG("dfd-026", "Validation failed",
        "Invalid device id, length must be at least of 3 characters"),
    INVALID_DEVICE_ID_ERROR_MSG("dfd-027", "Validation failed", "Device Id must be alphanumeric."),
    DEVICE_STATE_SORT_BY_FIELD_VALIDATION_ERR_MSG("dfd-028", "Validation failed",
        "Incorrect sortby field value. Use one of the following values: state|stateTimestamp|manufacturingDate|imei|"
            + "serialNumber|iccid|ssid|bssid|msisdn|imsi|factoryAdmin|packageSerialNumber|recordDate"),
    DEVICE_DETAILS_SORT_BY_FIELD_VALIDATION_BY_DEVICE_ID_ERR_MSG("va-029", "Validation failed",
        "Incorrect sortby field value. Use one of the following values: imei|serialNumber|model|iccid|ssid|bssid|"
            + "msisdn|imsi|factoryAdmin|state|packageSerialNumber|recordDate|createdDate|deviceId"),
    DEVICE_DETAILS_SORT_BY_FIELD_VALIDATION_ERR_MSG("dfd-030", "Validation failed",
        "Incorrect sortby field value. Use one of the following values: imei|serialNumber|model|iccid|ssid|bssid|"
            + "msisdn|imsi|factoryAdmin|state|packageSerialNumber|recordDate|createdDate"),
    ORDER_BY_FIELD_VALIDATION_ERR_MSG("dfd-031", "Validation failed",
        "orderby field should have either asc or desc type"),
    INVALID_INPUT("dfd-032", "Validation failed", "No data found for the given input"),
    DEVICE_NOT_FOUND_FOR_IMEI_MSG("dfd-033", "Resource not found", "Device not found for given imei"),
    FIND_DEVICE_STATE("dfd-034", "Success", "Device states retrieved successfully."),
    DEVICE_STATE_CHANGE("dfd-035", "Success", "Device states changed successfully."),
    STATE_CHANGE_ERROR("dfd-036", "Validation failed", "Unable to change device state"),
    FACTORY_DATA_NOT_FOUND("dfd-037", "Resource not found", "Factory data not found."),
    DEVICE_STATE_INVALID_INPUT("dfd-038", "Validation failed", "Either of factory id or imei is mandatory."),
    DEVICE_STATE_MANDATORY("dfd-039", "Validation failed", "State is mandatory."),
    DEVICE_UPDATE_SUCCESS("dfd-040", "Success", "Device updated successfully."),
    REQ_NULL("dfd-041", "Validation failed", "Request object (deviceUpdateRequest) is null"),
    MISSING_INPUT("dfd-042", "Validation failed",
        "One or more than one required attribute(s) value is missing either in currentValue or replaceWith input"
            + " json."),
    FACTORY_DATA_DOES_NOT_EXIST("dfd-043", "Resource not found", "Factory data not found."),
    DELETE_DEVICE("dfd-044", "Success", "Device deleted successfully."),
    DELETE_DEVICE_MISSING_INPUT("dfd-045", "Validation failed",
        "Either of imei or associationID must be present in the request."),
    DEVICE_DELETE_MSG("dfd-046", "PreCondition failed", "Cannot delete the device. Device has historical data."),
    DEVICE_ALREADY_EXIST_BY_VIN("dfd-047", "PreCondition failed", "Device already exists for VIN."),
    DEVICE_DETAILS_NOT_FOUND("dfd-048", "Resource not found", "No data found for the given input"),
    DEVICE_ALREADY_EXIST_BY_SERIAL_NUMBER("dfd-049", "PreCondition failed", "Device already exists for serial number."),
    DEVICE_STATE_SUCCESS("dfd-050", "Success", "Device states retrieved successfully."),
    SWM_SESSION_ID_NULL("dfd-051", "Internal server error", "SWM session id is null"),
    SWM_VEHICLE_CREATION_FAILED("dfd-052", "Internal server error",
        "SWM vehicle creation failed. Possible cause: SessionId expired"),
    SWM_VEHICLE_CREATION_RESPONSE_JSON_PARSE_FAILED("dfd-053", "Internal server error",
        "Unable to parse swm vehicle creation response json."),
    SWM_VEHICLE_CREATION_INTERNAL_ERROR("dfd-054", "Internal server error",
        "SWM vehicle creation failed due to SWM internal error."),
    DEVICE_DELETE_SUCCESS("dfd-055", "Success", "Device deleted successfully."),
    SWM_VEHICLE_DELETE_RESPONSE_JSON_PARSE_FAILED("dfd-056", "Internal server error",
        "Unable to parse swm vehicle delete response json."),
    SWM_VEHICLE_DELETE_FAILED("dfd-057", "Internal server error",
        "SWM vehicle deletion failed. Possible cause: SessionId expired"),
    SWM_VEHICLE_UPDATE_FAILED("dfd-058", "Internal server error",
        "SWM vehicle update failed. Possible cause: SessionId expired"),

    MISSING_USERID_RESPONSE("dfd-059", "Validation failed", "Missing 'user-id' in http request header"),
    INVALID_PAYLOAD_RESPONSE("dfd-060", "Validation failed", "Invalid payload data"),
    DUPLICATE_ENTRY_RESPONSE("dfd-061", "Validation failed",
        "One or more of the input factory data is already present in the DB. Duplicate entry."),
    INVALID_PAYLOAD_NULL_RESPONSE("dfd-062", "Validation failed",
        "Invalid payload data. Contains null values for fields."),
    INVALID_DATE_FORMAT_RESPONSE("dfd-063", "Validation failed",
        "Invalid date format passed. Valid format is yyyy/MM/dd"),
    STATE_RETRIEVAL_FAILED("dfd-064", "Validation failed", "Failed to retrieve device states for given IMEI"),
    INVALID_DEVICE_TYPE("dfd-065", "Validation failed", "Invalid Device Type."),
    DEVICE_RETRIEVE_SUCCESS("dfd-00066", "Success", "Device details retrieved successfully"),
    DEVICE_NOT_FOUND("dfd-00067", "Resource not found", "Factory data not found for given filter"),
    INVALID_DEVICE_FILTER("dfd-00068", "Validation failed", "Invalid device filter data"),
    DEVICE_UPDATE_FAILED("dfd-00069", "Internal server error", "Failed to unable device, please contact admin."),
    SYSTEM_PARAMETERS_API_FAILURE("dfd-070", "Internal server error", "System parameters service down"),
    DEVICE_TYPE_MANDATORY_PARAMETERS_EMPTY("dfd-071", "Validation failed",
        "Device Type Mandatory Params retrieved from system parameters API are empty"),
    MISSING_MANDATORY_REQUEST_PARAMS("dfd-072", "Validation failed",
        "One or more than one required attribute(s) value is missing in input json"),
    INVALID_REGION("dfd-073", "Validation failed", "Invalid Region.");

    private final String code;
    private final String message;
    private final String generalMessage;

    /**
     * Constructs a new {@code ApiMessageEnum} with the specified code, message, and general message.
     *
     * @param code           the code associated with the message
     * @param message        the detailed message
     * @param generalMessage the general message
     */
    ApiMessageEnum(String code, String message, String generalMessage) {
        this.code = code;
        this.message = message;
        this.generalMessage = generalMessage;
    }

    /**
     * Returns the code associated with the message.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the detailed message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the general message.
     *
     * @return the general message
     */
    public String getGeneralMessage() {
        return generalMessage;
    }
}
