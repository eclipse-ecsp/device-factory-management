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
 * Represents a version 5 device details DTO.
 */
public class DeviceDetailsDtoV5 extends DeviceDetailsBaseDto {
    private String serialNumber;
    private String imei;
    private String deviceId;
    private String state;
    private String vin;

    /**
     * Constructor for DeviceDetailsDtoV5.
     *
     * @param size         The size of the device details.
     * @param page         The page number of the device details.
     * @param sortBy       The field to sort the device details by.
     * @param orderBy      The order in which to sort the device details.
     * @param imei         The IMEI number of the device.
     * @param serialNumber The serial number of the device.
     * @param deviceId     The ID of the device.
     * @param state        The state of the device.
     * @param vin          The VIN (Vehicle Identification Number) of the device.
     */
    public DeviceDetailsDtoV5(String size, String page, String sortBy, String orderBy, String imei, String serialNumber,
                              String deviceId, String state, String vin) {
        super(size, page, sortBy, orderBy);
        this.imei = imei;
        this.serialNumber = serialNumber;
        this.deviceId = deviceId;
        this.state = state;
        this.vin = vin;
    }

    /**
     * Gets the serial number of the device.
     *
     * @return The serial number.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number of the device.
     *
     * @param serialNumber The serial number to set.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the IMEI number of the device.
     *
     * @return The IMEI number.
     */
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI number of the device.
     *
     * @param imei The IMEI number to set.
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Gets the ID of the device.
     *
     * @return The device ID.
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the ID of the device.
     *
     * @param deviceId The device ID to set.
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Gets the state of the device.
     *
     * @return The device state.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state of the device.
     *
     * @param state The device state to set.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the VIN (Vehicle Identification Number) of the device.
     *
     * @return The VIN.
     */
    public String getVin() {
        return vin;
    }

    /**
     * Sets the VIN (Vehicle Identification Number) of the device.
     *
     * @param vin The VIN to set.
     */
    public void setVin(String vin) {
        this.vin = vin;
    }
}
