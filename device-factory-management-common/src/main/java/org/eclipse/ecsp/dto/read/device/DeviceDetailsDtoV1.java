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
 * Represents a version 1 device details DTO.
 * Extends the base device details DTO.
 */
public class DeviceDetailsDtoV1 extends DeviceDetailsBaseDto {
    private String serialNumber;
    private String imei;

    /**
     * Constructs a new DeviceDetailsDtoV1 object with the specified parameters.
     *
     * @param serialNumber          the serial number of the device
     * @param imei                  the IMEI number of the device
     * @param ssid                  the SSID of the device
     * @param iccid                 the ICCID of the device
     * @param msisdn                the MSISDN of the device
     * @param imsi                  the IMSI of the device
     * @param bssid                 the BSSID of the device
     * @param packageserialnumber   the package serial number of the device
     */
    public DeviceDetailsDtoV1(String serialNumber, String imei, String ssid, String iccid, String msisdn, String imsi,
                              String bssid, String packageserialnumber) {
        super(ssid, iccid, msisdn, imsi, bssid, packageserialnumber);
        this.serialNumber = serialNumber;
        this.imei = imei;
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
}
