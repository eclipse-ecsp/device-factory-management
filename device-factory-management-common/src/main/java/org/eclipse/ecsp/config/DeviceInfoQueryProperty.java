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

package org.eclipse.ecsp.config;

import org.eclipse.ecsp.common.config.EnvConfigProperty;
import org.eclipse.ecsp.common.config.EnvConfigPropertyType;

/**
 * This enum represents the properties used for querying device information. It
 * implements the EnvConfigProperty interface.
 */
public enum DeviceInfoQueryProperty implements EnvConfigProperty {
    /**
     * Base URL for REST services.
     */
    SERVICE_REST_URL_BASE("service.rest_url_base"),

    /**
     * Indicates that the device is not connected.
     */
    NOT_CONNECTED("not_connected"),

    /**
     * Indicates that the device is not polling.
     */
    NOT_POLLING("not_polling"),

    /**
     * Enables automatic rollback functionality.
     */
    AUTO_ROLLBACK("auto_rollback"),

    /**
     * Represents a new state or property.
     */
    NEW("new"),

    /**
     * A dummy public property with a default value.
     */
    DUMMY_PUBLIC_PROPERTY("dummy.public_property", "default value"),

    /**
     * A dummy secured property with a default value.
     */
    DUMMY_SECURED_PROPERTY("dummy.secured_property", "default value", EnvConfigPropertyType.SECURED),

    /**
     * Client ID for Spring Authentication.
     */
    SPRING_AUTH_CLIENT_ID("client_id"),

    /**
     * Client secret for Spring Authentication.
     */
    SPRING_AUTH_CLIENT_SECRET("client_secret"),

    /**
     * Service URL for Spring Authentication.
     */
    SPRING_AUTH_SERVICE_URL("spring_auth_service_url"),

    /**
     * IP address of the Vault server.
     */
    VAULT_SERVER_IP_ADDRESS("vault_server_ip_address"),

    /**
     * Port number of the Vault server.
     */
    VAULT_SERVER_PORT("vault_server_port"),

    /**
     * Environment configuration for Vault.
     */
    VAULT_ENV("environment"),

    /**
     * Admin username for authentication.
     */
    ADMIN_USERNAME("admin_username"),

    /**
     * Admin password for authentication.
     */
    ADMIN_PASSWORD("admin_password"),

    /**
     * Client ID for HCP integration.
     */
    HCP_CLIENT_ID("hcp_client_id"),

    /**
     * Client secret for HCP integration.
     */
    HCP_CLIENT_SECRET("hcp_client_secret"),

    /**
     * Flag to enable or disable secret vault functionality.
     */
    SECRET_VAULT_ENABLE_FLG("secret_vault_enable_flg"),

    /**
     * URL for creating users in Spring Authentication.
     */
    USER_CREATE_URL_SPRING_AUTH("user_create_url_spring_auth"),

    /**
     * Flag to enable or disable Postgres Vault integration.
     */
    POSTGRES_VAULT_ENABLE_FLG("postgres_vault_enable_flg"),

    /**
     * URL for connecting to Postgres.
     */
    POSTGRES_URL("postgres_url"),

    /**
     * Driver class name for Postgres.
     */
    POSTGRES_DRIVER_CLASS_NAME("postgres_driver_class_name"),

    /**
     * Interval gap for Postgres Vault lease renewal.
     */
    POSTGRES_VAULT_LEASE_INTERVAL_GAP("postgres_vault_leaseIntervalGap"),

    /**
     * Interval for checking Postgres Vault refresh.
     */
    POSTGRES_VAULT_REFRESH_CHECK_INTERVAL("postgres_vault_refreshCheckInterval"),

    /**
     * Username for Postgres authentication.
     */
    POSTGRES_USERNAME("postgres_username"),

    /**
     * Password for Postgres authentication.
     */
    POSTGRES_PASSWORD("postgres_password"),

    /**
     * Base URL for device configuration management.
     */
    DEVICE_CONFIG_MGMT_BASE_URL("device_configmgmt_base_url"),

    /**
     * Timeout for polling status.
     */
    POLLING_STATUS_TIMEOUT("polling_status_timeout"),

    /**
     * Base version for device configuration management URL.
     */
    DEVICE_CONFIG_MGMT_BASE_VERSION("device_configmgmt_base_url_version"),

    /**
     * Flag to enable device subscription details.
     */
    ENABLE_DEVICE_SUBSCRIPTION_DETAILS("enable_device_subscription_details"),

    /**
     * Name of the S3 bucket.
     */
    S3_BUCKET_NAME("s3_bucket_name"),

    /**
     * Base URL for SWM (Solid Waste Management) services.
     */
    SWM_BASE_URL("swm_base_url"),

    /**
     * Login API URL for SWM services.
     */
    SWM_LOGIN_API_URL("swm_login_api_url"),

    /**
     * API for updating SWM data.
     */
    SWM_UPDATE_API("swm_update_api"),

    /**
     * API for deleting SWM data.
     */
    SWM_DELETE_API("swm_delete_api"),

    /**
     * API for fetching SWM vehicle models.
     */
    SWM_VEHICLE_MODELS_API("swm_vehicle_models"),

    /**
     * API for fetching SWM vehicles.
     */
    SWM_VEHICLES_API("swm_vehicles"),

    /**
     * Password for SWM authentication.
     */
    SWM_PASSWORD("swm_password"),

    /**
     * Username for SWM authentication.
     */
    SWM_USERNAME("swm_username"),

    /**
     * Domain for SWM services.
     */
    SWM_DOMAIN("swm_domain"),

    /**
     * Domain ID for SWM services.
     */
    SWM_DOMAIN_ID("swm_domain_id"),

    /**
     * Vehicle model ID for SWM services.
     */
    SWM_VEHICLE_MODEL_ID("swm_vehicle_model_id"),

    /**
     * Type of device creation.
     */
    DEVICE_CREATION_TYPE("device_creation_type"),

    /**
     * Flag to enable SWM integration.
     */
    SWM_INTEGRATION_ENABLED("swm_integration_enabled"),

    /**
     * Initial size of the connection pool.
     */
    INITIAL_POOL_SIZE("initial_pool_size"),

    /**
     * Minimum size of the connection pool.
     */
    MIN_POOL_SIZE("min_pool_size"),

    /**
     * Maximum size of the connection pool.
     */
    MAX_POOL_SIZE("max_pool_size"),

    /**
     * Maximum idle time for connections in the pool.
     */
    MAX_IDLE_TIME("max_idle_time"),

    /**
     * Increment size for acquiring new connections.
     */
    ACQUIRE_INCREMENT("acquire_increment"),

    /**
     * Period for testing idle connections in the pool.
     */
    IDLE_CONNECTION_TEST_PERIOD("idle_connection_test_period"),

    /**
     * Base URL for SaaS API services.
     */
    SAAS_API_BASE_URL("saas_api_base_url"),

    /**
     * Base version for SaaS API services.
     */
    SAAS_API_BASE_URL_VERSION("saas_api_base_url_version"),

    /**
     * Customer-related configuration.
     */
    CUSTOMER("customer"),

    /**
     * Flag to enable or disable API registry.
     */
    API_REGISTRY_ENABLED("api_registry_enabled"),

    /**
     * Version of the Spring application.
     */
    SPRING_APPLICATION_VERSION("spring_application_version"),

    /**
     * Paths to include in OpenAPI documentation.
     */
    OPENAPI_PATH_INCLUDE("openapi_path_include"),

    /**
     * Port number for the server.
     */
    SERVER_PORT("server_port"),

    /**
     * Name of the Spring application.
     */
    SPRING_APPLICATION_NAME("spring_application_name"),

    /**
     * Service name of the Spring application.
     */
    SPRING_APPLICATION_SERVICENAME("spring_application_servicename"),

    /**
     * Flag to enable or disable API security.
     */
    API_SECURITY_ENABLED("api_security_enabled"),

    /**
     * Paths to exclude from OpenAPI documentation.
     */
    OPENAPI_PATH_EXCLUDE("openapi_path_exclude"),

    /**
     * Context path for the API.
     */
    API_CONTEXT_PATH("api_context-path"),

    /**
     * Service name for API registry.
     */
    API_REGISTRY_SERVICE_NAME("api_registry_service-name");

    private String nameInFile;
    private String defaultValue;
    private EnvConfigPropertyType type;

    /**
     * Constructs a DeviceInfoQueryProperty with the specified name in the file. The
     * default value is set to null. The type is set to PUBLIC.
     *
     * @param nameInFile the name of the property in the file
     */
    private DeviceInfoQueryProperty(String nameInFile) {
        this(nameInFile, null);
    }

    /**
     * Constructs a DeviceInfoQueryProperty with the specified name in the file and
     * default value. The type is set to PUBLIC.
     *
     * @param nameInFile   the name of the property in the file
     * @param defaultValue the default value of the property
     */
    private DeviceInfoQueryProperty(String nameInFile, String defaultValue) {
        this(nameInFile, defaultValue, EnvConfigPropertyType.PUBLIC);
    }

    /**
     * Constructs a DeviceInfoQueryProperty with the specified name in the file,
     * default value, and type.
     *
     * @param nameInFile   the name of the property in the file
     * @param defaultValue the default value of the property
     * @param type         the type of the property
     */
    private DeviceInfoQueryProperty(String nameInFile, String defaultValue, EnvConfigPropertyType type) {
        this.nameInFile = nameInFile;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    /**
     * Returns the name of the property in the file.
     *
     * @return the name of the property in the file
     */
    public String getNameInFile() {
        return nameInFile;
    }

    /**
     * Returns the default value of the property.
     *
     * @return the default value of the property
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns the type of the property.
     *
     * @return the type of the property
     */
    public EnvConfigPropertyType getType() {
        return type;
    }
}
