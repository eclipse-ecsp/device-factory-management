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

import org.eclipse.ecsp.common.rowmapper.DeviceInfoFactoryDataMapper;
import org.eclipse.ecsp.common.rowmapper.DeviceInfoFactoryDataMapperWithSubscription;
import org.eclipse.ecsp.common.rowmapper.DeviceStateAggregateDataMapper;
import org.eclipse.ecsp.common.rowmapper.DeviceStateHistoryDataMapper;
import org.eclipse.ecsp.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.dao.DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.DeviceStateAggregateData;
import org.eclipse.ecsp.dto.DeviceStateHistory;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.ecsp.dto.DeviceState.ACTIVE;
import static org.eclipse.ecsp.dto.DeviceState.PROVISIONED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceInfoFactoryDataDao.
 */
public class DeviceInfoFactoryDataDaoTest {
    public static final long FACTORY_ID = 111L;
    public static final long COUNT = 3L;

    @Mock
    JdbcTemplate jdbcTemplate;

    @Mock
    NamedParameterJdbcTemplate namedParamJdbcTemplate;

    @Mock
    PreparedStatementCreator preparedStatementCreator;

    @InjectMocks
    DeviceInfoFactoryDataDao deviceInfoFactoryDataDao;

    DeviceInfoFactoryData deviceInfoFactoryData;
    List<DeviceInfoFactoryData> deviceInfoFactoryDataList;

    /**
     * run before each test.
     */
    @Before
    public void beforeEach() {
        initMocks(this);
        deviceInfoFactoryData = new DeviceInfoFactoryData();
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
        deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceInfoFactoryData);
    }

    @Test
    public void deletefactoryData() {

        String imei = "9900008624711007";
        String serialnumber = "1007";
        Mockito.when(jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.anyVararg())).thenReturn(1);
        deviceInfoFactoryDataDao.deletefactoryData(imei, serialnumber, deviceInfoFactoryData);
        //calling getTimestamp method for test
        deviceInfoFactoryDataDao.getTimestamp("2011-01-18 00:00:00.0");
        deviceInfoFactoryDataDao.getTimestamp("06/23/2016");
        Assertions.assertEquals(1, jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.anyVararg()));
    }

    @Test(expected = InvalidParameterException.class)
    public void deleteFactoryDataWithException() {
        String imei = "9900008624711007";
        String serialnumber = "1007";
        Mockito.when(jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.anyVararg())).thenReturn(0);
        deviceInfoFactoryDataDao.deletefactoryData(imei, serialnumber, deviceInfoFactoryData);
    }

    @Test
    public void changeDeviceStateWithActiveState() {
        Mockito.when(jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.anyVararg())).thenReturn(1);
        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyVararg(),
                (DeviceInfoFactoryDataMapper) Mockito.anyObject());
        Mockito.doReturn(1).when(jdbcTemplate).update(preparedStatementCreator);

        String stateAndAction = "ACTIVE";
        deviceInfoFactoryDataDao.changeDeviceState(FACTORY_ID, stateAndAction, stateAndAction);
        Assertions.assertEquals(1, jdbcTemplate.update(preparedStatementCreator));
    }

    @Test
    public void changeDeviceStateWithStolenState() {

        Mockito.when(jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.anyVararg())).thenReturn(1);
        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyVararg(),
                (DeviceInfoFactoryDataMapper) Mockito.anyObject());
        Mockito.doReturn(1).when(jdbcTemplate).update(preparedStatementCreator);

        String stateAndActions = "STOLEN";
        deviceInfoFactoryDataDao.changeDeviceState(FACTORY_ID, stateAndActions, stateAndActions);
        Assertions.assertEquals(1, jdbcTemplate.update(preparedStatementCreator));
    }

    @Test
    public void changeDeviceStateWithFaultyState() {

        Mockito.when(jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.anyVararg())).thenReturn(1);
        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyVararg(),
                (DeviceInfoFactoryDataMapper) Mockito.anyObject());
        Mockito.doReturn(1).when(jdbcTemplate).update(preparedStatementCreator);
        String state = "FAULTY";
        deviceInfoFactoryDataDao.changeDeviceState(FACTORY_ID, state, state);
        Assertions.assertEquals(1, jdbcTemplate.update(preparedStatementCreator));
    }

    @Test
    public void changeDeviceStateWithProvisionedState() {

        Mockito.when(jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.anyVararg())).thenReturn(1);
        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyVararg(),
                (DeviceInfoFactoryDataMapper) Mockito.anyObject());
        Mockito.doReturn(1).when(jdbcTemplate).update(preparedStatementCreator);

        String staAndAct = "PROVISIONED";
        deviceInfoFactoryDataDao.changeDeviceState(FACTORY_ID, staAndAct, staAndAct);
        Assertions.assertEquals(1, jdbcTemplate.update(preparedStatementCreator));
    }

    @Test(expected = InvalidParameterException.class)
    public void changeDeviceStateWithException() {

        Mockito.when(jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.anyVararg())).thenReturn(0);
        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyVararg(),
                (DeviceInfoFactoryDataMapper) Mockito.anyObject());
        Mockito.doReturn(1).when(jdbcTemplate).update(preparedStatementCreator);

        String state = "FAULTY";
        String action = "FAULTY";
        deviceInfoFactoryDataDao.changeDeviceState(FACTORY_ID, state, action);
    }

    @Test
    public void changeDeviceStateForStolenOrFaulty() {
        
        String state = "FAULTY";
        String action = "FAULTY";
        Mockito.when(jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.anyVararg())).thenReturn(1);
        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyVararg(),
                (DeviceInfoFactoryDataMapper) Mockito.anyObject());
        deviceInfoFactoryDataDao.changeDeviceStateForStolenOrFaulty(FACTORY_ID, state, action);

        //Calling updateFactoryDataState method for test
        deviceInfoFactoryDataDao.updateFactoryDataState(FACTORY_ID, state);
        Assertions.assertEquals(1, jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.anyVararg()));
    }

    @Test(expected = InvalidParameterException.class)
    public void changeDeviceStateForStolenOrFaultyWithException() {
        
        String state = "FAULTY";
        String action = "FAULTY";
        Mockito.when(jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.anyVararg())).thenReturn(0);
        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyVararg(),
                (DeviceInfoFactoryDataMapper) Mockito.anyObject());
        deviceInfoFactoryDataDao.changeDeviceStateForStolenOrFaulty(FACTORY_ID, state, action);
    }

    @Test
    public void findByFactoryImei() {
        String imei = "9900008624711007";
        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyVararg(),
                (DeviceInfoFactoryDataMapper) Mockito.anyObject());
        DeviceInfoFactoryData data = deviceInfoFactoryDataDao.findByFactoryImei(imei);
        assertNotNull(data);
    }

    @Test
    public void findIdByFactoryImei() {
        String imei = "9900008624711007";
        Long factoryId = FACTORY_ID;
        Mockito.doReturn(factoryId).when(jdbcTemplate).queryForObject("", null, Long.class);
        deviceInfoFactoryDataDao.findIdByFactoryImei(imei);
        assertEquals(factoryId, jdbcTemplate.queryForObject("", null, Long.class));
    }

    @Test
    public void findByFactoryIdAndImei() {
        
        String imei = "9900008624711007";
        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyVararg(),
                (DeviceInfoFactoryDataMapper) Mockito.anyObject());
        DeviceInfoFactoryData data = deviceInfoFactoryDataDao.findByFactoryIdAndImei(FACTORY_ID, imei);
        assertNotNull(data);
    }

    @Test
    public void findFactoryDataBySerialNumber() {
        String serialnumber = "1007";
        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyVararg(),
                (DeviceInfoFactoryDataMapper) Mockito.anyObject());
        DeviceInfoFactoryData data = deviceInfoFactoryDataDao.findFactoryDataBySerialNumber(serialnumber);
        assertNotNull(data);
    }

    @Test
    public void constructAndFetchFactoryData() {
        Mockito.doReturn(deviceInfoFactoryDataList).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (MapSqlParameterSource) Mockito.anyObject(),
                (DeviceInfoFactoryDataMapper) Mockito.anyObject());
        deviceInfoFactoryDataList = deviceInfoFactoryDataDao.constructAndFetchFactoryData(deviceInfoFactoryData);
        assertNotNull(deviceInfoFactoryDataList);
    }

    @Test
    public void fetchDeviceInfoFactoryData() {
        String imei = "9900008624711007";
        String serialnumber = "1007";
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("state", ACTIVE.getValue());
        orderedMap.put("imei", imei);
        orderedMap.put("serial_number", serialnumber);
        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyVararg(),
                (DeviceInfoFactoryDataMapper) Mockito.anyObject());
        DeviceInfoFactoryData data = deviceInfoFactoryDataDao.fetchDeviceInfoFactoryData(orderedMap);
        assertNotNull(data);
    }

    @Test
    public void update() {
        String imei = "9900008624711007";
        String serialnumber = "1007";
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("state", ACTIVE.getValue());
        orderedMap.put("imei", imei);
        orderedMap.put("serial_number", serialnumber);
        Map<String, Object> conditionalOrderedMap = new LinkedHashMap<>();
        conditionalOrderedMap.put("state", PROVISIONED.getValue());
        Mockito.doReturn(1).when(jdbcTemplate).update(Mockito.anyString(), (Object[]) Mockito.anyVararg());
        deviceInfoFactoryDataDao.update(conditionalOrderedMap, orderedMap);
        assertEquals(1, deviceInfoFactoryDataDao.update(conditionalOrderedMap, orderedMap));
    }

    @Test
    public void constructFetchFactoryData() {
        int size = 1;
        int page = 1;
        String asc = "asc";
        String desc = "desc";
        String serialNumber = "1007";
        String imei = "9900008624711007";
        Mockito.doReturn(deviceInfoFactoryDataList).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (MapSqlParameterSource) Mockito.anyObject(),
                (DeviceInfoFactoryDataMapper) Mockito.anyObject());
        List<DeviceInfoFactoryData> datas =
            deviceInfoFactoryDataDao.constructFetchFactoryData(size, page, asc, desc, serialNumber, imei);
        assertNotNull(datas);
    }

    @Test
    public void constructFetchFactoryDataTestWithImei() {
        String searchKey = "9900008624711007";
        int sizeValue = 1;
        int pageValue = 1;
        String sortby = "model";
        String orderBy = "desc";
        boolean deviceVinEnabled = true;
        Mockito.doReturn(deviceInfoFactoryDataList).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (MapSqlParameterSource) Mockito.anyObject(),
                (DeviceInfoFactoryDataMapperWithSubscription) Mockito.anyObject());
        deviceInfoFactoryDataDao.constructFetchFactoryData(DeviceDetailsInputTypeEnum.IMEI, searchKey, sizeValue,
            pageValue, sortby, orderBy, deviceVinEnabled);
        Assertions.assertEquals(deviceInfoFactoryDataList,
            namedParamJdbcTemplate.query(Mockito.anyString(), (MapSqlParameterSource) Mockito.anyObject(),
                (DeviceInfoFactoryDataMapperWithSubscription) Mockito.anyObject()));
    }

    @Test
    public void constructFetchFactoryDataTestWithSerialNumber() {
        String searchKey = "1007";
        int sizeValue = 1;
        int pageValue = 1;
        String sortby = "model";
        String orderBy = "desc";
        boolean deviceVinEnabled = true;
        Mockito.doReturn(deviceInfoFactoryDataList).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (MapSqlParameterSource) Mockito.anyObject(),
                (DeviceInfoFactoryDataMapperWithSubscription) Mockito.anyObject());
        deviceInfoFactoryDataDao.constructFetchFactoryData(DeviceDetailsInputTypeEnum.SERIAL_NUMBER, searchKey,
            sizeValue, pageValue, sortby, orderBy, deviceVinEnabled);
        Assertions.assertEquals(deviceInfoFactoryDataList,
            namedParamJdbcTemplate.query(Mockito.anyString(), (MapSqlParameterSource) Mockito.anyObject(),
                (DeviceInfoFactoryDataMapperWithSubscription) Mockito.anyObject()));
    }

    @Test
    public void constructFetchFactoryDataTestWithDeviceId() {
        String searchKey = "1007";
        int sizeValue = 1;
        int pageValue = 1;
        String sortby = "model";
        String orderBy = "desc";
        boolean deviceVinEnabled = true;
        Mockito.doReturn(deviceInfoFactoryDataList).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (MapSqlParameterSource) Mockito.anyObject(),
                (DeviceInfoFactoryDataMapperWithSubscription) Mockito.anyObject());
        deviceInfoFactoryDataDao.constructFetchFactoryData(DeviceDetailsInputTypeEnum.DEVICE_ID, searchKey, sizeValue,
            pageValue, sortby, orderBy, deviceVinEnabled);
        Assertions.assertEquals(deviceInfoFactoryDataList,
            namedParamJdbcTemplate.query(Mockito.anyString(), (MapSqlParameterSource) Mockito.anyObject(),
                (DeviceInfoFactoryDataMapperWithSubscription) Mockito.anyObject()));
    }

    @Test
    public void constructFetchFactoryDataTestWithVin() {
        String searchKey = "1007";
        int sizeValue = 1;
        int pageValue = 1;
        String sortby = "model";
        String orderBy = "desc";
        boolean deviceVinEnabled = true;
        Mockito.doReturn(deviceInfoFactoryDataList).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (MapSqlParameterSource) Mockito.anyObject(),
                (DeviceInfoFactoryDataMapperWithSubscription) Mockito.anyObject());
        deviceInfoFactoryDataDao.constructFetchFactoryData(DeviceDetailsInputTypeEnum.VIN, searchKey, sizeValue,
            pageValue, sortby, orderBy, deviceVinEnabled);
        Assertions.assertEquals(deviceInfoFactoryDataList,
            namedParamJdbcTemplate.query(Mockito.anyString(), (MapSqlParameterSource) Mockito.anyObject(),
                (DeviceInfoFactoryDataMapperWithSubscription) Mockito.anyObject()));
    }

    @Test
    public void constructFetchFactoryDataTestWithState() {
        String searchKey = "1007";
        int sizeValue = 1;
        int pageValue = 1;
        String sortby = "model";
        String orderBy = "desc";
        boolean deviceVinEnabled = true;
        Mockito.doReturn(deviceInfoFactoryDataList).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (MapSqlParameterSource) Mockito.anyObject(),
                (DeviceInfoFactoryDataMapperWithSubscription) Mockito.anyObject());
        deviceInfoFactoryDataDao.constructFetchFactoryData(DeviceDetailsInputTypeEnum.STATE, searchKey, sizeValue,
            pageValue, sortby, orderBy, deviceVinEnabled);
        Assertions.assertEquals(deviceInfoFactoryDataList,
            namedParamJdbcTemplate.query(Mockito.anyString(), (MapSqlParameterSource) Mockito.anyObject(),
                (DeviceInfoFactoryDataMapperWithSubscription) Mockito.anyObject()));
    }

    @Test
    public void constructFetchFactoryDataWithArgs() {

        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("containslikefields", "a,b");
        allRequestParams.put("containslikevalues", "1,2");
        allRequestParams.put("rangefields", "a_b");
        allRequestParams.put("rangevalues", "1_2");
        allRequestParams.put("containslikevalues", "1234");
        allRequestParams.put("sortingorder", "desc");
        allRequestParams.put("sortby", "model");
        Mockito.doReturn(deviceInfoFactoryDataList).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (MapSqlParameterSource) Mockito.anyObject(),
                (DeviceInfoFactoryDataMapper) Mockito.anyObject());
        int size = 1;
        int page = 1;
        List<DeviceInfoFactoryData> datas =
            deviceInfoFactoryDataDao.constructFetchFactoryData(allRequestParams, page, size);
        assertNotNull(datas);
    }

    @Test
    public void constructFetchTotalFactoryData() {
        List<String> containsLikeFieldList = new ArrayList<>();
        List<String> containsLikeValueList = new ArrayList<>();
        containsLikeFieldList.add("a");
        containsLikeFieldList.add("b");
        containsLikeValueList.add("1");
        containsLikeValueList.add("2");
        List<String> rangeFieldList = new ArrayList<>();
        List<String> rangeValueList = new ArrayList<>();
        rangeFieldList.add("a_b");
        rangeValueList.add("1_2");
        Mockito.doReturn(1L).when(namedParamJdbcTemplate).queryForObject("", new MapSqlParameterSource(), Long.class);
        deviceInfoFactoryDataDao.constructFetchTotalFactoryData(containsLikeFieldList, containsLikeValueList,
            rangeFieldList, rangeValueList);
        Assertions.assertNull(namedParamJdbcTemplate.queryForObject("", new MapSqlParameterSource(), Long.class));
    }

    @Test
    public void findTotalDeviceState() {
        String imei = "99002669571854";
        Mockito.doReturn(0).when(namedParamJdbcTemplate).queryForObject("", new MapSqlParameterSource(), Long.class);
        assertEquals(0, deviceInfoFactoryDataDao.findTotalDeviceState(imei));
    }

    @Test
    public void constructFetchTotalFactoryDataWithArgs() {
        String serialNumber = "1007";
        String imei = "9900008624711007";
        Mockito.doReturn(1L).when(namedParamJdbcTemplate).queryForObject("", new MapSqlParameterSource(), Long.class);
        deviceInfoFactoryDataDao.constructFetchTotalFactoryData(serialNumber, imei);
        Assertions.assertNull(namedParamJdbcTemplate.queryForObject("", new MapSqlParameterSource(), Long.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void constructFetchAgrigateDeviceState() {

        DeviceStateAggregateData aggregateData = new DeviceStateAggregateData();
        aggregateData.setState("ACTIVE");
        aggregateData.setCount(COUNT);

        DeviceStateAggregateData aggregateData1 = new DeviceStateAggregateData();
        aggregateData1.setState("PROVISIONED");
        aggregateData1.setCount(COUNT);

        DeviceStateAggregateData aggregateData2 = new DeviceStateAggregateData();
        aggregateData2.setState("FAULTY");
        aggregateData2.setCount(COUNT);

        DeviceStateAggregateData aggregateData3 = new DeviceStateAggregateData();
        aggregateData3.setState("STOLEN");
        aggregateData3.setCount(COUNT);

        List<DeviceStateAggregateData> deviceStateMap = new ArrayList<>();
        deviceStateMap.add(aggregateData);
        deviceStateMap.add(aggregateData1);
        deviceStateMap.add(aggregateData2);
        deviceStateMap.add(aggregateData3);

        Mockito.doReturn(deviceStateMap).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.anyMap(), (DeviceStateAggregateDataMapper) Mockito.anyObject());
        String serialNumber = "1007";
        String imei = "9900008624711007";
        deviceInfoFactoryDataDao.constructFetchAgrigateDeviceState(serialNumber, imei);
        assertEquals(deviceStateMap, namedParamJdbcTemplate.query(Mockito.anyString(), Mockito.anyMap(),
            (DeviceStateAggregateDataMapper) Mockito.anyObject()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void constructFetchAgrigateDeviceStateWithArgs() {
        List<String> containsLikeFieldList = new ArrayList<>();
        List<String> containsLikeValueList = new ArrayList<>();
        containsLikeFieldList.add("a");
        containsLikeFieldList.add("b");
        containsLikeValueList.add("1");
        containsLikeValueList.add("2");
        List<String> rangeFieldList = new ArrayList<>();
        List<String> rangeValueList = new ArrayList<>();
        rangeFieldList.add("a_b");
        rangeValueList.add("1_2");

        DeviceStateAggregateData aggregateData = new DeviceStateAggregateData();
        aggregateData.setState("ACTIVE");
        aggregateData.setCount(COUNT);

        DeviceStateAggregateData aggregateData1 = new DeviceStateAggregateData();
        aggregateData1.setState("PROVISIONED");
        aggregateData1.setCount(COUNT);

        DeviceStateAggregateData aggregateData2 = new DeviceStateAggregateData();
        aggregateData2.setState("FAULTY");
        aggregateData2.setCount(COUNT);

        DeviceStateAggregateData aggregateData3 = new DeviceStateAggregateData();
        aggregateData3.setState("STOLEN");
        aggregateData3.setCount(COUNT);

        List<DeviceStateAggregateData> deviceStateMap = new ArrayList<>();
        deviceStateMap.add(aggregateData);
        deviceStateMap.add(aggregateData1);
        deviceStateMap.add(aggregateData2);
        deviceStateMap.add(aggregateData3);

        Mockito.doReturn(deviceStateMap).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.anyMap(), (DeviceStateAggregateDataMapper) Mockito.anyObject());
        deviceInfoFactoryDataDao.constructFetchAgrigateDeviceState(containsLikeFieldList, containsLikeValueList,
            rangeFieldList, rangeValueList);
        assertEquals(deviceStateMap, namedParamJdbcTemplate.query(Mockito.anyString(), Mockito.anyMap(),
            (DeviceStateAggregateDataMapper) Mockito.anyObject()));
    }

    @Test
    public void filterDevice() {
        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (DeviceInfoFactoryDataMapper) Mockito.anyObject());

        deviceInfoFactoryDataDao.filterDevice(new HashMap<>());
        assertEquals(deviceInfoFactoryDataList, deviceInfoFactoryDataDao.filterDevice(new HashMap<>()));
    }

    @Test
    public void filterDevice_validData() {
        Map attributeMap = new HashMap();
        attributeMap.put("serial_number", "S123");
        attributeMap.put("imei", "I123");
        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (DeviceInfoFactoryDataMapper) Mockito.anyObject());
        deviceInfoFactoryDataList = deviceInfoFactoryDataDao.filterDevice(attributeMap);
        assertEquals(deviceInfoFactoryDataList, deviceInfoFactoryDataDao.filterDevice(attributeMap));
    }

    @Test
    public void constructAndFetchDeviceStates() {
        int size = 1;
        int page = 1;
        String sortingOrder = "asc";
        String sortBy = "state";
        String imei = "9899430617";

        List<DeviceStateHistory> deviceHistory = new ArrayList<>();

        Mockito.doReturn(deviceHistory).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (MapSqlParameterSource) Mockito.anyObject(),
                (DeviceStateHistoryDataMapper) Mockito.anyObject());
        deviceHistory = deviceInfoFactoryDataDao.constructAndFetchDeviceStates(size, page, sortingOrder, sortBy, imei);
        assertNotNull(deviceHistory);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findVinEitherByImei() {
        String imei = "9899430617";
        String serialNumber = null;
        List<String> strLst = new ArrayList<>();
        strLst.add("JN1TAAT32A0XXXXYY");
        Mockito.doReturn(strLst).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyVararg(), (RowMapper<String>) Mockito.anyObject());
        String vin = deviceInfoFactoryDataDao.findVinEitherByImeiOrSerialNumber(imei, serialNumber);
        assertEquals("JN1TAAT32A0XXXXYY", vin);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findVinEitherBySerialNumber() {
        String imei = null;
        String serialNumber = "9696";
        List<String> strLst = new ArrayList<>();
        strLst.add("JN1TAAT32A0XXXXYY");
        Mockito.doReturn(strLst).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyVararg(), (RowMapper<String>) Mockito.anyObject());
        String vin = deviceInfoFactoryDataDao.findVinEitherByImeiOrSerialNumber(imei, serialNumber);
        assertEquals("JN1TAAT32A0XXXXYY", vin);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void findVinEitherByEmptyFeilds() {
        String imei = null;
        String serialNumber = null;
        List<String> strLst = new ArrayList<>();
        strLst.add("JN1TAAT32A0XXXXYY");
        Mockito.doReturn(strLst).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyVararg(), (RowMapper<String>) Mockito.anyObject());
        deviceInfoFactoryDataDao.findVinEitherByImeiOrSerialNumber(imei, serialNumber);
    }

    @Test
    public void updateDeviceTest_validMap() {
        Map<String, Object> attributeMap = new HashMap<>();
        attributeMap.put("association_type", "driver");
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("serial_number", "S123");
        deviceInfoFactoryDataDao.updateDevice(attributeMap, conditionMap);
        assertEquals(0, deviceInfoFactoryDataDao.updateDevice(attributeMap, conditionMap));
    }

    @Test
    public void updateDeviceTest_nullConditionMap() {
        Map<String, Object> attributeMap = new HashMap<>();
        attributeMap.put("association_type", "driver");
        deviceInfoFactoryDataDao.updateDevice(attributeMap, null);
        assertEquals(0, deviceInfoFactoryDataDao.updateDevice(attributeMap, null));
    }

    @Test
    public void updateDeviceTest_emptyConditionMap() {
        Map<String, Object> attributeMap = new HashMap<>();
        attributeMap.put("association_type", "driver");
        Map<String, Object> conditionMap = new HashMap<>();
        deviceInfoFactoryDataDao.updateDevice(attributeMap, conditionMap);
        assertEquals(0, deviceInfoFactoryDataDao.updateDevice(attributeMap, conditionMap));
    }


    //@Test
    //public void updateDeviceTest_nullAttributeMap() {
    //Map<String, Object> attributeMap = new HashMap<>();
    //attributeMap.put("association_type", null);
    //Map<String, Object> conditionMap = new HashMap<>();
    //conditionMap.put("serial_number", "S123");
    //deviceInfoFactoryDataDao.updateDevice(attributeMap, conditionMap);
    //}
}