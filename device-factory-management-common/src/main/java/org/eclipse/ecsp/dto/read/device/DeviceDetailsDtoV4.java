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

package org.eclipse.ecsp.dto.read.device;

/**
 * Represents a version 4 device details DTO.
 * Extends the base class DeviceDetailsBaseDto.
 */
public class DeviceDetailsDtoV4 extends DeviceDetailsBaseDto {
    private String imei;

    /**
     * Constructs a DeviceDetailsDtoV4 object with the specified parameters.
     *
     * @param size   The size of the device details.
     * @param page   The page number of the device details.
     * @param sortBy The field to sort the device details by.
     * @param orderBy The order in which to sort the device details.
     * @param imei   The IMEI (International Mobile Equipment Identity) of the device.
     */
    public DeviceDetailsDtoV4(String size, String page, String sortBy, String orderBy, String imei) {
        super(size, page, sortBy, orderBy);
        this.imei = imei;
    }

    /**
     * Gets the IMEI (International Mobile Equipment Identity) of the device.
     *
     * @return The IMEI of the device.
     */
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI (International Mobile Equipment Identity) of the device.
     *
     * @param imei The IMEI of the device.
     */
    public void setImei(String imei) {
        this.imei = imei;
    }
}
