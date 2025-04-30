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

import org.eclipse.ecsp.dto.DeviceInfoFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryDataRequest;
import org.eclipse.ecsp.dto.DeviceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Inserts data into the DeviceInfoFactoryData table.
 *
 * @param factoryData The factory data to be inserted.
 * @param userId The user ID associated with the data.
 */
@Component
public class DeviceInfoFactoryDao {
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
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceInfoFactoryDao.class);
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DeviceInfoFactoryDataDao factoryDao;
    @Autowired
    private VinDetailsDao vinDetailsDao;

    /**
     * Inserts the given device factory data into the DeviceInfoFactory table.
     *
     * @param factoryData The device factory data to be inserted.
     * @param userId The ID of the user performing the insertion.
     */
    @Transactional
    public void insertData(final DeviceInfoFactoryDataRequest factoryData, final String userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LOGGER.info("Inserting data into DeviceInfoFactory");
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                    "insert into public.\"DeviceInfoFactoryData\" (\"manufacturing_date\",\"model\",\"imei\","
                        + "\"serial_number\",\"platform_version\",\"iccid\",\"ssid\",\"bssid\",\"msisdn\",\"imsi\","
                        + "\"record_date\",\"factory_admin\",\"created_date\",\"state\",\"package_serial_number\")"
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
                updatePreparedStatement(userId, ps, factoryData);
                return ps;
            }
        }, keyHolder);
        long factoryDataId = 0;
        DeviceInfoFactoryData factoryDataFromTable = null;
        LOGGER.info("Successfully inserted data into DeviceInfoFactory table");
        if (keyHolder.getKeys() != null && factoryData.getVin() != null) {
            Map<String, Object> keys = keyHolder.getKeys();
            factoryDataId = keys != null ? (long) keys.get("ID") : (long) 0;
            vinDetailsDao.insert(factoryDataId, factoryData.getVin());
        }
        if (factoryDataId != 0) {
            factoryDataFromTable = factoryDao.findByFactoryId(factoryDataId);
        } else {
            factoryDataFromTable = factoryDao.findByFactoryImei(factoryData.getImei());
        }
        factoryDao.createHistoryTableEntry(factoryDataFromTable, DeviceState.PROVISIONED.toString());
        LOGGER.info("Successfully inserted Data into DeviceInfoFactoryHistory table");
    }

    /**
     * Updates the prepared statement with the given device information factory data.
     *
     * @param userId       the user ID associated with the update
     * @param ps           the prepared statement to update
     * @param factoryData  the device information factory data to update the prepared statement with
     * @throws SQLException if there is an error updating the prepared statement
     */
    private void updatePreparedStatement(final String userId, PreparedStatement ps,
                                         DeviceInfoFactoryDataRequest factoryData) throws SQLException {
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
        ps.setString(INDEX_3, factoryData.getImei());
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
    }
}
