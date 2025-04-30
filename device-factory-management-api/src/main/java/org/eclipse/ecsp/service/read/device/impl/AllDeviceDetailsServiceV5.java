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
import org.eclipse.ecsp.common.RecordStats;
import org.eclipse.ecsp.common.ResponseConstants;
import org.eclipse.ecsp.common.exception.DeviceNotFoundException;
import org.eclipse.ecsp.config.DeviceInfoQueryProperty;
import org.eclipse.ecsp.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.dao.read.device.IdeviceDetailsDaoV5;
import org.eclipse.ecsp.dto.DeviceInfoAggregateFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryDataWithSubscription;
import org.eclipse.ecsp.dto.DeviceInfoQueryDto;
import org.eclipse.ecsp.dto.read.device.DeviceDetailsBaseDto;
import org.eclipse.ecsp.dto.read.device.DeviceDetailsDtoV5;
import org.eclipse.ecsp.service.DeviceInfoQueryValidator;
import org.eclipse.ecsp.service.read.device.AbstractAllDeviceDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.eclipse.ecsp.service.DeviceInfoQueryValidator.validateSortByAndOrderByFields;

/**
 * This class is an implementation of the AllDeviceDetailsService interface.
 * It provides methods to retrieve all factory data for device details.
 * The class extends the AbstractAllDeviceDetailsService class and overrides the findAllFactoryData method.
 */
@Service
public class AllDeviceDetailsServiceV5 extends
    AbstractAllDeviceDetailsService<DeviceDetailsBaseDto, DeviceInfoQueryDto<DeviceInfoFactoryDataWithSubscription>> {
    private static final int CAPACITY = 300;
    @Autowired
    @Qualifier(value = "deviceDetailsDaoV5")
    protected IdeviceDetailsDaoV5 deviceDetailsDao;

    /**
     * Checks if the total value is valid.
     *
     * @param total the total value to be checked
     * @return true if the total value is not null and greater than 0, false otherwise
     */
    private static boolean isTotalValid(Long total) {
        return (total != null && total > 0);
    }

    /**
     * Retrieves all factory data for device details based on the provided input parameters.
     *
     * @param baseDto    The base DTO containing the device details.
     * @param inputType  The input type(s) to filter the factory data. Can be IMEI, SERIAL_NUMBER, or DEVICE_ID.
     * @return A DTO containing the fetched factory data and related information.
     * @throws DeviceNotFoundException if the input is invalid and no device is found.
     */
    @Override
    public DeviceInfoQueryDto<DeviceInfoFactoryDataWithSubscription> findAllFactoryData(DeviceDetailsBaseDto baseDto,
                                                                                        String... inputType) {
        LOGGER.info("## findAllFactoryData Service - START");
        DeviceDetailsDtoV5 dto = (DeviceDetailsDtoV5) baseDto;

        String sortby = dto.getSortBy();
        String orderBy = dto.getOrderBy();

        boolean enableDeviceSubscriptionDetails =
            config.getBooleanValue(DeviceInfoQueryProperty.ENABLE_DEVICE_SUBSCRIPTION_DETAILS);
        LOGGER.info("## enableDeviceSubscriptionDetails flag: {}", enableDeviceSubscriptionDetails);

        Map<DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum, String> inputParamMap = DeviceInfoQueryValidator
            .validateAndGetInputParams(inputType);

        // inputParam can be IMEI or SERIAL_NUMBER or DEVICE_ID
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputParam = inputParamMap.keySet().iterator().next();

        String inputParamValue = inputParamMap.entrySet().iterator().next().getValue();

        validateSortByAndOrderByFields(DeviceInfoQueryValidator.DeviceInfoQueryFeatureType.DEVICE_DETAILS, sortby,
            orderBy, inputParam);

        Long total = deviceDetailsDao.constructFetchTotalFactoryDataForDeviceDetails(inputParam, inputParamValue);
        LOGGER.info("## total factory data count: {}", total);
        if (!isTotalValid(total) && StringUtils.isNotEmpty(inputParamValue)) {
            throw new DeviceNotFoundException(ResponseConstants.INVALID_INPUT);
        }
        String sizeValue = dto.getSize();
        String pageValue = dto.getPage();
        int page = resolvePageArgument(pageValue);
        int size = resolveSizeArgument(sizeValue);
        List<DeviceInfoFactoryDataWithSubscription> factoryDataList = null;
        if (isTotalValid(total)) {
            String sortByColumnName = DeviceInfoQueryValidator.DEVICE_DETAILS_SORT_BY_COLUMN_MAPPING.get(sortby);
            LOGGER.debug("## sortByColumnName: {}", sortByColumnName);
            factoryDataList =
                deviceDetailsDao.constructFetchFactoryData(inputParam, inputParamValue, size, page, sortByColumnName,
                    orderBy);
        }
        DeviceInfoAggregateFactoryData aggregateData = new DeviceInfoAggregateFactoryData();
        aggregateData.setStateCount(deviceDetailsDao.constructFetchAggregateFactoryData(inputParam, inputParamValue));
        // iterate list and set status on basis of imei
        setStatus(enableDeviceSubscriptionDetails, factoryDataList);

        RecordStats<DeviceInfoAggregateFactoryData> recordStats = new RecordStats<>();
        recordStats.setPage(page);
        recordStats.setSize(size);
        recordStats.setTotal(total != null ? total : Long.valueOf(0L));
        recordStats.setAggregate(aggregateData);

        DeviceInfoQueryDto<DeviceInfoFactoryDataWithSubscription> result = new DeviceInfoQueryDto<>();
        result.setRecordStats(recordStats);
        result.setData(factoryDataList);

        return result;
    }

    /**
     * Sets the status of device subscription details for a list of factory data.
     *
     * @param enableDeviceSubscriptionDetails a boolean indicating whether to enable device subscription details
     * @param factoryDataList a list of DeviceInfoFactoryDataWithSubscription objects containing factory data
     */
    private void setStatus(boolean enableDeviceSubscriptionDetails,
                           List<DeviceInfoFactoryDataWithSubscription> factoryDataList) {
        if (enableDeviceSubscriptionDetails) {
            StringBuilder imeiWithoutSubscriptionDetailsSb = new StringBuilder(CAPACITY);
            imeiWithoutSubscriptionDetailsSb.append("[");
            boolean isSubscriptionNotFoundForAnyImei = false;
            for (DeviceInfoFactoryDataWithSubscription entity : factoryDataList) {
                try {
                    ResponseEntity<String> response =
                        restClientLibrary.doGet(getSubscriptionServiceUrl(entity.getImei()),
                            createHeaders(),
                            String.class);
                    if (response.getStatusCode().equals(HttpStatus.OK)) {
                        String subscriberId = getValue(response.getBody(), SUBSCRIBER_ID);
                        String status = getValue(response.getBody(), SUBSCRIPTION_STATUS);
                        entity.setSubscriptionStatus(status);
                        entity.setSubscriberId(subscriberId);
                    }
                } catch (Exception e) {
                    imeiWithoutSubscriptionDetailsSb.append(entity.getImei()).append(",");
                    isSubscriptionNotFoundForAnyImei = true;
                }
            }
            if (isSubscriptionNotFoundForAnyImei) {
                imeiWithoutSubscriptionDetailsSb.deleteCharAt(imeiWithoutSubscriptionDetailsSb.lastIndexOf(","));
                imeiWithoutSubscriptionDetailsSb.append("]");
                LOGGER.warn("Unable to find subscription details for these IMEI: {}", imeiWithoutSubscriptionDetailsSb);
            }
            imeiWithoutSubscriptionDetailsSb.setLength(0);
        }
    }
}
