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
import org.eclipse.ecsp.common.rowmapper.HcpInfoMapper;
import org.eclipse.ecsp.dto.HcpInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Data Access Object for retrieving HCPInfo details.
 */
@Component
@Slf4j
public class HcpInfoDao {

    private static final String FACTORYID = "FACTORYID";
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * Find HCPInfo details by factoryId.
     *
     * @param factoryId factoryId
     * @return HcpInfo details
     */
    public HcpInfo findByFactoryId(Long factoryId) {
        log.debug("getting hcpinfo for factoryid {}", factoryId);
        String sql = "select * from \"HCPInfo\" where \"factory_data\" =:FACTORYID";
        MapSqlParameterSource params = new MapSqlParameterSource(FACTORYID, factoryId);
        List<HcpInfo> hcpInfos = namedParameterJdbcTemplate.query(sql, params, new HcpInfoMapper());
        if (CollectionUtils.isEmpty(hcpInfos)) {
            return null;
        }
        return hcpInfos.get(0);
    }
}
