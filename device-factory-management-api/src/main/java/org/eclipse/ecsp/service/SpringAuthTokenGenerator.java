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

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.apache.http.entity.ContentType;
import org.eclipse.ecsp.common.CommonConstants;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.common.util.HcpRestClientLibrary;
import org.eclipse.ecsp.config.DeviceInfoQueryProperty;
import org.eclipse.ecsp.dto.AccessTokenDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;

/**
 * The SpringAuthTokenGenerator class is responsible for generating Spring authentication tokens.
 * It fetches the client ID, client secret, and Spring authentication service URL from the environment configuration.
 * The tokens are generated using the client credentials grant type.
 */
@Service
@Lazy
public class SpringAuthTokenGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringAuthTokenGenerator.class);
    @Autowired
    private HcpRestClientLibrary restClientLibrary;
    @Autowired
    private EnvConfig<DeviceInfoQueryProperty> envConfig;
    private String clientId;
    private String clientSecret;
    private String springAuthServiceUrl;

    /**
     * Get the client ID.
     *
     * @return The client ID.
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Set the client ID.
     *
     * @param clientId The client ID to set.
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Fetches the Spring authentication token.
     *
     * @return The authentication token.
     */
    public String fetchSpringAuthToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CommonConstants.HEADER_CONTENT_TYPE_KEY, ContentType.APPLICATION_FORM_URLENCODED.toString());
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        map.add(CommonConstants.GRANT_TYPE_KEY, CommonConstants.SPRING_AUTH_CLIENT_CREDENTIALS);
        map.add(CommonConstants.SPRING_AUTH_CLIENT_ID, clientId);
        map.add(CommonConstants.SPRING_AUTH_CLIENT_SECRET, clientSecret);
        map.add(CommonConstants.SPRING_AUTH_SCOPE_KEY, CommonConstants.SPRING_AUTH_SCOPE_VALUE);

        LOGGER.info("Fetching spring auth token for config: {}", map);
        LOGGER.debug("Fetching spring auth token for config: {}", map);
        LOGGER.debug(" Spring Auth URL: {}", springAuthServiceUrl);
        ResponseEntity<String> response = restClientLibrary.doPost(springAuthServiceUrl, headers, map,
            String.class);
        String token = null;
        if (response.getStatusCode().equals(HttpStatus.OK)) {

            String responseBody = response.getBody();
            ObjectMapper mapper = new ObjectMapper();

            try {
                AccessTokenDetails accessTokenDetails = mapper.readValue(responseBody, AccessTokenDetails.class);
                token = accessTokenDetails.getAccessToken();
                LOGGER.info("Fetched spring auth token successfully");
                LOGGER.debug("Fetched spring auth token successfully");
            } catch (IOException e) {
                LOGGER.error("Exception occurred while parsing spring auth token", e);
            }
        }

        return token;
    }

    /**
     * Load secrets from vault.
     */
    @PostConstruct
    public void loadSecrets() {
        clientId = envConfig.getStringValue(DeviceInfoQueryProperty.SPRING_AUTH_CLIENT_ID).trim();
        clientSecret = envConfig.getStringValue(DeviceInfoQueryProperty.SPRING_AUTH_CLIENT_SECRET).trim();
        LOGGER.info("Fetched the client id and client secret details successfully");
        setClientId(clientId);
        springAuthServiceUrl = envConfig.getStringValue(DeviceInfoQueryProperty.SPRING_AUTH_SERVICE_URL);
        LOGGER.info("Fetched the spring auth url successfully");
    }
}