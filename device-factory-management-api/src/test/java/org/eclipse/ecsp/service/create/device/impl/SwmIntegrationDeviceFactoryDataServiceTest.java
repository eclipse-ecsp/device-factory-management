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
import org.eclipse.ecsp.common.exception.ApiTechnicalException;
import org.eclipse.ecsp.config.DeviceInfoQueryProperty;
import org.eclipse.ecsp.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.dao.VinDetailsDao;
import org.eclipse.ecsp.dao.create.device.IdeviceFactoryDataDao;
import org.eclipse.ecsp.dto.DeviceTypeMandatoryParam;
import org.eclipse.ecsp.dto.DeviceTypeMandatoryParams;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataBaseDto;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataDto;
import org.eclipse.ecsp.dto.swm.SwmRequest;
import org.eclipse.ecsp.service.SaasApiService;
import org.eclipse.ecsp.service.swm.IswmCrudService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for SwmIntegrationDeviceFactoryDataService.
 */
@RunWith(MockitoJUnitRunner.class)
public class SwmIntegrationDeviceFactoryDataServiceTest {


    public static final String DEFAULT = "true";
    @InjectMocks
    SwmIntegrationDeviceFactoryDataService swmIntegrationDeviceFactoryDataService;
    @Mock
    IdeviceFactoryDataDao createDeviceDao;
    @Mock
    VinDetailsDao vinDetailsDao;
    @Mock
    IswmCrudService<SwmRequest> swmService;
    @Mock
    SaasApiService saasApiService;
    DeviceFactoryDataDto[] factoryDataDtoRequest;
    DeviceFactoryDataDto factoryData;
    @Mock
    EnvConfig<DeviceInfoQueryProperty> config;

    @Mock
    DeviceInfoFactoryDataDao deviceInfoFactoryDataDao;

    /**
     * run before each test.
     */
    @Before
    public void beforeEach() {
        initMocks(this);

        ReflectionTestUtils.setField(swmIntegrationDeviceFactoryDataService, "allowedTypes", new String[]{"dongle"});

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
        factoryData.setDeviceType("dongle");
        factoryDataDtoRequest[0] = factoryData;
    }

    @Test(expected = ApiTechnicalException.class)
    public void createDeviceWithException() {
        DeviceFactoryDataBaseDto dto = new DeviceFactoryDataBaseDto() {
            @Override
            public DeviceFactoryDataDto[] getDeviceFactoryDataDtos() {
                return super.getDeviceFactoryDataDtos();
            }
        };

        dto.setDeviceFactoryDataDtos(factoryDataDtoRequest);
        dto.setUserId("xyz");
        dto.setVersion("V3");
        List<DeviceTypeMandatoryParam> deviceTypeMandatoryParamList = new ArrayList<>();
        DeviceTypeMandatoryParam mandatoryParam = new DeviceTypeMandatoryParam();
        mandatoryParam.setDeviceType(null);
        DeviceTypeMandatoryParams deviceTypeMandatoryParams =
            new DeviceTypeMandatoryParams(deviceTypeMandatoryParamList);
        Mockito.doReturn(deviceTypeMandatoryParams).when(saasApiService)
            .getDeviceTypeMandatoryParamsFromSystemParameter();
        swmIntegrationDeviceFactoryDataService.createDevice(dto);
    }

    @Test
    public void createDevice() {
        DeviceFactoryDataBaseDto dto = new DeviceFactoryDataBaseDto() {
            @Override
            public DeviceFactoryDataDto[] getDeviceFactoryDataDtos() {
                return super.getDeviceFactoryDataDtos();
            }
        };

        dto.setDeviceFactoryDataDtos(factoryDataDtoRequest);
        dto.setUserId("xyz");
        dto.setVersion("V3");

        DeviceTypeMandatoryParam param = new DeviceTypeMandatoryParam();
        param.setDeviceType("dongle");

        List<String> mandatoryParm = new ArrayList<>();
        mandatoryParm.add("imei");
        param.setMandatoryParams(mandatoryParm);

        List<DeviceTypeMandatoryParam> deviceTypeMandatoryParamList = new ArrayList<>();
        deviceTypeMandatoryParamList.add(param);

        DeviceTypeMandatoryParams deviceTypeMandatoryParams =
            new DeviceTypeMandatoryParams(deviceTypeMandatoryParamList);
        Mockito.doReturn(deviceTypeMandatoryParams).when(saasApiService)
            .getDeviceTypeMandatoryParamsFromSystemParameter();
        assertEquals(deviceTypeMandatoryParams, saasApiService.getDeviceTypeMandatoryParamsFromSystemParameter());
    }

    @Test
    public void createDeviceWithswmIntegrationEnabledFalse() {
        DeviceFactoryDataBaseDto dto = new DeviceFactoryDataBaseDto() {
            @Override
            public DeviceFactoryDataDto[] getDeviceFactoryDataDtos() {
                return super.getDeviceFactoryDataDtos();
            }
        };

        dto.setDeviceFactoryDataDtos(factoryDataDtoRequest);
        dto.setUserId("xyz");
        dto.setVersion("V3");

        DeviceTypeMandatoryParam param = new DeviceTypeMandatoryParam();
        param.setDeviceType("dongle");

        List<String> mandatoryParm = new ArrayList<>();
        mandatoryParm.add("imei");
        param.setMandatoryParams(mandatoryParm);

        List<DeviceTypeMandatoryParam> deviceTypeMandatoryParamList = new ArrayList<>();
        deviceTypeMandatoryParamList.add(param);

        DeviceTypeMandatoryParams deviceTypeMandatoryParams =
            new DeviceTypeMandatoryParams(deviceTypeMandatoryParamList);
        Mockito.doReturn(deviceTypeMandatoryParams).when(saasApiService)
            .getDeviceTypeMandatoryParamsFromSystemParameter();
        Mockito.doReturn("false").when(config).getStringValue(DeviceInfoQueryProperty.SWM_INTEGRATION_ENABLED);
        swmIntegrationDeviceFactoryDataService.createDevice(dto);
        assertEquals(deviceTypeMandatoryParams, saasApiService.getDeviceTypeMandatoryParamsFromSystemParameter());
    }

    @Test
    public void createDeviceWithoutVin() {
        DeviceFactoryDataDto factoryData1 = new DeviceFactoryDataDto();
        factoryData1.setManufacturingDate("2019/01/01");
        factoryData1.setModel("MX127777");
        factoryData1.setImei("9900008624711007");
        factoryData1.setSerialNumber("1007");
        factoryData1.setPlatformVersion("v1");
        factoryData1.setIccid("99911012000032041007");
        factoryData1.setSsid("SSID001007");
        factoryData1.setBssid("d8:c7:c8:44:32:1007");
        factoryData1.setMsisdn("+9190035631007");
        factoryData1.setImsi("4100728213931007");
        factoryData1.setRecordDate("2019/01/01");
        factoryData1.setVin(null);
        factoryData1.setDeviceType("dongle");
        DeviceFactoryDataDto[] factoryDataDtoRequest1 = new DeviceFactoryDataDto[1];
        factoryDataDtoRequest1[0] = factoryData1;
        DeviceFactoryDataBaseDto dto = new DeviceFactoryDataBaseDto() {
            @Override
            public DeviceFactoryDataDto[] getDeviceFactoryDataDtos() {
                return super.getDeviceFactoryDataDtos();
            }
        };

        dto.setDeviceFactoryDataDtos(factoryDataDtoRequest1);
        dto.setUserId("xyz");
        dto.setVersion("V3");

        DeviceTypeMandatoryParam param = new DeviceTypeMandatoryParam();
        param.setDeviceType("dongle");

        List<String> mandatoryParm = new ArrayList<>();
        mandatoryParm.add("imei");
        param.setMandatoryParams(mandatoryParm);

        List<DeviceTypeMandatoryParam> deviceTypeMandatoryParamList = new ArrayList<>();
        deviceTypeMandatoryParamList.add(param);

        DeviceTypeMandatoryParams deviceTypeMandatoryParams =
            new DeviceTypeMandatoryParams(deviceTypeMandatoryParamList);
        Mockito.doReturn(deviceTypeMandatoryParams).when(saasApiService)
            .getDeviceTypeMandatoryParamsFromSystemParameter();
        assertEquals(deviceTypeMandatoryParams, saasApiService.getDeviceTypeMandatoryParamsFromSystemParameter());
    }

    @Test
    public void createDeviceWithSwmIntegrationEnabledTrue() {
        DeviceFactoryDataBaseDto dto = new DeviceFactoryDataBaseDto() {
            @Override
            public DeviceFactoryDataDto[] getDeviceFactoryDataDtos() {
                return super.getDeviceFactoryDataDtos();
            }
        };

        dto.setDeviceFactoryDataDtos(factoryDataDtoRequest);
        dto.setUserId("xyz");
        dto.setVersion("V3");

        DeviceTypeMandatoryParam param = new DeviceTypeMandatoryParam();
        param.setDeviceType("dongle");

        List<String> mandatoryParm = new ArrayList<>();
        mandatoryParm.add("imei");
        param.setMandatoryParams(mandatoryParm);

        List<DeviceTypeMandatoryParam> deviceTypeMandatoryParamList = new ArrayList<>();
        deviceTypeMandatoryParamList.add(param);

        DeviceTypeMandatoryParams deviceTypeMandatoryParams =
            new DeviceTypeMandatoryParams(deviceTypeMandatoryParamList);
        Mockito.doReturn(deviceTypeMandatoryParams).when(saasApiService)
            .getDeviceTypeMandatoryParamsFromSystemParameter();
        Mockito.doReturn("true").when(config).getStringValue(DeviceInfoQueryProperty.SWM_INTEGRATION_ENABLED);
        swmIntegrationDeviceFactoryDataService.createDevice(dto);
        assertEquals(deviceTypeMandatoryParams, saasApiService.getDeviceTypeMandatoryParamsFromSystemParameter());
    }

}
