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
 * The {@code CommonConstants} class contains constant values used throughout the application.
 * It provides string constants, DAO SQL constants, SWM constants, logging constants, and other miscellaneous constants.
 * All the fields in this class are declared as public static final, making them accessible and immutable.
 */
public final class CommonConstants {

    // String constants
    /**
     * Constant string used for logging an error message when the system fails to 
     * retrieve the device selection information for the filter query in the 
     * Campaign Manager.
     */
    public static final String COULD_NOT_GET_INFORMATION_LOGGING =
        "Could Not Get Campaign Manager's Device Selection Info for the Filter Query:- ";
    /**
     * A constant message to ensure that a valid filter string is being used.
     * This message also emphasizes the importance of using the correct format
     * for the filter string.
     */
    public static final String USING_VALID_FILTER_LOGGING =
        "Please make sure you are using a valid filter String And the format is correct.";
    /**
     * A constant representing a dot followed by a space (". ").
     * This can be used as a delimiter or separator in string operations.
     */
    public static final String DOT_SPACE = ". ";

    // DAO SQL constants
    /**
     * SQL query to select the "HarmanID" column from the "DeviceInfo" table in the public schema.
     * This query retrieves the unique identifier associated with a device.
     */
    public static final String SELECT_HARMANID_FROM_DEVICEINFO =
        "select \"DeviceInfo\".\"HarmanID\" from public.\"DeviceInfo\" ";
    /**
     * SQL query to select the "HarmanID" from the "DeviceInfo" table.
     * This query joins the "DeviceInfo" and "Device" tables from the public schema.
     */
    public static final String SELECT_HARMANID_DEVICE =
        "select \"DeviceInfo\".\"HarmanID\" from public.\"DeviceInfo\",\"Device\" ";
    /**
     * Query to retrieve the list of Harman IDs from the "TempDeviceGroup" table
     * where the "IsMatching" column matches the specified criteria.
     * 
     * <p>This query is used to filter and fetch matching VINs based on the provided
     * conditions.
     */
    public static final String MATCHING_VIN_LIST_QUERY =
        "select \"TempDeviceGroup\".\"HarmanID\" from \"TempDeviceGroup\" where \"TempDeviceGroup\".\"IsMatching\""
            + " in (";
    /**
     * Default query to retrieve the list of Harman IDs from the "TempDeviceGroup" table
     * where the "CreatedBy" field matches a specific value.
     * 
     * <p>This query is used to fetch matching VINs based on the creator's identifier.
     * Ensure that the appropriate value is appended to the query before execution.
     */
    public static final String MATCHING_VIN_LIST_DEFAULT_QUERY =
        "select \"TempDeviceGroup\".\"HarmanID\" from \"TempDeviceGroup\" where \"CreatedBy\"=";
    /**
     * SQL query to select the "HarmanID" of devices that are active.
     *
     * <p>This query retrieves the "HarmanID" from the "DeviceInfo" table where it matches
     * the "HarmanID" in the "Device" table and the "IsActive" field in the "Device" table
     * is set to true.
     *
     * <p>Tables involved:
     * - "DeviceInfo": Contains device information.
     * - "Device": Contains device status and other related data.
     *
     * <p>Conditions:
     * - "DeviceInfo"."HarmanID" must match "Device"."HarmanID".
     * - "Device"."IsActive" must be true.
     */
    public static final String SELECT_HARMANID_DEVICE_ISACTIVE_TRUE =
        "select \"DeviceInfo\".\"HarmanID\" from public.\"DeviceInfo\",\"Device\" where "
            + "\"DeviceInfo\".\"HarmanID\"=\"Device\".\"HarmanID\" and \"Device\".\"IsActive\"=true ";
    /**
     * SQL condition string that joins the "DeviceInfo" and "Device" tables
     * based on the "HarmanID" field and ensures that the "Device" is active.
     *
     * <p>
     * This condition is used to filter records where the "HarmanID" in the
     * "DeviceInfo" table matches the "HarmanID" in the "Device" table, and
     * the "IsActive" field in the "Device" table is set to true.
     */
    public static final String AND_HARMAN_ID_AND_DEVICE_ACTIVE_TRUE =
        " and \"DeviceInfo\".\"HarmanID\"=\"Device\".\"HarmanID\" and \"Device\".\"IsActive\"=true";

    /**
     * Constant representing the name of the Bluetooth manufacturer.
     */
    public static final String BLUETOOTH_MANUFACTURER = "Bluetooth Manufacturer";
    /**
     * Represents the connection frequency setting.
     * This constant is used to define the frequency of connections in the system.
     */
    public static final String CONNECTION_FREQUENCY = "Connection Frequency";
    /**
     * Constant representing the key for the manufacturer.
     * This is typically used to identify or reference the manufacturer
     * in various contexts within the application.
     */
    public static final String MANUFACTURER = "Manufacturer";
    /**
     * Constant representing the key for the vehicle type.
     * This key is used to identify or categorize the type of vehicle.
     */
    public static final String VEHICLE_TYPE = "VehicleType";
    
    /**
     * Constant representing the key for the software version.
     * This key is used to identify or reference the software version
     * in various contexts within the application.
     */
    public static final String SW_VERSION = "SW-Version";
    
    /**
     * Constant representing the software version key.
     * This key is used to identify or reference the software version
     * in various contexts within the application.
     */
    public static final String HW_VERSION = "HW-Version";

    /**
     * A constant representing the key for the body type attribute.
     * This is typically used to identify or categorize the type of body
     * in a given context.
     */
    public static final String BODY_TYPE = "BodyType";
    
    /**
     * Constant representing the key for the "Country" field.
     */
    public static final String COUNTRY = "Country";
    /**
     * Constant representing the key for the "Series" field.
     */
    public static final String SERIES = "Series";
    /**
     * Constant representing the key for the "Model" field.
     */
    public static final String MODEL = "Model";
    /**
     * Constant representing the key for the "Make" field.
     */
    public static final String MAKE = "Make";
    /**
     * Constant representing the key for the "Year" field.
     */
    public static final String YEAR = "Year";

    /**
     * Constant representing the key for the "Authorization" field.
     */
    public static final String HEADER_NAME_AUTHORIZATION = "Authorization";
    /**
     * Constant representing the key for the "Content-Type" field.
     */
    public static final String HEADER_CONTENT_TYPE_KEY = "Content-Type";
    /**
     * Constant representing the key for the "VEHICLE_OWNER" field.
     */
    public static final String GUEST_USER_ROLE = "VEHICLE_OWNER";
    /**
     * Constant representing the key for the "Guest@" field.
     */
    public static final String GUEST_USER_NAME_GENERIC = "Guest@";
    /**
     * Constant representing the key for the "GUEST_" field.
     */
    public static final String GUEST_USER_PASS_GENERIC = "GUEST_";
    
    /**
     * Constant representing the key for the "grant_type" field.
     */
    public static final String GRANT_TYPE_KEY = "grant_type";
    /**
     * Constant representing the key for the "password" field.
     */
    public static final String PASSWORD_TYPE = "password";
    /**
     * Constant representing the key for the "username" field.
     */
    public static final String USERNAME = "username";
    /**
     * Constant representing the key for the "scope" field.
     */
    public static final String SCOPE_KEY = "scope";
    /**
     * Constant representing the key for the "scopes" field.
     */
    public static final String SCOPE_VALUE =
        "ManageUserRolesAndPermissions SelfManage ManageUsers ManageUserSubscriptionsAndBilling IgniteSystem";
    /**
     * Constant representing the key for the "client_credentials" field.
     */
    public static final String SPRING_AUTH_CLIENT_CREDENTIALS = "client_credentials";
    /**
     * Constant representing the key for the "client_id" field.
     */
    public static final String SPRING_AUTH_CLIENT_ID = "client_id";
    /**
     * Constant representing the key for the "client_secret" field.
     */
    public static final String SPRING_AUTH_CLIENT_SECRET = "client_secret";
    /**
     * Constant representing the key for the "SPRING_AUTH_SCOPE_VALUE" field.
     */
    public static final String SPRING_AUTH_SCOPE_VALUE = "SelfManage OAuth2ClientMgmt IgniteSystem ManageUsers";
    /**
     * Constant representing the key for the "scopes" field.
     */
    public static final String SPRING_AUTH_SCOPE_KEY = "scopes";

    // SWM constants
    /**
     * Constant representing the key for the "userName" field.
     */
    public static final String USER_NAME = "userName";
    /**
     * Constant representing the key for the "sessionId" field.
     */
    public static final String SESSIONID = "sessionId";
    /**
     * Constant representing the key for the "domain" field.
     */
    public static final String SWM_DOMAIN = "domain";
    /**
     * Constant representing the key for the "application/json"" field.
     */
    public static final String APPLICATION_JSON = "application/json";
    /**
     * Constant representing the key for the "SWM_DOMAIN_ID_VAL" field.
     */
    public static final String SWM_DOMAIN_ID_VAL = "40835552926145409927638592560729411453";
    /**
     * Constant representing the key for the "vehicleModelYear" field.
     */
    public static final String SWM_VEHICLE_MODEL_YEAR = "vehicleModelYear";
    /**
     * Constant representing the key for the "Success" field.
     */
    public static final String SUCCESS = "Success";
    /**
     * Constant representing the key for the "NEW" field.
     */
    public static final String ACTION_NEW = "NEW";
    /**
     * Constant representing the key for the "Vehicle already exists" field.
     */
    public static final String SWM_VEHICLE_ALREADY_EXIST = "Vehicle already exists";
    // String constants
    /**
     * Constant representing the key for the "AUTH-Token" field.
     */
    public static final String AUTH_TOKEN = "AUTH-Token";
    /**
     * Constant representing the key for the "HarmanID" field.
     */
    public static final String HARMAN_ID = "HarmanID";
    /**
     * Constant representing the key for the "RandomNumber" field.
     */
    public static final String RANDOM_NUMBER = "RandomNumber";
    /**
     * Constant representing the key for the "PassCode" field.
     */
    public static final String PASS_CODE = "PassCode";
    /**
     * Constant representing the key for the "UserID" field.
     */
    public static final String USER_ID = "UserID";
    /**
     * Constant representing the key for the "FirstName" field.
     */
    public static final String FIRST_NAME = "FirstName";
    /**
     * Constant representing the key for the "LastName" field.
     */
    public static final String LAST_NAME = "LastName";
    /**
     * Constant representing the key for the "Email" field.
     */
    public static final String EMAIL = "Email";
    /**
     * Constant representing the key for the "ID" field.
     */
    public static final String ID = "ID";
    /**
     * Constant representing the key for the "registered_scope_id" field.
     */
    public static final String REGISTERED_SCOPE_ID = "registered_scope_id";
    /**
     * Constant representing the key for the "BOTSERIAL" field.
     */
    public static final String BOT_DEVICE_PREFIX = "BOTSERIAL";

    // Logging
    /**
     * Constant representing the key for the "updateRole log" field.
     */
    public static final String UPDATE_ROLE_LOG = "updateRole:{}";
    /**
     * Constant representing the key for the "showApproveUser log" field.
     */
    public static final String SHOW_APPROVE_USER_LOG = "showApproveUser:{}";
    /**
     * Constant representing the key for the "GET_UPDATE_PACKAGE_INFO" field.
     */
    public static final String GET_UPDATE_PACKAGE_INFO =
        "Entering getUpdatePackageInfo(UpdatePackageFilterBean updatePackage)";

    // DAO SQL constants
    /**
     * Constant representing the key for the "SELECT_USERID_GROUP_BY_USERID query" field.
     */
    public static final String SELECT_USERID_GROUP_BY_USERID =
        "select \"UserID\" from \"UserOEM\" group by \"UserID\" having count(*)=1";
    /**
     * Constant representing the key for the "SELECT_OEMID_WHERE_USERID query" field.
     */
    public static final String SELECT_OEMID_WHERE_USERID = "select \"OEMID\" from \"UserOEM\" where \"UserID\"=?";
    /**
     * Constant representing the key for the "SELECT_FROM_DEVICE_INFO_FACTORY_DATA_WHERE query" field.
     */
    public static final String SELECT_FROM_DEVICE_INFO_FACTORY_DATA_WHERE =
        "select * from public.\"DeviceInfoFactoryData\" where ";
    /**
     * Constant representing the key for the "DELETE_FROM_DEVICE_INFO_FACTORY_DATA_WHERE query" field.
     */
    public static final String DELETE_FROM_DEVICE_INFO_FACTORY_DATA_WHERE =
        "delete from  public.\"DeviceInfoFactoryData\" where ";
    /**
     * Constant representing the key for the "SELECT_ID_FROM_DEVICE_INFO_FACTORY_DATA_WHERE query" field.
     */
    public static final String SELECT_ID_FROM_DEVICE_INFO_FACTORY_DATA_WHERE =
        "select \"ID\" from public.\"DeviceInfoFactoryData\" where ";
    /**
     * Constant representing the key for the "SELECT_USER_ID_FROM_USER_OEM query" field.
     */
    public static final String SELECT_USER_ID_FROM_USER_OEM = "SELECT \"UserID\" FROM \"UserOEM\" WHERE \"OEMID\" IN (";
    /**
     * Constant representing the key for the "SELECT_FROM_DEVICE_INFO_FACTORY_DATA query" field.
     */
    public static final String SELECT_FROM_DEVICE_INFO_FACTORY_DATA = "select * from public.\"DeviceInfoFactoryData\" ";
    /**
     * Constant representing the key for the "JOIN_CONDITION query" field.
     */
    public static final String JOIN_CONDITION =
        " WHERE \"DeviceInfoFactoryData\".\"ID\"=\"device_association\".\"factory_data\"";
    /**
     * Constant representing the key for the "SELECT_FROM_DEVICE_INFO_FACTORY_DATA_AND_ASSOCIATION query" field.
     */
    public static final String SELECT_FROM_DEVICE_INFO_FACTORY_DATA_AND_ASSOCIATION =
        "select * from public.\"DeviceInfoFactoryData\" inner join public.\"device_association\" on "
            + "\"DeviceInfoFactoryData\".\"ID\"=\"device_association\".\"factory_data\" ";
    /**
     * Constant representing the key for the "SELECT_FROM_DEVICE_INFO_FACTORY_DATA_FOR_IMEI query" field.
     */
    public static final String SELECT_FROM_DEVICE_INFO_FACTORY_DATA_FOR_IMEI =
        "select \"ID\", \"manufacturing_date\", \"model\",\"imei\",\"serial_number\",\"platform_version\",\"iccid\","
            + "\"ssid\",\"bssid\",\"msisdn\",\"imsi\",\"record_date\",\"factory_created_date\",\"factory_admin\","
            + "\"state\",\"package_serial_number\" from public.\"DeviceInfoFactoryDataHistory\" ";
    /**
     * Constant representing the key for the "VIN_DETAILS_JOIN_CONDITION query" field.
     */
    public static final String VIN_DETAILS_JOIN_CONDITION =
        " left outer join public.\"vin_details\" on \"DeviceInfoFactoryData\".\"ID\"=\"vin_details\".\"reference_id\"";

    /**
     * Constant representing the key for the "URL_SEPARATOR" field.
     */
    public static final String URL_SEPARATOR = "/";
    /**
     * Constant representing the key for the "systemparameters" field.
     */
    public static final String SYS_PARAMS = "systemparameters";
    /**
     * Constant representing the key for the "systemparamkeys" field.
     */
    public static final String SYS_PARAM_KEYS = "systemparamkeys";
    /**
     * Constant representing the key for the "?" field.
     */
    public static final String QUESTION_MARK = "?";
    /**
     * Constant representing the key for the "=" field.
     */
    public static final String EQUAL = "=";
    /**
     * Constant representing the key for the "AND" field.
     */
    public static final String AND = "&";
    /**
     * Constant representing the key for the "Content-Type" field.
     */
    public static final String CONTENT_TYPE = "Content-Type";
    /**
     * Constant representing the key for the "accept" field.
     */
    public static final String ACCEPT = "accept";
    /**
     * Constant representing the key for the "deviceTypeMandatoryParams" field.
     */
    public static final String DEVICE_TYPE_MANDATORY_PARAMS = "deviceTypeMandatoryParams";


    /**
     * The CommonConstants class contains constants that are commonly used throughout the application.
     * This class cannot be instantiated as it only provides constants.
     */
    private CommonConstants() {

    }
}
