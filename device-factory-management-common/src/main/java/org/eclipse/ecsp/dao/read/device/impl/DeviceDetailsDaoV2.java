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
import org.eclipse.ecsp.common.rowmapper.DeviceInfoFactoryDataMapper;
import org.eclipse.ecsp.common.rowmapper.DeviceStateAggregateDataMapper;
import org.eclipse.ecsp.dao.read.device.AbstractDeviceDetailsDaoV2;
import org.eclipse.ecsp.dto.DeviceInfoAggregateFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.DeviceState;
import org.eclipse.ecsp.dto.DeviceStateAggregateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static org.eclipse.ecsp.common.rowmapper.DeviceInfoFactoryDataMapper.getDeviceInfoFactoryDataMapper;

/**
 * This class represents a data access object for managing device details.
 * It extends the AbstractDeviceDetailsDaoV2 class.
 * The class provides methods for fetching device data from the database.
 */
@Repository
public class DeviceDetailsDaoV2 extends AbstractDeviceDetailsDaoV2 {

    private static final String PAGE_FILTER = "LIMIT :limit OFFSET :offset";
    private final NamedParameterJdbcTemplate namedParamJdbcTemplate;

    /**
     * Constructs a new instance of the DeviceDetailsDaoV2 class.
     *
     * @param namedParamJdbcTemplate The NamedParameterJdbcTemplate used for database operations.
     */
    @Autowired
    public DeviceDetailsDaoV2(NamedParameterJdbcTemplate namedParamJdbcTemplate) {
        this.namedParamJdbcTemplate = namedParamJdbcTemplate;
    }

    /**
     * Constructs and executes a query to fetch the total factory data count for a given serial number and IMEI.
     *
     * @param serialNumber The serial number of the device.
     * @param imei The IMEI number of the device.
     * @return The total count of factory data for the device.
     */
    public Long constructFetchTotalFactoryData(String serialNumber, String imei) {
        LOGGER.info("Inside constructFetchTotalFactoryData method");
        StringBuilder queryCreator = new StringBuilder("select count(*) from public.\"DeviceInfoFactoryData\" ");
        queryCreator.append(constructImeiSerialNumberFilter(serialNumber, imei));
        LOGGER.info("Query generated :: {}", queryCreator);
        Long deviceCount =
            namedParamJdbcTemplate.queryForObject(queryCreator.toString(), new MapSqlParameterSource(), Long.class);
        LOGGER.info("Exit constructFetchTotalFactoryData method");
        return deviceCount;
    }

    /**
     * Constructs a filter string based on the provided serial number and IMEI.
     *
     * @param serialNumber The serial number to filter by. Can be null or empty.
     * @param imei The IMEI to filter by. Can be null or empty.
     * @return The constructed filter string.
     */
    private String constructImeiSerialNumberFilter(String serialNumber, String imei) {
        StringBuilder stringQueryFilter = new StringBuilder();
        if (!StringUtils.isEmpty(imei)) {
            stringQueryFilter = new StringBuilder("WHERE ");
            stringQueryFilter.append("imei LIKE '%");
            stringQueryFilter.append(imei.replace("'", "''").replace("_", "__"));
            stringQueryFilter.append("%' ");
        }
        if (!StringUtils.isEmpty(serialNumber)) {
            if (!StringUtils.isEmpty(imei)) {
                stringQueryFilter.append("and ");
            } else {
                stringQueryFilter = new StringBuilder("WHERE ");
            }
            stringQueryFilter.append("serial_number LIKE '%");
            stringQueryFilter.append(serialNumber.replace("'", "''").replace("_", "__"));
            stringQueryFilter.append("%' ");
        }
        return stringQueryFilter.toString();
    }

    /**
     * Constructs and fetches factory data based on the specified parameters.
     *
     * @param size          The number of records to fetch.
     * @param page          The page number of the records to fetch.
     * @param asc           The ascending order field.
     * @param desc          The descending order field.
     * @param serialNumber  The serial number filter.
     * @param imei          The IMEI filter.
     * @return              A list of DeviceInfoFactoryData objects that match the specified criteria.
     */
    public List<DeviceInfoFactoryData> constructFetchFactoryData(int size, int page, String asc, String desc,
                                                                 String serialNumber, String imei) {
        LOGGER.info("Inside constructFetchFactoryData method");

        StringBuilder queryBuilder = new StringBuilder(CommonConstants.SELECT_FROM_DEVICE_INFO_FACTORY_DATA);

        queryBuilder.append(constructImeiSerialNumberFilter(serialNumber, imei));
        queryBuilder.append(constructOrderByFilter(asc, desc));
        queryBuilder.append(constructPageFilter());

        MapSqlParameterSource parameters = constructParameterSource(size, page);
        DeviceInfoFactoryDataMapper dataMapper = getDeviceInfoFactoryDataMapper();

        LOGGER.info("Query generated: {}", queryBuilder);
        List<DeviceInfoFactoryData> deviceInfo =
            namedParamJdbcTemplate.query(queryBuilder.toString(), parameters, dataMapper);
        LOGGER.info("Exit constructAndFetchFactoryData method");
        return deviceInfo;
    }

    /**
     * Constructs the ORDER BY filter based on the provided ascending and descending values.
     *
     * @param asc  The ascending value to be used in the ORDER BY filter.
     * @param desc The descending value to be used in the ORDER BY filter.
     * @return The constructed ORDER BY filter as a string.
     */
    private String constructOrderByFilter(String asc, String desc) {
        StringBuilder queryOrderBy = new StringBuilder();
        if (StringUtils.isNotEmpty(asc)) {
            queryOrderBy.append(" ORDER BY \"");
            queryOrderBy.append(asc);
            queryOrderBy.append("\" COLLATE \"C\" ASC ");
        } else if (StringUtils.isNotEmpty(desc)) {
            queryOrderBy.append(" ORDER BY \"");
            queryOrderBy.append(desc);
            queryOrderBy.append("\" COLLATE \"C\" DESC ");
        } else {
            queryOrderBy.append(" ORDER BY \"ID\" ASC ");
        }
        return queryOrderBy.toString();
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
     * @return A MapSqlParameterSource object containing the limit and offset parameters.
     */
    private MapSqlParameterSource constructParameterSource(int size, int page) {
        page = page - 1;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("limit", size);
        parameters.addValue("offset", page * size);
        return parameters;
    }

    /**
     * Constructs and fetches the aggregate device state for the given serial number and IMEI.
     *
     * @param serialNumber The serial number of the device.
     * @param imei The IMEI number of the device.
     * @return The aggregate device state count.
     */
    public DeviceInfoAggregateFactoryData.StateCount constructFetchAggregateDeviceState(String serialNumber,
                                                                                        String imei) {
        LOGGER.info("Inside constructFetchAggregateDeviceState method");

        StringBuilder queryCreator = new StringBuilder(
            "select state, count(state) as count from public.\"DeviceInfoFactoryData\" ");
        queryCreator.append(constructImeiSerialNumberFilter(serialNumber, imei));
        queryCreator.append("group BY state ");
        List<DeviceStateAggregateData> deviceStateMap = namedParamJdbcTemplate.query(queryCreator.toString(),
            Collections.emptyMap(), new DeviceStateAggregateDataMapper());
        DeviceInfoAggregateFactoryData.StateCount stateCount = new DeviceInfoAggregateFactoryData.StateCount();
        DeviceState.convertStateCount(stateCount, deviceStateMap);
        return stateCount;
    }
}
