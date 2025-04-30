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

import org.eclipse.ecsp.common.rowmapper.HcpInfoMapper;
import org.eclipse.ecsp.dto.HcpInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for HcpInfoDao.
 *
 * @author sabarish sembanan
 */
public class HcpInfoDaoTest {

    private static final String FACTORYID = "FACTORYID";
    private static final long ID = 1002L;

    @InjectMocks
    HcpInfoDao dao;
    @Mock
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void findByFactoryId_null() {
        List<HcpInfo> hcpInfos = new ArrayList<>();
        String sql = "select * from \"HCPInfo\" where \"factory_data\" =:FACTORYID";
        MapSqlParameterSource params = new MapSqlParameterSource(FACTORYID, ID);

        Mockito.doReturn(hcpInfos).when(namedParameterJdbcTemplate).query(sql, params, new HcpInfoMapper());
        HcpInfo hcpInfo = dao.findByFactoryId(ID);

        assertNull(hcpInfo);
    }

    @Test
    public void findByFactoryId() {
        HcpInfo info = new HcpInfo();
        info.setFactoryId("1002L");
        info.setSerialNumber("S123");
        List<HcpInfo> hcpInfos = new ArrayList<>();
        hcpInfos.add(info);
        String sql = "select * from \"HCPInfo\" where \"factory_data\" =:FACTORYID";
        MapSqlParameterSource params = new MapSqlParameterSource(FACTORYID, ID);

        Mockito.doReturn(hcpInfos).when(namedParameterJdbcTemplate)
            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class), Mockito.any(HcpInfoMapper.class));
        HcpInfo hcpInfo = dao.findByFactoryId(ID);

        assertNotNull(hcpInfo);
    }
}
