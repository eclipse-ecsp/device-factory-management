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

package org.eclipse.ecsp.controller;

import org.eclipse.ecsp.common.ApiResponse;
import org.eclipse.ecsp.common.HcpServicesBaseResponse;
import org.eclipse.ecsp.common.RecordStats;
import org.eclipse.ecsp.common.ResponseConstants;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.common.enums.ApiMessageEnum;
import org.eclipse.ecsp.common.exception.ApiTechnicalException;
import org.eclipse.ecsp.common.exception.ApiValidationFailedException;
import org.eclipse.ecsp.common.exception.DeleteDeviceException;
import org.eclipse.ecsp.common.exception.DeviceNotFoundException;
import org.eclipse.ecsp.common.exception.InputParamValidationException;
import org.eclipse.ecsp.common.exception.InvalidDeviceStateException;
import org.eclipse.ecsp.common.exception.PageParamResolverException;
import org.eclipse.ecsp.common.exception.SizeParamResolverException;
import org.eclipse.ecsp.config.DeviceInfoQueryProperty;
import org.eclipse.ecsp.dto.DeviceData;
import org.eclipse.ecsp.dto.DeviceFilterDto;
import org.eclipse.ecsp.dto.DeviceInfoAggregateFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryDataRequest;
import org.eclipse.ecsp.dto.DeviceInfoFactoryDataWithSubscription;
import org.eclipse.ecsp.dto.DeviceInfoPage;
import org.eclipse.ecsp.dto.DeviceInfoQueryDto;
import org.eclipse.ecsp.dto.DeviceInfoQuerySuccessResponse;
import org.eclipse.ecsp.dto.DeviceState;
import org.eclipse.ecsp.dto.DeviceStateHistory;
import org.eclipse.ecsp.dto.DeviceUpdateRequest;
import org.eclipse.ecsp.dto.SimpleResponseMessage;
import org.eclipse.ecsp.dto.StateChange;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataDto;
import org.eclipse.ecsp.dto.read.device.DeviceDetailsBaseDto;
import org.eclipse.ecsp.service.DeviceInfoQueryService;
import org.eclipse.ecsp.service.create.device.IdeviceFactoryDataService;
import org.eclipse.ecsp.service.read.device.IallDeviceDetailsService;
import org.eclipse.ecsp.service.read.device.IdeviceDetailsService;
import org.eclipse.ecsp.webutil.ExtendedApiResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.ServletRequestBindingException;

import javax.management.InvalidAttributeValueException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.eclipse.ecsp.common.enums.ApiMessageEnum.DEVICE_NOT_FOUND;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.DEVICE_UPDATE_FAILED;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.FACTORY_DATA_CREATE_SUCCESS;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.GENERAL_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceController.
 *
 * @author ayush agrahari
 */
public class DeviceControllerTest {
    public static final String USER_ID = "apiTest43";
    public static final int STATUS_CODE_200 = 200;
    public static final int STATUS_CODE_500 = 500;
    public static final int STATUS_CODE_400 = 400;
    public static final int STATUS_CODE_404 = 404;
    public static final int STATUS_CODE_412 = 412;
    public static final long ID = 12345L;
    public static final String SUCCESS = "Success";
    public static final String DEFAULT = "default";


    @Mock
    protected EnvConfig<DeviceInfoQueryProperty> config;
    DeviceFactoryDataDto[] factoryDataDtoRequest;
    DeviceFactoryDataDto factoryDataDto;
    List<DeviceInfoFactoryData> expectedFactoryData;
    DeviceInfoFactoryData deviceInfoFactoryData;
    List<DeviceInfoFactoryDataWithSubscription> deviceInfoFactoryDataWithSubscriptionList;
    DeviceInfoFactoryDataWithSubscription deviceInfoFactoryDataWithSubscription;
    DeviceUpdateRequest deviceUpdateRequest;
    @InjectMocks
    private DeviceController deviceController;
    @Mock
    private DeviceInfoQueryService deviceInfoQueryService;
    @Mock
    private IdeviceFactoryDataService deviceFactoryDataService;
    @Mock
    private IdeviceDetailsService<DeviceDetailsBaseDto> deviceDetailsServiceV1;
    @Mock
    private IallDeviceDetailsService<DeviceDetailsBaseDto, DeviceInfoPage<List<DeviceInfoFactoryData>>>
        allDeviceDetailsServiceV2;
    @Mock
    private IallDeviceDetailsService<DeviceDetailsBaseDto, DeviceInfoQueryDto<DeviceInfoFactoryDataWithSubscription>>
        allDeviceDetailsServiceV4;
    @Mock
    private IallDeviceDetailsService<DeviceDetailsBaseDto, DeviceInfoQueryDto<DeviceInfoFactoryDataWithSubscription>>
        allDeviceDetailsServiceV5;
    @Mock
    private IdeviceFactoryDataService guestUserDeviceFactoryDataService;
    @Mock
    private IdeviceFactoryDataService swmIntegrationDeviceFactoryDataService;

    /**
     * run before each test.
     */
    @Before
    public void beforeEach() {
        initMocks(this);
        factoryDataDtoRequest = new DeviceFactoryDataDto[1];
        factoryDataDto = new DeviceFactoryDataDto();
        factoryDataDto.setManufacturingDate("2019/01/01");
        factoryDataDto.setImei("9900008624711007");
        factoryDataDto.setSerialNumber("1007");
        factoryDataDto.setIccid("99911012000032041007");
        factoryDataDto.setSsid("SSID001007");
        factoryDataDto.setBssid("d8:c7:c8:44:32:1007");
        factoryDataDto.setMsisdn("+9190035631007");
        factoryDataDto.setImsi("4100728213931007");
        factoryDataDto.setRecordDate("2019/01/01");
        factoryDataDtoRequest[0] = factoryDataDto;

        expectedFactoryData = new ArrayList<>();
        deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setManufacturingDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        deviceInfoFactoryData.setImei("9900008624711007");
        deviceInfoFactoryData.setSerialNumber("1007");
        deviceInfoFactoryData.setIccid("99911012000032041007");
        deviceInfoFactoryData.setSsid("SSID001007");
        deviceInfoFactoryData.setBssid("d8:c7:c8:44:32:1007");
        deviceInfoFactoryData.setMsisdn("+9190035631007");
        deviceInfoFactoryData.setImsi("4100728213931007");
        deviceInfoFactoryData.setRecordDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        expectedFactoryData.add(deviceInfoFactoryData);

        deviceInfoFactoryDataWithSubscriptionList = new ArrayList<>();
        deviceInfoFactoryDataWithSubscription = new DeviceInfoFactoryDataWithSubscription();
        deviceInfoFactoryDataWithSubscription.setImei("9900008624711007");
        deviceInfoFactoryDataWithSubscription.setSerialNumber("1007");
        deviceInfoFactoryDataWithSubscription.setIccid("99911012000032041007");
        deviceInfoFactoryDataWithSubscription.setSsid("SSID001007");
        deviceInfoFactoryDataWithSubscription.setBssid("d8:c7:c8:44:32:1007");
        deviceInfoFactoryDataWithSubscription.setMsisdn("+9190035631007");
        deviceInfoFactoryDataWithSubscription.setImsi("4100728213931007");
        deviceInfoFactoryDataWithSubscription.setRecordDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        deviceInfoFactoryDataWithSubscription.setSubscriberId("1");
        deviceInfoFactoryDataWithSubscription.setSubscriptionStatus("COMPLETED");
        deviceInfoFactoryDataWithSubscriptionList.add(deviceInfoFactoryDataWithSubscription);

        DeviceData cv = new DeviceData();
        cv.setImei("9900008624711007");
        cv.setSerialNumber("1007");
        cv.setIccid("99911012000032041007");
        cv.setSsid("SSID001007");
        cv.setMsisdn("+9190035631007");
        cv.setImsi("4100728213931007");
        cv.setRecordDate("2019/01/01");

        DeviceData mv = new DeviceData();
        mv.setImei("9900008624711007");
        mv.setSerialNumber("1007");
        mv.setIccid("99911012000032041007");
        mv.setSsid("SSID00100");
        mv.setMsisdn("+9190035631007");
        mv.setImsi("4100728213931007");
        mv.setRecordDate("2019/01/01");

        deviceUpdateRequest = new DeviceUpdateRequest();
        deviceUpdateRequest.setCurrentValue(cv);
        deviceUpdateRequest.setReplaceWith(mv);
    }

    @Test
    public void createDevicesV1() {
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

        Mockito.doNothing().when(deviceInfoQueryService).saveDeviceFactoryData(Mockito.any(), Mockito.any());
        ResponseEntity<?> responseEntity = deviceController.addDevices(factoryDataRequest, USER_ID);
        SimpleResponseMessage actualSimpleResponseMessage = (SimpleResponseMessage) responseEntity.getBody();

        assertNotNull(actualSimpleResponseMessage);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
        assertEquals(SUCCESS, actualSimpleResponseMessage.getMessage());
    }

    @Test
    public void createDevicesV2() throws Exception {

        Mockito.doReturn(DEFAULT).when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doReturn(true).when(deviceFactoryDataService).createDevice(Mockito.any());
        ResponseEntity<?> responseEntity = deviceController.createDevicesV2(factoryDataDtoRequest, USER_ID);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();

        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
        assertEquals(FACTORY_DATA_CREATE_SUCCESS.getCode(), actualApiResponse.getCode());
        assertEquals(FACTORY_DATA_CREATE_SUCCESS.getMessage(), actualApiResponse.getMessage());
    }

    @Test
    public void createDevicesV2_falseCreateDeviceStatus() throws Exception {

        Mockito.doReturn(DEFAULT).when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doReturn(false).when(deviceFactoryDataService).createDevice(Mockito.any());
        ResponseEntity<?> responseEntity = deviceController.createDevicesV2(factoryDataDtoRequest, USER_ID);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();

        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_500, responseEntity.getStatusCodeValue());
        assertEquals(ApiMessageEnum.GENERAL_ERROR.getCode(), actualApiResponse.getCode());
        assertEquals(ApiMessageEnum.GENERAL_ERROR.getMessage(), actualApiResponse.getMessage());
    }

    @Test
    public void createDevicesV2_throwApiValidationException() throws Exception {
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doThrow(ApiValidationFailedException.class).when(swmIntegrationDeviceFactoryDataService)
            .createDevice(Mockito.any());
        ResponseEntity responseEntity = deviceController.createDevicesV2(factoryDataDtoRequest, USER_ID);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertEquals(STATUS_CODE_400, responseEntity.getStatusCodeValue());
    }

    @Test
    public void createDevicesV2_throwServletRequestBindingException() throws Exception {
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        ServletRequestBindingException ex =
            new ServletRequestBindingException("Missing header 'user-id' for method parameter type");
        Mockito.doThrow(ex).when(swmIntegrationDeviceFactoryDataService).createDevice(Mockito.any());
        deviceController.createDevicesV2(factoryDataDtoRequest, USER_ID);
        boolean actualResponse = false;
        try {
            swmIntegrationDeviceFactoryDataService.createDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void createDevicesV2_throwHttpMessageNotReadableException() throws Exception {
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doThrow(HttpMessageNotReadableException.class).when(swmIntegrationDeviceFactoryDataService)
            .createDevice(Mockito.any());
        deviceController.createDevicesV2(factoryDataDtoRequest, USER_ID);
        boolean actualResponse = false;
        try {
            swmIntegrationDeviceFactoryDataService.createDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void createDevicesV2_throwDuplicateKeyException() throws Exception {
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doThrow(DuplicateKeyException.class).when(swmIntegrationDeviceFactoryDataService)
            .createDevice(Mockito.any());
        deviceController.createDevicesV2(factoryDataDtoRequest, USER_ID);
        boolean actualResponse = false;
        try {
            swmIntegrationDeviceFactoryDataService.createDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void createDevicesV2_throwUncategorizedSqlException() throws Exception {
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        String errorMsg = "Invalid date format passed";
        Exception ex = new Exception(errorMsg);
        Mockito.doThrow(ex).when(swmIntegrationDeviceFactoryDataService).createDevice(Mockito.any());
        deviceController.createDevicesV2(factoryDataDtoRequest, USER_ID);
        boolean actualResponse = false;
        try {
            swmIntegrationDeviceFactoryDataService.createDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void createDevicesV2_throwException() throws Exception {
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Exception ex = new Exception("xyz");
        Mockito.doThrow(ex).when(swmIntegrationDeviceFactoryDataService).createDevice(Mockito.any());
        deviceController.createDevicesV2(factoryDataDtoRequest, USER_ID);
        boolean actualResponse = false;
        try {
            swmIntegrationDeviceFactoryDataService.createDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }


    @Test
    public void createDevicesV2_throwDataIntegrityViolationException() throws Exception {
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doThrow(DataIntegrityViolationException.class).when(swmIntegrationDeviceFactoryDataService)
            .createDevice(Mockito.any());
        deviceController.createDevicesV2(factoryDataDtoRequest, USER_ID);
        boolean actualResponse = false;
        try {
            swmIntegrationDeviceFactoryDataService.createDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void createDevicesV2_throwDeleteDeviceException() throws Exception {
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doThrow(DeleteDeviceException.class).when(swmIntegrationDeviceFactoryDataService)
            .createDevice(Mockito.any());
        deviceController.createDevicesV2(factoryDataDtoRequest, USER_ID);
        boolean actualResponse = false;
        try {
            swmIntegrationDeviceFactoryDataService.createDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void createDevicesV3() throws Exception {

        Mockito.doReturn("default").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doReturn(true).when(deviceFactoryDataService).createDevice(Mockito.any());
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doReturn(true).when(swmIntegrationDeviceFactoryDataService).createDevice(Mockito.any());
        Mockito.doReturn("guestUser").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doReturn(true).when(guestUserDeviceFactoryDataService).createDevice(Mockito.any());

        ResponseEntity<?> responseEntity = deviceController.createDevicesV3(factoryDataDtoRequest, USER_ID);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
        assertEquals(FACTORY_DATA_CREATE_SUCCESS.getCode(), actualApiResponse.getCode());
        assertEquals(FACTORY_DATA_CREATE_SUCCESS.getMessage(), actualApiResponse.getMessage());
    }

    @Test
    public void createDevicesV3_throwServletRequestBindingException() throws Exception {

        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        ServletRequestBindingException ex =
            new ServletRequestBindingException("Missing header 'user-id' for method parameter type");
        Mockito.doThrow(ex).when(swmIntegrationDeviceFactoryDataService).createDevice(Mockito.any());
        deviceController.createDevicesV3(factoryDataDtoRequest, USER_ID);
        boolean actualResponse = false;
        try {
            swmIntegrationDeviceFactoryDataService.createDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void createDevicesV3_falseCreateDeviceStatus() throws Exception {

        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doReturn(false).when(swmIntegrationDeviceFactoryDataService).createDevice(Mockito.any());

        ResponseEntity<?> responseEntity = deviceController.createDevicesV3(factoryDataDtoRequest, USER_ID);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertEquals(STATUS_CODE_500, responseEntity.getStatusCodeValue());
        assertEquals(GENERAL_ERROR.getCode(), actualApiResponse.getCode());
        assertEquals(GENERAL_ERROR.getMessage(), actualApiResponse.getMessage());
    }

    @Test
    public void createDevicesV3_throwApiValidationFailedException() throws Exception {
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doThrow(ApiValidationFailedException.class).when(swmIntegrationDeviceFactoryDataService)
            .createDevice(Mockito.any());
        deviceController.createDevicesV3(factoryDataDtoRequest, USER_ID);
        boolean actualResponse = false;
        try {
            swmIntegrationDeviceFactoryDataService.createDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void createDevicesV3_throwUncategorizedSqlException() throws Exception {
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Exception ex = new Exception("Invalid date format passed");
        Mockito.doThrow(ex).when(swmIntegrationDeviceFactoryDataService).createDevice(Mockito.any());
        deviceController.createDevicesV2(factoryDataDtoRequest, USER_ID);
        boolean actualResponse = false;
        try {
            swmIntegrationDeviceFactoryDataService.createDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void createDevicesV3_throwException() throws Exception {

        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Exception ex = new Exception("xyz");
        Mockito.doThrow(ex).when(swmIntegrationDeviceFactoryDataService).createDevice(Mockito.any());
        deviceController.createDevicesV3(factoryDataDtoRequest, USER_ID);
        boolean actualResponse = false;
        try {
            swmIntegrationDeviceFactoryDataService.createDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void createDevicesV3_throwApiTechnicalException() throws Exception {
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doThrow(ApiTechnicalException.class).when(swmIntegrationDeviceFactoryDataService)
            .createDevice(Mockito.any());
        deviceController.createDevicesV3(factoryDataDtoRequest, USER_ID);
        boolean actualResponse = false;
        try {
            swmIntegrationDeviceFactoryDataService.createDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void createDevicesV3_throwHttpMessageNotReadableException() throws Exception {
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doThrow(HttpMessageNotReadableException.class).when(swmIntegrationDeviceFactoryDataService)
            .createDevice(Mockito.any());
        deviceController.createDevicesV3(factoryDataDtoRequest, USER_ID);
        boolean actualResponse = false;
        try {
            swmIntegrationDeviceFactoryDataService.createDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void createDevicesV3_throwDuplicateKeyException() throws Exception {
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doThrow(DuplicateKeyException.class).when(swmIntegrationDeviceFactoryDataService)
            .createDevice(Mockito.any());
        deviceController.createDevicesV3(factoryDataDtoRequest, USER_ID);
        boolean actualResponse = false;
        try {
            swmIntegrationDeviceFactoryDataService.createDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void createDevicesV3_throwDataIntegrityViolationException() throws Exception {
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doThrow(DataIntegrityViolationException.class).when(swmIntegrationDeviceFactoryDataService)
            .createDevice(Mockito.any());
        deviceController.createDevicesV3(factoryDataDtoRequest, USER_ID);
        boolean actualResponse = false;
        try {
            swmIntegrationDeviceFactoryDataService.createDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void createDevicesV3_throwDeleteDeviceException() throws Exception {
        Mockito.doReturn("swmIntegration").when(config).getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        Mockito.doThrow(DeleteDeviceException.class).when(swmIntegrationDeviceFactoryDataService)
            .createDevice(Mockito.any());
        deviceController.createDevicesV3(factoryDataDtoRequest, USER_ID);
        boolean actualResponse = false;
        try {
            swmIntegrationDeviceFactoryDataService.createDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV1() {

        Mockito.doReturn(expectedFactoryData).when(deviceDetailsServiceV1).findFactoryData(Mockito.any());
        ResponseEntity<?> responseEntity = deviceController
            .findFactoryData("9900008624711007", null, null, null, null, null, null, null);

        int actualStatus = responseEntity.getStatusCode().value();
        List<DeviceInfoFactoryData> actualFactoryData = (List<DeviceInfoFactoryData>) responseEntity.getBody();
        assertNotNull(actualFactoryData);
        assertEquals(STATUS_CODE_200, actualStatus);
        assertEquals(expectedFactoryData.get(0).getModel(), actualFactoryData.get(0).getModel());
        assertEquals(expectedFactoryData.get(0).getImei(), actualFactoryData.get(0).getImei());
        assertEquals(expectedFactoryData.get(0).getSerialNumber(), actualFactoryData.get(0).getSerialNumber());
        assertEquals(expectedFactoryData.get(0).getPlatformVersion(), actualFactoryData.get(0).getPlatformVersion());
        assertEquals(expectedFactoryData.get(0).getIccid(), actualFactoryData.get(0).getIccid());
        assertEquals(expectedFactoryData.get(0).getSsid(), actualFactoryData.get(0).getSsid());
        assertEquals(expectedFactoryData.get(0).getBssid(), actualFactoryData.get(0).getBssid());
        assertEquals(expectedFactoryData.get(0).getMsisdn(), actualFactoryData.get(0).getMsisdn());
        assertEquals(expectedFactoryData.get(0).getImsi(), actualFactoryData.get(0).getImsi());
    }

    @Test
    public void getDeviceDetailsV1_throwDeviceNotFoundException() {
        Mockito.doThrow(DeviceNotFoundException.class).when(deviceDetailsServiceV1).findFactoryData(Mockito.any());
        deviceController.findFactoryData(null, "9900008624711007", null, null, null, null, null, null);
        boolean actualResponse = false;
        try {
            deviceDetailsServiceV1.findFactoryData(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV1_throwException() {
        Mockito.doThrow(ApiValidationFailedException.class).when(deviceDetailsServiceV1).findFactoryData(Mockito.any());
        deviceController.findFactoryData(null, null, "9900008624711007", null, null, null, null, null);
        boolean actualResponse = false;
        try {
            deviceDetailsServiceV1.findFactoryData(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV2() {
        DeviceInfoAggregateFactoryData aggregateData = new DeviceInfoAggregateFactoryData();
        DeviceInfoAggregateFactoryData.StateCount stateCount = new DeviceInfoAggregateFactoryData.StateCount();
        stateCount.setProvisioned(1L);
        aggregateData.setStateCount(stateCount);
        aggregateData.setCount(1L);
        DeviceInfoPage.HcpPageable hcpPageable = new DeviceInfoPage.HcpPageable(1, 1, 1L);
        DeviceInfoPage<List<DeviceInfoFactoryData>> deviceInfoPage =
            new DeviceInfoPage(aggregateData, expectedFactoryData, hcpPageable);
        Mockito.doReturn(deviceInfoPage).when(allDeviceDetailsServiceV2).findAllFactoryData(Mockito.any());
        ResponseEntity<?> responseEntity = deviceController
            .getAllFactoryData("true", null, null, null, null, null, "9900008624711007");

        DeviceInfoPage deviceInfoPage1 = (DeviceInfoPage) responseEntity.getBody();
        List<DeviceInfoFactoryData> actualFactoryData = deviceInfoPage1.getDevices();
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
        assertEquals(expectedFactoryData.get(0).getModel(), actualFactoryData.get(0).getModel());
        assertEquals(expectedFactoryData.get(0).getImei(), actualFactoryData.get(0).getImei());
        assertEquals(expectedFactoryData.get(0).getSerialNumber(), actualFactoryData.get(0).getSerialNumber());
        assertEquals(expectedFactoryData.get(0).getPlatformVersion(), actualFactoryData.get(0).getPlatformVersion());
        assertEquals(expectedFactoryData.get(0).getIccid(), actualFactoryData.get(0).getIccid());
        assertEquals(expectedFactoryData.get(0).getSsid(), actualFactoryData.get(0).getSsid());
        assertEquals(expectedFactoryData.get(0).getBssid(), actualFactoryData.get(0).getBssid());
        assertEquals(expectedFactoryData.get(0).getMsisdn(), actualFactoryData.get(0).getMsisdn());
        assertEquals(expectedFactoryData.get(0).getImsi(), actualFactoryData.get(0).getImsi());
    }

    @Test
    public void getDeviceDetailsV2_throwIllegalArgumentException() {
        Mockito.doThrow(IllegalArgumentException.class).when(allDeviceDetailsServiceV2)
            .findAllFactoryData(Mockito.any());
        deviceController.getAllFactoryData("true", null, null, null, null, "9900008624711007", null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV2.findAllFactoryData(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV2_throwInputParamValidationException() {
        Mockito.doThrow(InputParamValidationException.class).when(allDeviceDetailsServiceV2)
            .findAllFactoryData(Mockito.any());
        deviceController.getAllFactoryData("true", "2", null, null, null, null, null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV2.findAllFactoryData(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV2_throwPageParamResolverException() {
        Mockito.doThrow(PageParamResolverException.class).when(allDeviceDetailsServiceV2)
            .findAllFactoryData(Mockito.any());
        deviceController.getAllFactoryData("true", null, "4", null, null, "9900008624711007", "8900008624711007");
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV2.findAllFactoryData(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV2_throwSizeParamResolverException() {
        Mockito.doThrow(SizeParamResolverException.class).when(allDeviceDetailsServiceV2)
            .findAllFactoryData(Mockito.any());
        deviceController.getAllFactoryData("true", null, null, "asc", null, "9900008624711007", null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV2.findAllFactoryData(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV2_throwDeviceNotFoundException() {
        Mockito.doThrow(DeviceNotFoundException.class).when(allDeviceDetailsServiceV2)
            .findAllFactoryData(Mockito.any());
        deviceController.getAllFactoryData("true", null, null, null, "desc", null, "8900008624711007");
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV2.findAllFactoryData(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV2_throwException() {
        Mockito.doThrow(ApiValidationFailedException.class).when(allDeviceDetailsServiceV2)
            .findAllFactoryData(Mockito.any());
        deviceController.getAllFactoryData("true", "20", "4", "asc", null, "9900008624711007", "8900008624711007");
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV2.findAllFactoryData(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV4() {

        RecordStats<DeviceInfoAggregateFactoryData> recordStats = new RecordStats<>();
        recordStats.setPage(1);
        recordStats.setSize(1);
        recordStats.setTotal(1);
        recordStats.setAggregate(null);
        DeviceInfoQueryDto<DeviceInfoFactoryDataWithSubscription> allFactoryData = new DeviceInfoQueryDto<>();
        allFactoryData.setRecordStats(recordStats);
        allFactoryData.setData(deviceInfoFactoryDataWithSubscriptionList);

        DeviceInfoQuerySuccessResponse expectedResponse = new DeviceInfoQuerySuccessResponse();
        expectedResponse.setHttpStatusCode(STATUS_CODE_200);
        expectedResponse.setRequestId("req1");
        expectedResponse.setCode("HCP-001");
        expectedResponse.setReason("All Devices Retrieved based on given IMEI");
        expectedResponse.setMessage("Device details fetched based on either full/partial imei");
        expectedResponse.setRecordStats(allFactoryData.getRecordStats());
        expectedResponse.setData(allFactoryData.getData());

        Mockito.doReturn(false).when(config)
            .getBooleanValue(DeviceInfoQueryProperty.ENABLE_DEVICE_SUBSCRIPTION_DETAILS);
        Mockito.doReturn(allFactoryData).when(allDeviceDetailsServiceV4)
            .findAllFactoryData(Mockito.any(), Mockito.any());
        ResponseEntity<?> responseEntity = deviceController.getAllFactoryData(null, null, null, null, null);

        DeviceInfoQuerySuccessResponse actualResponse = (DeviceInfoQuerySuccessResponse) responseEntity.getBody();

        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
        assertEquals(expectedResponse.getCode(), actualResponse.getCode());
        assertEquals(expectedResponse.getReason(), actualResponse.getReason());
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
        List<DeviceInfoFactoryDataWithSubscription> actualResponseData = (List) actualResponse.getData();
        List<DeviceInfoFactoryDataWithSubscription> expectedFactoryData = expectedResponse.getData();
        assertEquals(expectedFactoryData.get(0).getModel(), actualResponseData.get(0).getModel());
        assertEquals(expectedFactoryData.get(0).getImei(), actualResponseData.get(0).getImei());
        assertEquals(expectedFactoryData.get(0).getSerialNumber(), actualResponseData.get(0).getSerialNumber());
        assertEquals(expectedFactoryData.get(0).getPlatformVersion(), actualResponseData.get(0).getPlatformVersion());
        assertEquals(expectedFactoryData.get(0).getIccid(), actualResponseData.get(0).getIccid());
        assertEquals(expectedFactoryData.get(0).getSsid(), actualResponseData.get(0).getSsid());
        assertEquals(expectedFactoryData.get(0).getBssid(), actualResponseData.get(0).getBssid());
        assertEquals(expectedFactoryData.get(0).getMsisdn(), actualResponseData.get(0).getMsisdn());
        assertEquals(expectedFactoryData.get(0).getImsi(), actualResponseData.get(0).getImsi());
        assertEquals(expectedFactoryData.get(0).getSubscriberId(), actualResponseData.get(0).getSubscriberId());
        assertEquals(expectedFactoryData.get(0).getSubscriptionStatus(),
            actualResponseData.get(0).getSubscriptionStatus());
    }

    @Test
    public void getDeviceDetailsV4_throwPageParamResolverException() {
        Mockito.doThrow(PageParamResolverException.class).when(allDeviceDetailsServiceV4)
            .findAllFactoryData(Mockito.any(), Mockito.any());
        deviceController.getAllFactoryData(null, null, null, null, null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV4.findAllFactoryData(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV4_throwSizeParamResolverException() {
        Mockito.doThrow(SizeParamResolverException.class).when(allDeviceDetailsServiceV4)
            .findAllFactoryData(Mockito.any(), Mockito.any());
        deviceController.getAllFactoryData(null, null, null, null, null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV4.findAllFactoryData(Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV4_throwInputParamValidationException() {
        Mockito.doThrow(InputParamValidationException.class).when(allDeviceDetailsServiceV4)
            .findAllFactoryData(Mockito.any(), Mockito.any());
        deviceController.getAllFactoryData(null, null, null, null, null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV4.findAllFactoryData(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV4_throwDeviceNotFoundException() {
        Mockito.doThrow(DeviceNotFoundException.class).when(allDeviceDetailsServiceV4)
            .findAllFactoryData(Mockito.any(), Mockito.any());
        deviceController.getAllFactoryData(null, null, null, null, null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV4.findAllFactoryData(Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV4_throwException() {
        Mockito.doThrow(ApiValidationFailedException.class).when(allDeviceDetailsServiceV4)
            .findAllFactoryData(Mockito.any(), Mockito.any());
        deviceController.getAllFactoryData(null, null, null, null, null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV4.findAllFactoryData(Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV4_throwPageParamResolverException_Imei() {
        Mockito.doThrow(PageParamResolverException.class).when(allDeviceDetailsServiceV4)
            .findAllFactoryData(Mockito.any(), Mockito.any());
        deviceController.getAllFactoryData("12345", null, null, null, null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV4.findAllFactoryData(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV4_throwSizeParamResolverException_Imei() {
        Mockito.doThrow(SizeParamResolverException.class).when(allDeviceDetailsServiceV4)
            .findAllFactoryData(Mockito.any(), Mockito.any());
        deviceController.getAllFactoryData("12345", "20", null, null, null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV4.findAllFactoryData(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV4_throwInputParamValidationException_Imei() {
        Mockito.doThrow(InputParamValidationException.class).when(allDeviceDetailsServiceV4)
            .findAllFactoryData(Mockito.any(), Mockito.any());
        deviceController.getAllFactoryData("12345", null, "4", null, null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV4.findAllFactoryData(Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV4_throwDeviceNotFoundException_Imei() {
        Mockito.doThrow(DeviceNotFoundException.class).when(allDeviceDetailsServiceV4)
            .findAllFactoryData(Mockito.any(), Mockito.any());
        deviceController.getAllFactoryData("12345", null, null, "imei", null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV4.findAllFactoryData(Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV4_throwException_Imei() {
        Mockito.doThrow(ApiValidationFailedException.class).when(allDeviceDetailsServiceV4)
            .findAllFactoryData(Mockito.any(), Mockito.any());
        deviceController.getAllFactoryData("12345", null, null, null, "imei");
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV4.findAllFactoryData(Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV5() {
        DeviceInfoAggregateFactoryData aggregateData = new DeviceInfoAggregateFactoryData();
        DeviceInfoAggregateFactoryData.StateCount stateCount = new DeviceInfoAggregateFactoryData.StateCount();
        stateCount.setProvisioned(1L);
        aggregateData.setStateCount(stateCount);
        aggregateData.setCount(1L);
        DeviceInfoPage.HcpPageable hcpPageable = new DeviceInfoPage.HcpPageable(1, 1, 1L);
        DeviceInfoPage<List<DeviceInfoFactoryData>> deviceInfoPage =
            new DeviceInfoPage(aggregateData, expectedFactoryData, hcpPageable);
        Mockito.doReturn(deviceInfoPage).when(allDeviceDetailsServiceV2).findAllFactoryData(Mockito.any());
        ResponseEntity<?> responseEntity = deviceController
            .getAllFactoryData(null, null, null, null, null, null, null);

        DeviceInfoPage deviceInfoPage1 = (DeviceInfoPage) responseEntity.getBody();
        List<DeviceInfoFactoryData> actualFactoryData = deviceInfoPage1.getDevices();
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
        assertEquals(expectedFactoryData.get(0).getModel(), actualFactoryData.get(0).getModel());
        assertEquals(expectedFactoryData.get(0).getImei(), actualFactoryData.get(0).getImei());
        assertEquals(expectedFactoryData.get(0).getSerialNumber(), actualFactoryData.get(0).getSerialNumber());
        assertEquals(expectedFactoryData.get(0).getPlatformVersion(), actualFactoryData.get(0).getPlatformVersion());
        assertEquals(expectedFactoryData.get(0).getIccid(), actualFactoryData.get(0).getIccid());
        assertEquals(expectedFactoryData.get(0).getSsid(), actualFactoryData.get(0).getSsid());
        assertEquals(expectedFactoryData.get(0).getBssid(), actualFactoryData.get(0).getBssid());
        assertEquals(expectedFactoryData.get(0).getMsisdn(), actualFactoryData.get(0).getMsisdn());
        assertEquals(expectedFactoryData.get(0).getImsi(), actualFactoryData.get(0).getImsi());
    }

    @Test
    public void getDeviceDetailsV5_actualMethodCall() {

        RecordStats<DeviceInfoAggregateFactoryData> recordStats = new RecordStats<>();
        recordStats.setPage(1);
        recordStats.setSize(1);
        recordStats.setTotal(1);
        recordStats.setAggregate(null);
        DeviceInfoQueryDto<DeviceInfoFactoryDataWithSubscription> allFactoryData = new DeviceInfoQueryDto<>();
        allFactoryData.setRecordStats(recordStats);
        allFactoryData.setData(deviceInfoFactoryDataWithSubscriptionList);

        DeviceInfoQuerySuccessResponse expectedResponse = new DeviceInfoQuerySuccessResponse();
        expectedResponse.setHttpStatusCode(STATUS_CODE_200);
        expectedResponse.setRequestId("req1");
        expectedResponse.setCode("HCP-001");
        expectedResponse.setReason("Device details retrieval");
        expectedResponse.setMessage(
            "Device details fetched based on either full/partial imei or serialNumber or deviceId");
        expectedResponse.setRecordStats(allFactoryData.getRecordStats());
        expectedResponse.setData(allFactoryData.getData());

        Mockito.doReturn(false).when(config)
            .getBooleanValue(DeviceInfoQueryProperty.ENABLE_DEVICE_SUBSCRIPTION_DETAILS);
        Mockito.doReturn(allFactoryData).when(allDeviceDetailsServiceV5).findAllFactoryData(
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<?> responseEntity = deviceController
            .getAllFactoryDataWithSubscriptionStatus("9900008624711007", "1", "1", "1007",
                null, "TESTVIN_Make:Test_Model:200_Year:2014_Type:Car_0", null, null, null);

        DeviceInfoQuerySuccessResponse actualResponse = (DeviceInfoQuerySuccessResponse) responseEntity.getBody();

        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
        assertEquals(expectedResponse.getCode(), actualResponse.getCode());
        assertEquals(expectedResponse.getReason(), actualResponse.getReason());
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
        List<DeviceInfoFactoryDataWithSubscription> actualFactoryData = (List) actualResponse.getData();
        List<DeviceInfoFactoryDataWithSubscription> expectedFactoryData = expectedResponse.getData();
        assertEquals(expectedFactoryData.get(0).getModel(), actualFactoryData.get(0).getModel());
        assertEquals(expectedFactoryData.get(0).getImei(), actualFactoryData.get(0).getImei());
        assertEquals(expectedFactoryData.get(0).getSerialNumber(), actualFactoryData.get(0).getSerialNumber());
        assertEquals(expectedFactoryData.get(0).getPlatformVersion(), actualFactoryData.get(0).getPlatformVersion());
        assertEquals(expectedFactoryData.get(0).getIccid(), actualFactoryData.get(0).getIccid());
        assertEquals(expectedFactoryData.get(0).getSsid(), actualFactoryData.get(0).getSsid());
        assertEquals(expectedFactoryData.get(0).getBssid(), actualFactoryData.get(0).getBssid());
        assertEquals(expectedFactoryData.get(0).getMsisdn(), actualFactoryData.get(0).getMsisdn());
        assertEquals(expectedFactoryData.get(0).getImsi(), actualFactoryData.get(0).getImsi());
    }

    @Test
    public void getDeviceDetailsV5_throwPageParamResolverException() {
        Mockito.doThrow(PageParamResolverException.class).when(allDeviceDetailsServiceV5).findAllFactoryData(
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        deviceController
            .getAllFactoryDataWithSubscriptionStatus("9900008624711007", "1", "1", "1007",
                null, "TESTVIN_Make:Test_Model:200_Year:2014_Type:Car_0", null, null, null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV5.findAllFactoryData(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV5_throwSizeParamResolverException() {
        Mockito.doThrow(SizeParamResolverException.class).when(allDeviceDetailsServiceV5).findAllFactoryData(
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        deviceController
            .getAllFactoryDataWithSubscriptionStatus("9900008624711007", "1", "1", "1007",
                null, "TESTVIN_Make:Test_Model:200_Year:2014_Type:Car_0", null, null, null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV5.findAllFactoryData(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV5_throwInputParamValidationException() {
        Mockito.doThrow(InputParamValidationException.class).when(allDeviceDetailsServiceV5).findAllFactoryData(
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        deviceController
            .getAllFactoryDataWithSubscriptionStatus("9900008624711007", "1", "1", "1007",
                null, "TESTVIN_Make:Test_Model:200_Year:2014_Type:Car_0", null, null, null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV5.findAllFactoryData(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV5_throwInputIllegalArgumentException() {

        Mockito.doThrow(IllegalArgumentException.class).when(allDeviceDetailsServiceV5).findAllFactoryData(
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        deviceController
            .getAllFactoryDataWithSubscriptionStatus("9900008624711007", "1", "1", "1007",
                null, "TESTVIN_Make:Test_Model_Model:200_Year:2014_Type:Car_0", null, null, null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV5.findAllFactoryData(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV5_throwDeviceNotFoundException_imei() {
        Mockito.doThrow(DeviceNotFoundException.class).when(allDeviceDetailsServiceV5).findAllFactoryData(
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        deviceController
            .getAllFactoryDataWithSubscriptionStatus("123455", "1", "1", "1007",
                null, "TESTVIN_Make:Test_Model_Model:200_Year:2014_Type:Car_0", "imei", "imei", "ACTIVE");
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV5.findAllFactoryData(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV5_throwDeviceNotFoundException_serialNumber() {

        Mockito.doThrow(DeviceNotFoundException.class).when(allDeviceDetailsServiceV5).findAllFactoryData(
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        deviceController
            .getAllFactoryDataWithSubscriptionStatus(null, "1", "1", "1007",
                null, "TESTVIN_Make:Test_Model_Model:200_Year:2014_Type:Car_0", null, null, null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV5.findAllFactoryData(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV5_throwDeviceNotFoundException_deviceId() {
        Mockito.doThrow(DeviceNotFoundException.class).when(allDeviceDetailsServiceV5).findAllFactoryData(
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        deviceController
            .getAllFactoryDataWithSubscriptionStatus(null, "1", "1", null,
                "har123", "TESTVIN_Make:Test_Model_Model:200_Year:2014_Type:Car_0", null, null, null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV5.findAllFactoryData(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV5_throwDeviceNotFoundException_state() {
        Mockito.doThrow(DeviceNotFoundException.class).when(allDeviceDetailsServiceV5).findAllFactoryData(
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        deviceController
            .getAllFactoryDataWithSubscriptionStatus(null, "1", "1", null,
                null, "TESTVIN_Make:Test_Model_Model:200_Year:2014_Type:Car_0", null, null, "PROVISIONED");
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV5.findAllFactoryData(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV5_throwDeviceNotFoundException() {

        Mockito.doThrow(DeviceNotFoundException.class).when(allDeviceDetailsServiceV5).findAllFactoryData(
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        deviceController
            .getAllFactoryDataWithSubscriptionStatus(null, "1", "1", null,
                null, "TESTVIN_Make:Test_Model_Model:200_Year:2014_Type:Car_0", null, null, null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV5.findAllFactoryData(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV5_throwException() {
        Mockito.doThrow(ApiValidationFailedException.class).when(allDeviceDetailsServiceV5).findAllFactoryData(
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        deviceController
            .getAllFactoryDataWithSubscriptionStatus(null, "1", "1", null,
                null, "TESTVIN_Make:Test_Model_Model:200_Year:2014_Type:Car_0", null, null, null);
        boolean actualResponse = false;
        try {
            allDeviceDetailsServiceV5.findAllFactoryData(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void deleteDeviceDetailsV1() {

        Mockito.doNothing().when(deviceInfoQueryService).deleteDeviceFactoryData(Mockito.any(), Mockito.any());
        ResponseEntity<?> responseEntity = deviceController.deleteDevice("9900008624711007", "1007", USER_ID);

        SimpleResponseMessage actualSimpleResponseMessage = (SimpleResponseMessage) responseEntity.getBody();
        assertNotNull(actualSimpleResponseMessage);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
        assertEquals(SUCCESS, actualSimpleResponseMessage.getMessage());
    }

    @Test
    public void deleteDeviceDetailsV1_emptyImeiSerialNumber() {
        ResponseEntity<?> responseEntity = deviceController.deleteDevice(null, null, USER_ID);
        assertNotNull(USER_ID);
    }

    @Test
    public void deleteDeviceDetailsV1_throwDeleteDeviceException() {
        Mockito.doThrow(DeleteDeviceException.class).when(deviceInfoQueryService)
            .deleteDeviceFactoryData(Mockito.any(), Mockito.any());
        deviceController.deleteDevice("9900008624711007", "1007", USER_ID);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.deleteDeviceFactoryData(Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void deleteDeviceDetailsV1_throwIllegalArgumentException() {
        Mockito.doThrow(IllegalArgumentException.class).when(deviceInfoQueryService)
            .deleteDeviceFactoryData(Mockito.any(), Mockito.any());
        deviceController.deleteDevice("9900008624711007", "1007", USER_ID);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.deleteDeviceFactoryData(Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void deleteDeviceDetailsV1_throwInputParamValidationException() {
        Mockito.doThrow(InputParamValidationException.class).when(deviceInfoQueryService)
            .deleteDeviceFactoryData(Mockito.any(), Mockito.any());
        deviceController.deleteDevice("9900008624711007", "1007", USER_ID);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.deleteDeviceFactoryData(Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void deleteDeviceDetailsV1_throwDeviceNotFoundException() {
        Mockito.doThrow(DeviceNotFoundException.class).when(deviceInfoQueryService)
            .deleteDeviceFactoryData(Mockito.any(), Mockito.any());
        deviceController.deleteDevice("9900008624711007", "1007", USER_ID);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.deleteDeviceFactoryData(Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void deleteDeviceDetailsV1_throwException() {
        Mockito.doThrow(ApiValidationFailedException.class).when(deviceInfoQueryService)
            .deleteDeviceFactoryData(Mockito.any(), Mockito.any());
        deviceController.deleteDevice("9900008624711007", "1007", USER_ID);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.deleteDeviceFactoryData(Mockito.any(), Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void putDeviceState() throws Exception {
        StateChange st = new StateChange();
        st.setImei("1234");
        st.setFactoryId(0L);
        st.setState(DeviceState.PROVISIONED);

        Mockito.doNothing().when(deviceInfoQueryService).changeDeviceState(Mockito.any());
        ResponseEntity<Object> responseEntity = deviceController.changeDeviceState(st);

        SimpleResponseMessage actualSimpleResponseMessage = (SimpleResponseMessage) responseEntity.getBody();
        assertNotNull(actualSimpleResponseMessage);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
        assertEquals(SUCCESS, actualSimpleResponseMessage.getMessage());
    }

    @Test
    public void updateDevice() throws Exception {

        Mockito.doNothing().when(deviceInfoQueryService).updateDeviceInfoFactoryData(Mockito.any());
        ResponseEntity<?> responseEntity = deviceController.updateDevice(deviceUpdateRequest, USER_ID);

        SimpleResponseMessage actualSimpleResponseMessage = (SimpleResponseMessage) responseEntity.getBody();
        assertNotNull(actualSimpleResponseMessage);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
        assertEquals(SUCCESS, actualSimpleResponseMessage.getMessage());
    }

    @Test
    public void updateDevice_InvalidAttributeValueException() throws Exception {
        Mockito.doThrow(InvalidAttributeValueException.class).when(deviceInfoQueryService)
            .updateDeviceInfoFactoryData(Mockito.any());
        deviceController.updateDevice(deviceUpdateRequest, USER_ID);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.updateDeviceInfoFactoryData(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void updateDevice_ParseException() throws Exception {
        Mockito.doThrow(ParseException.class).when(deviceInfoQueryService).updateDeviceInfoFactoryData(Mockito.any());
        deviceController.updateDevice(deviceUpdateRequest, USER_ID);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.updateDeviceInfoFactoryData(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void updateDevice_DeviceNotFoundException() throws Exception {
        Mockito.doThrow(DeviceNotFoundException.class).when(deviceInfoQueryService)
            .updateDeviceInfoFactoryData(Mockito.any());
        deviceController.updateDevice(deviceUpdateRequest, USER_ID);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.updateDeviceInfoFactoryData(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void updateDevice_DataAccessException()
        throws DataAccessException, ParseException, InvalidAttributeValueException {
        Mockito.doThrow(new DataAccessException("...") {
        }).when(deviceInfoQueryService).updateDeviceInfoFactoryData(Mockito.any(DeviceUpdateRequest.class));
        deviceController.updateDevice(deviceUpdateRequest, USER_ID);
        assertNotNull(USER_ID);
    }

    @Test
    public void updateDevice_ApiValidationFailedException() throws Exception {
        Mockito.doThrow(ApiValidationFailedException.class).when(deviceInfoQueryService)
            .updateDeviceInfoFactoryData(Mockito.any());
        deviceController.updateDevice(deviceUpdateRequest, USER_ID);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.updateDeviceInfoFactoryData(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void updateDevice_InvalidDeviceStateException() throws Exception {
        Mockito.doThrow(InvalidDeviceStateException.class).when(deviceInfoQueryService)
            .updateDeviceInfoFactoryData(Mockito.any());
        deviceController.updateDevice(deviceUpdateRequest, USER_ID);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.updateDeviceInfoFactoryData(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void updateDevice_Exception() throws Exception {
        Mockito.doThrow(IllegalArgumentException.class).when(deviceInfoQueryService)
            .updateDeviceInfoFactoryData(Mockito.any());
        deviceController.updateDevice(deviceUpdateRequest, USER_ID);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.updateDeviceInfoFactoryData(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV3() {
        DeviceInfoAggregateFactoryData aggregateData = new DeviceInfoAggregateFactoryData();
        DeviceInfoAggregateFactoryData.StateCount stateCount = new DeviceInfoAggregateFactoryData.StateCount();
        stateCount.setProvisioned(1L);
        aggregateData.setStateCount(stateCount);
        aggregateData.setCount(1L);
        DeviceInfoPage.HcpPageable hcpPageable = new DeviceInfoPage.HcpPageable(1, 1, 1L);
        DeviceInfoPage<List<DeviceInfoFactoryData>> deviceInfoPage =
            new DeviceInfoPage(aggregateData, expectedFactoryData, hcpPageable);
        Mockito.doReturn(deviceInfoPage).when(deviceInfoQueryService).getAllFactoryDataV3(Mockito.anyMap());
        ResponseEntity<?> responseEntity = deviceController.getAllFactoryDataV3(null, "true", null,
            null, null, null, null, null, null, null);

        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void getDeviceDetailsV3_throwDeviceNotFoundException() {
        Mockito.doThrow(DeviceNotFoundException.class).when(deviceInfoQueryService)
            .getAllFactoryDataV3(Mockito.anyMap());
        deviceController.getAllFactoryDataV3(null, "true", null,
            null, null, null, null, null, null, null);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.getAllFactoryDataV3(Mockito.anyMap());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV3_throwPageParamResolverException() {
        Mockito.doThrow(PageParamResolverException.class).when(deviceInfoQueryService)
            .getAllFactoryDataV3(Mockito.anyMap());
        deviceController.getAllFactoryDataV3(null, "true", null,
            null, null, null, null, null, null, null);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.getAllFactoryDataV3(Mockito.anyMap());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV3_throwSizeParamResolverException() {

        Mockito.doThrow(SizeParamResolverException.class).when(deviceInfoQueryService)
            .getAllFactoryDataV3(Mockito.anyMap());
        deviceController.getAllFactoryDataV3(null, "true", null,
            null, null, null, null, null, null, null);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.getAllFactoryDataV3(Mockito.anyMap());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceDetailsV3_throwException() {
        Mockito.doThrow(ApiValidationFailedException.class).when(deviceInfoQueryService)
            .getAllFactoryDataV3(Mockito.anyMap());
        deviceController.getAllFactoryDataV3(null, "true", null,
            null, null, null, null, null, null, null);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.getAllFactoryDataV3(Mockito.anyMap());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceImeiState() {
        RecordStats recordStats = new RecordStats<>();
        recordStats.setPage(1);
        recordStats.setSize(1);
        recordStats.setTotal(1);

        DeviceStateHistory history1 = new DeviceStateHistory();
        history1.setManufacturingDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        history1.setModel("MX127777");
        history1.setImei("9900008624711007");
        history1.setSerialNumber("1007");
        history1.setPlatformVersion("v1");
        history1.setIccid("99911012000032041007");
        history1.setSsid("SSID001007");
        history1.setBssid("d8:c7:c8:44:32:1007");
        history1.setMsisdn("+9190035631007");
        history1.setImsi("4100728213931007");
        history1.setRecordDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        history1.setState("PROVISIONED");

        DeviceStateHistory history2 = new DeviceStateHistory();
        history2.setManufacturingDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        history2.setModel("MX127777");
        history2.setImei("9900008624711007");
        history2.setSerialNumber("1007");
        history2.setPlatformVersion("v1");
        history2.setIccid("99911012000032041007");
        history2.setSsid("SSID001007");
        history2.setBssid("d8:c7:c8:44:32:1007");
        history2.setMsisdn("+9190035631007");
        history2.setImsi("4100728213931007");
        history2.setRecordDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        history2.setState("ACTIVE");

        List<DeviceStateHistory> deviceStates = new ArrayList<>();
        deviceStates.add(history1);
        deviceStates.add(history2);

        DeviceInfoQuerySuccessResponse deviceInfoQuerySuccessResponse = new DeviceInfoQuerySuccessResponse<>();
        deviceInfoQuerySuccessResponse.setCode("HCP-0001");
        deviceInfoQuerySuccessResponse.setData(deviceStates);
        deviceInfoQuerySuccessResponse.setHttpStatusCode(STATUS_CODE_200);
        deviceInfoQuerySuccessResponse.setMessage(ResponseConstants.DEVICE_STATE_RETRIEVAL_SUCCESS_MSG);
        deviceInfoQuerySuccessResponse.setReason("Device states retrieval");
        deviceInfoQuerySuccessResponse.setRecordStats(recordStats);
        deviceInfoQuerySuccessResponse.setRequestId("Req-0001");

        Mockito.doReturn(deviceInfoQuerySuccessResponse).when(deviceInfoQueryService)
            .findAllDeviceStates(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<HcpServicesBaseResponse> responseEntity =
            deviceController.findAllDeviceStates("9900008624711007", null, null,
                null, null);

        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void getDeviceImeiState_throwPageParamResolverException() {
        Mockito.doThrow(PageParamResolverException.class).when(deviceInfoQueryService)
            .findAllDeviceStates(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        deviceController.findAllDeviceStates("9900008624711007", null, null,
            null, null);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.findAllDeviceStates(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceImeiState_throwSizeParamResolverException() {
        Mockito.doThrow(SizeParamResolverException.class).when(deviceInfoQueryService)
            .findAllDeviceStates(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        deviceController.findAllDeviceStates("9900008624711007", null, null,
            null, null);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.findAllDeviceStates(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceImeiState_throwDeviceNotFoundException() {
        Mockito.doThrow(DeviceNotFoundException.class).when(deviceInfoQueryService)
            .findAllDeviceStates(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        deviceController.findAllDeviceStates("9900008624711007", null, null,
            null, null);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.findAllDeviceStates(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceImeiState_throwInputParamValidationException() {
        Mockito.doThrow(InputParamValidationException.class).when(deviceInfoQueryService)
            .findAllDeviceStates(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        deviceController.findAllDeviceStates("9900008624711007", null, null,
            null, null);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.findAllDeviceStates(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void getDeviceImeiState_throwException() {
        Mockito.doThrow(ApiValidationFailedException.class).when(deviceInfoQueryService)
            .findAllDeviceStates(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        deviceController.findAllDeviceStates("9900008624711007", null, null,
            null, null);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.findAllDeviceStates(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void postDeviceFilter() {

        DeviceFilterDto deviceFilterDto = new DeviceFilterDto();

        Mockito.doReturn(expectedFactoryData).when(deviceInfoQueryService).filterDevice(Mockito.any());
        ResponseEntity<ExtendedApiResponse<Object>> responseEntity = deviceController.findByFilter(deviceFilterDto);
        assertNotNull(responseEntity);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void postDeviceFilter_zeroResult() {

        DeviceFilterDto deviceFilterDto = new DeviceFilterDto();

        List<DeviceInfoFactoryData> data = new ArrayList<>();

        Mockito.doReturn(data).when(deviceInfoQueryService).filterDevice(Mockito.any());
        ResponseEntity<ExtendedApiResponse<Object>> responseEntity = deviceController.findByFilter(deviceFilterDto);
        assertNotNull(responseEntity);
        assertEquals(STATUS_CODE_404, responseEntity.getStatusCodeValue());
        assertEquals(DEVICE_NOT_FOUND.getGeneralMessage(), responseEntity.getBody().getMessage());
    }

    @Test
    public void postDeviceFilter_throwException() {
        DeviceFilterDto deviceFilterDto = new DeviceFilterDto();
        Mockito.doThrow(ApiValidationFailedException.class).when(deviceInfoQueryService).filterDevice(Mockito.any());
        deviceController.findByFilter(deviceFilterDto);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.filterDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void patchUpdateDevice() {

        Mockito.doReturn(1).when(deviceInfoQueryService).updateDevice(Mockito.any());
        ResponseEntity<ExtendedApiResponse<Object>> responseEntity =
            deviceController.updateDevice(deviceInfoFactoryData, ID);
        assertNotNull(responseEntity);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void patchUpdateDevice_zeroUpdateCount() {

        Mockito.doReturn(0).when(deviceInfoQueryService).updateDevice(Mockito.any());
        ResponseEntity<ExtendedApiResponse<Object>> responseEntity =
            deviceController.updateDevice(deviceInfoFactoryData, ID);
        assertNotNull(responseEntity);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
        assertEquals(DEVICE_UPDATE_FAILED.getGeneralMessage(), responseEntity.getBody().getMessage());
    }

    @Test
    public void patchUpdateDevice_throwApiValidationFailedException() {
        Mockito.doThrow(ApiValidationFailedException.class).when(deviceInfoQueryService).updateDevice(Mockito.any());
        deviceController.updateDevice(deviceInfoFactoryData, ID);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.updateDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void patchUpdateDevice_throwException() {
        Mockito.doThrow(SecurityException.class).when(deviceInfoQueryService).updateDevice(Mockito.any());
        deviceController.updateDevice(deviceInfoFactoryData, ID);
        boolean actualResponse = false;
        try {
            deviceInfoQueryService.updateDevice(Mockito.any());
        } catch (Exception e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

}
