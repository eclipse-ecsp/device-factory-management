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

package org.eclipse.ecsp.dao.create.device.impl;

import org.eclipse.ecsp.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.dao.VinDetailsDao;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for SwmIntegrationVehicleFactoryDataDao.
 */
public class SwmIntegrationVehicleFactoryDataDaoTest {
    public static final long ID = 4L;
    public static final int FACTORY_ID = 46;

    @InjectMocks
    SwmIntegrationVehicleFactoryDataDao swmIntegrationVehicleFactoryDataDao;

    @Mock
    VinDetailsDao vinDetailsDao;

    @Mock
    DeviceInfoFactoryDataDao factoryDao;

    @Mock
    JdbcTemplate jdbcTemplate;

    DeviceFactoryDataDto factoryDatadto;

    /**
     * run before each test.
     */
    @Before
    public void beforeEach() {
        initMocks(this);
        factoryDatadto = new DeviceFactoryDataDto();
        factoryDatadto.setVin("JN1TAAT32A0XXXXYY");
        factoryDatadto.setManufacturingDate("2019/01/01");
        factoryDatadto.setModel("MX127777");
        factoryDatadto.setImei("9900008624711007");
        factoryDatadto.setSerialNumber("1007");
        factoryDatadto.setPlatformVersion("v1");
        factoryDatadto.setIccid("99911012000032041007");
        factoryDatadto.setSsid("SSID001007");
        factoryDatadto.setBssid("d8:c7:c8:44:32:1007");
        factoryDatadto.setMsisdn("+9190035631007");
        factoryDatadto.setImsi("4100728213931007");
        factoryDatadto.setRecordDate("2019/01/01");
    }

    @Test
    public void createDevice() {
        String userId = "IgniteSystem";
        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();

        Mockito.when(
                jdbcTemplate.update(Mockito.any(PreparedStatementCreator.class), Mockito.any(GeneratedKeyHolder.class)))
            .thenAnswer(invocation -> {
                Object[] args = invocation.getArguments();
                Map<String, Object> keyMap = new HashMap<>();
                keyMap.put("ID", ID);
                ((GeneratedKeyHolder) args[1]).getKeyList().add(keyMap);

                return 1;
            }).thenReturn(1);

        Mockito.doReturn(deviceInfoFactoryData).when(factoryDao).findByFactoryId(FACTORY_ID);
        Mockito.doNothing().when(vinDetailsDao).insert(0L, userId);
        swmIntegrationVehicleFactoryDataDao.createDevice(factoryDatadto, userId);
        Assertions.assertTrue(swmIntegrationVehicleFactoryDataDao.createDevice(factoryDatadto, userId));
    }

}
