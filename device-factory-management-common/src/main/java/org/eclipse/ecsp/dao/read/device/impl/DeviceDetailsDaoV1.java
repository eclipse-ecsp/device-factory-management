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
import org.eclipse.ecsp.dao.read.device.AbstractDeviceDetailsDaoV1;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * This class represents a DAO (Data Access Object) for retrieving device details.
 * It provides methods to interact with the database and retrieve device information.
 */
@Repository
public class DeviceDetailsDaoV1 extends AbstractDeviceDetailsDaoV1 {

    private final NamedParameterJdbcTemplate namedParamJdbcTemplate;

    /**
     * This class represents a DAO (Data Access Object) for retrieving device details.
     * It provides methods to interact with the database and retrieve device information.
     */
    @Autowired
    public DeviceDetailsDaoV1(NamedParameterJdbcTemplate namedParamJdbcTemplate) {
        this.namedParamJdbcTemplate = namedParamJdbcTemplate;
    }

    /**
     * Appends "and" to the queryCreator if needed.
     *
     * @param needAnd        flag indicating whether "and" is needed
     * @param queryCreator   the query creator
     * @return the updated queryCreator
     */
    private static StringBuilder appendAnd(boolean needAnd, StringBuilder queryCreator) {
        if (needAnd) {
            return queryCreator.append("and ");
        } else {
            return null;
        }
    }

    /**
     * Constructs and fetches factory data based on the request parameters.
     *
     * @param requestParams  the request parameters
     * @return list of DeviceInfoFactoryData
     */
    public List<DeviceInfoFactoryData> constructAndFetchFactoryData(Map<String, String> requestParams) {
        LOGGER.info("Inside constructAndFetchFactoryData method");
        MapSqlParameterSource mapSqlParameter = new MapSqlParameterSource();
        StringBuilder queryCreator = new StringBuilder(CommonConstants.SELECT_FROM_DEVICE_INFO_FACTORY_DATA);
        queryCreator.append("WHERE ");
        boolean needAnd = false;
        String imei = getRequestParamsFromMap(requestParams, "imei");
        needAnd = verifyImei(mapSqlParameter, queryCreator, needAnd, imei);
        String serialNumber = getRequestParamsFromMap(requestParams, "serialNumber");
        needAnd = verifySerialNumber(mapSqlParameter, queryCreator, needAnd, serialNumber);
        String ssid = getRequestParamsFromMap(requestParams, "ssid");
        if (StringUtils.isNotBlank(ssid)) {
            appendAnd(needAnd, queryCreator);
            queryCreator.append("ssid = :ssid ");
            mapSqlParameter.addValue("ssid", ssid);
            needAnd = true;
        }
        String iccid = getRequestParamsFromMap(requestParams, "iccid");
        if (StringUtils.isNotBlank(iccid)) {
            appendAnd(needAnd, queryCreator);
            queryCreator.append("iccid = :iccid ");
            mapSqlParameter.addValue("iccid", iccid);
            needAnd = true;
        }
        String msisdn = getRequestParamsFromMap(requestParams, "msisdn");
        if (StringUtils.isNotBlank(msisdn)) {
            appendAnd(needAnd, queryCreator);
            queryCreator.append("msisdn = :msisdn ");
            mapSqlParameter.addValue("msisdn", msisdn);
            needAnd = true;
        }
        String imsi = getRequestParamsFromMap(requestParams, "imsi");
        if (StringUtils.isNotBlank(imsi)) {
            appendAnd(needAnd, queryCreator);
            queryCreator.append("imsi = :imsi ");
            mapSqlParameter.addValue("imsi", imsi);
            needAnd = true;
        }
        String bssid = getRequestParamsFromMap(requestParams, "bssid");
        if (StringUtils.isNotBlank(bssid)) {
            appendAnd(needAnd, queryCreator);
            queryCreator.append("bssid = :bssid ");
            mapSqlParameter.addValue("bssid", bssid);
            needAnd = true;
        }
        String packageSerialNumber = getRequestParamsFromMap(requestParams, "packageSerialNumber");
        if (StringUtils.isNotBlank(packageSerialNumber)) {
            appendAnd(needAnd, queryCreator);
            queryCreator.append("package_serial_number = :packageSerialNumber ");
            mapSqlParameter.addValue("packageSerialNumber", packageSerialNumber);
        }

        LOGGER.info("Query generated :: {} ", queryCreator);
        List<DeviceInfoFactoryData> deviceInfoFactoryDataList =
            namedParamJdbcTemplate.query(queryCreator.toString(), mapSqlParameter,
                new DeviceInfoFactoryDataMapper());
        LOGGER.info("Exit constructAndFetchFactoryData method");
        return deviceInfoFactoryDataList;
    }

    /**
     * Verifies the IMEI and adds it to the query if not blank.
     *
     * @param mapSqlParameter   the MapSqlParameterSource object
     * @param queryCreator      the query creator
     * @param needAnd           flag indicating whether "and" is needed
     * @param imei              the IMEI value
     * @return the updated needAnd flag
     */
    private boolean verifyImei(MapSqlParameterSource mapSqlParameter, StringBuilder queryCreator, boolean needAnd,
                              String imei) {
        if (StringUtils.isNotBlank(imei)) {
            queryCreator.append("imei = :imei ");
            mapSqlParameter.addValue("imei", imei);
            needAnd = true;
        }
        return needAnd;
    }

    /**
     * Verifies the serial number and adds it to the query if not blank.
     *
     * @param mapSqlParameter   the MapSqlParameterSource object
     * @param queryCreator      the query creator
     * @param needAnd           flag indicating whether "and" is needed
     * @param serialNumber      the serial number value
     * @return the updated needAnd flag
     */
    private boolean verifySerialNumber(MapSqlParameterSource mapSqlParameter, StringBuilder queryCreator,
                                       boolean needAnd, String serialNumber) {
        if (StringUtils.isNotBlank(serialNumber)) {
            appendAnd(needAnd, queryCreator);
            queryCreator.append("serial_number = :serialNumber ");
            mapSqlParameter.addValue("serialNumber", serialNumber);
            needAnd = true;
        }
        return needAnd;
    }

    /**
     * Retrieves the value from the requestParams map based on the key.
     *
     * @param requestParams  the request parameters map
     * @param key            the key to retrieve the value
     * @return the value from the map or null if not found
     */
    private String getRequestParamsFromMap(Map<String, String> requestParams, String key) {
        if (requestParams != null && !requestParams.isEmpty() && requestParams.containsKey(key)) {
            return requestParams.get(key);
        }
        return null;
    }
}
