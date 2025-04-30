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

package org.eclipse.ecsp.service.read.device;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.common.ResponseConstants;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.common.exception.PageParamResolverException;
import org.eclipse.ecsp.common.exception.SizeParamResolverException;
import org.eclipse.ecsp.common.util.HcpRestClientLibrary;
import org.eclipse.ecsp.config.DeviceInfoQueryProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

/**
 * This abstract class provides a base implementation for the all device details service.
 * It contains common methods and constants used by the concrete implementations.
 *
 * @param <I> the input type for the service
 * @param <O> the output type for the service
 */
public abstract class AbstractAllDeviceDetailsService<I, O> implements IallDeviceDetailsService<I, O> {
    private static final int DEFAULT_PAGE_VALUE = 1;
    private static final int DEFAULT_PAGE_SIZE_PARAM_VALUE = 20;
    private static final int MIN_PAGE_SIZE_PARAM = 1;
    private static final int MAX_PAGE_SIZE_PARAM = 5000;
    /**
     * A constant representing the key for the subscriber ID.
     * This key is used to identify the subscriber in various operations
     * within the device management service.
     */
    public static final String SUBSCRIBER_ID = "subscriberId";

    /**
     * Constant representing the subscription status key.
     * This key is used to identify the status of a subscription in the system.
     */
    public static final String SUBSCRIPTION_STATUS = "status";
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractAllDeviceDetailsService.class);
    @Resource(name = "envConfig")
    protected EnvConfig<DeviceInfoQueryProperty> config;

    @Autowired
    protected HcpRestClientLibrary restClientLibrary;

    /**
     * Resolves the page argument from the given parameter value.
     *
     * @param paramValue the parameter value
     * @return the resolved page value
     * @throws PageParamResolverException if the page value is invalid
     */
    protected int resolvePageArgument(String paramValue) {
        int page = DEFAULT_PAGE_VALUE;
        if (!StringUtils.isEmpty(paramValue)) {
            try {
                page = Integer.parseUnsignedInt(paramValue);
                if (page == 0) {
                    throw new PageParamResolverException(ResponseConstants.ZERO_PAGE_VALUE_TYPE_MESSAGE);
                }
            } catch (NumberFormatException e) {
                LOGGER.debug("Page should be unsigned number: paramValue{}, exception - {}.", paramValue,
                    e.getMessage());
                throw new PageParamResolverException(ResponseConstants.WRONG_PAGE_VALUE_TYPE_MESSAGE);
            }
        }
        return page;
    }

    /**
     * Resolves the size argument from the given parameter value.
     *
     * @param paramValue the parameter value
     * @return the resolved size value
     * @throws SizeParamResolverException if the size value is invalid
     */
    public int resolveSizeArgument(String paramValue) {
        int size = DEFAULT_PAGE_SIZE_PARAM_VALUE;

        if (!StringUtils.isEmpty(paramValue)) {
            try {
                size = Integer.parseUnsignedInt(paramValue);
            } catch (NumberFormatException e) {
                LOGGER.debug("Page size should be unsigned number: paramValue{}, exception - {}.", paramValue,
                    e.getMessage());
                throw new SizeParamResolverException(String.format(ResponseConstants.WRONG_PAGE_SIZE_MESSAGE,
                    MIN_PAGE_SIZE_PARAM, MAX_PAGE_SIZE_PARAM));
            }
        }
        if (size < MIN_PAGE_SIZE_PARAM || size > MAX_PAGE_SIZE_PARAM) {
            LOGGER.debug("page size must be between {} and {}.", MIN_PAGE_SIZE_PARAM, MAX_PAGE_SIZE_PARAM);
            throw new SizeParamResolverException(
                String.format(ResponseConstants.WRONG_PAGE_SIZE_MESSAGE, MIN_PAGE_SIZE_PARAM, MAX_PAGE_SIZE_PARAM));
        }
        return size;
    }

    /**
     * Creates the HTTP headers for the request.
     *
     * @return the created HttpHeaders object
     */
    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return headers;
    }

    /**
     * Gets the value of the specified field from the JSON string.
     *
     * @param jsonStr   the JSON string
     * @param fieldName the name of the field
     * @return the value of the field, or an empty string if not found
     */
    protected String getValue(String jsonStr, String fieldName) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try {
            root = mapper.readTree(jsonStr);
            JsonNode node = root.findValue(fieldName);
            return node != null ? node.asText() : "";
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Returns the subscription URL for the device config management component.
     *
     * @param imei the IMEI of the device
     * @return the subscription URL
     */
    protected String getSubscriptionServiceUrl(String imei) {
        String baseUrl = config.getStringValue(DeviceInfoQueryProperty.DEVICE_CONFIG_MGMT_BASE_URL);
        String baseVersion = config.getStringValue(DeviceInfoQueryProperty.DEVICE_CONFIG_MGMT_BASE_VERSION);

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl).append("/").append(baseVersion).append("/")
            .append("devices").append("/").append(imei)
            .append("/").append("subscription");
        return urlBuilder.toString();
    }
}
