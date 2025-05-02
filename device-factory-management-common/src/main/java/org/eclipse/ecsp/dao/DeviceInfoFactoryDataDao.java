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

package org.eclipse.ecsp.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.common.CommonConstants;
import org.eclipse.ecsp.common.SharedConstants;
import org.eclipse.ecsp.common.rowmapper.DeviceInfoFactoryDataMapper;
import org.eclipse.ecsp.common.rowmapper.DeviceInfoFactoryDataMapperWithSubscription;
import org.eclipse.ecsp.common.rowmapper.DeviceStateAggregateDataMapper;
import org.eclipse.ecsp.common.rowmapper.DeviceStateHistoryDataMapper;
import org.eclipse.ecsp.common.util.SqlUtility;
import org.eclipse.ecsp.dto.DeviceInfoAggregateFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryDataWithSubscription;
import org.eclipse.ecsp.dto.DeviceState;
import org.eclipse.ecsp.dto.DeviceStateAggregateData;
import org.eclipse.ecsp.dto.DeviceStateHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.ecsp.common.CommonConstants.DELETE_FROM_DEVICE_INFO_FACTORY_DATA_WHERE;
import static org.eclipse.ecsp.common.CommonConstants.SELECT_FROM_DEVICE_INFO_FACTORY_DATA_WHERE;
import static org.eclipse.ecsp.common.util.SqlUtility.constructQueryWithInClause;
import static org.eclipse.ecsp.common.util.SqlUtility.constructUpdateQuery;
import static org.eclipse.ecsp.dto.DeviceState.DEACTIVATED;
import static org.eclipse.ecsp.dto.DeviceState.PROVISIONED;
import static org.eclipse.ecsp.dto.DeviceState.convertStateCount;
import static org.eclipse.ecsp.dto.DeviceState.valueOf;

/**
 * This class represents a Data Access Object (DAO) for managing device factory data.
 * It provides methods for CRUD operations on the "DeviceInfoFactoryData" table in the database.
 */
@Repository
@Slf4j
public class DeviceInfoFactoryDataDao {

    /**
     * A constant representing the column name "ID" in the database.
     * This is used to reference the "ID" field in SQL queries or database operations.
     */
    public static final String ID = "\"ID\"";

    /**
     * Constant representing the database column or field name for the IMEI (International Mobile Equipment Identity).
     * This is used to uniquely identify a mobile device in the system.
     */
    public static final String IMEI = "imei";

    /**
     * Constant representing the column name for the serial number in the database.
     */
    public static final String SERIAL_NUMBER = "serial_number";
    private static final String UPDATE_DEVICE_INFO_FACTORY_DATA_SQL = "update public.\"DeviceInfoFactoryData\" set ";
    private static final String STATE = "state";
    private static final String VIN_BY_SERIAL_NUMBER =
        "select b.vin from \"DeviceInfoFactoryData\" a, vin_details b where a.\"ID\"=b.reference_id and"
            + " a.serial_number=?";
    private static final String VIN_BY_IMEI =
        "select vin from vin_details v  left join \"DeviceInfoFactoryData\" d on v.reference_id=d.\"ID\" where"
            + " d.imei=?";
    private static final String AND = " and ";
    private static final String WHERE = " WHERE ";
    private static final String WHERE_OPERATOR = "WHERE ";
    private static final String PAGE_FILTER = "LIMIT :limit OFFSET :offset";
    private static final int INDEX_2 = 2;
    private static final int INDEX_3 = 3;
    private static final int INDEX_4 = 4;
    private static final int INDEX_5 = 5;
    private static final int INDEX_6 = 6;
    private static final int INDEX_7 = 7;
    private static final int INDEX_8 = 8;
    private static final int INDEX_9 = 9;
    private static final int INDEX_10 = 10;
    private static final int INDEX_11 = 11;
    private static final int INDEX_12 = 12;
    private static final int INDEX_13 = 13;
    private static final int INDEX_14 = 14;
    private static final int INDEX_15 = 15;
    private static final int INDEX_16 = 16;
    private static final int INDEX_17 = 17;
    private static final int INDEX_18 = 18;
    private static final int INDEX_19 = 19;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParamJdbcTemplate;

    /**
     * Appends the "and" keyword to the given query creator if needed.
     *
     * @param needAnd        a boolean indicating whether the "and" keyword is needed
     * @param queryCreator   the StringBuilder representing the query creator
     * @return               the modified query creator with the "and" keyword appended, or null if not needed
     */
    private static StringBuilder appendAnd(boolean needAnd, StringBuilder queryCreator) {
        if (needAnd) {
            return queryCreator.append("and ");
        } else {
            return null;
        }
    }

    /**
     * Deletes factory data based on the provided IMEI, serial number, and current data.
     *
     * @param imei           the IMEI of the device (optional)
     * @param serialnumber   the serial number of the device (optional)
     * @param currentData    the current device information factory data
     * @throws InvalidParameterException if the factory data cannot be deleted because the device is not in the
     *      PROVISIONED state
     */
    public void deletefactoryData(String imei, String serialnumber, DeviceInfoFactoryData currentData) {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put(STATE, PROVISIONED.getValue());
        if (!StringUtils.isEmpty(imei)) {
            orderedMap.put(IMEI, imei);
        }
        if (!StringUtils.isEmpty(serialnumber)) {
            orderedMap.put(SERIAL_NUMBER, serialnumber);
        }
        String deletesql = SqlUtility.getPreparedSql(DELETE_FROM_DEVICE_INFO_FACTORY_DATA_WHERE, AND, orderedMap);
        Object[] values = SqlUtility.getArrayValues(orderedMap);
        int deleted = jdbcTemplate.update(deletesql, values);
        if (deleted == 0) {
            log.error("factory data can't be deleted as the device is not in  :{} state", PROVISIONED);
            throw new InvalidParameterException(
                String.format("factory data can't be deleted as the device is not in  : %s state", PROVISIONED));
        }

        // update history table
        updateHistoryTable(currentData, DEACTIVATED.getValue());

    }

    /**
     * Changes the state of a device for a given factory ID.
     *
     * @param factoryId the ID of the factory
     * @param state the new state of the device
     * @param action the action performed for the state change
     * @throws InvalidParameterException if the factory data is not found for the given factory ID
     */
    // @Transactional
    public void changeDeviceState(long factoryId, String state, final String action) {
        DeviceState deviceState = valueOf(state);
        String sql;
        int updated;
        log.debug("Changing device state for factory id {} and state {}", factoryId, state);
        switch (deviceState) {
            case STOLEN:
                sql = "update public.\"DeviceInfoFactoryData\" set isstolen=true where \"ID\"=?";
                updated = jdbcTemplate.update(sql, factoryId);
                break;
            case FAULTY:
                sql = "update public.\"DeviceInfoFactoryData\" set isfaulty=true where \"ID\"=?";
                updated = jdbcTemplate.update(sql, factoryId);
                break;
            case ACTIVE:
            default:
                sql =
                    "update public.\"DeviceInfoFactoryData\" set state=?, isstolen=false, isfaulty=false where"
                        + " \"ID\"=?";
                updated = jdbcTemplate.update(sql, state, factoryId);
                break;
        }
        log.debug("Number of records updated for state change  : {}", updated);
        if (updated == 0) {
            log.error("factory data not found for factoryId :{}", factoryId);
            throw new InvalidParameterException(String.format("factory data not found for factoryId : %s", factoryId));
        }
        log.debug("Updating the history table for factory id : {}", factoryId);
        createHistoryTableEntry(factoryId, action);
    }

    /**
     * Changes the state of a device to "Stolen" or "Faulty" for a given factory ID.
     *
     * @param factoryId the ID of the factory
     * @param state the new state of the device ("Stolen" or "Faulty")
     * @param action the action performed on the device
     * @throws InvalidParameterException if the factory data is not found for the given factory ID and state
     */
    public void changeDeviceStateForStolenOrFaulty(long factoryId, String state, final String action) {
        String sql = "update public.\"DeviceInfoFactoryData\" set state=?  where \"ID\"=?";
        int updated = jdbcTemplate.update(sql, state, factoryId);

        log.debug("Number of records updated  : {}", updated);
        if (updated == 0) {
            log.error("Factory data not found for the factoryId: {} with Stolen or Faulty state", factoryId);
            throw new InvalidParameterException(
                String.format("Factory data not found for the factoryId: %s with Stolen or Faulty state", factoryId));
        } else {
            log.debug("Updating the history table for factory id : {}", factoryId);
            createHistoryTableEntry(factoryId, action);
        }
    }

    /**
     * Creates a history table entry for the given device information factory data and action.
     *
     * @param deviceInfoFactoryData The device information factory data.
     * @param action                The action performed.
     */
    public void createHistoryTableEntry(DeviceInfoFactoryData deviceInfoFactoryData, String action) {
        updateHistoryTable(deviceInfoFactoryData, action);
    }

    /**
     * Creates a history table entry for the given factory ID and action.
     *
     * @param factoryId The ID of the factory.
     * @param action The action performed.
     * @throws InvalidParameterException if factory data is not found for the given factory ID.
     */
    private void createHistoryTableEntry(long factoryId, final String action) {
        final DeviceInfoFactoryData deviceInfoFactoryData = findByFactoryId(factoryId);
        if (deviceInfoFactoryData != null) {
            log.debug("Updating the history table");
            updateHistoryTable(deviceInfoFactoryData, action);
            log.debug("Done with history update");
        } else {
            throw new InvalidParameterException(String.format("factory data not found for factoryId : %s", factoryId));
        }
    }

    /**
     * Updates the state of the factory data with the given factory ID.
     *
     * @param factoryId the ID of the factory data to update
     * @param state the new state to set for the factory data
     * @return the number of rows affected by the update operation
     */
    // Updates only the factory data table state
    public int updateFactoryDataState(long factoryId, String state) {

        String sql = "update public.\"DeviceInfoFactoryData\" set state=? where \"ID\"=?";
        return jdbcTemplate.update(sql, state, factoryId);

    }

    /**
     * Updates the history table with the provided device information and action.
     *
     * @param deviceInfoFactoryData The device information to be updated in the history table.
     * @param action                The action performed on the device.
     */
    private void updateHistoryTable(final DeviceInfoFactoryData deviceInfoFactoryData, final String action) {
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String insertStatement =
                    "INSERT INTO public.\"DeviceInfoFactoryDataHistory\"(\"factory_id\", \"manufacturing_date\","
                        + " \"model\",\"imei\",\"serial_number\",\"platform_version\",\"iccid\",\"ssid\",\"bssid\","
                        + "\"msisdn\",\"imsi\",\"record_date\",\"factory_created_date\",\"factory_admin\",\"state\","
                        + "\"action\",\"created_timestamp\",\"package_serial_number\",\"device_type\") "
                        + "    VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement preparedStatement = con.prepareStatement(insertStatement);
                preparedStatement.setLong(1, deviceInfoFactoryData.getId());
                preparedStatement.setTimestamp(INDEX_2, deviceInfoFactoryData.getManufacturingDate());
                preparedStatement.setString(INDEX_3, deviceInfoFactoryData.getModel());
                preparedStatement.setString(INDEX_4, deviceInfoFactoryData.getImei());
                preparedStatement.setString(INDEX_5, deviceInfoFactoryData.getSerialNumber());
                preparedStatement.setString(INDEX_6, deviceInfoFactoryData.getPlatformVersion());
                preparedStatement.setString(INDEX_7, deviceInfoFactoryData.getIccid());
                preparedStatement.setString(INDEX_8, deviceInfoFactoryData.getSsid());
                preparedStatement.setString(INDEX_9, deviceInfoFactoryData.getBssid());
                preparedStatement.setString(INDEX_10, deviceInfoFactoryData.getMsisdn());
                preparedStatement.setString(INDEX_11, deviceInfoFactoryData.getImsi());
                preparedStatement.setTimestamp(INDEX_12, deviceInfoFactoryData.getRecordDate());
                preparedStatement.setTimestamp(INDEX_13, deviceInfoFactoryData.getCreatedDate());
                preparedStatement.setString(INDEX_14, deviceInfoFactoryData.getFactoryAdmin());
                preparedStatement.setString(INDEX_15, deviceInfoFactoryData.getState());
                preparedStatement.setString(INDEX_16, action);
                preparedStatement.setTimestamp(INDEX_17, new Timestamp(Calendar.getInstance().getTimeInMillis()));
                preparedStatement.setString(INDEX_18, deviceInfoFactoryData.getPackageSerialNumber());
                preparedStatement.setString(INDEX_19, deviceInfoFactoryData.getDeviceType());
                log.info("History Prepared Statement : " + preparedStatement);
                return preparedStatement;
            }
        });
    }

    /**
     * Converts a string date to a Timestamp object.
     *
     * @param date the string representation of the date in the format "yyyy-MM-dd hh:mm:ss.SSS"
     * @return the Timestamp object representing the converted date, or null if the conversion fails
     */
    public Timestamp getTimestamp(String date) {
        Timestamp timestamp = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(date);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
        } catch (Exception e) {
            log.error("Error while convert string date to Timestamp : {}", e.getMessage());
        }
        return timestamp;
    }

    /**
     * Retrieves the DeviceInfoFactoryData object associated with the given factoryId.
     *
     * @param factoryId the ID of the factory
     * @return the DeviceInfoFactoryData object, or null if not found
     */
    public DeviceInfoFactoryData findByFactoryId(long factoryId) {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put(ID, factoryId);

        String preparedSql = SqlUtility.getPreparedSql(SELECT_FROM_DEVICE_INFO_FACTORY_DATA_WHERE, AND, orderedMap);
        Object[] arrayValues = SqlUtility.getArrayValues(orderedMap);
        if (preparedSql == null || arrayValues == null) {
            return null;
        }
        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = jdbcTemplate
            .query(preparedSql, arrayValues, new DeviceInfoFactoryDataMapper());

        if (!deviceInfoFactoryDataList.isEmpty()) {
            return deviceInfoFactoryDataList.get(0);
        }
        log.error("Factory data not found for factoryId: {}", factoryId);
        return null;
    }

    /**
     * Finds the DeviceInfoFactoryData object by factory IMEI.
     *
     * @param imei The IMEI of the device to search for.
     * @return The DeviceInfoFactoryData object if found, or null if not found.
     */
    public DeviceInfoFactoryData findByFactoryImei(String imei) {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put(IMEI, imei);

        String sql = SqlUtility.getPreparedSql(SELECT_FROM_DEVICE_INFO_FACTORY_DATA_WHERE, AND, orderedMap);

        Object[] values = SqlUtility.getArrayValues(orderedMap);
        if (sql != null) {
            List<DeviceInfoFactoryData> deviceInfoFactoryDataList = jdbcTemplate.query(sql, values,
                new DeviceInfoFactoryDataMapper());

            if (!deviceInfoFactoryDataList.isEmpty()) {
                return deviceInfoFactoryDataList.get(0);
            }
        } else {
            log.error("Factory data not found for Imei : {}", imei);
        }
        return null;
    }

    /**
     * Finds the ID of a device factory data entry by its IMEI.
     *
     * @param imei the IMEI of the device
     * @return the ID of the device factory data entry, or null if not found
     */
    public Long findIdByFactoryImei(String imei) {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put(IMEI, imei);
        String sql =
            SqlUtility.getPreparedSql(CommonConstants.SELECT_ID_FROM_DEVICE_INFO_FACTORY_DATA_WHERE, AND, orderedMap);
        Object[] values = SqlUtility.getArrayValues(orderedMap);
        Long factoryId = null;
        if (sql != null) {
            factoryId = jdbcTemplate.queryForObject(sql, values,
                Long.class);

        }
        return factoryId;
    }

    /**
     * Finds and returns the DeviceInfoFactoryData object based on the given factory ID and IMEI.
     *
     * @param factoryId the ID of the factory
     * @param imei the IMEI of the device
     * @return the DeviceInfoFactoryData object if found, or null if not found
     */
    public DeviceInfoFactoryData findByFactoryIdAndImei(long factoryId, String imei) {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put(IMEI, imei);
        orderedMap.put(ID, factoryId);

        String sql = SqlUtility.getPreparedSql(SELECT_FROM_DEVICE_INFO_FACTORY_DATA_WHERE, AND, orderedMap);

        Object[] values = SqlUtility.getArrayValues(orderedMap);
        if (sql != null) {
            List<DeviceInfoFactoryData> deviceInfoFactoryDataList = jdbcTemplate.query(sql, values,
                new DeviceInfoFactoryDataMapper());

            if (!deviceInfoFactoryDataList.isEmpty()) {
                return deviceInfoFactoryDataList.get(0);
            }
        } else {
            log.error("Factory data not found for Imei : {} and factory Id :{}", imei, factoryId);
        }
        return null;
    }

    /**
     * Finds the factory data for a device based on its serial number.
     *
     * @param serialNumber The serial number of the device.
     * @return The DeviceInfoFactoryData object representing the factory data of the device, or null if not found.
     */
    public DeviceInfoFactoryData findFactoryDataBySerialNumber(String serialNumber) {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put(SERIAL_NUMBER, serialNumber);

        String sql = SqlUtility.getPreparedSql(SELECT_FROM_DEVICE_INFO_FACTORY_DATA_WHERE, AND, orderedMap);

        Object[] values = SqlUtility.getArrayValues(orderedMap);
        if (sql != null) {
            List<DeviceInfoFactoryData> deviceInfoFactoryDataList = jdbcTemplate.query(sql, values,
                new DeviceInfoFactoryDataMapper());

            if (!deviceInfoFactoryDataList.isEmpty()) {
                return deviceInfoFactoryDataList.get(0);
            }
        } else {
            log.error("Factory data not found for serialNumber : {}", serialNumber);
        }
        return null;
    }

    /**
     * Constructs and fetches factory data based on the provided DeviceInfoFactoryData object.
     * The method constructs a SQL query based on the non-empty fields of the factoryData object,
     * and executes the query using named parameters to retrieve the matching factory data records.
     *
     * @param factoryData The DeviceInfoFactoryData object containing the search criteria.
     * @return A list of DeviceInfoFactoryData objects that match the search criteria.
     */
    public List<DeviceInfoFactoryData> constructAndFetchFactoryData(DeviceInfoFactoryData factoryData) {
        MapSqlParameterSource mapSqlParameter = new MapSqlParameterSource();
        StringBuilder queryCreator = new StringBuilder(CommonConstants.SELECT_FROM_DEVICE_INFO_FACTORY_DATA);
        queryCreator.append(WHERE_OPERATOR);
        boolean isAdded = false;
        if (!StringUtils.isEmpty(factoryData.getImei())) {
            queryCreator.append("imei = :imei ");
            mapSqlParameter.addValue("imei", factoryData.getImei());
            isAdded = true;
        }
        if (!StringUtils.isEmpty(factoryData.getImsi())) {
            appendAnd(isAdded, queryCreator);
            queryCreator.append("imsi = :imsi ");
            mapSqlParameter.addValue("imsi", factoryData.getImsi());
            isAdded = true;
        }
        if (!StringUtils.isEmpty(factoryData.getMsisdn())) {
            appendAnd(isAdded, queryCreator);
            queryCreator.append("msisdn = :msisdn ");
            mapSqlParameter.addValue("msisdn", factoryData.getMsisdn());
            isAdded = true;
        }
        if (!StringUtils.isEmpty(factoryData.getSsid())) {
            appendAnd(isAdded, queryCreator);
            queryCreator.append("ssid = :ssid ");
            mapSqlParameter.addValue("ssid", factoryData.getSsid());
            isAdded = true;
        }
        if (!StringUtils.isEmpty(factoryData.getIccid())) {
            appendAnd(isAdded, queryCreator);
            queryCreator.append("iccid = :iccid ");
            mapSqlParameter.addValue("iccid", factoryData.getIccid());
            isAdded = true;
        }
        if (!StringUtils.isEmpty(factoryData.getBssid())) {
            appendAnd(isAdded, queryCreator);
            queryCreator.append("bssid = :bssid ");
            mapSqlParameter.addValue("bssid", factoryData.getBssid());
            isAdded = true;
        }
        if (!StringUtils.isEmpty(factoryData.getSerialNumber())) {
            appendAnd(isAdded, queryCreator);
            queryCreator.append("serial_number = :serialNumber ");
            mapSqlParameter.addValue("serialNumber", factoryData.getSerialNumber());
        }
        log.info("Constructed query to fire : {} ", queryCreator);
        log.info("DeviceData from constructFetchData : {} ", factoryData);
        return namedParamJdbcTemplate.query(queryCreator.toString(), mapSqlParameter,
            new DeviceInfoFactoryDataMapper());

    }

    /**
     * Fetches the device information factory data based on the provided ordered map.
     *
     * @param orderedMap The ordered map containing attribute-value pairs for the query.
     * @return The DeviceInfoFactoryData object representing the fetched device information factory data,
     *         or null if no matching data is found.
     */
    public DeviceInfoFactoryData fetchDeviceInfoFactoryData(Map<String, Object> orderedMap) {
        String sql = SqlUtility.getPreparedSql(SELECT_FROM_DEVICE_INFO_FACTORY_DATA_WHERE, AND, orderedMap);

        Object[] values = SqlUtility.getArrayValues(orderedMap);
        log.info("PreparedSql : {}", sql);
        if (values != null) {
            log.info("Values Size: {}", values.length);
        }
        if (sql != null) {
            List<DeviceInfoFactoryData> deviceInfoFactoryDataList = jdbcTemplate.query(sql, values,
                new DeviceInfoFactoryDataMapper());

            if (!CollectionUtils.isEmpty(deviceInfoFactoryDataList)) {
                log.info("device is found with above condition: {}", deviceInfoFactoryDataList.get(0));
                return deviceInfoFactoryDataList.get(0);
            }
        } else {
            log.error("Factory data not found for attribute:value : {}", orderedMap);
        }
        return null;
    }

    /**
     * Updates the device information in the database based on the provided conditional and ordered maps.
     *
     * @param conditionalOrderedMap A map containing the conditional parameters for the update query.
     * @param orderedMap            A map containing the ordered parameters for the update query.
     * @return The number of rows affected by the update operation.
     */
    public int update(Map<String, Object> conditionalOrderedMap, Map<String, Object> orderedMap) {

        String sql = UPDATE_DEVICE_INFO_FACTORY_DATA_SQL;
        String operator = " , ";
        sql = SqlUtility.getPreparedSql(sql, operator, orderedMap);
        sql = sql + " where ";
        operator = AND;
        sql = SqlUtility.getPreparedSql(sql, operator, conditionalOrderedMap);
        log.info("update->FinalPreparedSql: {} ", sql);
        for (Map.Entry<String, Object> entry : conditionalOrderedMap.entrySet()) {
            orderedMap.put(entry.getKey(), entry.getValue());
        }

        Object[] values = SqlUtility.getArrayValues(orderedMap);
        if (values != null) {
            log.info("update->FinalValuesSize: {}", values.length);
        }
        int row = jdbcTemplate.update(sql, values);
        log.info("rownumber updated: {}", row);
        return row;
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
     * @return              A list of DeviceInfoFactoryData objects that match the specified parameters.
     */
    public List<DeviceInfoFactoryData> constructFetchFactoryData(int size, int page, String asc, String desc,
                                                                 String serialNumber, String imei) {
        log.info("Inside constructFetchFactoryData method");

        StringBuilder queryBuilder = new StringBuilder(CommonConstants.SELECT_FROM_DEVICE_INFO_FACTORY_DATA);

        queryBuilder.append(constructImeiSerialNumberFilter(serialNumber, imei));
        queryBuilder.append(constructOrderByFilter(asc, desc));
        queryBuilder.append(constructPageFilter());

        MapSqlParameterSource parameters = constructParameterSource(size, page);
        DeviceInfoFactoryDataMapper dataMapper = DeviceInfoFactoryDataMapper.getDeviceInfoFactoryDataMapper();

        log.info("Query generated: {}", queryBuilder);

        List<DeviceInfoFactoryData> deviceInfo =
            namedParamJdbcTemplate.query(queryBuilder.toString(), parameters, dataMapper);

        log.info("Exit constructAndFetchFactoryData method");

        return deviceInfo;
    }

    /**
     * Constructs and fetches factory data based on the provided request parameters, page, and size.
     *
     * @param requestParams   the map of request parameters
     * @param page            the page number
     * @param size            the number of items per page
     * @return a list of DeviceInfoFactoryData objects
     */
    public List<DeviceInfoFactoryData> constructFetchFactoryData(Map<String, String> requestParams, int page,
                                                                 int size) {
        log.debug("Inside constructFetchFactoryData method");

        StringBuilder queryBuilder = new StringBuilder(CommonConstants.SELECT_FROM_DEVICE_INFO_FACTORY_DATA);
        List<String> containsLikeFieldList =
            SqlUtility.getList(getRequestParamsFromMap(requestParams, "containslikefields"), SharedConstants.COMMA);
        List<String> containsLikeValueList =
            SqlUtility.getList(getRequestParamsFromMap(requestParams, "containslikevalues"), SharedConstants.COMMA);
        List<String> rangeFieldList =
            SqlUtility.getList(getRequestParamsFromMap(requestParams, "rangefields"), SharedConstants.COMMA);
        List<String> rangeValueList =
            SqlUtility.getList(getRequestParamsFromMap(requestParams, "rangevalues"), SharedConstants.COMMA);
        String likeQuery = SqlUtility.prepareLikeQuery(containsLikeFieldList, containsLikeValueList);
        String rangeQuery = SqlUtility.prepareRangeQuery(rangeFieldList, rangeValueList);

        if (!likeQuery.isEmpty() && !rangeQuery.isEmpty()) {
            queryBuilder.append(WHERE + likeQuery + AND + rangeQuery);
        } else if ((likeQuery.isEmpty() && !rangeQuery.isEmpty()) || (!likeQuery.isEmpty() && rangeQuery.isEmpty())) {
            queryBuilder.append(WHERE + rangeQuery + likeQuery);
        }

        String sortOrder = getRequestParamsFromMap(requestParams, "sortingorder");
        String sortBy = getRequestParamsFromMap(requestParams, "sortbyparam");
        queryBuilder.append(SqlUtility.prepareSortByAndOrderByQuery(sortBy, sortOrder));
        queryBuilder.append(constructPageFilter());

        MapSqlParameterSource parameters = constructParameterSource(size, page);
        DeviceInfoFactoryDataMapper dataMapper = DeviceInfoFactoryDataMapper.getDeviceInfoFactoryDataMapper();

        log.debug("V3 Query generated: {}", queryBuilder.toString());

        List<DeviceInfoFactoryData> deviceInfo =
            namedParamJdbcTemplate.query(queryBuilder.toString(), parameters, dataMapper);

        log.debug("Exiting V3 constructAndFetchFactoryData method");

        return deviceInfo;
    }

    /**
     * Constructs and fetches factory data based on the provided parameters.
     *
     * @param type              The type of device details input (IMEI, SERIAL_NUMBER, DEVICE_ID, VIN, STATE).
     * @param searchKey         The search key used for filtering the data.
     * @param sizeValue         The number of results to fetch per page.
     * @param pageValue         The page number of the results to fetch.
     * @param sortby            The field to sort the results by.
     * @param orderBy           The order in which to sort the results (ASC or DESC).
     * @param deviceVinEnabled  Indicates whether device VIN details should be included in the query.
     * @return                  A list of DeviceInfoFactoryDataWithSubscription objects representing the fetched
     *      factory data.
     */
    public List<DeviceInfoFactoryDataWithSubscription> constructFetchFactoryData(DeviceDetailsInputTypeEnum type,
                                                                                 String searchKey,
                                                                                 int sizeValue, int pageValue,
                                                                                 String sortby, String orderBy,
                                                                                 boolean deviceVinEnabled) {

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

        if (deviceVinEnabled) {
            queryCreator.append(CommonConstants.VIN_DETAILS_JOIN_CONDITION);
        }

        if (!filterQuery.isEmpty()) {
            queryCreator.append(WHERE + filterQuery);
        }

        if (type.equals(DeviceDetailsInputTypeEnum.DEVICE_ID) || type.equals(DeviceDetailsInputTypeEnum.VIN)) {
            queryCreator.append(SqlUtility.prepareSortByAndOrderByQuery(sortby, orderBy, "DeviceInfoFactoryData"));
        } else {
            queryCreator.append(SqlUtility.prepareSortByAndOrderByQuery(sortby, orderBy));
        }
        queryCreator.append(constructPageFilter());

        MapSqlParameterSource parameters = constructParameterSource(sizeValue, pageValue);
        DeviceInfoFactoryDataMapperWithSubscription dataMapper = DeviceInfoFactoryDataMapperWithSubscription
            .getDeviceInfoFactoryDataWithSubscriptionMapper();

        return namedParamJdbcTemplate.query(queryCreator.toString(), parameters, dataMapper);
    }

    /**
     * Constructs and fetches the aggregate device state count based on the provided parameters.
     *
     * @param containsLikeFieldList  The list of fields to perform "contains-like" search on.
     * @param containsLikeValueList  The list of values to search for in the "contains-like" search.
     * @param rangeFieldList         The list of fields to perform range search on.
     * @param rangeValueList         The list of values to search for in the range search.
     * @return The state count of the aggregate device information.
     */
    public DeviceInfoAggregateFactoryData.StateCount constructFetchAgrigateDeviceState(
        List<String> containsLikeFieldList,
        List<String> containsLikeValueList,
        List<String> rangeFieldList, List<String> rangeValueList) {
        log.debug("Inside constructFetchAgrigateFactoryData method");

        StringBuilder queryCreator = new StringBuilder(
            "select state, count(state) as count from public.\"DeviceInfoFactoryData\" ");
        String likeQuery = SqlUtility.prepareLikeQuery(containsLikeFieldList, containsLikeValueList);
        String rangeQuery = SqlUtility.prepareRangeQuery(rangeFieldList, rangeValueList);

        if (!likeQuery.isEmpty() && !rangeQuery.isEmpty()) {
            queryCreator.append(WHERE + likeQuery + AND + rangeQuery);
        } else if ((likeQuery.isEmpty() && !rangeQuery.isEmpty()) || (!likeQuery.isEmpty() && rangeQuery.isEmpty())) {
            queryCreator.append(WHERE + likeQuery + rangeQuery);
        }

        queryCreator.append("group BY state ");
        log.debug("V3constructFetchAgrigateDeviceState->query:{}", queryCreator.toString());
        List<DeviceStateAggregateData> deviceStateMap = namedParamJdbcTemplate.query(queryCreator.toString(),
            Collections.emptyMap(), new DeviceStateAggregateDataMapper());
        DeviceInfoAggregateFactoryData.StateCount stateCount = new DeviceInfoAggregateFactoryData.StateCount();
        convertStateCount(stateCount, deviceStateMap);
        log.debug("Exiting V3 constructFetchAgrigateDeviceState");
        return stateCount;
    }

    /**
     * Constructs and fetches the aggregate device state count based on the provided serial number and IMEI.
     *
     * @param serialNumber The serial number of the device.
     * @param imei The IMEI (International Mobile Equipment Identity) of the device.
     * @return The aggregate device state count.
     */
    public DeviceInfoAggregateFactoryData.StateCount constructFetchAgrigateDeviceState(String serialNumber,
                                                                                       String imei) {
        log.info("Inside constructFetchAgrigateFactoryData method");

        StringBuilder queryCreator = new StringBuilder(
            "select state, count(state) as count from public.\"DeviceInfoFactoryData\" ");
        queryCreator.append(constructImeiSerialNumberFilter(serialNumber, imei));
        queryCreator.append("group BY state ");
        List<DeviceStateAggregateData> deviceStateMap = namedParamJdbcTemplate.query(queryCreator.toString(),
            Collections.emptyMap(), new DeviceStateAggregateDataMapper());
        DeviceInfoAggregateFactoryData.StateCount stateCount = new DeviceInfoAggregateFactoryData.StateCount();
        convertStateCount(stateCount, deviceStateMap);
        return stateCount;
    }

    /**
     * Constructs and executes a query to fetch the total count of factory data for a device
     * based on the provided serial number and IMEI.
     *
     * @param serialNumber The serial number of the device.
     * @param imei The IMEI (International Mobile Equipment Identity) of the device.
     * @return The total count of factory data for the device.
     */
    public Long constructFetchTotalFactoryData(String serialNumber, String imei) {
        log.info("Inside constructFetchTotalFactoryData method");
        StringBuilder queryCreator = new StringBuilder("select count(*) from public.\"DeviceInfoFactoryData\" ");
        queryCreator.append(constructImeiSerialNumberFilter(serialNumber, imei));
        log.info("Query generated :: {}", queryCreator);
        Long deviceCount = namedParamJdbcTemplate.queryForObject(queryCreator.toString(),
            new MapSqlParameterSource(), Long.class);
        log.info("Exit constructFetchTotalFactoryData method");
        return deviceCount;
    }

    /**
     * Constructs and executes a query to fetch the total count of factory data for devices based on the provided
     * criteria.
     *
     * @param containsLikeFieldList  a list of fields to be used in the 'LIKE' clause of the query
     * @param containsLikeValueList  a list of values to be used in the 'LIKE' clause of the query
     * @param rangeFieldList         a list of fields to be used in the range condition of the query
     * @param rangeValueList         a list of values to be used in the range condition of the query
     * @return the total count of factory data for devices that match the given criteria
     */
    public Long constructFetchTotalFactoryData(List<String> containsLikeFieldList, List<String> containsLikeValueList,
                                               List<String> rangeFieldList, List<String> rangeValueList) {
        log.debug("Inside constructFetchTotalFactoryData method");

        StringBuilder queryCreator = new StringBuilder("select count(*) from public.\"DeviceInfoFactoryData\" ");
        String likeQuery = SqlUtility.prepareLikeQuery(containsLikeFieldList, containsLikeValueList);
        String rangeQuery = SqlUtility.prepareRangeQuery(rangeFieldList, rangeValueList);
        if (!likeQuery.isEmpty() && !rangeQuery.isEmpty()) {
            queryCreator.append(WHERE + likeQuery + AND + rangeQuery);
        } else if ((likeQuery.isEmpty() && !rangeQuery.isEmpty()) || (!likeQuery.isEmpty() && rangeQuery.isEmpty())) {
            queryCreator.append(WHERE + rangeQuery + likeQuery);
        }

        log.debug("constructFetchTotalFactoryDataV3 -> finalquery :: {}", queryCreator.toString());
        Long deviceCount = namedParamJdbcTemplate.queryForObject(queryCreator.toString(),
            new MapSqlParameterSource(), Long.class);
        log.debug("Exit constructFetchTotalFactoryDataV3 method");
        return deviceCount;
    }

    /**
     * Constructs a filter query based on the provided serial number and IMEI.
     *
     * @param serialNumber The serial number to filter by. Can be null or empty.
     * @param imei The IMEI to filter by. Can be null or empty.
     * @return A string representing the constructed filter query.
     */
    private String constructImeiSerialNumberFilter(String serialNumber, String imei) {
        StringBuilder stringQueryFilter = new StringBuilder();
        if (!StringUtils.isEmpty(imei)) {
            stringQueryFilter = new StringBuilder(WHERE_OPERATOR);
            stringQueryFilter.append("imei LIKE '%");
            stringQueryFilter.append(imei.replace("'", "''").replace("_", "__"));
            stringQueryFilter.append("%' ");
        }
        if (!StringUtils.isEmpty(serialNumber)) {
            if (!StringUtils.isEmpty(imei)) {
                stringQueryFilter.append("and ");
            } else {
                stringQueryFilter = new StringBuilder(WHERE_OPERATOR);
            }
            stringQueryFilter.append("serial_number LIKE '%");
            stringQueryFilter.append(serialNumber.replace("'", "''").replace("_", "__"));
            stringQueryFilter.append("%' ");
        }
        return stringQueryFilter.toString();
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
     * @param page The page number of the items to retrieve.
     * @return A MapSqlParameterSource object containing the constructed parameters.
     */
    private MapSqlParameterSource constructParameterSource(int size, int page) {
        page = page - 1;
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        parameters.addValue("limit", size);
        parameters.addValue("offset", page * size);

        return parameters;
    }

    /**
     * Constructs a filter query for searching device information by IMEI.
     *
     * @param imei The IMEI to filter by.
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
     * @return A string representing the filter condition based on the serial number.
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
     * Constructs a filter string for querying device information based on the provided device ID.
     *
     * @param deviceId The device ID to filter by.
     * @return The constructed filter string.
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
     * Constructs a filter query for searching VINs.
     *
     * @param vin The VIN to search for.
     * @return The constructed filter query.
     */
    private String constructVinFilter(String vin) {
        StringBuilder stringQueryFilter = new StringBuilder();
        if (!StringUtils.isEmpty(vin)) {
            stringQueryFilter = new StringBuilder(" vin LIKE '%");
            stringQueryFilter.append(vin.replace("'", "''").replace("_", "__"));
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
     * Finds the total device state for the given IMEI.
     *
     * @param imei the IMEI of the device
     * @return the total device state count
     */
    public long findTotalDeviceState(String imei) {
        log.debug("Inside findTotalDeviceState method");
        String query = "select count(*) from public.\"DeviceInfoFactoryDataHistory\" where \"imei\"='" + imei + "';";
        log.debug("findTotalDeviceState -> finalquery :: {}", query);
        Long deviceCount = namedParamJdbcTemplate.queryForObject(query, new MapSqlParameterSource(), Long.class);
        log.debug("Exit findTotalDeviceState method");
        return deviceCount != null ? deviceCount : 0;
    }

    /**
     * Constructs and fetches the device states based on the provided parameters.
     *
     * @param size         The number of device states to fetch.
     * @param page         The page number of the device states to fetch.
     * @param sortingOrder The sorting order of the device states.
     * @param sortBy       The field to sort the device states by.
     * @param imei         The IMEI number of the device.
     * @return A list of device state history objects.
     */
    public List<DeviceStateHistory> constructAndFetchDeviceStates(int size, int page, String sortingOrder,
                                                                  String sortBy, String imei) {

        log.debug("Inside constructAndFetchTimelineDetails method");
        String query = "select * from public.\"DeviceInfoFactoryDataHistory\" where \"imei\"='" + imei + "'";
        StringBuilder queryBuilder = new StringBuilder(query);
        queryBuilder.append(SqlUtility.prepareSortByAndOrderByQuery(sortBy, sortingOrder));
        queryBuilder.append(constructPageFilter());
        MapSqlParameterSource parameters = constructParameterSource(size, page);
        log.debug("SQL generated {}", queryBuilder.toString());
        DeviceStateHistoryDataMapper dataMapper = DeviceStateHistoryDataMapper.getDeviceSatetHistoryDataMapper();
        List<DeviceStateHistory> deviceHistory =
            namedParamJdbcTemplate.query(queryBuilder.toString(), parameters, dataMapper);
        log.debug("Exit constructAndFetchDeviceStates method");
        return deviceHistory;
    }

    /**
     * Finds the VIN (Vehicle Identification Number) either by IMEI (International Mobile Equipment Identity) or
     * serial number.
     *
     * @param imei          The IMEI of the device.
     * @param serialNumber  The serial number of the device.
     * @return The VIN associated with the given IMEI or serial number, or null if no VIN is found.
     * @throws IllegalArgumentException if both imei and serial number are empty or null.
     */
    public String findVinEitherByImeiOrSerialNumber(String imei, String serialNumber) {
        String vin = null;
        try {
            log.debug("## findVinEitherByImeiOrSerialNumber - START imei: {}, serialNumber: {}", imei, serialNumber);
            if (StringUtils.isEmpty(serialNumber) && StringUtils.isEmpty(imei)) {
                throw new IllegalArgumentException("Either imei or serial number is mandatory");
            }
            List<String> strLst;
            if (StringUtils.isNotEmpty(serialNumber)) {
                log.debug("# VIN_BY_SERIAL_NUMBER: {}", VIN_BY_SERIAL_NUMBER);
                strLst = jdbcTemplate.query(VIN_BY_SERIAL_NUMBER, new Object[]{serialNumber}, new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getString(1);
                    }
                });
            } else if (StringUtils.isNotEmpty(imei)) {
                log.debug("# VIN_BY_IMEI: {}", VIN_BY_IMEI);
                strLst = jdbcTemplate.query(VIN_BY_IMEI, new Object[]{imei}, new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getString(1);
                    }
                });
            } else {
                throw new IllegalArgumentException("Either imei or serial number is mandatory");
            }
            log.debug("##deviceFactory list size: {}", strLst.size());
            vin = strLst.size() == 1 ? strLst.get(0) : null;
        } catch (DataAccessException e) {
            log.error(
                "## Exception occurred while trying to find the vin, could be that no record found for given input: ",
                e);
        }
        log.debug("## findVinEitherByImeiOrSerialNumber - END vin: {}", vin);
        return vin;
    }

    /**
     * Filters the device information based on the provided attribute map.
     *
     * @param attributeMap the map containing the attributes to filter the device information
     * @return a list of DeviceInfoFactoryData objects that match the filter criteria
     */
    public List<DeviceInfoFactoryData> filterDevice(Map<String, Object> attributeMap) {
        log.info("## filterDevice DAO - START");
        String sql = constructQueryWithInClause(SELECT_FROM_DEVICE_INFO_FACTORY_DATA_WHERE, attributeMap);
        log.info("## SQL: {}", sql);
        return jdbcTemplate.query(sql, new DeviceInfoFactoryDataMapper());
    }

    /**
     * Updates the device information in the database based on the provided attribute and condition maps.
     *
     * @param attributeMap  a map containing the attributes to be updated
     * @param conditionMap  a map containing the conditions to be applied for the update
     * @return the number of rows affected by the update operation
     */
    public int updateDevice(Map<String, Object> attributeMap, Map<String, Object> conditionMap) {
        log.info("## updateDevice DAO - START");
        String sql = constructUpdateQuery(UPDATE_DEVICE_INFO_FACTORY_DATA_SQL, attributeMap, conditionMap);
        log.info("## SQL: {}", sql);
        return jdbcTemplate.update(sql);
    }

    /**
     * Retrieves the value associated with the specified key from the given request parameters map.
     *
     * @param requestParams The map containing the request parameters.
     * @param key The key to retrieve the value for.
     * @return The value associated with the specified key, or null if the key is not found or the map is empty.
     */
    private String getRequestParamsFromMap(Map<String, String> requestParams, String key) {
        if (requestParams != null && !requestParams.isEmpty() && requestParams.containsKey(key)) {
            return requestParams.get(key);
        }
        return null;
    }

    /**
     * Enum representing the types of input that can be used to identify device details.
     */
    public enum DeviceDetailsInputTypeEnum {

        /**
         * International Mobile Equipment Identity (IMEI) number, a unique identifier for mobile devices.
         */
        IMEI, 

        /**
         * Serial number, a unique identifier assigned to a device by the manufacturer.
         */
        SERIAL_NUMBER, 

        /**
         * Device ID, a unique identifier specific to the device.
         */
        DEVICE_ID, 

        /**
         * Vehicle Identification Number (VIN), a unique code used to identify individual motor vehicles.
         */
        VIN, 

        /**
         * State, representing the current state or status of the device.
         */
        STATE;
    }
}
