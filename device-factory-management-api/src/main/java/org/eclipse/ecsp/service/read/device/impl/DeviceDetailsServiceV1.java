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

package org.eclipse.ecsp.service.read.device.impl;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.common.exception.DeviceNotFoundException;
import org.eclipse.ecsp.dao.read.device.IdeviceDetailsDaoV1;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.read.device.DeviceDetailsBaseDto;
import org.eclipse.ecsp.dto.read.device.DeviceDetailsDtoV1;
import org.eclipse.ecsp.service.read.device.AbstractDeviceDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides the implementation for retrieving device details from the factory data.
 */
@Service
public class DeviceDetailsServiceV1 extends AbstractDeviceDetailsService<DeviceDetailsBaseDto> {

    private final IdeviceDetailsDaoV1 deviceInfoQueryFindDaoV1;

    /**
     * Constructs a new DeviceDetailsServiceV1 with the specified device details DAO.
     *
     * @param deviceInfoQueryFindDaoV1 the device details DAO to be used
     */
    @Autowired
    public DeviceDetailsServiceV1(
        @Qualifier(value = "deviceDetailsDaoV1") IdeviceDetailsDaoV1 deviceInfoQueryFindDaoV1) {
        this.deviceInfoQueryFindDaoV1 = deviceInfoQueryFindDaoV1;
    }

    /**
     * Retrieves the factory data for the given device details.
     *
     * @param baseDto The base device details.
     * @return The list of factory data for the device.
     * @throws DeviceNotFoundException If no data is found in the inventory for the requested inputs.
     */
    @Override
    public List<DeviceInfoFactoryData> findFactoryData(DeviceDetailsBaseDto baseDto) {
        LOGGER.info("## findFactoryData Service - START");
        DeviceDetailsDtoV1 dto = (DeviceDetailsDtoV1) baseDto;
        String serialNumber = dto.getSerialNumber();
        String imei = dto.getImei();
        String ssid = dto.getSsid();
        String iccid = dto.getIccid();
        String msisdn = dto.getMsisdn();
        String imsi = dto.getImsi();
        String bssid = dto.getBssid();
        String packageserialnumber = dto.getPackageserialnumber();
        if (StringUtils.isEmpty(serialNumber) && StringUtils.isEmpty(imei) && StringUtils.isEmpty(ssid)
            && StringUtils.isEmpty(iccid)
            && StringUtils.isEmpty(msisdn) && StringUtils.isEmpty(imsi) && StringUtils.isEmpty(bssid) && StringUtils
            .isEmpty(packageserialnumber)) {
            throw new DeviceNotFoundException("Either Serial number or IMEI is mandatory");
        }
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("serialNumber", serialNumber);
        requestParams.put("imei", imei);
        requestParams.put("ssid", ssid);
        requestParams.put("iccid", iccid);
        requestParams.put("msisdn", msisdn);
        requestParams.put("imsi", imsi);
        requestParams.put("bssid", bssid);
        requestParams.put("packageSerialNumber", packageserialnumber);
        List<DeviceInfoFactoryData> factoryDataList = deviceInfoQueryFindDaoV1
            .constructAndFetchFactoryData(requestParams);

        if (CollectionUtils.isEmpty(factoryDataList)) {
            throw new DeviceNotFoundException("No data is found in inventory for the requested inputs");
        }
        return factoryDataList;
    }
}
