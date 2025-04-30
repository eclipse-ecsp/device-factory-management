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

import java.io.Serializable;

/**
 * This abstract class represents the base data for a device factory.
 * It provides common properties and methods for device factory data objects.
 */
public abstract class DeviceFactoryDataBaseDto implements Serializable {
    protected DeviceFactoryDataDto[] deviceFactoryDataDtos;
    protected String userId;
    protected String version;

    /**
     * Default constructor.
     */
    public DeviceFactoryDataBaseDto() {
    }

    /**
     * Constructs a new DeviceFactoryDataBaseDto object with the specified parameters.
     *
     * @param deviceFactoryDataDtos An array of DeviceFactoryDataDto objects.
     * @param userId                The user ID associated with the device factory data.
     * @param version               The version of the device factory data.
     */
    public DeviceFactoryDataBaseDto(DeviceFactoryDataDto[] deviceFactoryDataDtos, String userId, String version) {
        this.deviceFactoryDataDtos = deviceFactoryDataDtos;
        this.userId = userId;
        this.version = version;
    }

    /**
     * Get the array of device factory data objects.
     *
     * @return The array of device factory data objects.
     */
    public DeviceFactoryDataDto[] getDeviceFactoryDataDtos() {
        return deviceFactoryDataDtos;
    }

    /**
     * Set the array of device factory data objects.
     *
     * @param deviceFactoryDataDtos The array of device factory data objects.
     */
    public void setDeviceFactoryDataDtos(DeviceFactoryDataDto[] deviceFactoryDataDtos) {
        this.deviceFactoryDataDtos = deviceFactoryDataDtos;
    }

    /**
     * Get the user ID associated with the device factory.
     *
     * @return The user ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Set the user ID associated with the device factory.
     *
     * @param userId The user ID.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Get the version of the device factory.
     *
     * @return The version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set the version of the device factory.
     *
     * @param version The version.
     */
    public void setVersion(String version) {
        this.version = version;
    }
}
