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

import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.common.util.HcpRestClientLibrary;
import org.eclipse.ecsp.config.DeviceInfoQueryProperty;
import org.eclipse.ecsp.dao.create.device.IdeviceFactoryDataDao;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataBaseDto;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataCreateDto;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataDto;
import org.eclipse.ecsp.service.SpringAuthTokenGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;


/**
 * Test class for GuestUserDeviceFactoryDataService.
 */
@RunWith(MockitoJUnitRunner.class)
public class GuestUserDeviceFactoryDataServiceTest {

    public static final String USER_ID = "apitest43";
    public static final String DEFAULT = "default";
    @Mock
    protected EnvConfig<DeviceInfoQueryProperty> config;
    @InjectMocks
    GuestUserDeviceFactoryDataService deviceFactoryDataService;
    @Mock
    IdeviceFactoryDataDao createDeviceDao;

    @Mock
    HcpRestClientLibrary restClientLibrary;

    DeviceFactoryDataDto[] factoryDataDtoRequest;
    DeviceFactoryDataDto factoryData;
    @Mock
    private SpringAuthTokenGenerator springAuthTokenGenerator;

    /**
     * run before each test.
     */
    @Before
    public void beforeEach() {
        initMocks(this);
        factoryDataDtoRequest = new DeviceFactoryDataDto[1];
        factoryData = new DeviceFactoryDataDto();
        factoryData.setManufacturingDate("2019/01/01");
        factoryData.setModel("MX127777");
        factoryData.setImei("9900008624711007");
        factoryData.setSerialNumber("1007");
        factoryData.setPlatformVersion("v1");
        factoryData.setIccid("99911012000032041007");
        factoryData.setSsid("SSID001007");
        factoryData.setBssid("d8:c7:c8:44:32:1007");
        factoryData.setMsisdn("+9190035631007");
        factoryData.setImsi("4100728213931007");
        factoryData.setRecordDate("2019/01/01");
        factoryData.setVin("JN1TAAT32A0XXXXYY");
        factoryDataDtoRequest[0] = factoryData;
    }

    @Test
    public void createDevice() {

        ResponseEntity<String> responseEntity = new ResponseEntity<>("Success", HttpStatus.OK);
        Mockito.doReturn(responseEntity).when(restClientLibrary)
            .doPost(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        DeviceFactoryDataBaseDto deviceFactoryDataBaseDto =
            new DeviceFactoryDataCreateDto(factoryDataDtoRequest, USER_ID, "V3");
        assertTrue(deviceFactoryDataService.createDevice(deviceFactoryDataBaseDto));

    }

    @Test
    public void createDevice_SpringAuth() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("Success", HttpStatus.CREATED);
        Mockito.doReturn(DEFAULT).when(config).getStringValue(DeviceInfoQueryProperty.USER_CREATE_URL_SPRING_AUTH);
        Mockito.doReturn("token").when(springAuthTokenGenerator).fetchSpringAuthToken();
        Mockito.doReturn(responseEntity).when(restClientLibrary)
            .doPost(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        DeviceFactoryDataBaseDto deviceFactoryDataBaseDto =
            new DeviceFactoryDataCreateDto(factoryDataDtoRequest, USER_ID, "V3");
        assertTrue(deviceFactoryDataService.createDevice(deviceFactoryDataBaseDto));

    }

    @Test
    public void createDeviceWithoutVin() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("Success", HttpStatus.OK);
        Mockito.doReturn(responseEntity).when(restClientLibrary)
            .doPost(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        DeviceFactoryDataBaseDto deviceFactoryDataBaseDto =
            new DeviceFactoryDataCreateDto(factoryDataDtoRequest, USER_ID, "V3");
        assertTrue(deviceFactoryDataService.createDevice(deviceFactoryDataBaseDto));

    }

    @Test
    public void createDeviceWithBadResponse() {

        ResponseEntity<String> responseEntity = new ResponseEntity<>("Success", HttpStatus.BAD_REQUEST);
        Mockito.doReturn(responseEntity).when(restClientLibrary)
            .doPost(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        DeviceFactoryDataBaseDto deviceFactoryDataBaseDto =
            new DeviceFactoryDataCreateDto(factoryDataDtoRequest, USER_ID, "V3");
        assertTrue(deviceFactoryDataService.createDevice(deviceFactoryDataBaseDto));

    }

}
