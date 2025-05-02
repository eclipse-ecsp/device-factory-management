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

import java.io.Serializable;

/**
 * The base class for device details DTOs.
 */
public abstract class DeviceDetailsBaseDto implements Serializable {
    /**
     * The SSID (Service Set Identifier) of the device.
     */
    protected String ssid;

    /**
     * The ICCID (Integrated Circuit Card Identifier) of the device.
     */
    protected String iccid;

    /**
     * The MSISDN (Mobile Station International Subscriber Directory Number) of the device.
     */
    protected String msisdn;

    /**
     * The IMSI (International Mobile Subscriber Identity) of the device.
     */
    protected String imsi;

    /**
     * The BSSID (Basic Service Set Identifier) of the device.
     */
    protected String bssid;

    /**
     * The package serial number of the device.
     */
    protected String packageserialnumber;

    /**
     * The ascending order value for sorting.
     */
    protected String asc;

    /**
     * The descending order value for sorting.
     */
    protected String desc;

    /**
     * The size of the device details, typically used for pagination.
     */
    protected String size;

    /**
     * The page number of the device details, typically used for pagination.
     */
    protected String page;

    /**
     * The field by which the device details should be sorted.
     */
    protected String sortBy;

    /**
     * The order in which to sort the device details.
     * This can be used to specify ascending or descending order.
     */
    protected String orderBy;

    /**
     * Default constructor.
     */
    public DeviceDetailsBaseDto() {
    }

    /**
     * Constructor for initializing device details with ssid, iccid, msisdn, imsi, bssid, and packageserialnumber.
     *
     * @param ssid                 The SSID of the device.
     * @param iccid                The ICCID of the device.
     * @param msisdn               The MSISDN of the device.
     * @param imsi                 The IMSI of the device.
     * @param bssid                The BSSID of the device.
     * @param packageserialnumber  The package serial number of the device.
     */
    public DeviceDetailsBaseDto(String ssid, String iccid, String msisdn, String imsi, String bssid,
                                String packageserialnumber) {
        this.ssid = ssid;
        this.iccid = iccid;
        this.msisdn = msisdn;
        this.imsi = imsi;
        this.bssid = bssid;
        this.packageserialnumber = packageserialnumber;
    }

    /**
     * Constructor for initializing device details with size, page, sortBy, and orderBy.
     *
     * @param size    The size of the device details.
     * @param page    The page number of the device details.
     * @param sortBy  The field to sort the device details by.
     * @param orderBy The order in which to sort the device details.
     */
    public DeviceDetailsBaseDto(String size, String page, String sortBy, String orderBy) {
        this.size = size;
        this.page = page;
        this.sortBy = sortBy;
        this.orderBy = orderBy;
    }

    /**
     * Gets the SSID of the device.
     *
     * @return The SSID of the device.
     */
    public String getSsid() {
        return ssid;
    }

    /**
     * Sets the SSID of the device.
     *
     * @param ssid The SSID of the device.
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * Gets the ICCID of the device.
     *
     * @return The ICCID of the device.
     */
    public String getIccid() {
        return iccid;
    }

    /**
     * Sets the ICCID of the device.
     *
     * @param iccid The ICCID of the device.
     */
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
     * Gets the MSISDN of the device.
     *
     * @return The MSISDN of the device.
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the MSISDN of the device.
     *
     * @param msisdn The MSISDN of the device.
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * Gets the IMSI of the device.
     *
     * @return The IMSI of the device.
     */
    public String getImsi() {
        return imsi;
    }

    /**
     * Sets the IMSI of the device.
     *
     * @param imsi The IMSI of the device.
     */
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
     * Gets the BSSID of the device.
     *
     * @return The BSSID of the device.
     */
    public String getBssid() {
        return bssid;
    }

    /**
     * Sets the BSSID of the device.
     *
     * @param bssid The BSSID of the device.
     */
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    /**
     * Gets the package serial number of the device.
     *
     * @return The package serial number of the device.
     */
    public String getPackageserialnumber() {
        return packageserialnumber;
    }

    /**
     * Sets the package serial number of the device.
     *
     * @param packageserialnumber The package serial number of the device.
     */
    public void setPackageserialnumber(String packageserialnumber) {
        this.packageserialnumber = packageserialnumber;
    }

    /**
     * Gets the ASC value.
     *
     * @return The ASC value.
     */
    public String getAsc() {
        return asc;
    }

    /**
     * Sets the ASC value.
     *
     * @param asc The ASC value.
     */
    public void setAsc(String asc) {
        this.asc = asc;
    }

    /**
     * Gets the DESC value.
     *
     * @return The DESC value.
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the DESC value.
     *
     * @param desc The DESC value.
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Gets the size of the device details.
     *
     * @return The size of the device details.
     */
    public String getSize() {
        return size;
    }

    /**
     * Sets the size of the device details.
     *
     * @param size The size of the device details.
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * Gets the page number of the device details.
     *
     * @return The page number of the device details.
     */
    public String getPage() {
        return page;
    }

    /**
     * Sets the page number of the device details.
     *
     * @param page The page number of the device details.
     */
    public void setPage(String page) {
        this.page = page;
    }

    /**
     * Gets the field to sort the device details by.
     *
     * @return The field to sort the device details by.
     */
    public String getSortBy() {
        return sortBy;
    }

    /**
     * Sets the field to sort the device details by.
     *
     * @param sortBy The field to sort the device details by.
     */
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    /**
     * Gets the order in which to sort the device details.
     *
     * @return The order in which to sort the device details.
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * Sets the order in which to sort the device details.
     *
     * @param orderBy The order in which to sort the device details.
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
