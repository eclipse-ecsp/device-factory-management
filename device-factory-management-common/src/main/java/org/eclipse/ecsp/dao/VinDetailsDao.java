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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class represents the Data Access Object (DAO) for managing VIN details.
 */
@Repository
@Slf4j
public class VinDetailsDao {
    public static final int INDEX_2 = 2;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParamJdbcTemplate;

    /**
     * Inserts data into the vin_details table.
     *
     * @param factoryData The factory data
     * @param vin The VIN
     */
    public void insert(final long factoryData, final String vin) {
        log.info("Inserting data into vin_details");
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection paramConnection) throws SQLException {
                PreparedStatement ps = paramConnection
                    .prepareStatement("insert into public.\"vin_details\" (\"vin\",\"reference_id\") values(?,?)");
                ps.setString(1, vin);
                ps.setLong(INDEX_2, factoryData);
                return ps;
            }
        });
        log.info("Successfully Inserted data into vin_details");
    }

    /**
     * Checks if a VIN is present or not.
     *
     * @param vin The VIN
     * @return true if the VIN is present, false otherwise
     */
    public boolean checkForVin(String vin) {
        StringBuilder queryCreator = new StringBuilder("select count(*) from public.\"vin_details\" ");
        queryCreator.append("WHERE vin='");
        queryCreator.append(vin.replace("'", "''").replace("_", "__"));
        queryCreator.append("' ");
        log.info("Query generated :: {}", queryCreator);
        Long deviceCount = namedParamJdbcTemplate.queryForObject(queryCreator.toString(),
            new MapSqlParameterSource(), Long.class);
        deviceCount = deviceCount != null ? deviceCount : 0;
        boolean isVinPresent = false;
        if (deviceCount != 0) {
            isVinPresent = true;
        }
        return isVinPresent;
    }

    /**
     * Updates the VIN for factory data.
     *
     * @param factoryData The factory data
     * @param vin The VIN
     */
    public void updateVin(final long factoryData, final String vin) {
        log.debug("Updating vin_details");
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection paramConnection) throws SQLException {
                PreparedStatement ps = paramConnection
                    .prepareStatement("update public.\"vin_details\" set \"vin\" = ? where \"reference_id\" = ?");
                ps.setString(1, vin);
                ps.setLong(INDEX_2, factoryData);
                return ps;
            }
        });
        log.debug("Successfully updated vin_details");
    }

    /**
     * Checks if a VIN is present for the given factory data.
     *
     * @param factoryData The factory data
     * @param vin The VIN
     * @return true if the VIN is present, false otherwise
     */
    public boolean checkForCurretVin(final long factoryData, String vin) {
        StringBuilder queryCreator = new StringBuilder("select count(*) from public.\"vin_details\" ");
        queryCreator.append("WHERE vin='");
        queryCreator.append(vin.replace("'", "''").replace("_", "__"));
        queryCreator.append("' ");
        queryCreator.append(" and reference_id=" + factoryData);
        log.info("Query generated :: {}", queryCreator);
        Long deviceCount =
            namedParamJdbcTemplate.queryForObject(queryCreator.toString(), new MapSqlParameterSource(), Long.class);
        deviceCount = deviceCount != null ? deviceCount : 0;
        boolean isVinPresent = false;
        if (deviceCount != 0) {
            isVinPresent = true;
        }
        return isVinPresent;
    }
}
