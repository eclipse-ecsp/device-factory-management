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

import lombok.extern.slf4j.Slf4j;
import org.eclipse.ecsp.common.exception.ControllerExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

/**
 * Configuration class for the HcpDeviceInfoQueryWebappWebConfig.
 * This class enables web MVC and provides configuration for handling exceptions and argument resolvers.
 */
@Configuration
@EnableWebMvc
@Slf4j
public class HcpDeviceInfoQueryWebappWebConfig implements WebMvcConfigurer {
    /**
     * Adds custom argument resolvers to the list of existing argument resolvers.
     *
     * @param argumentResolvers the list of existing argument resolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        // addArgumentResolvers.
    }

    /**
     * Configure the exception resolvers for handling exceptions in controllers.
     *
     * @param exceptionResolvers a list of exception resolvers.
     */
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        //Enabling handling of unhandled exception in controllers
        exceptionResolvers.add(new ControllerExceptionHandler());
    }


    //@Bean
    //public ViewResolver viewResolver() {
    //UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
    //viewResolver.setViewClass(JstlView.class);
    //viewResolver.setPrefix("/WEB-INF/jsp/");
    //viewResolver.setSuffix(".jsp");
    //return viewResolver;
    //}
    //
    //
    //@Bean
    //public CommonsMultipartResolver multipartResolver() {
    //CommonsMultipartResolver resolver = new CommonsMultipartResolver();
    //resolver.setMaxUploadSize(1000000);
    //resolver.setMaxInMemorySize(1000000);
    //return resolver;
    //
    //}
}