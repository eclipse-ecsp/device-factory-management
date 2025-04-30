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

package org.eclipse.ecsp.dao.read.device;

import org.eclipse.ecsp.dto.DeviceInfoAggregateFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;

import java.util.List;

/**
 * This interface provides methods to fetch device details from the database.
 */
public interface IdeviceDetailsDaoV2 {

    /**
     * Fetches the total factory data for a given serial number and IMEI.
     *
     * @param serialNumber The serial number of the device.
     * @param imei         The IMEI number of the device.
     * @return The count of total factory data.
     */
    Long constructFetchTotalFactoryData(String serialNumber, String imei);

    /**
     * Fetches the factory data for a given size, page, sorting order, serial number, and IMEI.
     *
     * @param size         The number of records to fetch.
     * @param page         The page number.
     * @param asc          The sorting order in ascending or descending order.
     * @param desc         The sorting order in descending or ascending order.
     * @param serialNumber The serial number of the device.
     * @param imei         The IMEI number of the device.
     * @return A list of DeviceInfoFactoryData objects.
     */
    List<DeviceInfoFactoryData> constructFetchFactoryData(int size, int page, String asc, String desc,
                                                          String serialNumber, String imei);

    /**
     * Fetches the total device state for a given serial number and IMEI.
     *
     * @param serialNumber The serial number of the device.
     * @param imei         The IMEI number of the device.
     * @return The state count of the device.
     */
    DeviceInfoAggregateFactoryData.StateCount constructFetchAggregateDeviceState(String serialNumber, String imei);
}
