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

package org.eclipse.ecsp.service;

import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.common.exception.ApiValidationFailedException;
import org.eclipse.ecsp.common.exception.DeleteDeviceException;
import org.eclipse.ecsp.common.exception.DeviceNotFoundException;
import org.eclipse.ecsp.common.exception.InputParamValidationException;
import org.eclipse.ecsp.common.exception.InvalidDeviceStateException;
import org.eclipse.ecsp.common.exception.InvalidDeviceStateTransitionException;
import org.eclipse.ecsp.common.exception.PageParamResolverException;
import org.eclipse.ecsp.common.exception.SizeParamResolverException;
import org.eclipse.ecsp.common.exception.UpdateDeviceException;
import org.eclipse.ecsp.config.DeviceInfoQueryProperty;
import org.eclipse.ecsp.dao.DeviceInfoFactoryDao;
import org.eclipse.ecsp.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.dao.HcpInfoDao;
import org.eclipse.ecsp.dao.VinDetailsDao;
import org.eclipse.ecsp.dto.DeviceData;
import org.eclipse.ecsp.dto.DeviceFilterDto;
import org.eclipse.ecsp.dto.DeviceInfoAggregateFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryDataRequest;
import org.eclipse.ecsp.dto.DeviceState;
import org.eclipse.ecsp.dto.DeviceUpdateRequest;
import org.eclipse.ecsp.dto.HcpInfo;
import org.eclipse.ecsp.dto.StateChange;
import org.eclipse.ecsp.dto.swm.SwmRequest;
import org.eclipse.ecsp.service.swm.IswmCrudService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import javax.management.InvalidAttributeValueException;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceInfoQueryService.
 */
public class DeviceInfoQueryServiceTest {
    public static final boolean RETURN_TRUE = true;
    public static final boolean RETURN_FALSE = false;
    public static final String SWM_INTEGRATION = "swmIntegration";
    private static final String UPDATED = "UPDATED";
    public static final long ID_1 = 111L;
    public static final long ID_2 = 222L;
    public static final long RETURN_VALUE = 10L;

    @Mock
    protected EnvConfig<DeviceInfoQueryProperty> config;
    @InjectMocks
    private DeviceInfoQueryService deviceInfoQueryService;
    @Mock
    private VinDetailsDao vinDetailsDao;
    @Mock
    private HcpInfoDao hcpInfoDao;
    @Mock
    private DeviceInfoFactoryDao deviceInfoFactoryDao;
    @Mock
    private IswmCrudService<SwmRequest> swmService;
    @Mock
    private DeviceInfoFactoryDataDao deviceInfoFactoryDataDao;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void deleteDeviceFactoryDataTest() throws Exception {
        DeviceInfoFactoryData queryData = new DeviceInfoFactoryData();
        queryData.setImei("1234455");
        queryData.setSerialNumber("1007");

        List<DeviceInfoFactoryData> factoryList = new ArrayList<>();
        factoryList.add(queryData);

        Mockito.doReturn(factoryList).when(deviceInfoFactoryDataDao).constructAndFetchFactoryData(Mockito.any());
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doReturn(RETURN_TRUE).when(config).getBooleanValue(DeviceInfoQueryProperty.SWM_INTEGRATION_ENABLED);
        Mockito.doReturn("abcd_123").when(deviceInfoFactoryDataDao)
            .findVinEitherByImeiOrSerialNumber(Mockito.any(), Mockito.any());
        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .deletefactoryData(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(RETURN_TRUE).when(swmService).deleteVehicle(Mockito.any());
        deviceInfoQueryService.deleteDeviceFactoryData(null, null);
        Assertions.assertEquals("abcd_123",
            deviceInfoFactoryDataDao.findVinEitherByImeiOrSerialNumber(Mockito.any(), Mockito.any()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteDeviceFactoryDataTest_invalidSerialNumber() {
        deviceInfoQueryService.deleteDeviceFactoryData("1234455", "10-07");
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteDeviceFactoryDataTest_invalidImei() {
        deviceInfoQueryService.deleteDeviceFactoryData("1234455a", "1007");
    }

    @Test(expected = DeleteDeviceException.class)
    public void deleteDeviceFactoryDataTest_throwDeleteDeviceException() {
        DeviceInfoFactoryData queryData = new DeviceInfoFactoryData();
        queryData.setImei("1234455");
        queryData.setSerialNumber("1007");

        List<DeviceInfoFactoryData> factoryList = new ArrayList<>();
        factoryList.add(queryData);

        Mockito.doReturn(factoryList).when(deviceInfoFactoryDataDao).constructAndFetchFactoryData(Mockito.any());
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doReturn(RETURN_TRUE).when(config).getBooleanValue(DeviceInfoQueryProperty.SWM_INTEGRATION_ENABLED);
        Mockito.doReturn("abcde_1465").when(deviceInfoFactoryDataDao)
            .findVinEitherByImeiOrSerialNumber(Mockito.any(), Mockito.any());
        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .deletefactoryData(Mockito.any(), Mockito.any(), Mockito.any());
        deviceInfoQueryService.deleteDeviceFactoryData(null, null);
    }

    @Test(expected = DeviceNotFoundException.class)
    public void deleteDeviceFactoryDataTest_factoryListNull() throws DeviceNotFoundException {
        List<DeviceInfoFactoryData> factoryList = new ArrayList<>();

        Mockito.doReturn(factoryList).when(deviceInfoFactoryDataDao).constructAndFetchFactoryData(Mockito.any());
        deviceInfoQueryService.deleteDeviceFactoryData("9900008624711007", "1007");
    }

    @Test(expected = DeleteDeviceException.class)
    public void deleteDeviceFactoryDataTest_VinNull() throws DeleteDeviceException {
        DeviceInfoFactoryData queryData = new DeviceInfoFactoryData();
        queryData.setImei("1234455");
        queryData.setSerialNumber("1007");

        List<DeviceInfoFactoryData> factoryList = new ArrayList<>();
        factoryList.add(queryData);

        Mockito.doReturn(factoryList).when(deviceInfoFactoryDataDao).constructAndFetchFactoryData(Mockito.any());
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doReturn(RETURN_TRUE).when(config).getBooleanValue(DeviceInfoQueryProperty.SWM_INTEGRATION_ENABLED);
        Mockito.doReturn(null).when(deviceInfoFactoryDataDao)
            .findVinEitherByImeiOrSerialNumber(Mockito.any(), Mockito.any());
        deviceInfoQueryService.deleteDeviceFactoryData(null, null);
    }

    @Test
    public void deleteDeviceFactoryDataTest_SwmIntegrationDisabled() throws DeleteDeviceException {
        DeviceInfoFactoryData queryData = new DeviceInfoFactoryData();
        queryData.setImei("1234455");
        queryData.setSerialNumber("1007");

        List<DeviceInfoFactoryData> factoryList = new ArrayList<>();
        factoryList.add(queryData);

        Mockito.doReturn(factoryList).when(deviceInfoFactoryDataDao).constructAndFetchFactoryData(Mockito.any());
        Mockito.doReturn("default").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doReturn(RETURN_FALSE).when(config).getBooleanValue(DeviceInfoQueryProperty.SWM_INTEGRATION_ENABLED);
        deviceInfoQueryService.deleteDeviceFactoryData("123", "123");
        Assertions.assertEquals(factoryList, deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()));
    }

    @Test
    public void changeDeviceStateTest() throws InvalidAttributeValueException {
        StateChange stateChange = new StateChange();
        stateChange.setFactoryId(ID_1);
        stateChange.setImei("1234");
        stateChange.setState(DeviceState.STOLEN);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID_2);
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
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        deviceInfoFactoryData.setState(DeviceState.ACTIVE.getValue());

        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceState(deviceInfoFactoryData.getId(), stateChange.getState().getValue(),
                UPDATED);
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao)
            .findByFactoryIdAndImei(Mockito.anyLong(), Mockito.anyString());
        deviceInfoQueryService.changeDeviceState(stateChange);
        Assertions.assertEquals(deviceInfoFactoryData,
            deviceInfoFactoryDataDao.findByFactoryIdAndImei(Mockito.anyLong(), Mockito.anyString()));
    }

    @Test(expected = InvalidParameterException.class)
    public void changeDeviceStateTest_byFactoryId_InvalidParameterException() throws InvalidAttributeValueException {
        StateChange stateChange = new StateChange();
        stateChange.setFactoryId(ID_1);
        stateChange.setImei("1234");
        stateChange.setState(DeviceState.STOLEN);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID_2);
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
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        deviceInfoFactoryData.setState(DeviceState.ACTIVE.getValue());

        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceState(deviceInfoFactoryData.getId(), stateChange.getState().getValue(),
                UPDATED);
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao).findByFactoryId(Mockito.anyLong());
        deviceInfoQueryService.changeDeviceState(stateChange);
    }

    @Test(expected = InvalidParameterException.class)
    public void changeDeviceStateTest_byFactoryId_nullFactoryId() throws InvalidAttributeValueException {
        StateChange stateChange = new StateChange();
        stateChange.setFactoryId(null);
        stateChange.setImei("1234");
        stateChange.setState(DeviceState.STOLEN);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID_2);
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
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        deviceInfoFactoryData.setState(DeviceState.ACTIVE.getValue());

        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceState(deviceInfoFactoryData.getId(), stateChange.getState().getValue(),
                UPDATED);
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao).findByFactoryId(Mockito.anyLong());
        deviceInfoQueryService.changeDeviceState(stateChange);
    }

    @Test(expected = InvalidParameterException.class)
    public void changeDeviceStateTest_byFactoryImei_InvalidParameterException() throws InvalidAttributeValueException {
        StateChange stateChange = new StateChange();
        stateChange.setFactoryId(ID_2);
        stateChange.setImei(null);
        stateChange.setState(DeviceState.STOLEN);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID_2);
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
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        deviceInfoFactoryData.setState(DeviceState.ACTIVE.getValue());

        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceState(deviceInfoFactoryData.getId(), stateChange.getState().getValue(),
                UPDATED);
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao).findByFactoryImei(Mockito.anyString());
        deviceInfoQueryService.changeDeviceState(stateChange);
    }

    @Test
    public void changeDeviceStateTest_byFactoryId() throws InvalidAttributeValueException {
        StateChange stateChange = new StateChange();
        stateChange.setFactoryId(ID_1);
        stateChange.setImei(null);
        stateChange.setState(DeviceState.STOLEN);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID_2);
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
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        deviceInfoFactoryData.setState(DeviceState.ACTIVE.getValue());

        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceState(deviceInfoFactoryData.getId(), stateChange.getState().getValue(),
                UPDATED);
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao).findByFactoryId(Mockito.anyLong());
        deviceInfoQueryService.changeDeviceState(stateChange);
        Assertions.assertEquals(deviceInfoFactoryData, deviceInfoFactoryDataDao.findByFactoryId(Mockito.anyLong()));
    }

    @Test
    public void changeDeviceStateTest_byFactoryId_FaultyToActive() throws InvalidAttributeValueException {
        StateChange stateChange = new StateChange();
        stateChange.setFactoryId(ID_1);
        stateChange.setImei(null);
        stateChange.setState(DeviceState.ACTIVE);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID_2);
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
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        deviceInfoFactoryData.setState(DeviceState.FAULTY.getValue());

        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setSerialNumber("S123");
        hcpInfo.setFactoryId("111");
        hcpInfo.setHarmanId("H123");

        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceState(deviceInfoFactoryData.getId(), stateChange.getState().getValue(),
                UPDATED);
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao).findByFactoryId(Mockito.anyLong());
        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findByFactoryId(Mockito.any());
        deviceInfoQueryService.changeDeviceState(stateChange);
        Assertions.assertEquals(deviceInfoFactoryData, deviceInfoFactoryDataDao.findByFactoryId(Mockito.anyLong()));
    }

    @Test
    public void changeDeviceStateTest_byFactoryId_NullHcpInfo() {
        StateChange stateChange = new StateChange();
        stateChange.setFactoryId(ID_1);
        stateChange.setImei(null);
        stateChange.setState(DeviceState.ACTIVE);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID_2);
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
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        deviceInfoFactoryData.setState(DeviceState.FAULTY.getValue());

        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceState(deviceInfoFactoryData.getId(), stateChange.getState().getValue(),
                UPDATED);
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao).findByFactoryId(Mockito.anyLong());
        Mockito.doReturn(null).when(hcpInfoDao).findByFactoryId(Mockito.any());
        assertThrows(InvalidDeviceStateTransitionException.class,
            () -> deviceInfoQueryService.changeDeviceState(stateChange));
    }

    @Test
    public void changeDeviceStateTest_byFactoryId_BlankHarmanId() {
        StateChange stateChange = new StateChange();
        stateChange.setFactoryId(ID_1);
        stateChange.setImei(null);
        stateChange.setState(DeviceState.ACTIVE);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID_2);
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
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        deviceInfoFactoryData.setState(DeviceState.FAULTY.getValue());

        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setSerialNumber("S123");
        hcpInfo.setFactoryId("111");
        hcpInfo.setHarmanId("");

        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceState(deviceInfoFactoryData.getId(), stateChange.getState().getValue(),
                UPDATED);
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao).findByFactoryId(Mockito.anyLong());
        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findByFactoryId(Mockito.any());
        assertThrows(InvalidDeviceStateTransitionException.class,
            () -> deviceInfoQueryService.changeDeviceState(stateChange));
    }

    @Test
    public void changeDeviceStateTest_byImei() throws InvalidAttributeValueException {
        StateChange stateChange = new StateChange();
        stateChange.setFactoryId(null);
        stateChange.setImei("1234");
        stateChange.setState(DeviceState.STOLEN);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID_2);
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
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        deviceInfoFactoryData.setState(DeviceState.ACTIVE.getValue());

        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceState(deviceInfoFactoryData.getId(), stateChange.getState().getValue(),
                UPDATED);
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao).findByFactoryImei(Mockito.anyString());
        deviceInfoQueryService.changeDeviceState(stateChange);
        Assertions.assertEquals(deviceInfoFactoryData, deviceInfoFactoryDataDao.findByFactoryImei(Mockito.anyString()));
    }

    @Test(expected = InvalidAttributeValueException.class)
    public void changeDeviceStateTest_factoryIdNull() throws InvalidAttributeValueException {
        StateChange stateChange = new StateChange();
        stateChange.setFactoryId(null);
        stateChange.setImei(null);
        stateChange.setState(DeviceState.STOLEN);
        deviceInfoQueryService.changeDeviceState(stateChange);
    }

    @Test(expected = InvalidAttributeValueException.class)
    public void changeDeviceStateTest_stateNull() throws InvalidAttributeValueException {
        StateChange stateChange = new StateChange();
        stateChange.setFactoryId(ID_1);
        stateChange.setImei("1234");
        stateChange.setState(null);
        deviceInfoQueryService.changeDeviceState(stateChange);
    }

    @Test
    public void changeDeviceStateTest_faultyState() throws InvalidAttributeValueException {
        StateChange stateChange = new StateChange();
        stateChange.setFactoryId(ID_1);
        stateChange.setImei("1234");
        stateChange.setState(DeviceState.STOLEN);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID_2);
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
        deviceInfoFactoryData.setFaulty(true);
        deviceInfoFactoryData.setStolen(false);
        deviceInfoFactoryData.setState(DeviceState.ACTIVE.getValue());

        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceState(deviceInfoFactoryData.getId(), stateChange.getState().getValue(),
                UPDATED);
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao)
            .findByFactoryIdAndImei(Mockito.anyLong(), Mockito.anyString());
        deviceInfoQueryService.changeDeviceState(stateChange);
        Assertions.assertEquals(deviceInfoFactoryData,
            deviceInfoFactoryDataDao.findByFactoryIdAndImei(Mockito.anyLong(), Mockito.anyString()));
    }

    @Test
    public void changeDeviceStateTest_stolenState() throws InvalidAttributeValueException {
        StateChange stateChange = new StateChange();
        stateChange.setFactoryId(ID_1);
        stateChange.setImei("1234");
        stateChange.setState(DeviceState.PROVISIONED);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID_2);
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
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(true);
        deviceInfoFactoryData.setState(DeviceState.ACTIVE.getValue());

        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceState(deviceInfoFactoryData.getId(), stateChange.getState().getValue(),
                UPDATED);
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao)
            .findByFactoryIdAndImei(Mockito.anyLong(), Mockito.anyString());
        deviceInfoQueryService.changeDeviceState(stateChange);
        Assertions.assertEquals(deviceInfoFactoryData,
            deviceInfoFactoryDataDao.findByFactoryIdAndImei(Mockito.anyLong(), Mockito.anyString()));
    }

    @Test(expected = InvalidDeviceStateTransitionException.class)
    public void changeDeviceStateTest_illegalState() throws InvalidAttributeValueException {
        StateChange stateChange = new StateChange();
        stateChange.setFactoryId(ID_1);
        stateChange.setImei("1234");
        stateChange.setState(DeviceState.STOLEN);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID_2);
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
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(true);
        deviceInfoFactoryData.setState(DeviceState.ACTIVE.getValue());

        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceState(deviceInfoFactoryData.getId(), stateChange.getState().getValue(),
                UPDATED);
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao)
            .findByFactoryIdAndImei(Mockito.anyLong(), Mockito.anyString());
        deviceInfoQueryService.changeDeviceState(stateChange);
    }

    @Test(expected = InvalidDeviceStateException.class)
    public void changeDeviceStateTest_inValidState1() throws InvalidAttributeValueException {
        StateChange stateChange = new StateChange();
        stateChange.setFactoryId(ID_1);
        stateChange.setImei("1234");
        stateChange.setState(DeviceState.ACTIVE);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID_2);
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
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        deviceInfoFactoryData.setState("ACTV");

        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceState(deviceInfoFactoryData.getId(), stateChange.getState().getValue(),
                UPDATED);
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao)
            .findByFactoryIdAndImei(Mockito.anyLong(), Mockito.anyString());
        deviceInfoQueryService.changeDeviceState(stateChange);
    }

    @Test(expected = InvalidAttributeValueException.class)
    public void updateDeviceInfoFactoryDataTest_InvalidAttributeValueException() throws Exception {
        DeviceUpdateRequest deviceUpdateRequest = new DeviceUpdateRequest();
        deviceInfoQueryService.updateDeviceInfoFactoryData(deviceUpdateRequest);
    }

    @Test(expected = DeviceNotFoundException.class)
    public void updateDeviceInfoFactoryDataTest_DeviceNotFoundException() throws Exception {
        DeviceData cv = new DeviceData();
        DeviceData mv = new DeviceData();

        DeviceUpdateRequest deviceUpdateRequest = new DeviceUpdateRequest();
        deviceUpdateRequest.setCurrentValue(cv);
        deviceUpdateRequest.setReplaceWith(mv);

        deviceInfoQueryService.updateDeviceInfoFactoryData(deviceUpdateRequest);
    }

    @Test(expected = InvalidDeviceStateException.class)
    public void updateDeviceInfoFactoryDataTest_InvalidDeviceStateException() throws Exception {
        DeviceData cv = new DeviceData();
        DeviceData mv = new DeviceData();

        DeviceUpdateRequest deviceUpdateRequest = new DeviceUpdateRequest();
        deviceUpdateRequest.setCurrentValue(cv);
        deviceUpdateRequest.setReplaceWith(mv);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao)
            .fetchDeviceInfoFactoryData(Mockito.any());
        deviceInfoQueryService.updateDeviceInfoFactoryData(deviceUpdateRequest);
    }

    @Test
    public void updateDeviceInfoFactoryDataTest() throws Exception {
        DeviceData cv = new DeviceData();
        cv.setVin("VIN");
        DeviceData mv = new DeviceData();
        mv.setVin("VIN");

        DeviceUpdateRequest deviceUpdateRequest = new DeviceUpdateRequest();
        deviceUpdateRequest.setCurrentValue(cv);
        deviceUpdateRequest.setReplaceWith(mv);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setState(DeviceState.PROVISIONED.getValue());
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao)
            .fetchDeviceInfoFactoryData(Mockito.any());
        Mockito.doReturn(1).when(deviceInfoFactoryDataDao).update(Mockito.any(), Mockito.any());
        Mockito.doReturn(SWM_INTEGRATION).when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doReturn(true).when(vinDetailsDao).checkForCurretVin(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(false).when(vinDetailsDao).checkForVin(Mockito.any());
        Mockito.doReturn(false).when(config).getBooleanValue(DeviceInfoQueryProperty.SWM_INTEGRATION_ENABLED);
        deviceInfoQueryService.updateDeviceInfoFactoryData(deviceUpdateRequest);
        Assertions.assertEquals(deviceInfoFactoryData,
            deviceInfoFactoryDataDao.fetchDeviceInfoFactoryData(Mockito.any()));
    }

    @Test(expected = ApiValidationFailedException.class)
    public void updateDeviceInfoFactoryDataTest_validationFailed() throws Exception {
        DeviceData cv = new DeviceData();
        cv.setVin("VIN");
        DeviceData mv = new DeviceData();
        mv.setVin("VIN");

        DeviceUpdateRequest deviceUpdateRequest = new DeviceUpdateRequest();
        deviceUpdateRequest.setCurrentValue(cv);
        deviceUpdateRequest.setReplaceWith(mv);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setState(DeviceState.PROVISIONED.getValue());
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao)
            .fetchDeviceInfoFactoryData(Mockito.any());
        Mockito.doReturn(1).when(deviceInfoFactoryDataDao).update(Mockito.any(), Mockito.any());
        Mockito.doReturn(SWM_INTEGRATION).when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doReturn(false).when(vinDetailsDao).checkForCurretVin(Mockito.anyLong(), Mockito.anyString());
        deviceInfoQueryService.updateDeviceInfoFactoryData(deviceUpdateRequest);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void updateDeviceInfoFactoryDataTest_vinExist() throws Exception {
        DeviceData cv = new DeviceData();
        cv.setVin("VIN");
        DeviceData mv = new DeviceData();
        mv.setVin("VIN1");

        DeviceUpdateRequest deviceUpdateRequest = new DeviceUpdateRequest();
        deviceUpdateRequest.setCurrentValue(cv);
        deviceUpdateRequest.setReplaceWith(mv);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setState(DeviceState.PROVISIONED.getValue());
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao)
            .fetchDeviceInfoFactoryData(Mockito.any());
        Mockito.doReturn(1).when(deviceInfoFactoryDataDao).update(Mockito.any(), Mockito.any());
        Mockito.doReturn(SWM_INTEGRATION).when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doReturn(true).when(vinDetailsDao).checkForCurretVin(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(true).when(vinDetailsDao).checkForVin(Mockito.any());
        deviceInfoQueryService.updateDeviceInfoFactoryData(deviceUpdateRequest);
    }

    @Test
    public void updateDeviceInfoFactoryDataTest_vinUpdated() throws Exception {
        DeviceData cv = new DeviceData();
        cv.setVin("VIN");
        DeviceData mv = new DeviceData();
        mv.setVin("VIN1");

        DeviceUpdateRequest deviceUpdateRequest = new DeviceUpdateRequest();
        deviceUpdateRequest.setCurrentValue(cv);
        deviceUpdateRequest.setReplaceWith(mv);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setState(DeviceState.PROVISIONED.getValue());
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao)
            .fetchDeviceInfoFactoryData(Mockito.any());
        Mockito.doReturn(1).when(deviceInfoFactoryDataDao).update(Mockito.any(), Mockito.any());
        Mockito.doReturn(SWM_INTEGRATION).when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doReturn(true).when(vinDetailsDao).checkForCurretVin(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(false).when(vinDetailsDao).checkForVin(Mockito.any());
        Mockito.doNothing().when(vinDetailsDao).updateVin(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(false).when(config).getBooleanValue(DeviceInfoQueryProperty.SWM_INTEGRATION_ENABLED);
        deviceInfoQueryService.updateDeviceInfoFactoryData(deviceUpdateRequest);
        Assertions.assertEquals(deviceInfoFactoryData,
            deviceInfoFactoryDataDao.fetchDeviceInfoFactoryData(Mockito.any()));
    }

    @Test(expected = InvalidAttributeValueException.class)
    public void updateDeviceInfoFactoryDataTest_performSwmVehicleUpdateException() throws Exception {
        DeviceData cv = new DeviceData();
        cv.setVin("VIN");
        cv.setChassisNumber("chassisNumber");
        cv.setProductionWeek(null);
        DeviceData mv = new DeviceData();
        mv.setVin("VIN1");
        mv.setChassisNumber("chassisNumber");
        mv.setProductionWeek(null);

        DeviceUpdateRequest deviceUpdateRequest = new DeviceUpdateRequest();
        deviceUpdateRequest.setCurrentValue(cv);
        deviceUpdateRequest.setReplaceWith(mv);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setState(DeviceState.PROVISIONED.getValue());
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao)
            .fetchDeviceInfoFactoryData(Mockito.any());
        Mockito.doReturn(1).when(deviceInfoFactoryDataDao).update(Mockito.any(), Mockito.any());
        Mockito.doReturn(SWM_INTEGRATION).when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doReturn(true).when(vinDetailsDao).checkForCurretVin(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(false).when(vinDetailsDao).checkForVin(Mockito.any());
        Mockito.doNothing().when(vinDetailsDao).updateVin(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(true).when(config).getBooleanValue(DeviceInfoQueryProperty.SWM_INTEGRATION_ENABLED);
        deviceInfoQueryService.updateDeviceInfoFactoryData(deviceUpdateRequest);
    }

    @Test
    public void updateDeviceInfoFactoryDataTest_performSwmVehicleUpdate() throws Exception {
        DeviceData cv = new DeviceData();
        cv.setVin("VIN");
        cv.setChassisNumber("chassisNumber");
        cv.setProductionWeek("ProductionWeek");
        DeviceData mv = new DeviceData();
        mv.setVin("VIN1");
        mv.setChassisNumber("chassisNumber");
        mv.setProductionWeek("ProductionWeek");
        mv.setImei("1212");
        mv.setSerialNumber("1111");

        DeviceUpdateRequest deviceUpdateRequest = new DeviceUpdateRequest();
        deviceUpdateRequest.setCurrentValue(cv);
        deviceUpdateRequest.setReplaceWith(mv);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setState(DeviceState.PROVISIONED.getValue());
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao)
            .fetchDeviceInfoFactoryData(Mockito.any());
        Mockito.doReturn(1).when(deviceInfoFactoryDataDao).update(Mockito.any(), Mockito.any());
        Mockito.doReturn(SWM_INTEGRATION).when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doReturn(true).when(vinDetailsDao).checkForCurretVin(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(false).when(vinDetailsDao).checkForVin(Mockito.any());
        Mockito.doNothing().when(vinDetailsDao).updateVin(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(true).when(config).getBooleanValue(DeviceInfoQueryProperty.SWM_INTEGRATION_ENABLED);
        Mockito.when(
                deviceInfoFactoryDataDao.findVinEitherByImeiOrSerialNumber(Mockito.anyString(), Mockito.anyString()))
            .thenReturn("VIN");
        Mockito.doReturn(true).when(swmService).updateVehicle(Mockito.any());
        deviceInfoQueryService.updateDeviceInfoFactoryData(deviceUpdateRequest);
        Assertions.assertEquals(deviceInfoFactoryData,
            deviceInfoFactoryDataDao.fetchDeviceInfoFactoryData(Mockito.any()));
    }

    @Test(expected = UpdateDeviceException.class)
    public void updateDeviceInfoFactoryDataTest_performSwmVehicleUpdateDeviceException() throws Exception {
        DeviceData cv = new DeviceData();
        cv.setVin("VIN");
        cv.setChassisNumber("chassisNumber");
        cv.setProductionWeek("ProductionWeek");
        DeviceData mv = new DeviceData();
        mv.setVin("VIN1");
        mv.setChassisNumber("chassisNumber");
        mv.setProductionWeek("ProductionWeek");
        mv.setSerialNumber("1111");
        mv.setImei("2222");

        DeviceUpdateRequest deviceUpdateRequest = new DeviceUpdateRequest();
        deviceUpdateRequest.setCurrentValue(cv);
        deviceUpdateRequest.setReplaceWith(mv);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setState(DeviceState.PROVISIONED.getValue());
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao)
            .fetchDeviceInfoFactoryData(Mockito.any());
        Mockito.doReturn(1).when(deviceInfoFactoryDataDao).update(Mockito.any(), Mockito.any());
        Mockito.doReturn(SWM_INTEGRATION).when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doReturn(true).when(vinDetailsDao).checkForCurretVin(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(false).when(vinDetailsDao).checkForVin(Mockito.any());
        Mockito.doNothing().when(vinDetailsDao).updateVin(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(true).when(config).getBooleanValue(DeviceInfoQueryProperty.SWM_INTEGRATION_ENABLED);
        Mockito.when(
                deviceInfoFactoryDataDao.findVinEitherByImeiOrSerialNumber(Mockito.anyString(), Mockito.anyString()))
            .thenReturn("VIN");
        Mockito.doReturn(false).when(swmService).updateVehicle(Mockito.any());
        deviceInfoQueryService.updateDeviceInfoFactoryData(deviceUpdateRequest);
    }

    @Test(expected = UpdateDeviceException.class)
    public void updateDeviceInfoFactoryDataTest_vinNull() throws Exception {
        DeviceData cv = new DeviceData();
        cv.setVin("VIN");
        cv.setChassisNumber("chassisNumber");
        cv.setProductionWeek("ProductionWeek");
        DeviceData mv = new DeviceData();
        mv.setVin("VIN1");
        mv.setChassisNumber("chassisNumber");
        mv.setProductionWeek("ProductionWeek");

        DeviceUpdateRequest deviceUpdateRequest = new DeviceUpdateRequest();
        deviceUpdateRequest.setCurrentValue(cv);
        deviceUpdateRequest.setReplaceWith(mv);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setState(DeviceState.PROVISIONED.getValue());
        Mockito.doReturn(deviceInfoFactoryData).when(deviceInfoFactoryDataDao)
            .fetchDeviceInfoFactoryData(Mockito.any());
        Mockito.doReturn(1).when(deviceInfoFactoryDataDao).update(Mockito.any(), Mockito.any());
        Mockito.doReturn(SWM_INTEGRATION).when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doReturn(true).when(vinDetailsDao).checkForCurretVin(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(false).when(vinDetailsDao).checkForVin(Mockito.any());
        Mockito.doNothing().when(vinDetailsDao).updateVin(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(true).when(config).getBooleanValue(DeviceInfoQueryProperty.SWM_INTEGRATION_ENABLED);
        Mockito.when(
                deviceInfoFactoryDataDao.findVinEitherByImeiOrSerialNumber(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(null);
        deviceInfoQueryService.updateDeviceInfoFactoryData(deviceUpdateRequest);
    }

    @Test(expected = PageParamResolverException.class)
    public void getAllFactoryDataV3Test_pageZero() {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("page", "0");
        requestParams.put("isdetailsrequired", "true");
        deviceInfoQueryService.getAllFactoryDataV3(requestParams);
    }

    @Test(expected = DeviceNotFoundException.class)
    public void getAllFactoryDataV3Test_EmptyRequest() {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("isdetailsrequired", "");
        requestParams.put("page", "");
        requestParams.put("sortingorder", "");
        deviceInfoQueryService.getAllFactoryDataV3(requestParams);
    }

    @Test(expected = DeviceNotFoundException.class)
    public void getAllFactoryDataV3Test_incorrectSort() {
        Map<String, String> requestParamsForIncorrectSort = new HashMap<>();
        requestParamsForIncorrectSort.put("page", "0");
        requestParamsForIncorrectSort.put("isdetailsrequired", "true");
        requestParamsForIncorrectSort.put("sortingorder", "abc");
        deviceInfoQueryService.getAllFactoryDataV3(requestParamsForIncorrectSort);
    }

    @Test(expected = DeviceNotFoundException.class)
    public void getAllFactoryDataV3Test_incorrectDetailsRequired() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("page", "0");
        allRequestParams.put("isdetailsrequired", "true1");
        deviceInfoQueryService.getAllFactoryDataV3(allRequestParams);
    }

    @Test(expected = DeviceNotFoundException.class)
    public void getAllFactoryDataV3Test_incorrectSortBy() {
        Map<String, String> requestParamsForIncorrectSortBy = new HashMap<>();
        requestParamsForIncorrectSortBy.put("page", "0");
        requestParamsForIncorrectSortBy.put("isdetailsrequired", "true");
        requestParamsForIncorrectSortBy.put("sortbyparam", "imei1");
        deviceInfoQueryService.getAllFactoryDataV3(requestParamsForIncorrectSortBy);
    }

    @Test(expected = PageParamResolverException.class)
    public void getAllFactoryDataV3Test_invalidPage() {
        Map<String, String> requestParamsForInvalidPage = new HashMap<>();
        requestParamsForInvalidPage.put("page", "-1");
        requestParamsForInvalidPage.put("isdetailsrequired", "true");
        deviceInfoQueryService.getAllFactoryDataV3(requestParamsForInvalidPage);
    }

    @Test(expected = SizeParamResolverException.class)
    public void getAllFactoryDataV3Test_incorrectSize() {
        Map<String, String> requestParamsForIncorrectSize = new HashMap<>();
        requestParamsForIncorrectSize.put("page", "1");
        requestParamsForIncorrectSize.put("isdetailsrequired", "true");
        requestParamsForIncorrectSize.put("size", "null");
        deviceInfoQueryService.getAllFactoryDataV3(requestParamsForIncorrectSize);
    }

    @Test(expected = DeviceNotFoundException.class)
    public void getAllFactoryDataV3Test_invalidRange() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("page", "1");
        allRequestParams.put("isdetailsrequired", "true");
        allRequestParams.put("size", "1");
        allRequestParams.put("rangefields", "manufacturing_date1");
        allRequestParams.put("rangevalues", "2612309_253729");
        deviceInfoQueryService.getAllFactoryDataV3(allRequestParams);
    }

    @Test(expected = DeviceNotFoundException.class)
    public void getAllFactoryDataV3Test_invalidInputRange() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("page", "1");
        allRequestParams.put("isdetailsrequired", "true");
        allRequestParams.put("size", "1");
        allRequestParams.put("rangefields", "manufacturing_date");
        deviceInfoQueryService.getAllFactoryDataV3(allRequestParams);
    }

    @Test(expected = DeviceNotFoundException.class)
    public void getAllFactoryDataV3Test_invalidRangeValue() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("page", "1");
        allRequestParams.put("isdetailsrequired", "true");
        allRequestParams.put("size", "1");
        allRequestParams.put("rangefields", "manufacturing_date");
        allRequestParams.put("rangevalues", "2612309_253729");
        deviceInfoQueryService.getAllFactoryDataV3(allRequestParams);
    }

    @Test(expected = DeviceNotFoundException.class)
    public void getAllFactoryDataV3Test_incorrectRangeValue() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("page", "1");
        allRequestParams.put("isdetailsrequired", "true");
        allRequestParams.put("size", "1");
        allRequestParams.put("rangefields", "manufacturing_date");
        allRequestParams.put("rangevalues", "2612309_253729_123");
        deviceInfoQueryService.getAllFactoryDataV3(allRequestParams);
    }

    @Test(expected = DeviceNotFoundException.class)
    public void getAllFactoryDataV3Test_incorrectContains() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("page", "1");
        allRequestParams.put("isdetailsrequired", "true");
        allRequestParams.put("size", "1");
        allRequestParams.put("containslikefields", "imei1");
        deviceInfoQueryService.getAllFactoryDataV3(allRequestParams);
    }

    @Test(expected = DeviceNotFoundException.class)
    public void getAllFactoryDataV3Test_incorrectContainsValue() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("page", "1");
        allRequestParams.put("isdetailsrequired", "true");
        allRequestParams.put("size", "1");
        allRequestParams.put("containslikefields", "imei");
        allRequestParams.put("containslikevalues", "1234");
        deviceInfoQueryService.getAllFactoryDataV3(allRequestParams);
    }

    @Test(expected = DeviceNotFoundException.class)
    public void getAllFactoryDataV3Test_invalidContainsValue() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("page", "1");
        allRequestParams.put("isdetailsrequired", "true");
        allRequestParams.put("size", "1");
        allRequestParams.put("containslikefields", "imei1");
        allRequestParams.put("containslikevalues", "1234,5678");
        deviceInfoQueryService.getAllFactoryDataV3(allRequestParams);
    }

    @Test(expected = DeviceNotFoundException.class)
    public void getAllFactoryDataV3Test_invalidContainsData() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("page", "1");
        allRequestParams.put("isdetailsrequired", "true");
        allRequestParams.put("size", "1");
        allRequestParams.put("containslikefields", "imei");
        allRequestParams.put("containslikevalues", "1234");
        deviceInfoQueryService.getAllFactoryDataV3(allRequestParams);
    }

    @Test
    public void getAllFactoryDataV3Test() {
        DeviceInfoAggregateFactoryData.StateCount st = new DeviceInfoAggregateFactoryData.StateCount();
        st.setFaulty(1L);
        st.setActive(1L);
        st.setStolen(1L);
        st.setProvisioned(1L);

        Mockito.doReturn(RETURN_VALUE).when(deviceInfoFactoryDataDao)
                .constructFetchTotalFactoryData(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(st).when(deviceInfoFactoryDataDao)
            .constructFetchAgrigateDeviceState(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("page", "1");
        allRequestParams.put("isdetailsrequired", "false");
        allRequestParams.put("size", "1");
        deviceInfoQueryService.getAllFactoryDataV3(allRequestParams);
        Assertions.assertEquals(RETURN_VALUE,
            deviceInfoFactoryDataDao.constructFetchTotalFactoryData(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any()));
    }

    @Test
    public void getAllFactoryDataV3Test_detailsRequiredTrue() {
        DeviceInfoAggregateFactoryData.StateCount st = new DeviceInfoAggregateFactoryData.StateCount();
        st.setFaulty(1L);
        st.setActive(1L);
        st.setStolen(1L);
        st.setProvisioned(1L);

        Mockito.doReturn(RETURN_VALUE).when(deviceInfoFactoryDataDao)
                .constructFetchTotalFactoryData(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(st).when(deviceInfoFactoryDataDao)
            .constructFetchAgrigateDeviceState(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("page", "1");
        allRequestParams.put("isdetailsrequired", "true");
        allRequestParams.put("size", "1");
        deviceInfoQueryService.getAllFactoryDataV3(allRequestParams);
        Assertions.assertEquals(RETURN_VALUE,
            deviceInfoFactoryDataDao.constructFetchTotalFactoryData(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any()));
    }

    @Test(expected = SizeParamResolverException.class)
    public void getAllFactoryDataV3Test_invalidSize() {
        Map<String, String> requestParamsForInvalidSize = new HashMap<>();
        requestParamsForInvalidSize.put("page", "1");
        requestParamsForInvalidSize.put("isdetailsrequired", "true");
        requestParamsForInvalidSize.put("size", "0");
        deviceInfoQueryService.getAllFactoryDataV3(requestParamsForInvalidSize);
    }

    @Test(expected = DeviceNotFoundException.class)
    public void findAllDeviceStatesTest_deviceNotFoundException() {

        Mockito.doReturn(0L).when(deviceInfoFactoryDataDao).findTotalDeviceState(Mockito.anyString());
        deviceInfoQueryService.findAllDeviceStates(null, null, null, null, null);
    }

    @Test
    public void findAllDeviceStatesTest() {

        Mockito.doReturn(RETURN_VALUE).when(deviceInfoFactoryDataDao).findTotalDeviceState(Mockito.anyString());
        deviceInfoQueryService.findAllDeviceStates("12234", "1", "1", null, null);
        Assertions.assertEquals(RETURN_VALUE, deviceInfoFactoryDataDao.findTotalDeviceState(Mockito.anyString()));
    }

    @Test(expected = InputParamValidationException.class)
    public void findAllDeviceStatesTest_invalidSortBy() {

        deviceInfoQueryService.findAllDeviceStates(null, "1", "1", null, "imei1");
    }

    @Test(expected = InputParamValidationException.class)
    public void findAllDeviceStatesTest_invalidOrderBy() {

        deviceInfoQueryService.findAllDeviceStates(null, "1", "1", "asc1", null);
    }

    @Test
    public void saveDeviceFactoryDataTest() {

        DeviceInfoFactoryDataRequest factoryData = new DeviceInfoFactoryDataRequest();
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
        DeviceInfoFactoryDataRequest[] factoryDataRequest = new DeviceInfoFactoryDataRequest[1];
        factoryDataRequest[0] = factoryData;

        Mockito.doNothing().when(deviceInfoFactoryDao).insertData(Mockito.any(), Mockito.anyString());
        deviceInfoQueryService.saveDeviceFactoryData(factoryDataRequest, null);
        Assertions.assertNotNull(deviceInfoFactoryDao);
    }

    @Test
    public void filterDeviceTest() {
        DeviceFilterDto deviceFilterDto = new DeviceFilterDto();
        deviceFilterDto.setImei(Arrays.asList("1234", "2345"));
        deviceFilterDto.setSerialNumber(Arrays.asList("SS12", "SS13"));

        Mockito.doReturn(null).when(deviceInfoFactoryDataDao).filterDevice(Mockito.any());
        deviceInfoQueryService.filterDevice(deviceFilterDto);
        Assertions.assertNotNull(deviceInfoFactoryDao);
    }

    @Test
    public void filterDeviceTestWithNullSerialNumber() {
        DeviceFilterDto deviceFilterDto = new DeviceFilterDto();
        deviceFilterDto.setImei(Arrays.asList("1234", "2345"));
        deviceFilterDto.setSerialNumber(null);

        Mockito.doReturn(null).when(deviceInfoFactoryDataDao).filterDevice(Mockito.any());
        deviceInfoQueryService.filterDevice(deviceFilterDto);
        Assertions.assertNotNull(deviceInfoFactoryDao);
    }

    @Test
    public void updateDeviceTest() {
        DeviceInfoFactoryData difd = new DeviceInfoFactoryData();
        difd.setImei("9900008624711007");
        difd.setSerialNumber("1007");
        difd.setState("ACTIVE");

        Mockito.doReturn(1).when(deviceInfoFactoryDataDao).updateDevice(Mockito.any(), Mockito.any());
        deviceInfoQueryService.updateDevice(difd);
        Assertions.assertEquals(1, deviceInfoFactoryDataDao.updateDevice(Mockito.any(), Mockito.any()));
    }

    @Test
    public void updateDeviceTestWithNullImeiAndSerialNumber() {
        DeviceInfoFactoryData difd = new DeviceInfoFactoryData();
        difd.setImei(null);
        difd.setSerialNumber(null);
        difd.setState(null);
        Mockito.doReturn(1).when(deviceInfoFactoryDataDao).updateDevice(Mockito.any(), Mockito.any());
        deviceInfoQueryService.updateDevice(difd);
        Assertions.assertEquals(1, deviceInfoFactoryDataDao.updateDevice(Mockito.any(), Mockito.any()));
    }

    @Test(expected = InputParamValidationException.class)
    public void updateDeviceTest_invalidImei() {
        DeviceInfoFactoryData difd = new DeviceInfoFactoryData();
        difd.setImei("99");
        difd.setSerialNumber("1007");
        difd.setState("ACTIVE");

        deviceInfoQueryService.updateDevice(difd);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateDeviceTest_invalidSerialNumber() {
        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setImei("9900");
        deviceInfoFactoryData.setSerialNumber("10-7");
        deviceInfoFactoryData.setState("ACTIVE");

        deviceInfoQueryService.updateDevice(deviceInfoFactoryData);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateDeviceTest_smallSerialNumber() {
        DeviceInfoFactoryData deviceInfoFactoryDataForSmallSn = new DeviceInfoFactoryData();
        deviceInfoFactoryDataForSmallSn.setImei("9900");
        deviceInfoFactoryDataForSmallSn.setSerialNumber("as");
        deviceInfoFactoryDataForSmallSn.setState("ACTIVE");

        deviceInfoQueryService.updateDevice(deviceInfoFactoryDataForSmallSn);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void updateDeviceTest_invalidState() {
        DeviceInfoFactoryData deviceInfoFactoryDataForInvalidState = new DeviceInfoFactoryData();
        deviceInfoFactoryDataForInvalidState.setImei("9900");
        deviceInfoFactoryDataForInvalidState.setSerialNumber("1007");
        deviceInfoFactoryDataForInvalidState.setState("ACTIVE1");

        deviceInfoQueryService.updateDevice(deviceInfoFactoryDataForInvalidState);
    }

    @Test(expected = InputParamValidationException.class)
    public void updateDeviceTest_inCorrectImei() {
        DeviceInfoFactoryData deviceInfoFactoryDataForIncorrectImei = new DeviceInfoFactoryData();
        deviceInfoFactoryDataForIncorrectImei.setImei("9900a");
        deviceInfoFactoryDataForIncorrectImei.setSerialNumber("1007");
        deviceInfoFactoryDataForIncorrectImei.setState("ACTIVE");

        deviceInfoQueryService.updateDevice(deviceInfoFactoryDataForIncorrectImei);
    }

}
