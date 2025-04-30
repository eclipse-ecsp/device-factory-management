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

package org.eclipse.ecsp.service.swm.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.eclipse.ecsp.common.CommonConstants;
import org.eclipse.ecsp.common.exception.ApiTechnicalException;
import org.eclipse.ecsp.config.DeviceInfoQueryProperty;
import org.eclipse.ecsp.dto.swm.RepresentationObject;
import org.eclipse.ecsp.dto.swm.SwmActionResult;
import org.eclipse.ecsp.dto.swm.SwmRequest;
import org.eclipse.ecsp.dto.swm.SwmResponse;
import org.eclipse.ecsp.dto.swm.SwmUpdateVehicleRequest;
import org.eclipse.ecsp.dto.swm.SwmVehicleIds;
import org.eclipse.ecsp.dto.swm.SwmVehiclesRequest;
import org.eclipse.ecsp.dto.swm.SwmVinRequest;
import org.eclipse.ecsp.dto.swm.VehicleModelsDto;
import org.eclipse.ecsp.dto.swm.VehiclePost;
import org.eclipse.ecsp.service.swm.AbstractSwmCrudService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.eclipse.ecsp.common.enums.ApiMessageEnum.SWM_VEHICLE_CREATION_FAILED;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.SWM_VEHICLE_CREATION_INTERNAL_ERROR;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.SWM_VEHICLE_CREATION_RESPONSE_JSON_PARSE_FAILED;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.SWM_VEHICLE_DELETE_FAILED;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.SWM_VEHICLE_DELETE_RESPONSE_JSON_PARSE_FAILED;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.SWM_VEHICLE_UPDATE_FAILED;

/**
 * This class implements the CRUD operations for SWM (Smart Waste Management) vehicles.
 * It extends the AbstractSwmCrudService class and provides implementations for creating, updating, and deleting
 * vehicles.
 */
@Service
public class SwmCrudServiceImpl extends AbstractSwmCrudService<SwmRequest> {

    /**
     * Creates a vehicle in the SWM (Smart Waste Management) system.
     *
     * @param swmRequest The SWM request containing the details of the vehicle to be created.
     * @return true if the vehicle is created successfully, false otherwise.
     * @throws ApiTechnicalException If there is an error while creating the vehicle.
     */
    @Override
    public boolean createVehicle(SwmRequest swmRequest) {
        LOGGER.debug("## SWM createVehicle - START");
        HttpHeaders headers = createHeaders(findSessionId());
        SwmVehiclesRequest swmVehiclesRequest = (SwmVehiclesRequest) swmRequest;
        updateVehicleModelId(headers, swmVehiclesRequest);
        HttpEntity<SwmVehiclesRequest> entity = new HttpEntity<>(swmVehiclesRequest, headers);
        LOGGER.debug("## calling swm Create vehicle api :{} with payload: {} ", createVehicleUrl, entity.getBody());
        ResponseEntity<String> response =
            restTemplate.exchange(createVehicleUrl, HttpMethod.POST, entity, String.class);
        LOGGER.debug("## Response code: {}, from: {}", response.getStatusCode(), createVehicleUrl);
        boolean createVehicleStatus = false;
        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                SwmResponse swmResponse = mapper.readValue(response.getBody(), SwmResponse.class);
                SwmActionResult swmActionResult = swmResponse.getActionResult().get(0);
                if (0 == swmActionResult.getCode()) {
                    LOGGER.debug("## Vehicle Create Successfully : {} ", swmVehiclesRequest);
                    createVehicleStatus = true;
                } else {
                    String errorMessage;
                    if (swmActionResult.getReasonMessage() != null) {
                        errorMessage = swmActionResult.getReasonMessage().getLocalizedMessage();
                    } else {
                        errorMessage = swmActionResult.getResultMessage().getLocalizedMessage();
                    }
                    LOGGER.error("## SWM Vehicles creation failed: {} ", errorMessage);
                    return handleWhenVehicleAlreadyCreatedInSwm(errorMessage);
                }
            } catch (IOException e) {
                throw new ApiTechnicalException(SWM_VEHICLE_CREATION_RESPONSE_JSON_PARSE_FAILED.getCode(),
                    SWM_VEHICLE_CREATION_RESPONSE_JSON_PARSE_FAILED.getMessage(),
                    SWM_VEHICLE_CREATION_RESPONSE_JSON_PARSE_FAILED.getGeneralMessage());
            } catch (Exception e) {
                throw new ApiTechnicalException(SWM_VEHICLE_CREATION_FAILED.getCode(),
                    SWM_VEHICLE_CREATION_FAILED.getMessage(), SWM_VEHICLE_CREATION_FAILED.getGeneralMessage());
            }
        }
        LOGGER.debug("## SWM createVehicle - END");
        return createVehicleStatus;
    }

    /**
     * Handles the case when a vehicle is already created in the SWM (Smart Waste Management) system.
     *
     * @param errorMessage The error message indicating that the vehicle already exists in SWM.
     * @return true if the vehicle already exists in SWM, false otherwise.
     * @throws ApiTechnicalException if the vehicle creation in SWM encounters an internal error.
     */
    private boolean handleWhenVehicleAlreadyCreatedInSwm(String errorMessage) {
        if (CommonConstants.SWM_VEHICLE_ALREADY_EXIST.equals(errorMessage)) {
            LOGGER.debug("SWM error message: {}", errorMessage);
            return true;
        } else {
            throw new ApiTechnicalException(SWM_VEHICLE_CREATION_INTERNAL_ERROR.getCode(),
                SWM_VEHICLE_CREATION_INTERNAL_ERROR.getMessage(),
                SWM_VEHICLE_CREATION_INTERNAL_ERROR.getGeneralMessage());
        }
    }

    /**
     * Updates the vehicle model ID for the given SwmVehiclesRequest.
     *
     * @param headers The HttpHeaders object containing the request headers.
     * @param swmVehiclesRequest The SwmVehiclesRequest object to update.
     */
    private void updateVehicleModelId(HttpHeaders headers, SwmVehiclesRequest swmVehiclesRequest) {
        LOGGER.debug("## updateVehicleModelId - START");
        List<VehiclePost> vehiclePosts = swmVehiclesRequest.getVehiclePost();
        for (VehiclePost vp : vehiclePosts) {
            vp.setVehicleModelId(findVehicleModelId(headers, vp.getVehicleModelId()));
        }
        LOGGER.debug("## updateVehicleModelId - END");
    }

    /**
     * Finds the vehicle model ID based on the provided factory data model.
     *
     * @param headers The HttpHeaders object containing the request headers.
     * @param factoryDataModel The factory data model to search for.
     * @return The ID of the matched vehicle model, or the default vehicle model ID if no match is found.
     */
    private String findVehicleModelId(HttpHeaders headers, String factoryDataModel) {
        LOGGER.debug("## findVehicleModelId - START factoryDataModel: {}", factoryDataModel);
        String id = null;
        try {
            LOGGER.debug("## Calling swm vehicle Models api: {} ", vehicleModelsUrl);
            ResponseEntity<String> response = callSwmVehicleModelApi(headers);
            String responseJson = response.getBody();
            LOGGER.debug("## responseJson: {}", responseJson);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            VehicleModelsDto vehicleModelsDto = mapper.readValue(responseJson, VehicleModelsDto.class);
            List<RepresentationObject> representationObjects = vehicleModelsDto.getRepresentationObjects();
            for (RepresentationObject robj : representationObjects) {
                String modelCode = robj.getModelCode();
                if (modelCode.equals(factoryDataModel)) {
                    id = robj.getId();
                    LOGGER.debug("## Matched Factory model: {} with SWM modelCode: {}, so its id: {}", factoryDataModel,
                        modelCode, id);
                    break;
                }
            }
            if (id == null) {
                id = vehicleModelId;
            }
        } catch (RuntimeException ex) {
            LOGGER.error("failed to get Model Id from SWM so using vehicleModelId from yaml with Exception:{}",
                ex.getMessage());
            id = vehicleModelId;
        } catch (Exception e) {
            LOGGER.error("## Failed to get Model Id from SWM so using vehicleModelId from yaml");
            id = vehicleModelId;
        }
        LOGGER.debug("## findVehicleModelId - END id: {}", id);
        return id;
    }

    /**
     * Updates a vehicle in the system based on the provided SwmRequest.
     *
     * @param swmRequest The SwmRequest object containing the details of the vehicle to be updated.
     * @return true if the vehicle update is successful, false otherwise.
     * @throws ApiTechnicalException if there is an error during the vehicle update process.
     */
    @Override
    public boolean updateVehicle(SwmRequest swmRequest) {
        LOGGER.debug("## updateVehicle - START swmRequest: {}", swmRequest);
        boolean updateStatus;
        try {
            SwmUpdateVehicleRequest swmUpdateVehicleRequest = (SwmUpdateVehicleRequest) swmRequest;
            SwmVinRequest swmVinRequest = new SwmVinRequest();
            swmVinRequest.setVin(swmUpdateVehicleRequest.getVin());
            String id = findIdToDeleteAndUpdateVehicle(swmVinRequest);
            LOGGER.debug("## Before swm update vehicle - id: {}", id);
            swmUpdateVehicleRequest.setId(id);
            HttpEntity<SwmUpdateVehicleRequest> entity =
                new HttpEntity<>(swmUpdateVehicleRequest, createHeaders(findSessionId()));
            LOGGER.debug("## updateVehicleUrl: {}, swmUpdateVehicleRequest: {}", updateVehicleUrl,
                swmUpdateVehicleRequest);
            ResponseEntity<String> updateVehicleResponse =
                restTemplate.exchange(updateVehicleUrl, HttpMethod.PUT, entity, String.class);
            LOGGER.debug("## updateVehicleResponse status code: {}", updateVehicleResponse.getStatusCode().value());
            HttpStatusCode statusCode = updateVehicleResponse.getStatusCode();
            updateStatus = statusCode.value() == HttpStatus.OK.value();
        } catch (RuntimeException ex) {
            LOGGER.error("vehicle updation failed.Error Message:{}", ex.getMessage());
            throw new ApiTechnicalException(SWM_VEHICLE_UPDATE_FAILED.getCode(),
                SWM_VEHICLE_UPDATE_FAILED.getMessage(), SWM_VEHICLE_UPDATE_FAILED.getGeneralMessage());
        } catch (Exception e) {
            throw new ApiTechnicalException(SWM_VEHICLE_UPDATE_FAILED.getCode(),
                SWM_VEHICLE_UPDATE_FAILED.getMessage(), SWM_VEHICLE_UPDATE_FAILED.getGeneralMessage());
        }
        LOGGER.debug("## vehicle update status: {}", updateStatus);
        LOGGER.debug("## updateVehicle - END");
        return updateStatus;
    }

    /**
     * Deletes a vehicle based on the provided SwmRequest.
     *
     * @param swmRequest The SwmRequest containing the necessary information for deleting the vehicle.
     * @return true if the vehicle is successfully deleted, false otherwise.
     * @throws ApiTechnicalException If there is an error while deleting the vehicle.
     */
    @Override
    public boolean deleteVehicle(SwmRequest swmRequest) {
        LOGGER.debug("## deleteVehicle - START swmRequest: {}", swmRequest);
        boolean deleteStatus;
        try {
            LOGGER.debug("## calling swm delete vehicle api :{} ", deleteVehicleUrl);
            LOGGER.debug("Step-1 Find id using vehiclesUrl: {}", vehiclesUrl);
            String id = findIdToDeleteAndUpdateVehicle(swmRequest);
            if (id != null) {
                LOGGER.debug("## Found id: {} for given VIN", id);
                LOGGER.debug("## Step-2 Call swm delete api: {}", deleteVehicleUrl);
                SwmVehicleIds swmVehicleIds = new SwmVehicleIds();
                List<String> vehicleIdList = new ArrayList<>();
                vehicleIdList.add(id);
                swmVehicleIds.setVehicleIds(vehicleIdList);
                HttpEntity<SwmVehicleIds> deleteEntity =
                    new HttpEntity<>(swmVehicleIds, createHeaders(findSessionId()));
                ResponseEntity<String> deleteResponse =
                    restTemplate.exchange(deleteVehicleUrl, HttpMethod.PUT, deleteEntity, String.class);
                HttpStatusCode deleteResponseStatusCode = deleteResponse.getStatusCode();
                if (deleteResponseStatusCode.value() == HttpStatus.OK.value()) {
                    LOGGER.debug("## Vehicle deleted from SWM successfully");
                    deleteStatus = true;
                } else {
                    LOGGER.info("## Unable to delete vehicle from SWM, deleteResponseStatusCode: {}",
                        deleteResponseStatusCode.value());
                    deleteStatus = false;
                }
            } else {
                deleteStatus = false;
            }
        } catch (IOException e) {
            throw new ApiTechnicalException(SWM_VEHICLE_DELETE_RESPONSE_JSON_PARSE_FAILED.getCode(),
                SWM_VEHICLE_DELETE_RESPONSE_JSON_PARSE_FAILED.getMessage(),
                SWM_VEHICLE_DELETE_RESPONSE_JSON_PARSE_FAILED.getGeneralMessage());
        } catch (Exception e) {
            throw new ApiTechnicalException(SWM_VEHICLE_DELETE_FAILED.getCode(),
                SWM_VEHICLE_DELETE_FAILED.getMessage(), SWM_VEHICLE_DELETE_FAILED.getGeneralMessage());
        }
        LOGGER.debug("## vehicle deleted status: {}", deleteStatus);
        LOGGER.debug("## deleteVehicle - END");
        return deleteStatus;
    }

    /**
     * Populates the data required for the service.
     * This method is annotated with @PostConstruct to indicate that it should be executed after the bean is
     * constructed.
     * It retrieves various configuration values from the environment and initializes the necessary variables.
     * The retrieved values are used to construct the URLs required for login, vehicle creation, vehicle update,
     * vehicle deletion,
     * and retrieving vehicle models and vehicles.
     * The retrieved values are also logged for debugging purposes.
     */
    @PostConstruct
    public void populateData() {
        LOGGER.debug("## PostConstruct - populateData - START");
        swmPassword = envConfig.getStringValue(DeviceInfoQueryProperty.SWM_PASSWORD);
        LOGGER.debug("## swmPasswordPropertyName: {}", swmPassword);
        vehicleModelId = envConfig.getStringValue(DeviceInfoQueryProperty.SWM_VEHICLE_MODEL_ID);
        swmUserName = envConfig.getStringValue(DeviceInfoQueryProperty.SWM_USERNAME);
        swmDomain = envConfig.getStringValue(DeviceInfoQueryProperty.SWM_DOMAIN);

        String baseUrl = envConfig.getStringValue(DeviceInfoQueryProperty.SWM_BASE_URL);
        String loginApi = envConfig.getStringValue(DeviceInfoQueryProperty.SWM_LOGIN_API_URL);
        String updateApi = envConfig.getStringValue(DeviceInfoQueryProperty.SWM_UPDATE_API);
        loginUrl = baseUrl + loginApi;
        createVehicleUrl = baseUrl + updateApi;
        updateVehicleUrl = createVehicleUrl;

        String deleteApi = envConfig.getStringValue(DeviceInfoQueryProperty.SWM_DELETE_API);
        String vehicleModelsApi = envConfig.getStringValue(DeviceInfoQueryProperty.SWM_VEHICLE_MODELS_API);
        String vehiclesApi = envConfig.getStringValue(DeviceInfoQueryProperty.SWM_VEHICLES_API);
        deleteVehicleUrl = baseUrl + deleteApi;
        vehicleModelsUrl = baseUrl + vehicleModelsApi;
        vehiclesUrl = baseUrl + vehiclesApi;
        LOGGER.debug("swmUserName: {}", swmUserName);
        LOGGER.debug("swmPassword: {}", "xxxxx");
        LOGGER.debug("loginUrl: {}", loginUrl);
        LOGGER.debug("createVehicleUrl: {}", createVehicleUrl);
        LOGGER.debug("updateVehicleUrl: {}", updateVehicleUrl);
        LOGGER.debug("deleteVehicleUrl: {}", deleteVehicleUrl);
        LOGGER.debug("vehicleModelsUrl: {}", vehicleModelsUrl);
        LOGGER.debug("vehiclesUrl: {}", vehiclesUrl);
        LOGGER.debug("## PostConstruct - populateData - END");
    }
}
