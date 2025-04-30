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

import org.eclipse.ecsp.dao.create.device.AbstractDeviceFactoryDataDao;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataDto;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * This class represents a data access object for managing device factory data.
 * It extends the AbstractDeviceFactoryDataDao class.
 */
@Repository
public class DeviceFactoryDataDao extends AbstractDeviceFactoryDataDao {

    /**
     * Creates a device using the provided factory data and user ID.
     *
     * @param factoryData The factory data for creating the device.
     * @param userId      The ID of the user creating the device.
     * @return True if the device creation is successful, false otherwise.
     */
    @Override
    public boolean createDevice(DeviceFactoryDataDto factoryData, String userId) {
        KeyHolder keyHolder = createDeviceUsingFactoryData(factoryData, userId);
        return keyHolder != null;
    }
}
