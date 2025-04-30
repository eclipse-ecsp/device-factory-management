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
 * Represents a version 2 of the Device Details DTO.
 * This class extends the DeviceDetailsBaseDto class and provides additional fields for device details.
 */
public class DeviceDetailsDtoV2 extends DeviceDetailsBaseDto {

    private String isDetailsRequired;
    private String imei;
    private String serialNumber;

    /**
     * Constructs a new DeviceDetailsDtoV2 object with the specified parameters.
     *
     * @param size             The size of the device details.
     * @param page             The page number of the device details.
     * @param sortBy           The field to sort the device details by.
     * @param orderBy          The order in which to sort the device details.
     * @param isDetailsRequired The flag indicating if the device details are required.
     * @param imei             The IMEI number of the device.
     * @param serialNumber     The serial number of the device.
     */
    public DeviceDetailsDtoV2(String size, String page, String sortBy, String orderBy, String isDetailsRequired,
                              String imei,
                              String serialNumber) {
        super(size, page, sortBy, orderBy);
        this.isDetailsRequired = isDetailsRequired;
        this.imei = imei;
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the flag indicating if the device details are required.
     *
     * @return The flag indicating if the device details are required.
     */
    public String getIsDetailsRequired() {
        return isDetailsRequired;
    }

    /**
     * Sets the flag indicating if the device details are required.
     *
     * @param isDetailsRequired The flag indicating if the device details are required.
     */
    public void setIsDetailsRequired(String isDetailsRequired) {
        this.isDetailsRequired = isDetailsRequired;
    }

    /**
     * Gets the IMEI number of the device.
     *
     * @return The IMEI number of the device.
     */
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI number of the device.
     *
     * @param imei The IMEI number of the device.
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Gets the serial number of the device.
     *
     * @return The serial number of the device.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number of the device.
     *
     * @param serialNumber The serial number of the device.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
