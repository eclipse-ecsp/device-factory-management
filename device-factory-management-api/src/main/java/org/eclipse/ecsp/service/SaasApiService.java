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

import org.eclipse.ecsp.common.CommonConstants;
import org.eclipse.ecsp.common.exception.ApiTechnicalException;
import org.eclipse.ecsp.dto.DeviceTypeMandatoryParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.eclipse.ecsp.common.enums.ApiMessageEnum.SYSTEM_PARAMETERS_API_FAILURE;

/**
 * This class represents the SaasApiService, which is responsible for interacting with the SaaS API.
 */
@Service
public class SaasApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaasApiService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${saas_api_base_url}")
    private String baseUrl;

    @Value("${saas_api_base_url_version}")
    private String baseVersion;

    /**
     * Creates the HTTP headers for the API requests.
     *
     * @return The HttpHeaders object containing the headers.
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CommonConstants.ACCEPT, CommonConstants.APPLICATION_JSON);
        return headers;
    }

    /**
     * Retrieves the device type mandatory parameters from the SystemParameter.
     *
     * @return The DeviceTypeMandatoryParams object containing the mandatory parameters.
     * @throws ApiTechnicalException If there is an error while retrieving the parameters.
     */
    public DeviceTypeMandatoryParams getDeviceTypeMandatoryParamsFromSystemParameter() throws ApiTechnicalException {
        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        String systemParamsUrl = getSystemParamsUrlForSystemParamsKey(CommonConstants.DEVICE_TYPE_MANDATORY_PARAMS);
        LOGGER.info("## systemParamsUrl: {}", systemParamsUrl);
        try {
            ResponseEntity<DeviceTypeMandatoryParams> response =
                restTemplate.exchange(systemParamsUrl, HttpMethod.GET, entity, DeviceTypeMandatoryParams.class);
            LOGGER.debug("system param api responseBody: {} ", response.getBody());
            return response.getBody();
        } catch (Exception e) {
            throw new ApiTechnicalException(SYSTEM_PARAMETERS_API_FAILURE.getCode(),
                SYSTEM_PARAMETERS_API_FAILURE.getMessage(),
                SYSTEM_PARAMETERS_API_FAILURE.getGeneralMessage(), e);
        }
    }

    /**
     * Constructs the system parameters URL for the given system parameter keys.
     *
     * @param systemParamKeys The system parameter keys.
     * @return The constructed system parameters URL.
     */
    private String getSystemParamsUrlForSystemParamsKey(String systemParamKeys) {
        return baseUrl + CommonConstants.URL_SEPARATOR + baseVersion + CommonConstants.URL_SEPARATOR
            + CommonConstants.SYS_PARAMS + CommonConstants.QUESTION_MARK + CommonConstants.SYS_PARAM_KEYS
            + CommonConstants.EQUAL + systemParamKeys;
    }
}