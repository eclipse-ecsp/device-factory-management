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

package org.eclipse.ecsp.dao.read.device.impl;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.common.CommonConstants;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.common.rowmapper.DeviceInfoFactoryDataMapperWithSubscription;
import org.eclipse.ecsp.common.rowmapper.DeviceStateAggregateDataMapper;
import org.eclipse.ecsp.common.util.SqlUtility;
import org.eclipse.ecsp.config.DeviceInfoQueryProperty;
import org.eclipse.ecsp.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.dao.read.device.AbstractDeviceDetailsDaoV5;
import org.eclipse.ecsp.dto.DeviceInfoAggregateFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryDataWithSubscription;
import org.eclipse.ecsp.dto.DeviceState;
import org.eclipse.ecsp.dto.DeviceStateAggregateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static org.eclipse.ecsp.common.rowmapper.DeviceInfoFactoryDataMapperWithSubscription.getDeviceInfoFactoryDataWithSubscriptionMapper;

/**
 * This class represents the DAO (Data Access Object) for retrieving device details.
 * It provides methods to fetch device details based on different input types.
 */
@Repository
public class DeviceDetailsDaoV5 extends AbstractDeviceDetailsDaoV5 {
    private static final String WHERE_OPERATOR = " WHERE ";
    private static final String PAGE_FILTER = "LIMIT :limit OFFSET :offset";
    private final NamedParameterJdbcTemplate namedParamJdbcTemplate;
    @Resource(name = "envConfig")
    protected EnvConfig<DeviceInfoQueryProperty> config;

    /**
     * Constructs a new instance of the DeviceDetailsDaoV5 class.
     *
     * @param namedParamJdbcTemplate the NamedParameterJdbcTemplate to be used for database operations
     */
    @Autowired
    public DeviceDetailsDaoV5(NamedParameterJdbcTemplate namedParamJdbcTemplate) {
        this.namedParamJdbcTemplate = namedParamJdbcTemplate;
    }

    /**
     * Constructs and executes a query to fetch the total factory data count for device details based on the input type
     * and value.
     *
     * @param inputType      The input type for filtering the device details (IMEI, SERIAL_NUMBER, DEVICE_ID, VIN,
     *                       STATE).
     * @param inputTypeValue The value corresponding to the input type for filtering the device details.
     * @return The total count of factory data for device details based on the input type and value.
     */
    public Long constructFetchTotalFactoryDataForDeviceDetails(
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType,
        String inputTypeValue) {

        StringBuilder queryCreator;
        String filterQuery = "";
        queryCreator = new StringBuilder("select count(*) from public.\"DeviceInfoFactoryData\" ");
        switch (inputType) {
            case IMEI:
                filterQuery = constructImeiFilter(inputTypeValue);
                break;
            case SERIAL_NUMBER:
                filterQuery = constructSerialNumberFilter(inputTypeValue);
                break;
            case DEVICE_ID:
                queryCreator = new StringBuilder("select count(*) from public.\"device_association\" ");
                filterQuery = constructDeviceIdFilter(inputTypeValue);
                break;
            case VIN:
                queryCreator = new StringBuilder("select count(*) from public.\"vin_details\" ");
                filterQuery = constructVinFilter(inputTypeValue);
                break;
            case STATE:
                filterQuery = constructStateFilter(inputTypeValue);
                break;
            default:
                break;
        }

        if (!filterQuery.isEmpty()) {
            queryCreator.append(WHERE_OPERATOR + filterQuery);
        }

        return (namedParamJdbcTemplate.queryForObject(queryCreator.toString(), new MapSqlParameterSource(),
            Long.class));

    }

    /**
     * Constructs a VIN filter based on the provided VIN.
     *
     * @param vin The VIN to construct the filter for.
     * @return The constructed VIN filter.
     */
    private String constructVinFilter(String vin) {
        LOGGER.info("## constructVinFilter - START vin: {}", vin);
        StringBuilder stringQueryFilter = new StringBuilder();
        if (!StringUtils.isEmpty(vin)) {
            stringQueryFilter = new StringBuilder(" vin LIKE '%");
            stringQueryFilter.append(vin.replace("'", "''").replace("_", "__"));
            stringQueryFilter.append("%' ");
        }
        String vinQuery = stringQueryFilter.toString();
        LOGGER.info("## constructVinFilter - END vinQuery: {}", vinQuery);
        return vinQuery;
    }

    /**
     * Constructs a filter query for searching devices based on the IMEI.
     *
     * @param imei The IMEI to filter the devices by.
     * @return A string representation of the filter query.
     */
    private String constructImeiFilter(String imei) {
        StringBuilder stringQueryFilter = new StringBuilder();
        if (!StringUtils.isEmpty(imei)) {
            stringQueryFilter = new StringBuilder(" imei LIKE '%");
            stringQueryFilter.append(imei.replace("'", "''").replace("_", "__"));
            stringQueryFilter.append("%' ");
        }

        return stringQueryFilter.toString();
    }

    /**
     * Constructs a filter string based on the provided serial number.
     *
     * @param serialNumber The serial number to be used for filtering.
     * @return The constructed filter string.
     */
    private String constructSerialNumberFilter(String serialNumber) {
        StringBuilder stringQueryFilter = new StringBuilder();
        if (!StringUtils.isEmpty(serialNumber)) {
            stringQueryFilter = new StringBuilder(" serial_number LIKE '%");
            stringQueryFilter.append(serialNumber.replace("'", "''").replace("_", "__"));
            stringQueryFilter.append("%' ");
        }

        return stringQueryFilter.toString();
    }

    /**
     * Constructs a device ID filter based on the provided device ID.
     *
     * @param deviceId The device ID to filter by.
     * @return A string representation of the device ID filter.
     */
    private String constructDeviceIdFilter(String deviceId) {
        StringBuilder stringQueryFilter = new StringBuilder();
        if (!StringUtils.isEmpty(deviceId)) {
            stringQueryFilter = new StringBuilder(" harman_id LIKE '%");
            stringQueryFilter.append(deviceId.replace("'", "''").replace("_", "__"));
            stringQueryFilter.append("%' ");
        }

        return stringQueryFilter.toString();
    }

    /**
     * Constructs a filter string based on the provided state.
     *
     * @param state The state to filter by.
     * @return The constructed filter string.
     */
    private String constructStateFilter(String state) {
        StringBuilder stringQueryFilter = new StringBuilder();
        if (!StringUtils.isEmpty(state)) {
            stringQueryFilter = new StringBuilder(" state = '");
            stringQueryFilter.append(state.replace("'", "''"));
            stringQueryFilter.append("'");
        }

        return stringQueryFilter.toString();
    }

    /**
     * Constructs and fetches factory data based on the provided parameters.
     *
     * @param type        The type of device details input.
     * @param searchKey   The search key for filtering the data.
     * @param sizeValue   The number of records to fetch per page.
     * @param pageValue   The page number of the records to fetch.
     * @param sortBy      The field to sort the records by.
     * @param orderBy     The order in which to sort the records (ascending or descending).
     * @return A list of DeviceInfoFactoryDataWithSubscription objects containing the fetched factory data.
     */
    public List<DeviceInfoFactoryDataWithSubscription> constructFetchFactoryData(
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum
            type,
        String searchKey,
        int sizeValue, int pageValue, String sortBy, String orderBy) {
        LOGGER.info("## constructFetchFactoryData DAO - START");
        String filterQuery = "";
        StringBuilder queryCreator = new StringBuilder(CommonConstants.SELECT_FROM_DEVICE_INFO_FACTORY_DATA);
        switch (type) {
            case IMEI:
                filterQuery = constructImeiFilter(searchKey);
                break;
            case SERIAL_NUMBER:
                filterQuery = constructSerialNumberFilter(searchKey);
                break;
            case DEVICE_ID:
                queryCreator = new StringBuilder(CommonConstants.SELECT_FROM_DEVICE_INFO_FACTORY_DATA_AND_ASSOCIATION);
                filterQuery = constructDeviceIdFilter(searchKey);
                break;
            case VIN:
                queryCreator = new StringBuilder(CommonConstants.SELECT_FROM_DEVICE_INFO_FACTORY_DATA);
                filterQuery = constructVinFilter(searchKey);
                break;
            case STATE:
                filterQuery = constructStateFilter(searchKey);
                break;
            default:
                break;
        }

        //below condition is needed when device create flow is for guest user or swm integration
        String deviceCreationType = config.getStringValue(DeviceInfoQueryProperty.DEVICE_CREATION_TYPE);
        if ("guestUser".equals(deviceCreationType) || "swmIntegration".equals(deviceCreationType)) {
            queryCreator.append(CommonConstants.VIN_DETAILS_JOIN_CONDITION);
        }

        if (!filterQuery.isEmpty()) {
            queryCreator.append(WHERE_OPERATOR + filterQuery);
        }

        if (type.equals(DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.DEVICE_ID) || type.equals(
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.VIN)) {
            queryCreator.append(SqlUtility.prepareSortByAndOrderByQuery(sortBy, orderBy, "DeviceInfoFactoryData"));
        } else {
            queryCreator.append(SqlUtility.prepareSortByAndOrderByQuery(sortBy, orderBy));
        }
        queryCreator.append(constructPageFilter());

        MapSqlParameterSource parameters = constructParameterSource(sizeValue, pageValue);
        DeviceInfoFactoryDataMapperWithSubscription dataMapper = getDeviceInfoFactoryDataWithSubscriptionMapper();
        String factoryDetailsFetchQuery = queryCreator.toString();
        LOGGER.info("## Factory details fetch query: {}", factoryDetailsFetchQuery);
        List<DeviceInfoFactoryDataWithSubscription> result =
            namedParamJdbcTemplate.query(factoryDetailsFetchQuery, parameters, dataMapper);
        LOGGER.info("## constructFetchFactoryData DAO - END, number of factory details record found: {}",
            result.size());
        return result;
    }

    /**
     * Constructs the page filter.
     *
     * @return the page filter.
     */
    private String constructPageFilter() {
        return PAGE_FILTER;
    }

    /**
     * Constructs a MapSqlParameterSource object with the specified size and page parameters.
     *
     * @param size The number of items to retrieve.
     * @param page The page number.
     * @return The constructed MapSqlParameterSource object.
     */
    private MapSqlParameterSource constructParameterSource(int size, int page) {
        page = page - 1;
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        parameters.addValue("limit", size);
        parameters.addValue("offset", page * size);

        return parameters;
    }

    /**
     * Constructs and fetches the aggregate factory data for the given device details input type and search key.
     *
     * @param type      The type of device details input (IMEI, SERIAL_NUMBER, DEVICE_ID, STATE).
     * @param searchKey The search key used for filtering the data.
     * @return The state count of the device info aggregate factory data.
     */
    public DeviceInfoAggregateFactoryData.StateCount constructFetchAggregateFactoryData(
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum type, String searchKey) {

        StringBuilder queryCreator =
            new StringBuilder("select state, count(state) as count from public.\"DeviceInfoFactoryData\" ");
        String filterQuery = "";
        switch (type) {
            case IMEI:
                filterQuery = constructImeiFilter(searchKey);
                break;
            case SERIAL_NUMBER:
                filterQuery = constructSerialNumberFilter(searchKey);
                break;
            case DEVICE_ID:
                queryCreator = new StringBuilder(
                    "select state, count(state) as count from \"DeviceInfoFactoryData\", \"device_association\"");
                queryCreator.append(CommonConstants.JOIN_CONDITION);
                filterQuery = constructDeviceIdFilter(searchKey);
                break;
            case STATE:
                filterQuery = constructStateFilter(searchKey);
                break;
            default:
                break;
        }

        if (!filterQuery.isEmpty()) {
            if (type.equals(DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.DEVICE_ID)) {
                queryCreator.append(" AND " + filterQuery);
            } else {
                queryCreator.append(WHERE_OPERATOR + filterQuery);
            }
        }

        queryCreator.append("group BY state ");
        LOGGER.debug("constructFetchAggregrateFactoryData: Query generated :: {}", queryCreator);
        List<DeviceStateAggregateData> deviceStateMap = namedParamJdbcTemplate.query(queryCreator.toString(),
            Collections.emptyMap(), new DeviceStateAggregateDataMapper());
        DeviceInfoAggregateFactoryData.StateCount stateCount = new DeviceInfoAggregateFactoryData.StateCount();
        DeviceState.convertStateCount(stateCount, deviceStateMap);
        LOGGER.debug("Exit constructFetchAggregrateFactoryData method");
        return stateCount;
    }

}
