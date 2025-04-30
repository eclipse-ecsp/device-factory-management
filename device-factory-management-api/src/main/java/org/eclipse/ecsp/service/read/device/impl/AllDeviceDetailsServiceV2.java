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
import org.eclipse.ecsp.common.ResponseConstants;
import org.eclipse.ecsp.common.exception.DeviceNotFoundException;
import org.eclipse.ecsp.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.dao.read.device.IdeviceDetailsDaoV2;
import org.eclipse.ecsp.dto.DeviceInfoAggregateFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoPage;
import org.eclipse.ecsp.dto.read.device.DeviceDetailsBaseDto;
import org.eclipse.ecsp.dto.read.device.DeviceDetailsDtoV2;
import org.eclipse.ecsp.service.read.device.AbstractAllDeviceDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.eclipse.ecsp.common.util.Utils.validateImei;
import static org.eclipse.ecsp.common.util.Utils.validateSerialNumber;

/**
 * This class is an implementation of the AllDeviceDetailsService interface.
 * It provides the functionality to retrieve all device details from the database.
 * It extends the AbstractAllDeviceDetailsService class and overrides the findAllFactoryData method.
 */
@Service
public class AllDeviceDetailsServiceV2 extends
    AbstractAllDeviceDetailsService<DeviceDetailsBaseDto, DeviceInfoPage<List<DeviceInfoFactoryData>>> {

    protected final IdeviceDetailsDaoV2 deviceInfoQueryFindDaoV2;

    /**
     * This class is responsible for providing details of all devices.
     * It is used to retrieve device information from the database.
     */
    @Autowired
    public AllDeviceDetailsServiceV2(
        @Qualifier(value = "deviceDetailsDaoV2") IdeviceDetailsDaoV2 deviceInfoQueryFindDaoV2) {
        this.deviceInfoQueryFindDaoV2 = deviceInfoQueryFindDaoV2;
    }

    /**
     * Retrieves a page of factory data for devices based on the provided criteria.
     *
     * @param baseDto The base DTO containing the device details.
     * @param inputType The input types to filter the factory data.
     * @return A page of device factory data.
     * @throws DeviceNotFoundException If the device is not found based on the provided criteria.
     */
    @Override
    public DeviceInfoPage<List<DeviceInfoFactoryData>> findAllFactoryData(DeviceDetailsBaseDto baseDto,
                                                                          String... inputType) {
        DeviceDetailsDtoV2 dto = (DeviceDetailsDtoV2) baseDto;

        String asc = dto.getSortBy();
        String desc = dto.getOrderBy();
        String serialNumber = dto.getSerialNumber();
        String imei = dto.getImei();
        String isDetailsRequired = dto.getIsDetailsRequired();

        validateRequestData(asc, desc, isDetailsRequired);
        validateImei(imei);
        validateSerialNumber(serialNumber);
        String sizeValue = dto.getSize();
        String pageValue = dto.getPage();
        int page = resolvePageArgument(pageValue);
        int size = resolveSizeArgument(sizeValue);

        Long total = deviceInfoQueryFindDaoV2.constructFetchTotalFactoryData(serialNumber, imei);
        if (total == null || total <= 0) {

            if (StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(serialNumber)) {
                throw new DeviceNotFoundException(ResponseConstants.INVALID_IMEI_SERIAL_NUMBER_COMBINATION);
            }
            if (StringUtils.isNotEmpty(imei)) {
                throw new DeviceNotFoundException(ResponseConstants.INVALID_IMEI);
            }
            if (StringUtils.isNotEmpty(serialNumber)) {
                throw new DeviceNotFoundException(ResponseConstants.INVALID_SERIAL_NUMBER);
            }
        }
        List<DeviceInfoFactoryData> factoryDataList = null;
        DeviceInfoPage.HcpPageable hcpPageable = null;

        if (Boolean.parseBoolean(isDetailsRequired)) {
            factoryDataList =
                deviceInfoQueryFindDaoV2.constructFetchFactoryData(size, page, asc, desc, serialNumber, imei);
            hcpPageable = new DeviceInfoPage.HcpPageable(page, size, total);
        }

        DeviceInfoAggregateFactoryData aggregateData = new DeviceInfoAggregateFactoryData();
        aggregateData.setStateCount(deviceInfoQueryFindDaoV2.constructFetchAggregateDeviceState(serialNumber, imei));
        aggregateData.setCount(total);
        return new DeviceInfoPage(aggregateData, factoryDataList, hcpPageable);
    }

    /**
     * Validates the request data for the given parameters.
     *
     * @param asc              The ascending order parameter.
     * @param desc             The descending order parameter.
     * @param isDetailsRequired The flag indicating if details are required.
     * @throws IllegalArgumentException If any of the parameters are invalid.
     */
    private void validateRequestData(String asc, String desc, String isDetailsRequired) {
        if (StringUtils.isNotEmpty(asc) && !(DeviceInfoFactoryDataDao.IMEI.equalsIgnoreCase(asc)
            || DeviceInfoFactoryDataDao.SERIAL_NUMBER.equalsIgnoreCase(asc))) {
            LOGGER.debug("Invalid asc value:{}", asc);
            throw new IllegalArgumentException(ResponseConstants.WRONG_QUERY_ASC_DESC_VALUE);
        }
        if (StringUtils.isNotEmpty(desc) && !(DeviceInfoFactoryDataDao.IMEI.equalsIgnoreCase(desc)
            || DeviceInfoFactoryDataDao.SERIAL_NUMBER.equalsIgnoreCase(desc))) {
            LOGGER.debug("Invalid desc value:{}", desc);
            throw new IllegalArgumentException(ResponseConstants.WRONG_QUERY_ASC_DESC_VALUE);
        }

        if (!StringUtils.isNotEmpty(isDetailsRequired)
            || !(isDetailsRequired.equalsIgnoreCase("true") || isDetailsRequired.equalsIgnoreCase("false"))) {
            LOGGER.debug("Invalid isDetailsRequired value:{}", isDetailsRequired);
            throw new IllegalArgumentException(ResponseConstants.WRONG_ISDETAILSREQUIRED_VALUE);
        }
    }
}

