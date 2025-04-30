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
import org.eclipse.ecsp.common.rowmapper.DeviceStateAggregateDataMapper;
import org.eclipse.ecsp.dto.DeviceInfoAggregateFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.DeviceStateAggregateData;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceDetailsDaoV2.
 */
public class DeviceDetailsDaoV2Test {

    @InjectMocks
    DeviceDetailsDaoV2 deviceDetailsDaoV2;

    @Mock
    private NamedParameterJdbcTemplate namedParamJdbcTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void constructFetchTotalFactoryDataTest() {

        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class), Mockito.eq(Long.class));
        Long deviceCount = deviceDetailsDaoV2.constructFetchTotalFactoryData("12345", "1234");
        Assertions.assertEquals(1, deviceCount);
    }

    @Test
    public void constructFetchTotalFactoryDataTest_emptyImei() {

        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class), Mockito.eq(Long.class));
        Long deviceCount = deviceDetailsDaoV2.constructFetchTotalFactoryData("12345", "");
        Assertions.assertEquals(1, deviceCount);
    }

    @Test
    public void constructFetchFactoryDataTest() {

        DeviceInfoFactoryData deviceInfo = new DeviceInfoFactoryData();
        List<DeviceInfoFactoryData> deviceInfoList = new ArrayList<>();
        deviceInfoList.add(deviceInfo);
        String asc = null;
        String desc = null;
        Mockito.doReturn(deviceInfoList).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
                Mockito.any(DeviceInfoFactoryDataMapper.class));
        List<DeviceInfoFactoryData> actualDeviceInfoList =
            deviceDetailsDaoV2.constructFetchFactoryData(1, 1, asc, desc, "12345", "1234");
        Assertions.assertEquals(deviceInfoList, actualDeviceInfoList);
    }

    @Test
    public void constructFetchFactoryDataTest_withAsc() {

        DeviceInfoFactoryData deviceInfo = new DeviceInfoFactoryData();
        List<DeviceInfoFactoryData> deviceInfoList = new ArrayList<>();
        deviceInfoList.add(deviceInfo);

        Mockito.doReturn(deviceInfoList).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
                Mockito.any(DeviceInfoFactoryDataMapper.class));
        List<DeviceInfoFactoryData> actualDeviceInfoList =
            deviceDetailsDaoV2.constructFetchFactoryData(1, 1, "imei", null, "12345", "1234");
        Assertions.assertEquals(deviceInfoList, actualDeviceInfoList);
    }

    @Test
    public void constructFetchFactoryDataTest_withDesc() {

        DeviceInfoFactoryData deviceInfo = new DeviceInfoFactoryData();
        List<DeviceInfoFactoryData> deviceInfoList = new ArrayList<>();
        deviceInfoList.add(deviceInfo);

        Mockito.doReturn(deviceInfoList).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
                Mockito.any(DeviceInfoFactoryDataMapper.class));
        List<DeviceInfoFactoryData> actualDeviceInfoList =
            deviceDetailsDaoV2.constructFetchFactoryData(1, 1, null, "imei", "12345", "1234");
        Assertions.assertEquals(deviceInfoList, actualDeviceInfoList);
    }

    @Test
    public void constructFetchAggregateDeviceStateTest() {
        DeviceStateAggregateData data = new DeviceStateAggregateData();
        data.setState("PROVISIONED");
        data.setCount(1L);
        List<DeviceStateAggregateData> deviceStateMap = new ArrayList<>();
        deviceStateMap.add(data);


        Mockito.doReturn(deviceStateMap).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class),
                Mockito.any(DeviceStateAggregateDataMapper.class));
        DeviceInfoAggregateFactoryData.StateCount stateCount =
            deviceDetailsDaoV2.constructFetchAggregateDeviceState("12345", "1234");
        Assertions.assertNotNull(stateCount);
    }
}
