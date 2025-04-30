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

package org.eclipse.ecsp.service.create.device;

import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataBaseDto;

/**
 * This interface represents the service for creating device factory data.
 */
public interface IdeviceFactoryDataService {

    /**
     * Creates a device using the provided device factory data.
     *
     * @param deviceFactoryDataBaseDto The device factory data to create the device.
     * @return True if the device is created successfully, false otherwise.
     * @throws Exception If an error occurs during the device creation process.
     */
    boolean createDevice(DeviceFactoryDataBaseDto deviceFactoryDataBaseDto) throws Exception;
}
