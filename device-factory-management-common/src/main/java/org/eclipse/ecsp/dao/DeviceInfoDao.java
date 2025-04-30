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

import org.eclipse.ecsp.common.rowmapper.DeviceInfoRowMapper;
import org.eclipse.ecsp.dto.DeviceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This class represents a Data Access Object (DAO) for retrieving device information.
 * It provides methods to interact with the underlying database table "DeviceInfo".
 */
@Repository
public class DeviceInfoDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Retrieves a list of device information based on the given Harman ID.
     *
     * @param harmandId the Harman ID to search for
     * @return a list of DeviceInfo objects matching the given Harman ID
     */
    public List<DeviceInfo> findDeviceInfo(String harmandId) {
        String sql = "select * from \"DeviceInfo\" where \"HarmanID\" = ?";
        return jdbcTemplate.query(sql, new Object[]{harmandId}, new DeviceInfoRowMapper());
    }
}