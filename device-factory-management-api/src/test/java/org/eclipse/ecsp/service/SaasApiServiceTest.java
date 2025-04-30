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

import org.eclipse.ecsp.common.exception.ApiTechnicalException;
import org.eclipse.ecsp.dto.DeviceTypeMandatoryParam;
import org.eclipse.ecsp.dto.DeviceTypeMandatoryParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for SaasApiService.
 */
@RunWith(MockitoJUnitRunner.class)
public class SaasApiServiceTest {

    @InjectMocks
    SaasApiService saasApiService;

    @Mock
    RestTemplate restTemplate;

    /**
     * run before each test.
     */
    @Before
    public void beforeEach() {
        initMocks(this);

        ReflectionTestUtils.setField(saasApiService, "baseUrl", "https//:localhost:8080");
        ReflectionTestUtils.setField(saasApiService, "baseVersion", "v3");
    }

    @Test
    public void getDeviceTypeMandatoryParamsFromSystemParameter() {

        DeviceTypeMandatoryParam param = new DeviceTypeMandatoryParam();
        param.setDeviceType("dongle");

        List<String> mandatoryParm = new ArrayList<>();
        mandatoryParm.add("imei");
        param.setMandatoryParams(mandatoryParm);
        List<DeviceTypeMandatoryParam> deviceTypeMandatoryParamList = new ArrayList<>();
        deviceTypeMandatoryParamList.add(param);

        DeviceTypeMandatoryParams deviceTypeMandatoryParams =
            new DeviceTypeMandatoryParams(deviceTypeMandatoryParamList);

        ResponseEntity<DeviceTypeMandatoryParams> response = ResponseEntity.ok().body(deviceTypeMandatoryParams);

        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
            Mockito.eq(DeviceTypeMandatoryParams.class))).thenReturn(response);

        saasApiService.getDeviceTypeMandatoryParamsFromSystemParameter();
        Assertions.assertNotNull(response);
    }

    @Test(expected = ApiTechnicalException.class)
    public void getDeviceTypeMandatoryParamsFromSystemParameterWithException() {

        DeviceTypeMandatoryParam param = new DeviceTypeMandatoryParam();
        param.setDeviceType("dongle");

        List<String> mandatoryParm = new ArrayList<>();
        mandatoryParm.add("imei");
        param.setMandatoryParams(mandatoryParm);

        List<DeviceTypeMandatoryParam> deviceTypeMandatoryParamList = new ArrayList<>();
        deviceTypeMandatoryParamList.add(param);

        DeviceTypeMandatoryParams deviceTypeMandatoryParams = new DeviceTypeMandatoryParams(
            deviceTypeMandatoryParamList);

        ResponseEntity<DeviceTypeMandatoryParams> response = ResponseEntity.ok().body(deviceTypeMandatoryParams);

        saasApiService.getDeviceTypeMandatoryParamsFromSystemParameter();
    }
}