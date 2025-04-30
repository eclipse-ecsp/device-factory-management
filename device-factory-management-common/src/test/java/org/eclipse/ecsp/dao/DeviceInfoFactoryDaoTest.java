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

package org.eclipse.ecsp.dao;

import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryDataRequest;
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
 * Test class for DeviceInfoFactoryDao.
 */
public class DeviceInfoFactoryDaoTest {
    public static final long ID = 4L;

    @InjectMocks
    DeviceInfoFactoryDao deviceInfoFactoryDao;

    @Mock
    JdbcTemplate jdbcTemplate;

    @Mock
    DeviceInfoFactoryDataDao factoryDao;

    @Mock
    VinDetailsDao vinDetailsDao;

    private DeviceInfoFactoryDataRequest factoryData;

    /**
     * run before each test.
     */
    @Before
    public void beforeEach() {
        initMocks(this);
        factoryData = new DeviceInfoFactoryDataRequest();
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
    }

    @Test
    public void insertData() {

        long factoryDataId = 0;
        DeviceInfoFactoryData factoryDataFromTable = null;
        Mockito.when(
                jdbcTemplate.update(Mockito.any(PreparedStatementCreator.class), Mockito.any(GeneratedKeyHolder.class)))
            .thenAnswer(invocation -> {
                Object[] args = invocation.getArguments();
                Map<String, Object> keyMap = new HashMap<>();
                keyMap.put("ID", ID);
                ((GeneratedKeyHolder) args[1]).getKeyList().add(keyMap);

                return 1;
            }).thenReturn(1);
        Mockito.doNothing().when(vinDetailsDao).insert(factoryDataId, factoryData.getVin());
        Mockito.doReturn(factoryDataFromTable).when(factoryDao).findByFactoryId(factoryDataId);
        Mockito.doReturn(factoryDataFromTable).when(factoryDao).findByFactoryImei(factoryData.getImei());
        String userId = "IgniteSystem";
        deviceInfoFactoryDao.insertData(factoryData, userId);
        Assertions.assertEquals(0, factoryDataId);
    }

    @Test
    public void insertData1() {

        long factoryDataId = 0;
        DeviceInfoFactoryData factoryDataFromTable = null;
        Mockito.when(
                jdbcTemplate.update(Mockito.any(PreparedStatementCreator.class), Mockito.any(GeneratedKeyHolder.class)))
            .thenAnswer(invocation -> {
                Object[] args = invocation.getArguments();
                Map<String, Object> keyMap = new HashMap<>();
                keyMap.put("ID", 0L);
                ((GeneratedKeyHolder) args[1]).getKeyList().add(keyMap);

                return 1;
            }).thenReturn(1);
        Mockito.doNothing().when(vinDetailsDao).insert(factoryDataId, factoryData.getVin());
        Mockito.doReturn(factoryDataFromTable).when(factoryDao).findByFactoryId(factoryDataId);
        Mockito.doReturn(factoryDataFromTable).when(factoryDao).findByFactoryImei(factoryData.getImei());
        String userId = "IgniteSystem";
        deviceInfoFactoryDao.insertData(factoryData, userId);
        Assertions.assertEquals(0, factoryDataId);
    }
}
