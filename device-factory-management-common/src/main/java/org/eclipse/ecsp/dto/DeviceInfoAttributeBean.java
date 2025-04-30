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

package org.eclipse.ecsp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.TreeMap;

/**
 * Represents a device information attribute bean.
 */
@Slf4j
@JsonInclude(Include.NON_NULL)
public class DeviceInfoAttributeBean {
    @JsonProperty("SW-Version")
    private String swVersion;

    @JsonProperty("HW-Version")
    private String hwVersion;

    @JsonProperty("HCPClient-Version")
    private String hcpClientVersion;

    @JsonProperty("LibHCPClient-Version")
    private String libHcpClientVersion;

    @JsonProperty("Country")
    private String country;

    @JsonProperty("Manufacturer")
    private String manufacturer;

    @JsonProperty("Make")
    private String make;

    @JsonProperty("Model")
    private String model;

    @JsonProperty("Year")
    private String year;

    @JsonProperty("Bodytype")
    private String bodytype;

    @JsonProperty("Series")
    private String series;

    @JsonProperty("Vehicletype")
    private String vehicleType;

    @JsonProperty("Lastlogintime")
    private String lastLogintime;

    /**
     * Retrieves the value of the "Lastlogintime" property.
     *
     * @return The value of the "Lastlogintime" property.
     */
    @JsonProperty("Lastlogintime")
    public String getLastLogintime() {
        return lastLogintime;
    }

    /**
     * Sets the value of the "Lastlogintime" property.
     *
     * @param lastLogintime The value to set for the "Lastlogintime" property.
     */
    @JsonProperty("Lastlogintime")
    public void setLastLogintime(String lastLogintime) {
        this.lastLogintime = lastLogintime;
    }

    /**
     * Retrieves the value of the "Country" property.
     *
     * @return The value of the "Country" property.
     */
    @JsonProperty("Country")
    public String getCountry() {
        return country;
    }

    /**
     * Sets the value of the "Country" property.
     *
     * @param country The value to set for the "Country" property.
     */
    @JsonProperty("Country")
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Retrieves the value of the "Manufacturer" property.
     *
     * @return The value of the "Manufacturer" property.
     */
    @JsonProperty("Manufacturer")
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Sets the value of the "Manufacturer" property.
     *
     * @param manufacturer The value to set for the "Manufacturer" property.
     */
    @JsonProperty("Manufacturer")
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Retrieves the value of the "Make" property.
     *
     * @return The value of the "Make" property.
     */
    @JsonProperty("Make")
    public String getMake() {
        return make;
    }

    /**
     * Sets the value of the "Make" property.
     *
     * @param make The value to set for the "Make" property.
     */
    @JsonProperty("Make")
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * Retrieves the value of the "Model" property.
     *
     * @return The value of the "Model" property.
     */
    @JsonProperty("Model")
    public String getModel() {
        return model;
    }

    /**
     * Sets the value of the "Model" property.
     *
     * @param model The value to set for the "Model" property.
     */
    @JsonProperty("Model")
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Retrieves the value of the "Year" property.
     *
     * @return The value of the "Year" property.
     */
    @JsonProperty("Year")
    public String getYear() {
        return year;
    }

    /**
     * Sets the value of the "Year" property.
     *
     * @param year The value to set for the "Year" property.
     */
    @JsonProperty("Year")
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Retrieves the value of the "Bodytype" property.
     *
     * @return The value of the "Bodytype" property.
     */
    @JsonProperty("Bodytype")
    public String getBodytype() {
        return bodytype;
    }

    /**
     * Sets the value of the "Bodytype" property.
     *
     * @param bodytype The value to set for the "Bodytype" property.
     */
    @JsonProperty("Bodytype")
    public void setBodytype(String bodytype) {
        this.bodytype = bodytype;
    }

    /**
     * Retrieves the value of the "Series" property.
     *
     * @return The value of the "Series" property.
     */
    @JsonProperty("Series")
    public String getSeries() {
        return series;
    }

    /**
     * Sets the value of the "Series" property.
     *
     * @param series The value to set for the "Series" property.
     */
    @JsonProperty("Series")
    public void setSeries(String series) {
        this.series = series;
    }

    /**
     * Retrieves the value of the "Vehicletype" property.
     *
     * @return The value of the "Vehicletype" property.
     */
    @JsonProperty("Vehicletype")
    public String getVehicleType() {
        return vehicleType;
    }

    /**
     * Sets the value of the "Vehicletype" property.
     *
     * @param vehicleType The value to set for the "Vehicletype" property.
     */
    @JsonProperty("Vehicletype")
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    /**
     * Retrieves the value of the "SW-Version" property.
     *
     * @return The value of the "SW-Version" property.
     */
    @JsonProperty("SW-Version")
    public String getSwVersion() {
        return swVersion;
    }

    /**
     * Sets the value of the "SW-Version" property.
     *
     * @param swVersion The value to set for the "SW-Version" property.
     */
    @JsonProperty("SW-Version")
    public void setSwVersion(String swVersion) {
        this.swVersion = swVersion;
    }

    /**
     * Retrieves the value of the "HW-Version" property.
     *
     * @return The value of the "HW-Version" property.
     */
    @JsonProperty("HW-Version")
    public String getHwVersion() {
        return hwVersion;
    }

    /**
     * Sets the value of the "HW-Version" property.
     *
     * @param hwVersion The value to set for the "HW-Version" property.
     */
    @JsonProperty("HW-Version")
    public void setHwVersion(String hwVersion) {
        this.hwVersion = hwVersion;
    }

    /**
     * Retrieves the value of the "HCPClient-Version" property.
     *
     * @return The value of the "HCPClient-Version" property.
     */
    @JsonProperty("HCPClient-Version")
    public String getHcpClientVersion() {
        return hcpClientVersion;
    }

    /**
     * Sets the value of the "HCPClient-Version" property.
     *
     * @param hcpClientVersion The value to set for the "HCPClient-Version" property.
     */
    @JsonProperty("HCPClient-Version")
    public void setHcpClientVersion(String hcpClientVersion) {
        this.hcpClientVersion = hcpClientVersion;
    }

    /**
     * Retrieves the value of the "LibHCPClient-Version" property.
     *
     * @return The value of the "LibHCPClient-Version" property.
     */
    @JsonProperty("LibHCPClient-Version")
    public String getLibHcpClientVersion() {
        return libHcpClientVersion;
    }

    /**
     * Sets the value of the "LibHCPClient-Version" property.
     *
     * @param libHcpClientVersion The value to set for the "LibHCPClient-Version" property.
     */
    @JsonProperty("LibHCPClient-Version")
    public void setLibHcpClientVersion(String libHcpClientVersion) {
        this.libHcpClientVersion = libHcpClientVersion;
    }

    /**
     * Retrieves all non-null fields of the object as a TreeMap.
     *
     * @return A TreeMap containing the non-null fields of the object.
     * @throws IllegalAccessException If an error occurs while accessing the fields.
     */
    public TreeMap<String, String> getAllNonNullFields() throws IllegalAccessException {
        TreeMap<String, String> map = new TreeMap<String, String>();
        for (Field field : this.getClass().getDeclaredFields()) {

            log.debug("field = {}", field);
            if (field.isAnnotationPresent(JsonProperty.class)) {

                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                field.setAccessible(true);
                Object value = field.get(this);
                if (value != null) {
                    map.put(jsonProperty.value(), value.toString());
                }
            }
        }
        return map;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "DeviceInfoAttributeBean [swVersion=" + swVersion + ", hwVersion=" + hwVersion + ", hcpClientVersion="
            + hcpClientVersion
            + ", libHcpClientVersion=" + libHcpClientVersion + ", country=" + country + ", manufacturer="
            + manufacturer + ", make="
            + make + ", model=" + model + ", year=" + year + ", bodytype=" + bodytype + ", series=" + series
            + ", vehicleType="
            + vehicleType + ", lastLogintime=" + lastLogintime + "]";
    }
}
