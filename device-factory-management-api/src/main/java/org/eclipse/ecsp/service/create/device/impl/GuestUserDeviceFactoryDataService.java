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

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.entity.ContentType;
import org.eclipse.ecsp.common.CommonConstants;
import org.eclipse.ecsp.config.DeviceInfoQueryProperty;
import org.eclipse.ecsp.dao.create.device.IdeviceFactoryDataDao;
import org.eclipse.ecsp.dto.UserCreationDetailsSpringAuth;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataBaseDto;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataDto;
import org.eclipse.ecsp.service.create.device.AbstractDeviceFactoryDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Set;

/**
 * This class is responsible for creating guest user devices in the device factory management system.
 * It extends the AbstractDeviceFactoryDataService class and provides implementation for creating devices.
 */
@Service
@Transactional
public class GuestUserDeviceFactoryDataService extends AbstractDeviceFactoryDataService {

    private static final String EMAIL_PREFIX = "ignite3.0";
    private static final String EMAIL_DOMAIN = "@guestuser.com";
    private static final int MIN_LENGTH = 10;
    private static final int UPPER_BOUND = 16;
    private final IdeviceFactoryDataDao createDeviceDao;

    /**
     * This class represents a service for managing guest user device factory data.
     * It provides methods for creating and managing guest user devices.
     */
    @Autowired
    public GuestUserDeviceFactoryDataService(
        @Qualifier(value = "guestUserVehicleFactoryDataDao") IdeviceFactoryDataDao createDeviceDao) {
        this.createDeviceDao = createDeviceDao;
    }

    /**
     * Creates a device for the guest user.
     *
     * @param deviceFactoryDataBaseDto The DTO containing the device factory data.
     * @return true if the device creation is successful, false otherwise.
     */
    @Override
    public boolean createDevice(DeviceFactoryDataBaseDto deviceFactoryDataBaseDto) {
        DeviceFactoryDataDto[] vehicleFactoryDataDtos = deviceFactoryDataBaseDto.getDeviceFactoryDataDtos();
        String userId = deviceFactoryDataBaseDto.getUserId();
        for (DeviceFactoryDataDto deviceFactoryDataDto : vehicleFactoryDataDtos) {
            if (deviceFactoryDataDto.getVin() != null) {
                ResponseEntity<String> response = createGuestUser(deviceFactoryDataDto);
                if (response.getStatusCode().equals(HttpStatus.OK)
                    || response.getStatusCode().equals(HttpStatus.CREATED)) {
                    LOGGER.info("## Guest user created successfully, response: {}", response.getBody());
                    createDeviceDao.createDevice(deviceFactoryDataDto, userId);
                } else {
                    LOGGER.error("## Guest user creation failed for device Imei:{}, vin: {}",
                        deviceFactoryDataDto.getImei(),
                        deviceFactoryDataDto.getVin());
                }
            } else {
                LOGGER.error("## Guest user creation failed for device Imei:{}, vin: {}",
                    deviceFactoryDataDto.getImei(),
                    deviceFactoryDataDto.getVin());
            }
        }
        return true;
    }

    /**
     * Creates a guest user using the provided device factory data.
     *
     * @param deviceFactoryDataDto The DTO containing the device factory data.
     * @return The response entity containing the result of the guest user creation.
     */
    private ResponseEntity<String> createGuestUser(DeviceFactoryDataDto deviceFactoryDataDto) {
        Object userCreationRequest;
        String userCredentialUrl;
        LOGGER.debug("Spring Auth: Generating user creation request");
        userCreationRequest = generateUserCreationRequestSpringAuth(deviceFactoryDataDto.getVin());
        userCredentialUrl = config.getStringValue(DeviceInfoQueryProperty.USER_CREATE_URL_SPRING_AUTH);
        LOGGER.debug("userCreateURL: {}", userCredentialUrl);
        return restClientLibrary.doPost(userCredentialUrl, createHeaders(), userCreationRequest, String.class);
    }

    /**
     * Creates the headers for the HTTP request.
     *
     * @return The HttpHeaders object containing the headers.
     */
    private HttpHeaders createHeaders() {
        String token;
        token = springAuthTokenGenerator.fetchSpringAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set(CommonConstants.HEADER_CONTENT_TYPE_KEY, ContentType.APPLICATION_JSON.toString());
        headers.set(CommonConstants.HEADER_NAME_AUTHORIZATION, BEARER_KEY + token);
        return headers;
    }

    /**
     * Generates the user creation request for Spring Auth.
     *
     * @param vin The VIN of the device.
     * @return The user creation request object.
     */
    private Object generateUserCreationRequestSpringAuth(String vin) {
        SecureRandom random = new SecureRandom();
        int count = random.nextInt(MIN_LENGTH, UPPER_BOUND);
        String password = RandomStringUtils.random(count, 0, 0, true, true, null, new SecureRandom());
        UserCreationDetailsSpringAuth userDetails = new UserCreationDetailsSpringAuth();

        userDetails.setPassword(password);
        userDetails.setRoles(Set.of(CommonConstants.GUEST_USER_ROLE));
        userDetails.setUserName(CommonConstants.GUEST_USER_PASS_GENERIC + vin);
        userDetails.setAud(springAuthTokenGenerator.getClientId());
        userDetails.setEmail(EMAIL_PREFIX + userDetails.getUserName() + EMAIL_DOMAIN);

        return userDetails;
    }
}
