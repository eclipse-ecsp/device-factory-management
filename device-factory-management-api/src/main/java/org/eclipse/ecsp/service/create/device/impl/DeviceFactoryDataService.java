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
import org.eclipse.ecsp.common.util.Utils;
import org.eclipse.ecsp.dao.create.device.IdeviceFactoryDataDao;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataBaseDto;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataDto;
import org.eclipse.ecsp.service.create.device.AbstractDeviceFactoryDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.eclipse.ecsp.common.enums.ApiMessageEnum.INVALID_DEVICE_TYPE;
import static org.eclipse.ecsp.common.enums.ApiMessageEnum.INVALID_REGION;

/**
 * This class represents a service for creating devices in the device factory management system.
 * It extends the AbstractDeviceFactoryDataService class and provides methods for creating devices.
 */
@Service
@Transactional
public class DeviceFactoryDataService extends AbstractDeviceFactoryDataService {
    private static final int CAPACITY = 500;
    private final IdeviceFactoryDataDao createDeviceDao;

    @Value("#{'${allowed_device_types}'.split(',')}")
    private String[] allowedTypes;

    @Value("#{'${allowed_region}'.split(',')}")
    private String[] allowedRegions;

    /**
     * Constructs a new DeviceFactoryDataService with the specified createDeviceDao.
     *
     * @param createDeviceDao the data access object for device factory data
     */
    @Autowired
    public DeviceFactoryDataService(@Qualifier(value = "deviceFactoryDataDao") IdeviceFactoryDataDao createDeviceDao) {
        this.createDeviceDao = createDeviceDao;
    }

    /**
     * Constructs a new instance of the DeviceFactoryDataService class.
     */
    public DeviceFactoryDataService() {
        this.createDeviceDao = null;
    }

    /**
     * Creates a device based on the provided device factory data.
     *
     * @param deviceFactoryDataBaseDto The device factory data containing information about the device to be created.
     * @return True if the device creation is successful, false otherwise.
     */
    @Override
    public boolean createDevice(DeviceFactoryDataBaseDto deviceFactoryDataBaseDto) {
        LOGGER.debug("## createDevice Service - START");
        DeviceFactoryDataDto[] deviceInfoDataRequest = deviceFactoryDataBaseDto.getDeviceFactoryDataDtos();
        String version = deviceFactoryDataBaseDto.getVersion();
        String userId = deviceFactoryDataBaseDto.getUserId();
        StringBuilder deviceCreationSuccessSb = new StringBuilder(CAPACITY);
        StringBuilder deviceCreationFailedSb = new StringBuilder(CAPACITY);
        for (DeviceFactoryDataDto deviceFactoryData : deviceInfoDataRequest) {
            if (version.equals("V3")) {
                LOGGER.info("##validating Device Type : {}", deviceFactoryData.getDeviceType());
                if (!Utils.validateAllowedType(deviceFactoryData.getDeviceType(), allowedTypes)) {
                    throw new ApiValidationFailedException(INVALID_DEVICE_TYPE.getCode(),
                        INVALID_DEVICE_TYPE.getMessage(),
                        INVALID_DEVICE_TYPE.getGeneralMessage());
                }
                LOGGER.info("##validating Region : {}", deviceFactoryData.getRegion());
                if (deviceFactoryData.getRegion() != null
                    && !Utils.validateAllowedType(deviceFactoryData.getRegion(), allowedRegions)) {
                    throw new ApiValidationFailedException(INVALID_REGION.getCode(), INVALID_REGION.getMessage(),
                        INVALID_REGION.getGeneralMessage());
                }
            }
            boolean isDeviceCreated = createDeviceDao.createDevice(deviceFactoryData, userId);
            if (isDeviceCreated) {
                deviceCreationSuccessSb.append(deviceFactoryData.getImei()).append(" ");
            } else {
                deviceCreationFailedSb.append(deviceFactoryData.getImei()).append(" ");
            }
        }
        LOGGER.info("## Device created for IMEIs: {}", deviceCreationSuccessSb);
        LOGGER.info("## Failed to create device for IMEIs: {}",
            deviceCreationFailedSb.length() == 0 ? "N/A" : deviceCreationFailedSb);
        deviceCreationSuccessSb.setLength(0);
        deviceCreationFailedSb.setLength(0);
        return true;
    }
}
