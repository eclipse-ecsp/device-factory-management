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

import org.eclipse.ecsp.common.rowmapper.DeviceInfoFactoryDataMapperWithSubscription;
import org.eclipse.ecsp.common.rowmapper.DeviceStateAggregateDataMapper;
import org.eclipse.ecsp.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.dto.DeviceInfoAggregateFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryDataWithSubscription;
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
 * Test class for DeviceDetailsDaoV4.
 */
public class DeviceDetailsDaoV4Test {

    @InjectMocks
    DeviceDetailsDaoV4 deviceDetailsDaoV4;
    List<DeviceStateAggregateData> deviceStateMap;
    @Mock
    private NamedParameterJdbcTemplate namedParamJdbcTemplate;

    /**
     * run before each test.
     */
    @Before
    public void beforeEach() {
        initMocks(this);

        DeviceStateAggregateData data = new DeviceStateAggregateData();
        data.setState("PROVISIONED");
        data.setCount(1L);
        deviceStateMap = new ArrayList<>();
        deviceStateMap.add(data);
    }

    @Test
    public void constructFetchTotalFactoryDataForDeviceDetailsTest_Imei() {
        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class), Mockito.eq(Long.class));
        Long constructCount = deviceDetailsDaoV4.constructFetchTotalFactoryDataForDeviceDetails(
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.IMEI, "1234");
        Assertions.assertEquals(1, constructCount);
    }

    @Test
    public void constructFetchTotalFactoryDataForDeviceDetailsTest_SerialNumber() {
        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class), Mockito.eq(Long.class));
        Long constructCount = deviceDetailsDaoV4.constructFetchTotalFactoryDataForDeviceDetails(
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.SERIAL_NUMBER, "123456");
        Assertions.assertEquals(1, constructCount);
    }

    @Test
    public void constructFetchTotalFactoryDataForDeviceDetailsTest_DeviceId() {
        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class), Mockito.eq(Long.class));
        Long constructCount = deviceDetailsDaoV4.constructFetchTotalFactoryDataForDeviceDetails(
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.DEVICE_ID, "har126");
        Assertions.assertEquals(1, constructCount);
    }

    @Test
    public void constructFetchFactoryDataTest_Imei() {
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
                Mockito.any(DeviceInfoFactoryDataMapperWithSubscription.class));
        List<DeviceInfoFactoryDataWithSubscription> deviceInfoFactoryDataWithSubscriptionList =
            deviceDetailsDaoV4.constructFetchFactoryData(DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.IMEI,
                "1234", 1, 1, null, null);
        Assertions.assertNull(deviceInfoFactoryDataWithSubscriptionList);
    }

    @Test
    public void constructFetchFactoryDataTest_SerialNumber() {
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
                Mockito.any(DeviceInfoFactoryDataMapperWithSubscription.class));
        List<DeviceInfoFactoryDataWithSubscription> deviceInfoFactoryDataWithSubscriptionList =
            deviceDetailsDaoV4.constructFetchFactoryData(
                DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.SERIAL_NUMBER, "123456", 1, 1, null, null);
        Assertions.assertNull(deviceInfoFactoryDataWithSubscriptionList);
    }

    @Test
    public void constructFetchFactoryDataTest_DeviceId() {
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
                Mockito.any(DeviceInfoFactoryDataMapperWithSubscription.class));
        List<DeviceInfoFactoryDataWithSubscription> deviceInfoFactoryDataWithSubscriptionList =
            deviceDetailsDaoV4.constructFetchFactoryData(DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.DEVICE_ID,
                "123456", 1, 1, null, null);
        Assertions.assertNull(deviceInfoFactoryDataWithSubscriptionList);
    }

    @Test
    public void constructFetchFactoryDataTest_State() {
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
                Mockito.any(DeviceInfoFactoryDataMapperWithSubscription.class));
        List<DeviceInfoFactoryDataWithSubscription> deviceInfoFactoryDataWithSubscriptionList =
            deviceDetailsDaoV4.constructFetchFactoryData(DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.STATE,
                "PROVISIONED", 1, 1, null, null);
        Assertions.assertNull(deviceInfoFactoryDataWithSubscriptionList);
    }

    @Test
    public void constructFetchFactoryDataTest() {
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
                Mockito.any(DeviceInfoFactoryDataMapperWithSubscription.class));
        List<DeviceInfoFactoryDataWithSubscription> deviceInfoFactoryDataWithSubscriptionList =
            deviceDetailsDaoV4.constructFetchFactoryData(DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.VIN,
                "PROVISIONED", 1, 1, null, null);
        Assertions.assertNull(deviceInfoFactoryDataWithSubscriptionList);
    }

    @Test
    public void constructFetchAggregateFactoryDataTest_Imei() {
        Mockito.doReturn(deviceStateMap).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class),
                Mockito.any(DeviceStateAggregateDataMapper.class));
        DeviceInfoAggregateFactoryData.StateCount stateCount = deviceDetailsDaoV4.constructFetchAggregateFactoryData(
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.IMEI, "1234");
        Assertions.assertNotNull(stateCount);
    }

    @Test
    public void constructFetchAggregateFactoryDataTest_SerialNumber() {
        Mockito.doReturn(deviceStateMap).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class),
                Mockito.any(DeviceStateAggregateDataMapper.class));
        DeviceInfoAggregateFactoryData.StateCount stateCount = deviceDetailsDaoV4.constructFetchAggregateFactoryData(
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.SERIAL_NUMBER, "1234");
        Assertions.assertNotNull(stateCount);
    }

    @Test
    public void constructFetchAggregateFactoryDataTest_State() {
        Mockito.doReturn(deviceStateMap).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class),
                Mockito.any(DeviceStateAggregateDataMapper.class));
        DeviceInfoAggregateFactoryData.StateCount stateCount = deviceDetailsDaoV4.constructFetchAggregateFactoryData(
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.STATE, "PROVISIONED");
        Assertions.assertNotNull(stateCount);
    }

    @Test
    public void constructFetchAggregateFactoryDataTest_DeviceId() {
        Mockito.doReturn(deviceStateMap).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class),
                Mockito.any(DeviceStateAggregateDataMapper.class));
        DeviceInfoAggregateFactoryData.StateCount stateCount = deviceDetailsDaoV4.constructFetchAggregateFactoryData(
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.DEVICE_ID, "har123");
        Assertions.assertNotNull(stateCount);
    }
}
