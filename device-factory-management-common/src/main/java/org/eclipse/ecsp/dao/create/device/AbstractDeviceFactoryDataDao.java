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

package org.eclipse.ecsp.dao.create.device;

import org.eclipse.ecsp.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.DeviceState;
import org.eclipse.ecsp.dto.create.device.DeviceFactoryDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * This abstract class represents a data access object for device factory data.
 * It provides common functionality and constants used by concrete implementations.
 */
public abstract class AbstractDeviceFactoryDataDao implements IdeviceFactoryDataDao {
    /**
     * total 17 column out of which we insert 15 columns (we dont insert 2 column isstolen, isfaulty).
     */
    public static final String DEVICE_INFO_FACTORY_DATA_INSERT_SQL =
        "insert into public.\"DeviceInfoFactoryData\" (\"manufacturing_date\",\"model\",\"imei\",\"serial_number\","
            + "\"platform_version\",\"iccid\",\"ssid\",\"bssid\",\"msisdn\",\"imsi\",\"record_date\",\"factory_admin\","
            + "\"created_date\",\"state\",\"package_serial_number\",\"device_type\",\"region\")"
            + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    public static final String IMEI = "imei";
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractDeviceFactoryDataDao.class);
    public final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
    public static final int INDEX_2 = 2;
    public static final int INDEX_3 = 3;
    public static final int INDEX_4 = 4;
    public static final int INDEX_5 = 5;
    public static final int INDEX_6 = 6;
    public static final int INDEX_7 = 7;
    public static final int INDEX_8 = 8;
    public static final int INDEX_9 = 9;
    public static final int INDEX_10 = 10;
    public static final int INDEX_11 = 11;
    public static final int INDEX_12 = 12;
    public static final int INDEX_13 = 13;
    public static final int INDEX_14 = 14;
    public static final int INDEX_15 = 15;
    public static final int INDEX_16 = 16;
    public static final int INDEX_17 = 17;
    public static final int INDEX_18 = 18;
    public static final int INDEX_19 = 19;

    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    private DeviceInfoFactoryDataDao factoryDao;

    /**
     * Creates a device using the provided factory data and user ID.
     *
     * @param factoryData The factory data for creating the device.
     * @param userId The ID of the user performing the operation.
     * @return The KeyHolder object containing the generated keys.
     */
    protected KeyHolder createDeviceUsingFactoryData(DeviceFactoryDataDto factoryData, String userId) {
        LOGGER.debug("## createDeviceUsingFactoryData - DAO Start factoryData: {}", factoryData);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps =
                    con.prepareStatement(DEVICE_INFO_FACTORY_DATA_INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
                updatePreparedStatement(userId, ps, factoryData);
                return ps;
            }
        }, keyHolder);
        LOGGER.debug("Successfully inserted data into DeviceInfoFactory table");
        Map<String, Object> keys = keyHolder.getKeys();
        long factoryDataId = keys != null ? (long) keys.get("ID") : (long) 0;
        //Insert Into History Table as well
        DeviceInfoFactoryData factoryDataFromTable = factoryDao.findByFactoryId(factoryDataId);
        updateHistoryTable(factoryDataFromTable, DeviceState.PROVISIONED.toString());
        return keyHolder;
    }

    /**
     * Updates the prepared statement with the given device factory data.
     *
     * @param userId      the user ID associated with the update
     * @param ps          the prepared statement to update
     * @param factoryData the device factory data to update the prepared statement with
     * @throws SQLException if there is an error updating the prepared statement
     */
    protected void updatePreparedStatement(final String userId, PreparedStatement ps,
                                           DeviceFactoryDataDto factoryData) throws SQLException {
        final Timestamp createdTimestamp = new Timestamp(new Date().getTime());
        Timestamp manufacturedTime;
        Timestamp recordDateTime;
        try {
            manufacturedTime = new Timestamp(dateFormatter.parse(factoryData.getManufacturingDate()).getTime());
            recordDateTime = new Timestamp(dateFormatter.parse(factoryData.getRecordDate()).getTime());
        } catch (ParseException e) {
            throw new SQLException("Invalid date format passed. Valid format is yyyy/MM/dd");
        }
        ps.setTimestamp(1, manufacturedTime);
        ps.setString(INDEX_2, factoryData.getModel());
        String imei = factoryData.getImei();
        ps.setString(INDEX_3, imei);
        ps.setString(INDEX_4, factoryData.getSerialNumber());
        ps.setString(INDEX_5, factoryData.getPlatformVersion());
        ps.setString(INDEX_6, factoryData.getIccid());
        ps.setString(INDEX_7, factoryData.getSsid());
        ps.setString(INDEX_8, factoryData.getBssid());
        ps.setString(INDEX_9, factoryData.getMsisdn());
        ps.setString(INDEX_10, factoryData.getImsi());
        ps.setTimestamp(INDEX_11, recordDateTime);
        ps.setString(INDEX_12, userId);
        ps.setTimestamp(INDEX_13, createdTimestamp);
        ps.setString(INDEX_14, DeviceState.PROVISIONED.toString());
        ps.setString(INDEX_15, factoryData.getPackageSerialNumber());
        ps.setString(INDEX_16, factoryData.getDeviceType());
        ps.setString(INDEX_17, factoryData.getRegion());
    }

    /**
     * Updates the history table with the given factory data and action.
     *
     * @param factoryData The DeviceInfoFactoryData object containing the factory data.
     * @param action      The action performed on the factory data.
     */
    protected void updateHistoryTable(final DeviceInfoFactoryData factoryData, final String action) {
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
                preparedStatement.setLong(1, factoryData.getId());
                preparedStatement.setTimestamp(INDEX_2, factoryData.getManufacturingDate());
                preparedStatement.setString(INDEX_3, factoryData.getModel());
                preparedStatement.setString(INDEX_4, factoryData.getImei());
                preparedStatement.setString(INDEX_5, factoryData.getSerialNumber());
                preparedStatement.setString(INDEX_6, factoryData.getPlatformVersion());
                preparedStatement.setString(INDEX_7, factoryData.getIccid());
                preparedStatement.setString(INDEX_8, factoryData.getSsid());
                preparedStatement.setString(INDEX_9, factoryData.getBssid());
                preparedStatement.setString(INDEX_10, factoryData.getMsisdn());
                preparedStatement.setString(INDEX_11, factoryData.getImsi());
                preparedStatement.setTimestamp(INDEX_12, factoryData.getRecordDate());
                preparedStatement.setTimestamp(INDEX_13, factoryData.getCreatedDate());
                preparedStatement.setString(INDEX_14, factoryData.getFactoryAdmin());
                preparedStatement.setString(INDEX_15, factoryData.getState());
                preparedStatement.setString(INDEX_16, action);
                preparedStatement.setTimestamp(INDEX_17, new Timestamp(Calendar.getInstance().getTimeInMillis()));
                preparedStatement.setString(INDEX_18, factoryData.getPackageSerialNumber());
                preparedStatement.setString(INDEX_19, factoryData.getDeviceType());
                return preparedStatement;
            }
        });
    }
}
