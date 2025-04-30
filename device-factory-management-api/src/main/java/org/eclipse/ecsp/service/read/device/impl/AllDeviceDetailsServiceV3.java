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

import org.eclipse.ecsp.common.exception.ApiTechnicalException;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoPage;
import org.eclipse.ecsp.dto.read.device.DeviceDetailsBaseDto;
import org.eclipse.ecsp.service.read.device.AbstractAllDeviceDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class provides the implementation for retrieving all device details from the factory data.
 * It extends the AbstractAllDeviceDetailsService class and overrides the findAllFactoryData method.
 */
@Service
public class AllDeviceDetailsServiceV3
    extends AbstractAllDeviceDetailsService<DeviceDetailsBaseDto, DeviceInfoPage<List<DeviceInfoFactoryData>>> {

    /**
     * Retrieves all factory data for the given baseDto and inputType.
     * This method throws an ApiTechnicalException with the message "v3 not supported".
     *
     * @param baseDto    The base DTO for device details.
     * @param inputType  The input type for device details.
     * @return The page of device information containing a list of factory data.
     * @throws ApiTechnicalException If v3 is not supported.
     */
    @Override
    public DeviceInfoPage<List<DeviceInfoFactoryData>> findAllFactoryData(DeviceDetailsBaseDto baseDto,
                                                                          String... inputType) {
        throw new ApiTechnicalException("v3 not supported");
    }
}
