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
import org.eclipse.ecsp.dao.read.device.IdeviceDetailsDaoV1;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.read.device.DeviceDetailsDtoV1;
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
 * Test class for DeviceDetailsServiceV1.
 */
public class DeviceDetailsServiceV1Test {

    @Mock
    protected IdeviceDetailsDaoV1 deviceInfoQueryFindDaoV1;
    @InjectMocks
    private DeviceDetailsServiceV1 detailsServiceV1;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test(expected = DeviceNotFoundException.class)
    public void findFactoryDataTest_emptyInput() {

        DeviceDetailsDtoV1 dto = new DeviceDetailsDtoV1("", "", "", "", "", "", "", "");
        detailsServiceV1.findFactoryData(dto);
    }

    @Test(expected = DeviceNotFoundException.class)
    public void findFactoryDataTest_emptyFactoryDataList() {

        List<DeviceInfoFactoryData> factoryDataList = new ArrayList<>();

        Mockito.doReturn(factoryDataList).when(deviceInfoQueryFindDaoV1).constructAndFetchFactoryData(Mockito.anyMap());
        DeviceDetailsDtoV1 dto =
            new DeviceDetailsDtoV1("1234", "1122", "ssid1", "98994306", "+91989943", "798240553", "d9:c7:c8:44:32:40",
                "k1234");
        detailsServiceV1.findFactoryData(dto);
    }

    @Test
    public void findFactoryDataTest() {

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

        Mockito.doReturn(factoryDataList).when(deviceInfoQueryFindDaoV1).constructAndFetchFactoryData(Mockito.anyMap());
        DeviceDetailsDtoV1 dto =
            new DeviceDetailsDtoV1("1234", "1122", "ssid1", "98994306", "+91989943", "798240553", "d9:c7:c8:44:32:40",
                "k1234");
        detailsServiceV1.findFactoryData(dto);
        Assertions.assertEquals(factoryDataList,
            deviceInfoQueryFindDaoV1.constructAndFetchFactoryData(Mockito.anyMap()));
    }

    @Test
    public void findFactoryDataTest_emptySno() {

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

        Mockito.doReturn(factoryDataList).when(deviceInfoQueryFindDaoV1).constructAndFetchFactoryData(Mockito.anyMap());
        DeviceDetailsDtoV1 dto =
            new DeviceDetailsDtoV1("", "1122", "ssid1", "98994306", "+91989943", "798240553", "d9:c7:c8:44:32:40",
                "k1234");
        detailsServiceV1.findFactoryData(dto);
        Assertions.assertEquals(factoryDataList,
            deviceInfoQueryFindDaoV1.constructAndFetchFactoryData(Mockito.anyMap()));
    }

    @Test
    public void findFactoryDataTest_emptyImei() {

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

        Mockito.doReturn(factoryDataList).when(deviceInfoQueryFindDaoV1).constructAndFetchFactoryData(Mockito.anyMap());
        DeviceDetailsDtoV1 dto =
            new DeviceDetailsDtoV1("1234", "", "ssid1", "98994306", "+91989943", "798240553", "d9:c7:c8:44:32:40",
                "k1234");
        detailsServiceV1.findFactoryData(dto);
        Assertions.assertEquals(factoryDataList,
            deviceInfoQueryFindDaoV1.constructAndFetchFactoryData(Mockito.anyMap()));
    }

    @Test
    public void findFactoryDataTest_emptySsid() {

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

        Mockito.doReturn(factoryDataList).when(deviceInfoQueryFindDaoV1).constructAndFetchFactoryData(Mockito.anyMap());
        DeviceDetailsDtoV1 dto =
            new DeviceDetailsDtoV1("1234", "1122", "", "98994306", "+91989943", "798240553", "d9:c7:c8:44:32:40",
                "k1234");
        detailsServiceV1.findFactoryData(dto);
        Assertions.assertEquals(factoryDataList,
            deviceInfoQueryFindDaoV1.constructAndFetchFactoryData(Mockito.anyMap()));
    }

    @Test
    public void findFactoryDataTest_emptyIccid() {

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

        Mockito.doReturn(factoryDataList).when(deviceInfoQueryFindDaoV1).constructAndFetchFactoryData(Mockito.anyMap());
        DeviceDetailsDtoV1 dto =
            new DeviceDetailsDtoV1("1234", "1122", "ssid1", "", "+91989943", "798240553", "d9:c7:c8:44:32:40", "k1234");
        detailsServiceV1.findFactoryData(dto);
        Assertions.assertEquals(factoryDataList,
            deviceInfoQueryFindDaoV1.constructAndFetchFactoryData(Mockito.anyMap()));
    }

    @Test
    public void findFactoryDataTest_emptyMsisdn() {

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

        Mockito.doReturn(factoryDataList).when(deviceInfoQueryFindDaoV1).constructAndFetchFactoryData(Mockito.anyMap());
        DeviceDetailsDtoV1 dto =
            new DeviceDetailsDtoV1("1234", "1122", "ssid1", "98994306", "", "798240553", "d9:c7:c8:44:32:40", "k1234");
        detailsServiceV1.findFactoryData(dto);
        Assertions.assertEquals(factoryDataList,
            deviceInfoQueryFindDaoV1.constructAndFetchFactoryData(Mockito.anyMap()));
    }

    @Test
    public void findFactoryDataTest_emptyImsi() {

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

        Mockito.doReturn(factoryDataList).when(deviceInfoQueryFindDaoV1).constructAndFetchFactoryData(Mockito.anyMap());
        DeviceDetailsDtoV1 dto =
            new DeviceDetailsDtoV1("1234", "1122", "ssid1", "98994306", "+91989943", "", "d9:c7:c8:44:32:40", "k1234");
        detailsServiceV1.findFactoryData(dto);
        Assertions.assertEquals(factoryDataList,
            deviceInfoQueryFindDaoV1.constructAndFetchFactoryData(Mockito.anyMap()));
    }

    @Test
    public void findFactoryDataTest_emptyBssid() {

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

        Mockito.doReturn(factoryDataList).when(deviceInfoQueryFindDaoV1).constructAndFetchFactoryData(Mockito.anyMap());
        DeviceDetailsDtoV1 dto =
            new DeviceDetailsDtoV1("1234", "1122", "ssid1", "98994306", "+91989943", "798240553", "", "k1234");
        detailsServiceV1.findFactoryData(dto);
        Assertions.assertEquals(factoryDataList,
            deviceInfoQueryFindDaoV1.constructAndFetchFactoryData(Mockito.anyMap()));
    }

    @Test
    public void findFactoryDataTest_emptyPkgSno() {

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

        Mockito.doReturn(factoryDataList).when(deviceInfoQueryFindDaoV1).constructAndFetchFactoryData(Mockito.anyMap());
        DeviceDetailsDtoV1 dto =
            new DeviceDetailsDtoV1("1234", "1122", "ssid1", "98994306", "+91989943", "798240553", "d9:c7:c8:44:32:40",
                "");
        detailsServiceV1.findFactoryData(dto);
        Assertions.assertEquals(factoryDataList,
            deviceInfoQueryFindDaoV1.constructAndFetchFactoryData(Mockito.anyMap()));
    }
}
