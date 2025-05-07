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

import jakarta.annotation.Resource;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.common.config.EnvConfigLoader;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * Configuration class for HcpDeviceInfoQueryLib.
 */
@Configuration
@ImportAutoConfiguration(RestTemplateAutoConfiguration.class)
public class HcpDeviceInfoQueryLibConfig {

    @Resource(name = "envConfigLoaderDeviceInfoQuery")
    private EnvConfigLoader<DeviceInfoQueryProperty> envConfigLoader;

    /**
     * Creates an instance of EnvConfigLoader for DeviceInfoQueryProperty.
     *
     * @return The EnvConfigLoader instance.
     */
    @Bean(name = "envConfigLoaderDeviceInfoQuery")
    public EnvConfigLoader<DeviceInfoQueryProperty> envConfigLoader() {
        return new EnvConfigLoader<>(DeviceInfoQueryProperty.class, "deviceinfoquery");
    }

    /**
     * Retrieves the server configuration from the EnvConfigLoader.
     *
     * @return The server configuration.
     */
    @Bean(name = "envConfig")
    public EnvConfig<DeviceInfoQueryProperty> envConfig() {
        return envConfigLoader.getServerConfig();
    }

    @Resource(name = "envConfig")
    protected EnvConfig<DeviceInfoQueryProperty> config;

    /**
     * Creates and configures a {@link MethodValidationPostProcessor} bean.
     *
     * <p>
     * This bean enables method-level validation for beans annotated with 
     * validation constraints (e.g., {@code @NotNull}, {@code @Size}, etc.) 
     * in the application context. It ensures that method arguments and 
     * return values are validated according to the specified constraints.
     * </p>
     *
     * @return a configured {@link MethodValidationPostProcessor} instance
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
