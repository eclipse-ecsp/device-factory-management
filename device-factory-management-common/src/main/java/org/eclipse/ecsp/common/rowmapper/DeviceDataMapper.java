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

package org.eclipse.ecsp.common.rowmapper;

import org.eclipse.ecsp.common.DatabaseConstants;
import org.eclipse.ecsp.dto.DeviceData;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class is responsible for mapping the device data to an ordered map.
 */
public class DeviceDataMapper {

    /**
     * Retrieves an ordered map of device data.
     *
     * @param deviceData The device data object.
     * @return An ordered map containing the device data.
     * @throws ParseException If there is an error parsing the date.
     */
    public Map<String, Object> getOrderedMap(DeviceData deviceData) throws ParseException {
        if (null == deviceData) {
            return null;
        }
        LinkedHashMap<String, Object> orderedMap = new LinkedHashMap<>();
        SimpleDateFormat dateFormatter = new SimpleDateFormat(DatabaseConstants.DEVICEINFOFACTORYDATA_TIMESTAMP_FORMAT);
        dateFormatter.setLenient(false);

        if (deviceData.getManufacturingDate() != null) {
            orderedMap.put("manufacturing_date",
                new Timestamp(dateFormatter.parse(deviceData.getManufacturingDate()).getTime()));
        }

        if (deviceData.getModel() != null) {
            orderedMap.put("model", deviceData.getModel());
        }

        if (deviceData.getSerialNumber() != null) {
            orderedMap.put("serial_number", deviceData.getSerialNumber());
        }

        if (deviceData.getRecordDate() != null) {
            orderedMap.put("record_date", new Timestamp(dateFormatter.parse(deviceData.getRecordDate()).getTime()));
        }

        if (deviceData.getFactoryAdmin() != null) {
            orderedMap.put("factory_admin", deviceData.getFactoryAdmin());
        }

        if (deviceData.getPackageSerialNumber() != null) {
            orderedMap.put("package_serial_number", deviceData.getPackageSerialNumber());
        }

        if (deviceData.getImei() != null) {
            orderedMap.put("imei", deviceData.getImei());
        }
        if (deviceData.getIccid() != null) {
            orderedMap.put("iccid", deviceData.getIccid());
        }
        if (deviceData.getSsid() != null) {
            orderedMap.put("ssid", deviceData.getSsid());
        }
        if (deviceData.getBssid() != null) {
            orderedMap.put("bssid", deviceData.getBssid());
        }
        if (deviceData.getMsisdn() != null) {
            orderedMap.put("msisdn", deviceData.getMsisdn());
        }
        if (deviceData.getImsi() != null) {
            orderedMap.put("imsi", deviceData.getImsi());
        }
        if (deviceData.getPlatformVersion() != null) {
            orderedMap.put("platform_version", deviceData.getPlatformVersion());
        }

        return orderedMap;

    }
}
