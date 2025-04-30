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

package org.eclipse.ecsp.service.read.device.impl;

import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.common.exception.DeviceNotFoundException;
import org.eclipse.ecsp.common.util.HcpRestClientLibrary;
import org.eclipse.ecsp.config.DeviceInfoQueryProperty;
import org.eclipse.ecsp.dao.read.device.IdeviceDetailsDaoV5;
import org.eclipse.ecsp.dto.DeviceInfoAggregateFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryDataWithSubscription;
import org.eclipse.ecsp.dto.read.device.DeviceDetailsDtoV5;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for AllDeviceDetailsServiceV5.
 */
public class AllDeviceDetailsServiceV5Test {
    public static final long RETURN_VALUE = 10L;
    
    @Mock
    protected IdeviceDetailsDaoV5 deviceDetailsDao;
    @Mock
    protected HcpRestClientLibrary restClientLibrary;
    @Mock
    protected EnvConfig<DeviceInfoQueryProperty> config;
    List<DeviceInfoFactoryDataWithSubscription> factoryDataList;
    DeviceInfoFactoryDataWithSubscription deviceInfoFactoryData;
    @InjectMocks
    private AllDeviceDetailsServiceV5 allDeviceDetailsServiceV5;

    /**
     * run before each test.
     */
    @Before
    public void beforeEach() {
        initMocks(this);
        factoryDataList = new ArrayList<>();
        deviceInfoFactoryData = new DeviceInfoFactoryDataWithSubscription();
        deviceInfoFactoryData.setManufacturingDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        deviceInfoFactoryData.setModel("MX127777");
        deviceInfoFactoryData.setImei("9900008624711007");
        deviceInfoFactoryData.setSerialNumber("1007");
        deviceInfoFactoryData.setPlatformVersion("v1");
        deviceInfoFactoryData.setIccid("99911012000032041007");
        deviceInfoFactoryData.setSsid("SSID001007");
        deviceInfoFactoryData.setBssid("d8:c7:c8:44:32:1007");
        deviceInfoFactoryData.setMsisdn("+9190035631007");
        deviceInfoFactoryData.setImsi("4100728213931007");
        deviceInfoFactoryData.setRecordDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        deviceInfoFactoryData.setSubscriberId("xyz");
        deviceInfoFactoryData.setSubscriptionStatus("Active");
        factoryDataList.add(deviceInfoFactoryData);
    }

    @Test(expected = DeviceNotFoundException.class)
    public void findAllFactoryDataTest_throwException() {

        DeviceDetailsDtoV5 dto = new DeviceDetailsDtoV5("1", "1", null, null, null, null, null, null, null);

        Mockito.doReturn(0L).when(deviceDetailsDao)
            .constructFetchTotalFactoryDataForDeviceDetails(Mockito.any(), Mockito.any());
        Mockito.doReturn(false).when(config)
            .getBooleanValue(DeviceInfoQueryProperty.ENABLE_DEVICE_SUBSCRIPTION_DETAILS);
        allDeviceDetailsServiceV5.findAllFactoryData(dto, "9900008624711007");
    }

    @Test
    public void findAllFactoryDataTest() {

        DeviceInfoAggregateFactoryData.StateCount st = new DeviceInfoAggregateFactoryData.StateCount();
        st.setActive(0L);
        st.setFaulty(0L);

        Mockito.doReturn(st).when(deviceDetailsDao).constructFetchAggregateFactoryData(Mockito.any(), Mockito.any());
        Mockito.when(
            deviceDetailsDao.constructFetchFactoryData(Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.any(), Mockito.any())).thenReturn(factoryDataList);
        Mockito.doReturn(RETURN_VALUE).when(deviceDetailsDao)
            .constructFetchTotalFactoryDataForDeviceDetails(Mockito.any(), Mockito.any());
        Mockito.doReturn(true).when(config).getBooleanValue(DeviceInfoQueryProperty.ENABLE_DEVICE_SUBSCRIPTION_DETAILS);

        DeviceDetailsDtoV5 dto = new DeviceDetailsDtoV5("1", "1", null, null, null, null, null, null, null);
        allDeviceDetailsServiceV5.findAllFactoryData(dto, "9900008624711007");
        Assertions.assertEquals(RETURN_VALUE,
            deviceDetailsDao.constructFetchTotalFactoryDataForDeviceDetails(Mockito.any(), Mockito.any()));
    }

    @Test
    public void findAllFactoryDataTest_enableDeviceSubscriptionDetails() {

        DeviceInfoAggregateFactoryData.StateCount st = new DeviceInfoAggregateFactoryData.StateCount();
        st.setActive(0L);
        st.setFaulty(0L);

        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);

        Mockito.doReturn(response).when(restClientLibrary)
            .doGet(Mockito.any(), Mockito.any(), Mockito.eq(String.class));
        Mockito.doReturn(st).when(deviceDetailsDao).constructFetchAggregateFactoryData(Mockito.any(), Mockito.any());
        Mockito.when(
            deviceDetailsDao.constructFetchFactoryData(Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.any(), Mockito.any())).thenReturn(factoryDataList);
        Mockito.doReturn(RETURN_VALUE).when(deviceDetailsDao)
            .constructFetchTotalFactoryDataForDeviceDetails(Mockito.any(), Mockito.any());
        Mockito.doReturn(true).when(config).getBooleanValue(DeviceInfoQueryProperty.ENABLE_DEVICE_SUBSCRIPTION_DETAILS);

        DeviceDetailsDtoV5 dto = new DeviceDetailsDtoV5("1", "1", null, null, null, null, null, null, null);
        allDeviceDetailsServiceV5.findAllFactoryData(dto, "9900008624711007");
        Assertions.assertEquals(RETURN_VALUE,
            deviceDetailsDao.constructFetchTotalFactoryDataForDeviceDetails(Mockito.any(), Mockito.any()));
    }

}
