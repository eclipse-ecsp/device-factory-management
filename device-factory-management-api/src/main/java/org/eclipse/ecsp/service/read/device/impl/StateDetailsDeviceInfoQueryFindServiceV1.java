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

package org.eclipse.ecsp.service.read.device.impl;

import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.read.device.DeviceDetailsDtoV1;
import org.eclipse.ecsp.service.read.device.AbstractDeviceDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * This class represents a service for finding factory data for a device.
 * It extends the AbstractDeviceDetailsService class and provides an implementation
 * for the findFactoryData method.
 */
@Service
public class StateDetailsDeviceInfoQueryFindServiceV1 extends AbstractDeviceDetailsService<DeviceDetailsDtoV1> {

    /**
     * Finds the factory data for the given device details.
     *
     * @param dto The device details DTO.
     * @return The list of factory data for the device.
     */
    @Override
    public List<DeviceInfoFactoryData> findFactoryData(DeviceDetailsDtoV1 dto) {
        return Collections.emptyList();
    }
}
