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

import org.eclipse.ecsp.common.exception.DeviceNotFoundException;
import org.eclipse.ecsp.common.exception.InputParamValidationException;
import org.eclipse.ecsp.dao.read.device.IdeviceDetailsDaoV2;
import org.eclipse.ecsp.dto.DeviceInfoAggregateFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.read.device.DeviceDetailsDtoV2;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for AllDeviceDetailsServiceV2.
 */
public class AllDeviceDetailsServiceV2Test {

    public static final long RETURN_VALUE = 10L;

    @InjectMocks
    private AllDeviceDetailsServiceV2 allDeviceDetailsServiceV2;

    @Mock
    private IdeviceDetailsDaoV2 deviceInfoQueryFindDaoV2;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findAllFactoryDataTest_invalidAsc() {

        DeviceDetailsDtoV2 dto = new DeviceDetailsDtoV2("1", "1", null, null, "true", "12234", "34566");
        dto.setSortBy("imei1");

        allDeviceDetailsServiceV2.findAllFactoryData(dto, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void findAllFactoryDataTest_invalidDesc() {

        DeviceDetailsDtoV2 dto = new DeviceDetailsDtoV2("1", "1", null, null, "true", "12234", "34566");
        dto.setOrderBy("imei1");

        allDeviceDetailsServiceV2.findAllFactoryData(dto, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void findAllFactoryDataTest_invalidIsDetailsRequired() {

        DeviceDetailsDtoV2 dto = new DeviceDetailsDtoV2("1", "1", null, null, "true123", "12234", "34566");
        dto.setDesc("imei");

        allDeviceDetailsServiceV2.findAllFactoryData(dto, "");
    }

    @Test(expected = InputParamValidationException.class)
    public void findAllFactoryDataTest_invalidImei() {

        DeviceDetailsDtoV2 dto = new DeviceDetailsDtoV2("1", "1", null, null, "true",
            "12234a", "34566");
        dto.setAsc("imei");

        allDeviceDetailsServiceV2.findAllFactoryData(dto, "");
    }

    @Test(expected = InputParamValidationException.class)
    public void findAllFactoryDataTest_shortImei() {

        DeviceDetailsDtoV2 dto = new DeviceDetailsDtoV2("1", "1", null, null, "true",
            "12", "34566");
        dto.setAsc("imei");

        allDeviceDetailsServiceV2.findAllFactoryData(dto, "");
    }

    @Test
    public void findAllFactoryDataTest() {

        DeviceDetailsDtoV2 dto = new DeviceDetailsDtoV2("1", "1", null, null, "true",
            "1223", "34566");
        dto.setAsc("imei");

        Mockito.doReturn(RETURN_VALUE).when(deviceInfoQueryFindDaoV2)
            .constructFetchTotalFactoryData(Mockito.anyString(), Mockito.anyString());

        DeviceInfoAggregateFactoryData.StateCount st = new DeviceInfoAggregateFactoryData.StateCount();
        st.setProvisioned(1L);
        st.setStolen(1L);
        st.setFaulty(1L);
        st.setActive(1L);

        Mockito.doReturn(st).when(deviceInfoQueryFindDaoV2)
            .constructFetchAggregateDeviceState(Mockito.anyString(), Mockito.anyString());
        allDeviceDetailsServiceV2.findAllFactoryData(dto, "");
        Assertions.assertEquals(st,
            deviceInfoQueryFindDaoV2.constructFetchAggregateDeviceState(Mockito.anyString(), Mockito.anyString()));
    }

    @Test
    public void findAllFactoryDataTest_withIsDetailsRequired() {

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
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
        List<DeviceInfoFactoryData> factoryDataList = new ArrayList<>();
        factoryDataList.add(deviceInfoFactoryData);

        DeviceInfoAggregateFactoryData.StateCount st = new DeviceInfoAggregateFactoryData.StateCount();
        st.setProvisioned(1L);
        st.setStolen(1L);
        st.setFaulty(1L);
        st.setActive(1L);

        DeviceDetailsDtoV2 dto = new DeviceDetailsDtoV2("1", "1", null, null, "true", "9900008624711007", "1007");
        dto.setAsc("imei");

        Mockito.doReturn(RETURN_VALUE).when(deviceInfoQueryFindDaoV2)
            .constructFetchTotalFactoryData(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(factoryDataList).when(deviceInfoQueryFindDaoV2)
            .constructFetchFactoryData(1, 1, null, null, "1007", "9900008624711007");
        Mockito.doReturn(st).when(deviceInfoQueryFindDaoV2)
            .constructFetchAggregateDeviceState(Mockito.anyString(), Mockito.anyString());
        allDeviceDetailsServiceV2.findAllFactoryData(dto, "");
        Assertions.assertEquals(st,
            deviceInfoQueryFindDaoV2.constructFetchAggregateDeviceState(Mockito.anyString(), Mockito.anyString()));
    }

    @Test(expected = DeviceNotFoundException.class)
    public void findAllFactoryDataTest_nullTotalInvalidSerialNumber() {

        String imei = "";
        String serialNumber = "1007";
        DeviceDetailsDtoV2 dto = new DeviceDetailsDtoV2("1", "1", null, null, "true", imei, serialNumber);
        dto.setAsc("imei");

        Mockito.doReturn(null).when(deviceInfoQueryFindDaoV2)
            .constructFetchTotalFactoryData(Mockito.anyString(), Mockito.anyString());
        allDeviceDetailsServiceV2.findAllFactoryData(dto, "");
    }

    @Test(expected = DeviceNotFoundException.class)
    public void findAllFactoryDataTest_ZeroTotalInvalidSerialNumber() {

        String imei = "";
        String serialNumber = "1007";
        DeviceDetailsDtoV2 dto = new DeviceDetailsDtoV2("1", "1", null, null, "true", imei, serialNumber);
        dto.setAsc("imei");

        Mockito.doReturn(0L).when(deviceInfoQueryFindDaoV2)
            .constructFetchTotalFactoryData(Mockito.anyString(), Mockito.anyString());
        allDeviceDetailsServiceV2.findAllFactoryData(dto, "");
    }

    @Test(expected = DeviceNotFoundException.class)
    public void findAllFactoryDataTest_nullTotalInvalidImei() {

        DeviceDetailsDtoV2 dto = new DeviceDetailsDtoV2("1", "1", null, null, "true", "9900008624711007", "");
        dto.setAsc("imei");

        Mockito.doReturn(null).when(deviceInfoQueryFindDaoV2)
            .constructFetchTotalFactoryData(Mockito.anyString(), Mockito.anyString());
        allDeviceDetailsServiceV2.findAllFactoryData(dto, "");
    }

    @Test(expected = DeviceNotFoundException.class)
    public void findAllFactoryDataTest_nullTotalInvalidParameter() {

        DeviceDetailsDtoV2 dto = new DeviceDetailsDtoV2("1", "1", null, null, "true", "9900008624711007", "1007");
        dto.setAsc("imei");

        Mockito.doReturn(null).when(deviceInfoQueryFindDaoV2)
            .constructFetchTotalFactoryData(Mockito.anyString(), Mockito.anyString());
        allDeviceDetailsServiceV2.findAllFactoryData(dto, "");
    }
}
