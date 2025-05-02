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

import jakarta.validation.constraints.NotBlank;
import org.eclipse.ecsp.dto.validation.NullOrNotEmpty;
import java.io.Serializable;

/**
 * The DeviceFactoryDataDto class represents the data transfer object for device factory data.
 * It contains various properties related to a device.
 */
public class DeviceFactoryDataDto implements Serializable {
    private static final long serialVersionUID = -6497051690974541540L;

    @NotBlank(message = "manufacturingDate is required and not allowed to be empty")
    private String manufacturingDate;
    @NotBlank(message = "model is required and not allowed to be empty")
    private String model;
    @NullOrNotEmpty(message = "imei is not allowed be empty")
    private String imei;
    @NotBlank(message = "serialNumber is required and not allowed to be empty")
    private String serialNumber;
    @NullOrNotEmpty(message = "platformVersion is not allowed be empty")
    private String platformVersion;
    @NullOrNotEmpty(message = "iccid is not allowed be empty")
    private String iccid;
    @NullOrNotEmpty(message = "ssid is not allowed be empty")
    private String ssid;
    @NullOrNotEmpty(message = "bssid is not allowed be empty")
    private String bssid;
    @NullOrNotEmpty(message = "msisdn is not allowed be empty")
    private String msisdn;
    @NullOrNotEmpty(message = "imsi is not allowed be empty")
    private String imsi;
    @NotBlank(message = "recordDate is required and not allowed to be empty")
    private String recordDate;
    @NullOrNotEmpty(message = "packageSerialNumber is not allowed be empty")
    private String packageSerialNumber;
    //@NotBlank(message = "vin is required and not allowed to be empty")
    private String vin;
    @NullOrNotEmpty(message = "chassisNumber is not allowed be empty")
    private String chassisNumber;
    @NullOrNotEmpty(message = "plant is not allowed be empty")
    private String plant;
    @NullOrNotEmpty(message = "productionWeek is not allowed be empty")
    private String productionWeek;
    @NullOrNotEmpty(message = "vehicleModelYear is not allowed be empty")
    private String vehicleModelYear;
    @NullOrNotEmpty(message = "friendlyName is not allowed be empty")
    private String friendlyName;
    @NullOrNotEmpty(message = "deviceType is not allowed be empty")
    private String deviceType;
    @NullOrNotEmpty(message = "region is not allowed be empty")
    private String region;

    /**
     * Constructs a new DeviceFactoryDataDto object with default values.
     */
    public DeviceFactoryDataDto() {
        //don't do anything
    }

    /**
     * Gets the manufacturing date of the device.
     *
     * @return the manufacturing date of the device
     */
    public String getManufacturingDate() {
        return manufacturingDate;
    }

    /**
     * Sets the manufacturing date of the device.
     *
     * @param manufacturingDate the manufacturing date of the device
     */
    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    /**
     * Gets the model of the device.
     *
     * @return the model of the device
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the model of the device.
     *
     * @param model the model of the device
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Gets the IMEI (International Mobile Equipment Identity) of the device.
     *
     * @return the IMEI of the device
     */
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI (International Mobile Equipment Identity) of the device.
     *
     * @param imei the IMEI of the device
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Gets the serial number of the device.
     *
     * @return the serial number of the device
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number of the device.
     *
     * @param serialNumber the serial number of the device
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the platform version of the device.
     *
     * @return the platform version of the device
     */
    public String getPlatformVersion() {
        return platformVersion;
    }

    /**
     * Sets the platform version of the device.
     *
     * @param platformVersion the platform version of the device
     */
    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    /**
     * Gets the ICCID (Integrated Circuit Card Identifier) of the device.
     *
     * @return the ICCID of the device
     */
    public String getIccid() {
        return iccid;
    }

    /**
     * Sets the ICCID (Integrated Circuit Card Identifier) of the device.
     *
     * @param iccid the ICCID of the device
     */
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
     * Gets the SSID (Service Set Identifier) of the device.
     *
     * @return the SSID of the device
     */
    public String getSsid() {
        return ssid;
    }

    /**
     * Sets the SSID (Service Set Identifier) of the device.
     *
     * @param ssid the SSID of the device
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * Gets the BSSID (Basic Service Set Identifier) of the device.
     *
     * @return the BSSID of the device
     */
    public String getBssid() {
        return bssid;
    }

    /**
     * Sets the BSSID (Basic Service Set Identifier) of the device.
     *
     * @param bssid the BSSID of the device
     */
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    /**
     * Gets the MSISDN (Mobile Station International Subscriber Directory Number) of the device.
     *
     * @return the MSISDN of the device
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the MSISDN (Mobile Station International Subscriber Directory Number) of the device.
     *
     * @param msisdn the MSISDN of the device
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * Gets the IMSI (International Mobile Subscriber Identity) of the device.
     *
     * @return the IMSI of the device
     */
    public String getImsi() {
        return imsi;
    }

    /**
     * Sets the IMSI (International Mobile Subscriber Identity) of the device.
     *
     * @param imsi the IMSI of the device
     */
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
     * Gets the record date of the device.
     *
     * @return the record date of the device
     */
    public String getRecordDate() {
        return recordDate;
    }

    /**
     * Sets the record date of the device.
     *
     * @param recordDate the record date of the device
     */
    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    /**
     * Gets the package serial number of the device.
     *
     * @return the package serial number of the device
     */
    public String getPackageSerialNumber() {
        return packageSerialNumber;
    }

    /**
     * Sets the package serial number of the device.
     *
     * @param packageSerialNumber the package serial number of the device
     */
    public void setPackageSerialNumber(String packageSerialNumber) {
        this.packageSerialNumber = packageSerialNumber;
    }

    /**
     * Gets the VIN (Vehicle Identification Number) of the device.
     *
     * @return the VIN of the device
     */
    public String getVin() {
        return vin;
    }

    /**
     * Sets the VIN (Vehicle Identification Number) of the device.
     *
     * @param vin the VIN of the device
     */
    public void setVin(String vin) {
        this.vin = vin;
    }

    /**
     * Gets the chassis number of the device.
     *
     * @return the chassis number of the device
     */
    public String getChassisNumber() {
        return chassisNumber;
    }

    /**
     * Sets the chassis number of the device.
     *
     * @param chassisNumber the chassis number of the device
     */
    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    /**
     * Gets the plant of the device.
     *
     * @return the plant of the device
     */
    public String getPlant() {
        return plant;
    }

    /**
     * Sets the plant of the device.
     *
     * @param plant the plant of the device
     */
    public void setPlant(String plant) {
        this.plant = plant;
    }

    /**
     * Gets the production week of the device.
     *
     * @return the production week of the device
     */
    public String getProductionWeek() {
        return productionWeek;
    }

    /**
     * Sets the production week of the device.
     *
     * @param productionWeek the production week of the device
     */
    public void setProductionWeek(String productionWeek) {
        this.productionWeek = productionWeek;
    }

    /**
     * Gets the vehicle model year of the device.
     *
     * @return the vehicle model year of the device
     */
    public String getVehicleModelYear() {
        return vehicleModelYear;
    }

    /**
     * Sets the vehicle model year of the device.
     *
     * @param vehicleModelYear the vehicle model year of the device
     */
    public void setVehicleModelYear(String vehicleModelYear) {
        this.vehicleModelYear = vehicleModelYear;
    }

    /**
     * Gets the friendly name of the device.
     *
     * @return the friendly name of the device
     */
    public String getFriendlyName() {
        return friendlyName;
    }

    /**
     * Sets the friendly name of the device.
     *
     * @param friendlyName the friendly name of the device
     */
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    /**
     * Gets the device type.
     *
     * @return the device type
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Sets the device type.
     *
     * @param deviceType the device type
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * Gets the region of the device.
     *
     * @return the region of the device
     */
    public String getRegion() {
        return region;
    }

    /**
     * Sets the region of the device.
     *
     * @param region the region of the device
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * Returns a string representation of the DeviceFactoryDataDto object.
     * The string representation includes the values of all the fields in the object.
     *
     * @return a string representation of the DeviceFactoryDataDto object
     */
    @Override
    public String toString() {
        return "DeviceFactoryDataDto{"
            + "manufacturingDate='" + manufacturingDate + '\''
            + ", model='" + model + '\''
            + ", imei='" + imei + '\''
            + ", serialNumber='" + serialNumber + '\''
            + ", platformVersion='" + platformVersion + '\''
            + ", iccid='" + iccid + '\''
            + ", ssid='" + ssid + '\''
            + ", bssid='" + bssid + '\''
            + ", msisdn='" + msisdn + '\''
            + ", imsi='" + imsi + '\''
            + ", recordDate='" + recordDate + '\''
            + ", packageSerialNumber='" + packageSerialNumber + '\''
            + ", vin='" + vin + '\''
            + ", chassisNumber='" + chassisNumber + '\''
            + ", plant='" + plant + '\''
            + ", productionWeek='" + productionWeek + '\''
            + ", vehicleModelYear='" + vehicleModelYear + '\''
            + ", friendlyName='" + friendlyName + '\''
            + ", deviceType='" + deviceType + '\''
            + ", region='" + region + '\''
            + '}';
    }
}