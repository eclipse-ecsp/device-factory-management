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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.common.CommonConstants;
import org.eclipse.ecsp.common.rowmapper.DeviceInfoFactoryDataMapperWithSubscription;
import org.eclipse.ecsp.common.rowmapper.DeviceStateAggregateDataMapper;
import org.eclipse.ecsp.common.util.SqlUtility;
import org.eclipse.ecsp.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.dao.read.device.AbstractDeviceDetailsDaoV4;
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
 * This class represents a DAO (Data Access Object) for managing device details in the database.
 * It extends the AbstractDeviceDetailsDaoV4 class.
 * The DAO provides methods for fetching total factory data for device details,
 * constructing filter queries, and fetching factory data based on different input types.
 */
@Repository
public class DeviceDetailsDaoV4 extends AbstractDeviceDetailsDaoV4 {

    private static final String WHERE_OPERATOR = " WHERE ";
    private static final String PAGE_FILTER = "LIMIT :limit OFFSET :offset";
    private final NamedParameterJdbcTemplate namedParamJdbcTemplate;

    /**
     * Constructs a new DeviceDetailsDaoV4 object with the specified NamedParameterJdbcTemplate.
     *
     * @param namedParamJdbcTemplate the NamedParameterJdbcTemplate to be used for database operations
     */
    @Autowired
    public DeviceDetailsDaoV4(NamedParameterJdbcTemplate namedParamJdbcTemplate) {
        this.namedParamJdbcTemplate = namedParamJdbcTemplate;
    }

    /**
     * Constructs and executes a query to fetch the total factory data for device details based on the input type and
     * value.
     *
     * @param inputType      The input type for filtering the device details (IMEI, SERIAL_NUMBER, DEVICE_ID).
     * @param inputTypeValue The value corresponding to the input type.
     * @return The total count of factory data for device details.
     */
    public Long constructFetchTotalFactoryDataForDeviceDetails(
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType, String inputTypeValue) {

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
     * Constructs a filter query for searching IMEI values.
     *
     * @param imei The IMEI value to search for.
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
     * Constructs a filter string for searching serial numbers.
     *
     * @param serialNumber The serial number to search for.
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
     * Constructs and executes a query to fetch factory data based on the specified parameters.
     *
     * @param type       The type of device details input (IMEI, SERIAL_NUMBER, DEVICE_ID, STATE).
     * @param searchKey  The search key to filter the factory data.
     * @param sizeValue  The number of results to fetch per page.
     * @param pageValue  The page number of results to fetch.
     * @param sortBy     The field to sort the results by.
     * @param orderBy    The order in which to sort the results (ASC or DESC).
     * @return A list of DeviceInfoFactoryDataWithSubscription objects containing the fetched factory data.
     */
    public List<DeviceInfoFactoryDataWithSubscription> constructFetchFactoryData(
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum type,
        String searchKey,
        int sizeValue, int pageValue, String sortBy, String orderBy) {

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
            case STATE:
                filterQuery = constructStateFilter(searchKey);
                break;
            default:
                break;
        }

        if (!filterQuery.isEmpty()) {
            queryCreator.append(WHERE_OPERATOR + filterQuery);
        }

        queryCreator.append(SqlUtility.prepareSortByAndOrderByQuery(sortBy, orderBy));

        queryCreator.append(constructPageFilter());

        MapSqlParameterSource parameters = constructParameterSource(sizeValue, pageValue);
        DeviceInfoFactoryDataMapperWithSubscription dataMapper = getDeviceInfoFactoryDataWithSubscriptionMapper();

        return namedParamJdbcTemplate.query(queryCreator.toString(), parameters, dataMapper);
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
