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

import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.config.DeviceInfoQueryProperty;
import org.eclipse.ecsp.dao.DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum;
import org.eclipse.ecsp.dto.DeviceInfoAggregateFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryDataWithSubscription;
import org.eclipse.ecsp.dto.DeviceStateAggregateData;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceDetailsDaoV5.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceDetailsDaoV5Test {
    public static final long ID = 3L;
    public static final String DEFAULT = "swmIntegration";
    @Mock
    protected EnvConfig<DeviceInfoQueryProperty> config;
    @InjectMocks
    DeviceDetailsDaoV5 deviceDetailsDaoV5;
    @Mock
    private NamedParameterJdbcTemplate namedParamJdbcTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void constructFetchTotalFactoryDataForDeviceDetailsImei() {
        Assertions.assertNull(
            deviceDetailsDaoV5.constructFetchTotalFactoryDataForDeviceDetails(DeviceDetailsInputTypeEnum.IMEI,
                "008545093400"));
    }

    @Test
    public void constructFetchTotalFactoryDataForDeviceDetailsSerialNumber() {
        Assertions.assertNull(
            deviceDetailsDaoV5.constructFetchTotalFactoryDataForDeviceDetails(DeviceDetailsInputTypeEnum.SERIAL_NUMBER,
                "9696"));
    }

    @Test
    public void constructFetchTotalFactoryDataForDeviceDetailsDeviceId() {
        Assertions.assertNull(
            deviceDetailsDaoV5.constructFetchTotalFactoryDataForDeviceDetails(DeviceDetailsInputTypeEnum.DEVICE_ID,
                "46"));
    }

    @Test
    public void constructFetchTotalFactoryDataForDeviceDetailsVin() {
        Assertions.assertNull(
            deviceDetailsDaoV5.constructFetchTotalFactoryDataForDeviceDetails(DeviceDetailsInputTypeEnum.VIN,
                "JN1TAAT32A0XXXXYY"));
    }

    @Test
    public void constructFetchTotalFactoryDataForDeviceDetailsState() {
        Assertions.assertNull(
            deviceDetailsDaoV5.constructFetchTotalFactoryDataForDeviceDetails(DeviceDetailsInputTypeEnum.STATE,
                "ACTIVE"));
    }

    @Test
    public void constructFetchFactoryDataImei() {
        String searchKey = "";
        int sizeValue = 1;
        int pageValue = 1;
        String sortBy = "state";
        String orderBy = "ASC";
        Mockito.doReturn(DEFAULT).when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        List<DeviceInfoFactoryDataWithSubscription> deviceInfoFactoryDataWithSubscriptionList =
            deviceDetailsDaoV5.constructFetchFactoryData(DeviceDetailsInputTypeEnum.IMEI, searchKey, sizeValue,
                pageValue, sortBy, orderBy);
        Assertions.assertNotNull(deviceInfoFactoryDataWithSubscriptionList);
    }

    @Test
    public void constructFetchFactoryDataSerialNumber() {
        String searchKey = "";
        int sizeValue = 1;
        int pageValue = 1;
        String sortBy = "state";
        String orderBy = "ASC";
        Mockito.doReturn(DEFAULT).when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        List<DeviceInfoFactoryDataWithSubscription> deviceInfoFactoryDataWithSubscriptionList =
            deviceDetailsDaoV5.constructFetchFactoryData(DeviceDetailsInputTypeEnum.SERIAL_NUMBER, searchKey, sizeValue,
                pageValue, sortBy, orderBy);
        Assertions.assertNotNull(deviceInfoFactoryDataWithSubscriptionList);
    }

    @Test
    public void constructFetchFactoryDataDeviceId() {
        String searchKey = "";
        int sizeValue = 1;
        int pageValue = 1;
        String sortBy = "state";
        String orderBy = "ASC";
        Mockito.doReturn(DEFAULT).when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        List<DeviceInfoFactoryDataWithSubscription> deviceInfoFactoryDataWithSubscriptionList =
            deviceDetailsDaoV5.constructFetchFactoryData(DeviceDetailsInputTypeEnum.DEVICE_ID, searchKey, sizeValue,
                pageValue, sortBy, orderBy);
        Assertions.assertNotNull(deviceInfoFactoryDataWithSubscriptionList);
    }

    @Test
    public void constructFetchFactoryDataVin() {
        String searchKey = "";
        int sizeValue = 1;
        int pageValue = 1;
        String sortBy = "state";
        String orderBy = "ASC";
        Mockito.doReturn(DEFAULT).when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        List<DeviceInfoFactoryDataWithSubscription> deviceInfoFactoryDataWithSubscriptionList =
            deviceDetailsDaoV5.constructFetchFactoryData(DeviceDetailsInputTypeEnum.VIN, searchKey, sizeValue,
                pageValue, sortBy, orderBy);
        Assertions.assertNotNull(deviceInfoFactoryDataWithSubscriptionList);
    }

    @Test
    public void constructFetchFactoryDataState() {
        String searchKey = "";
        int sizeValue = 1;
        int pageValue = 1;
        String sortBy = "state";
        String orderBy = "ASC";
        Mockito.doReturn(DEFAULT).when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        List<DeviceInfoFactoryDataWithSubscription> deviceInfoFactoryDataWithSubscriptionList =
            deviceDetailsDaoV5.constructFetchFactoryData(DeviceDetailsInputTypeEnum.STATE, searchKey, sizeValue,
                pageValue, sortBy, orderBy);
        Assertions.assertNotNull(deviceInfoFactoryDataWithSubscriptionList);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void constructFetchAggregateFactoryDataImei() {
        List<DeviceStateAggregateData> deviceStateMap = new ArrayList<>();
        DeviceStateAggregateData aggregateData = new DeviceStateAggregateData();
        aggregateData.setState("ACTIVE");
        aggregateData.setCount(ID);
        deviceStateMap.add(aggregateData);
        String searchKey = "";
        DeviceInfoAggregateFactoryData.StateCount stateCount =
            deviceDetailsDaoV5.constructFetchAggregateFactoryData(DeviceDetailsInputTypeEnum.IMEI, searchKey);
        Assertions.assertNotNull(stateCount);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void constructFetchAggregateFactoryDataDeviceId() {
        List<DeviceStateAggregateData> deviceStateMap = new ArrayList<>();
        DeviceStateAggregateData aggregateData = new DeviceStateAggregateData();
        aggregateData.setState("ACTIVE");
        aggregateData.setCount(ID);
        deviceStateMap.add(aggregateData);
        String searchKey = "";
        DeviceInfoAggregateFactoryData.StateCount stateCount =
            deviceDetailsDaoV5.constructFetchAggregateFactoryData(DeviceDetailsInputTypeEnum.DEVICE_ID, searchKey);
        Assertions.assertNotNull(stateCount);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void constructFetchAggregateFactoryDataSerialNumber() {
        List<DeviceStateAggregateData> deviceStateMap = new ArrayList<>();
        DeviceStateAggregateData aggregateData = new DeviceStateAggregateData();
        aggregateData.setState("ACTIVE");
        aggregateData.setCount(ID);
        deviceStateMap.add(aggregateData);
        String searchKey = "";
        DeviceInfoAggregateFactoryData.StateCount stateCount =
            deviceDetailsDaoV5.constructFetchAggregateFactoryData(DeviceDetailsInputTypeEnum.SERIAL_NUMBER, searchKey);
        Assertions.assertNotNull(stateCount);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void constructFetchAggregateFactoryDataState() {
        List<DeviceStateAggregateData> deviceStateMap = new ArrayList<>();
        DeviceStateAggregateData aggregateData = new DeviceStateAggregateData();
        aggregateData.setState("ACTIVE");
        aggregateData.setCount(ID);
        deviceStateMap.add(aggregateData);
        String searchKey = "";
        DeviceInfoAggregateFactoryData.StateCount stateCount =
            deviceDetailsDaoV5.constructFetchAggregateFactoryData(DeviceDetailsInputTypeEnum.STATE, searchKey);
        Assertions.assertNotNull(stateCount);
    }

}
