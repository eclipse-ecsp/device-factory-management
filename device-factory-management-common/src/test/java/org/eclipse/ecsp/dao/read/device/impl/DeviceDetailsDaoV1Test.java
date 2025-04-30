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

package org.eclipse.ecsp.dao.read.device.impl;

import org.eclipse.ecsp.common.rowmapper.DeviceInfoFactoryDataMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceDetailsDaoV1.
 */
public class DeviceDetailsDaoV1Test {

    @InjectMocks
    DeviceDetailsDaoV1 deviceDetailsDaoV1;

    @Mock
    private NamedParameterJdbcTemplate namedParamJdbcTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void constructAndFetchFactoryDataTest() {
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
                Mockito.any(DeviceInfoFactoryDataMapper.class));
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("serialNumber", "1007");
        requestParams.put("imei", "1234");
        requestParams.put("ssid", "ss21");
        requestParams.put("iccid", "9899");
        requestParams.put("msisdn", "+917982");
        requestParams.put("imsi", "6789");
        requestParams.put("bssid", "d9:c7:c8:44:32:40");
        requestParams.put("packageSerialNumber", "k1234");
        deviceDetailsDaoV1.constructAndFetchFactoryData(requestParams);
        Assertions.assertNotNull(
            namedParamJdbcTemplate.query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
                Mockito.any(DeviceInfoFactoryDataMapper.class)));
    }
}
