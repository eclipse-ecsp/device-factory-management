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

package org.eclipse.ecsp.dto.create.device;

/**
 * Represents a data transfer object for creating device factory data.
 * This class extends the DeviceFactoryDataBaseDto class.
 */
public class DeviceFactoryDataCreateDto extends DeviceFactoryDataBaseDto {
    /**
     * Constructs a new DeviceFactoryDataCreateDto object.
     *
     * @param deviceFactoryDataDtos An array of DeviceFactoryDataDto objects.
     * @param userId                The user ID associated with the device factory data.
     * @param version               The version of the device factory data.
     */
    public DeviceFactoryDataCreateDto(DeviceFactoryDataDto[] deviceFactoryDataDtos, String userId, String version) {
        super(deviceFactoryDataDtos, userId, version);
    }
}
