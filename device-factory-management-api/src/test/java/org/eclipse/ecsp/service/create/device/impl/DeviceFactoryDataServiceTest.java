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

package org.eclipse.ecsp.service.create.device.impl;

import org.eclipse.ecsp.common.exception.ApiValidationFailedException;
import org.eclipse.ecsp.dao.create.device.IdeviceFactoryDataDao;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataBaseDto;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * DeviceFactoryDataServiceTest.
 */
public class DeviceFactoryDataServiceTest {

    DeviceFactoryDataDto[] factoryDataDtoRequest;
    DeviceFactoryDataDto factoryData;
    @InjectMocks
    private DeviceFactoryDataService deviceFactoryDataService;
    @Mock
    private IdeviceFactoryDataDao createDeviceDao;

    /**
     * run before each test.
     */
    @Before
    public void beforeEach() {
        initMocks(this);

        ReflectionTestUtils.setField(deviceFactoryDataService, "allowedTypes", new String[]{"dongle"});
        ReflectionTestUtils.setField(deviceFactoryDataService, "allowedRegions", new String[]{"TH"});

        factoryDataDtoRequest = new DeviceFactoryDataDto[1];
        factoryData = new DeviceFactoryDataDto();
        factoryData.setManufacturingDate("2019/01/01");
        factoryData.setModel("MX127777");
        factoryData.setImei("9900008624711007");
        factoryData.setSerialNumber("1007");
        factoryData.setPlatformVersion("v3");
        factoryData.setIccid("99911012000032041007");
        factoryData.setSsid("SSID001007");
        factoryData.setBssid("d8:c7:c8:44:32:1007");
        factoryData.setMsisdn("+9190035631007");
        factoryData.setImsi("4100728213931007");
        factoryData.setRecordDate("2019/01/01");
        factoryData.setDeviceType("dongle");
        factoryData.setRegion("TH");
        factoryDataDtoRequest[0] = factoryData;
    }

    @Test
    public void createDeviceTest() {

        DeviceFactoryDataBaseDto dto = new DeviceFactoryDataBaseDto() {
            @Override
            public DeviceFactoryDataDto[] getDeviceFactoryDataDtos() {
                return super.getDeviceFactoryDataDtos();
            }
        };

        dto.setDeviceFactoryDataDtos(factoryDataDtoRequest);
        dto.setUserId("xyz");
        dto.setVersion("V1");

        Mockito.doReturn(false).when(createDeviceDao).createDevice(Mockito.any(), Mockito.anyString());
        Assertions.assertTrue(deviceFactoryDataService.createDevice(dto));
    }

    @Test
    public void createDeviceTest_isDeviceCreated() {

        DeviceFactoryDataBaseDto dto = new DeviceFactoryDataBaseDto() {
            @Override
            public DeviceFactoryDataDto[] getDeviceFactoryDataDtos() {
                return super.getDeviceFactoryDataDtos();
            }
        };

        dto.setDeviceFactoryDataDtos(factoryDataDtoRequest);
        dto.setUserId("xyz");
        dto.setVersion("V1");

        Mockito.doReturn(true).when(createDeviceDao).createDevice(Mockito.any(), Mockito.anyString());
        Assertions.assertTrue(deviceFactoryDataService.createDevice(dto));
    }

    @Test(expected = ApiValidationFailedException.class)
    public void createDeviceTestInvalidAllowedType() {

        factoryData.setDeviceType("dongle1");

        DeviceFactoryDataBaseDto dto = new DeviceFactoryDataBaseDto() {
            @Override
            public DeviceFactoryDataDto[] getDeviceFactoryDataDtos() {
                return super.getDeviceFactoryDataDtos();
            }
        };

        dto.setDeviceFactoryDataDtos(factoryDataDtoRequest);
        dto.setUserId("xyz");
        dto.setVersion("V3");

        Mockito.doReturn(true).when(createDeviceDao).createDevice(Mockito.any(), Mockito.anyString());
        deviceFactoryDataService.createDevice(dto);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void createDeviceTestInvalidAllowedRegions() {

        factoryData.setRegion("TH1");

        DeviceFactoryDataBaseDto dto = new DeviceFactoryDataBaseDto() {
            @Override
            public DeviceFactoryDataDto[] getDeviceFactoryDataDtos() {
                return super.getDeviceFactoryDataDtos();
            }
        };

        dto.setDeviceFactoryDataDtos(factoryDataDtoRequest);
        dto.setUserId("xyz");
        dto.setVersion("V3");

        Mockito.doReturn(true).when(createDeviceDao).createDevice(Mockito.any(), Mockito.anyString());
        deviceFactoryDataService.createDevice(dto);
    }

}
