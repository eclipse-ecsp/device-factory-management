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
    public static final String COULD_NOT_GET_INFORMATION_LOGGING =
        "Could Not Get Campaign Manager's Device Selection Info for the Filter Query:- ";
    public static final String USING_VALID_FILTER_LOGGING =
        "Please make sure you are using a valid filter String And the format is correct.";
    public static final String DOT_SPACE = ". ";

    // DAO SQL constants
    public static final String SELECT_HARMANID_FROM_DEVICEINFO =
        "select \"DeviceInfo\".\"HarmanID\" from public.\"DeviceInfo\" ";
    public static final String SELECT_HARMANID_DEVICE =
        "select \"DeviceInfo\".\"HarmanID\" from public.\"DeviceInfo\",\"Device\" ";
    public static final String MATCHING_VIN_LIST_QUERY =
        "select \"TempDeviceGroup\".\"HarmanID\" from \"TempDeviceGroup\" where \"TempDeviceGroup\".\"IsMatching\""
            + " in (";
    public static final String MATCHING_VIN_LIST_DEFAULT_QUERY =
        "select \"TempDeviceGroup\".\"HarmanID\" from \"TempDeviceGroup\" where \"CreatedBy\"=";
    public static final String SELECT_HARMANID_DEVICE_ISACTIVE_TRUE =
        "select \"DeviceInfo\".\"HarmanID\" from public.\"DeviceInfo\",\"Device\" where "
            + "\"DeviceInfo\".\"HarmanID\"=\"Device\".\"HarmanID\" and \"Device\".\"IsActive\"=true ";
    public static final String AND_HARMAN_ID_AND_DEVICE_ACTIVE_TRUE =
        " and \"DeviceInfo\".\"HarmanID\"=\"Device\".\"HarmanID\" and \"Device\".\"IsActive\"=true";

    public static final String BLUETOOTH_MANUFACTURER = "Bluetooth Manufacturer";
    public static final String CONNECTION_FREQUENCY = "Connection Frequency";
    public static final String MANUFACTURER = "Manufacturer";
    public static final String VEHICLE_TYPE = "VehicleType";
    public static final String SW_VERSION = "SW-Version";
    public static final String HW_VERSION = "HW-Version";
    public static final String BODY_TYPE = "BodyType";
    public static final String COUNTRY = "Country";
    public static final String SERIES = "Series";
    public static final String MODEL = "Model";
    public static final String MAKE = "Make";
    public static final String YEAR = "Year";

    public static final String HEADER_NAME_AUTHORIZATION = "Authorization";
    public static final String HEADER_CONTENT_TYPE_KEY = "Content-Type";
    public static final String GUEST_USER_ROLE = "VEHICLE_OWNER";
    public static final String GUEST_USER_NAME_GENERIC = "Guest@";
    public static final String GUEST_USER_PASS_GENERIC = "GUEST_";

    public static final String GRANT_TYPE_KEY = "grant_type";
    public static final String PASSWORD_TYPE = "password";
    public static final String USERNAME = "username";
    public static final String SCOPE_KEY = "scope";
    public static final String SCOPE_VALUE =
        "ManageUserRolesAndPermissions SelfManage ManageUsers ManageUserSubscriptionsAndBilling IgniteSystem";
    public static final String SPRING_AUTH_CLIENT_CREDENTIALS = "client_credentials";
    public static final String SPRING_AUTH_CLIENT_ID = "client_id";
    public static final String SPRING_AUTH_CLIENT_SECRET = "client_secret";
    public static final String SPRING_AUTH_SCOPE_VALUE = "SelfManage OAuth2ClientMgmt IgniteSystem ManageUsers";
    public static final String SPRING_AUTH_SCOPE_KEY = "scopes";

    // SWM constants
    public static final String USER_NAME = "userName";
    public static final String SESSIONID = "sessionId";
    public static final String SWM_DOMAIN = "domain";
    public static final String APPLICATION_JSON = "application/json";
    public static final String SWM_DOMAIN_ID_VAL = "40835552926145409927638592560729411453";
    public static final String SWM_VEHICLE_MODEL_YEAR = "vehicleModelYear";
    public static final String SUCCESS = "Success";
    public static final String ACTION_NEW = "NEW";
    public static final String SWM_VEHICLE_ALREADY_EXIST = "Vehicle already exists";
    // String constants
    public static final String AUTH_TOKEN = "AUTH-Token";
    public static final String HARMAN_ID = "HarmanID";
    public static final String RANDOM_NUMBER = "RandomNumber";
    public static final String PASS_CODE = "PassCode";
    public static final String USER_ID = "UserID";
    public static final String FIRST_NAME = "FirstName";
    public static final String LAST_NAME = "LastName";
    public static final String EMAIL = "Email";
    public static final String ID = "ID";
    public static final String REGISTERED_SCOPE_ID = "registered_scope_id";
    public static final String BOT_DEVICE_PREFIX = "BOTSERIAL";

    // Logging
    public static final String UPDATE_ROLE_LOG = "updateRole:{}";
    public static final String SHOW_APPROVE_USER_LOG = "showApproveUser:{}";
    public static final String GET_UPDATE_PACKAGE_INFO =
        "Entering getUpdatePackageInfo(UpdatePackageFilterBean updatePackage)";

    // DAO SQL constants
    public static final String SELECT_USERID_GROUP_BY_USERID =
        "select \"UserID\" from \"UserOEM\" group by \"UserID\" having count(*)=1";
    public static final String SELECT_OEMID_WHERE_USERID = "select \"OEMID\" from \"UserOEM\" where \"UserID\"=?";
    public static final String SELECT_FROM_DEVICE_INFO_FACTORY_DATA_WHERE =
        "select * from public.\"DeviceInfoFactoryData\" where ";
    public static final String DELETE_FROM_DEVICE_INFO_FACTORY_DATA_WHERE =
        "delete from  public.\"DeviceInfoFactoryData\" where ";
    public static final String SELECT_ID_FROM_DEVICE_INFO_FACTORY_DATA_WHERE =
        "select \"ID\" from public.\"DeviceInfoFactoryData\" where ";
    public static final String SELECT_USER_ID_FROM_USER_OEM = "SELECT \"UserID\" FROM \"UserOEM\" WHERE \"OEMID\" IN (";
    public static final String SELECT_FROM_DEVICE_INFO_FACTORY_DATA = "select * from public.\"DeviceInfoFactoryData\" ";
    public static final String JOIN_CONDITION =
        " WHERE \"DeviceInfoFactoryData\".\"ID\"=\"device_association\".\"factory_data\"";
    public static final String SELECT_FROM_DEVICE_INFO_FACTORY_DATA_AND_ASSOCIATION =
        "select * from public.\"DeviceInfoFactoryData\" inner join public.\"device_association\" on "
            + "\"DeviceInfoFactoryData\".\"ID\"=\"device_association\".\"factory_data\" ";
    public static final String SELECT_FROM_DEVICE_INFO_FACTORY_DATA_FOR_IMEI =
        "select \"ID\", \"manufacturing_date\", \"model\",\"imei\",\"serial_number\",\"platform_version\",\"iccid\","
            + "\"ssid\",\"bssid\",\"msisdn\",\"imsi\",\"record_date\",\"factory_created_date\",\"factory_admin\","
            + "\"state\",\"package_serial_number\" from public.\"DeviceInfoFactoryDataHistory\" ";
    public static final String VIN_DETAILS_JOIN_CONDITION =
        " left outer join public.\"vin_details\" on \"DeviceInfoFactoryData\".\"ID\"=\"vin_details\".\"reference_id\"";

    public static final String URL_SEPARATOR = "/";
    public static final String SYS_PARAMS = "systemparameters";
    public static final String SYS_PARAM_KEYS = "systemparamkeys";
    public static final String QUESTION_MARK = "?";
    public static final String EQUALS = "=";
    public static final String AND = "&";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT = "accept";
    public static final String DEVICE_TYPE_MANDATORY_PARAMS = "deviceTypeMandatoryParams";


    /**
     * The CommonConstants class contains constants that are commonly used throughout the application.
     * This class cannot be instantiated as it only provides constants.
     */
    private CommonConstants() {

    }
}
