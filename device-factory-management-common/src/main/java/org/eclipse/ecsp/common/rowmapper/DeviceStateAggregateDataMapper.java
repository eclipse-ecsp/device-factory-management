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

package org.eclipse.ecsp.common.rowmapper;

import org.eclipse.ecsp.dto.DeviceStateAggregateData;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is responsible for mapping the result set to a DeviceStateAggregateData object.
 */
public class DeviceStateAggregateDataMapper implements RowMapper<DeviceStateAggregateData> {

    /**
        * Maps a row from the ResultSet to a DeviceStateAggregateData object.
        *
        * @param resultSet the ResultSet containing the row data
        * @param rowNum the row number
        * @return the mapped DeviceStateAggregateData object
        * @throws SQLException if a database access error occurs
        */
    @Override
    public DeviceStateAggregateData mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        DeviceStateAggregateData aggregateData = new DeviceStateAggregateData();
        aggregateData.setState(resultSet.getString("state"));
        aggregateData.setCount(resultSet.getLong("count"));
        return aggregateData;
    }
}
