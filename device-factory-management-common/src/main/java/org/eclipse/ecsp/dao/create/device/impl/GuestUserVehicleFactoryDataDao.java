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

import org.eclipse.ecsp.dao.VinDetailsDao;
import org.eclipse.ecsp.dao.create.device.AbstractDeviceFactoryDataDao;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * This class represents a data access object for managing guest user vehicle factory data.
 * It extends the AbstractDeviceFactoryDataDao class and provides methods for creating devices.
 */
@Repository
public class GuestUserVehicleFactoryDataDao extends AbstractDeviceFactoryDataDao {
    @Autowired
    private VinDetailsDao vinDetailsDao;

    /**
     * Creates a device using the provided device factory data and user ID.
     *
     * @param deviceFactoryDataDto The device factory data DTO.
     * @param userId The ID of the user.
     * @return true if the device is created successfully, false otherwise.
     */
    @Override
    public boolean createDevice(DeviceFactoryDataDto deviceFactoryDataDto, String userId) {
        KeyHolder keyHolder = createDeviceUsingFactoryData(deviceFactoryDataDto, userId);
        if (keyHolder.getKeys() != null && deviceFactoryDataDto.getVin() != null) {
            Map<String, Object> keys = keyHolder.getKeys();
            long factoryDataId = keys != null ? (long) keys.get("ID") : (long) 0;
            //need to insert vin in vehicle profile
            vinDetailsDao.insert(factoryDataId, deviceFactoryDataDto.getVin());
        } else {
            return false;
        }
        return true;
    }
}
