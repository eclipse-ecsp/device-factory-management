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

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for VinDetailsDao.
 */
public class VinDetailsDaoTest {

    private static final Long DEVICE_COUNT = 3L;
    private static final Long FACTORY_DATA = 10L;

    @InjectMocks
    VinDetailsDao vinDetailsDao;

    @Mock
    JdbcTemplate jdbcTemplate;

    @Mock
    NamedParameterJdbcTemplate namedParamJdbcTemplate;

    @Mock
    PreparedStatementCreator preparedStatementCreator;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void checkForVinTest() {
        String vin = "JN1TAAT32A0XXXXYY";
        StringBuilder queryCreator = new StringBuilder("select count(*) from public.\"vin_details\" ");
        queryCreator.append("WHERE vin='");
        queryCreator.append(vin.replaceAll("'", "''").replace("_", "__"));
        queryCreator.append("' ");

        Mockito.doReturn(DEVICE_COUNT).when(namedParamJdbcTemplate).queryForObject(queryCreator.toString(),
            new MapSqlParameterSource(), Long.class);

        vinDetailsDao.checkForVin(vin);
        Assertions.assertFalse(vinDetailsDao.checkForVin(vin));
    }

    @Test
    public void updateVinTest() {
        String vin = "JN1TAAT32A0XXXXYY";
        Mockito.doReturn(1).when(jdbcTemplate).update(preparedStatementCreator);

        vinDetailsDao.updateVin(FACTORY_DATA, vin);
        Assertions.assertEquals(1, jdbcTemplate.update(preparedStatementCreator));
    }

    @Test
    public void checkForCurretVinTest() {
        String vin = "JN1TAAT32A0XXXXYY";
        StringBuilder queryCreator = new StringBuilder("select count(*) from public.\"vin_details\" ");
        queryCreator.append("WHERE vin='");
        queryCreator.append(vin.replaceAll("'", "''").replace("_", "__"));
        queryCreator.append("' ");
        queryCreator.append(" and reference_id=" + FACTORY_DATA);

        Mockito.doReturn(DEVICE_COUNT).when(namedParamJdbcTemplate).queryForObject(queryCreator.toString(),
            new MapSqlParameterSource(), Long.class);

        vinDetailsDao.checkForCurretVin(FACTORY_DATA, vin);
        Assertions.assertFalse(vinDetailsDao.checkForCurretVin(FACTORY_DATA, vin));
    }


    @Test
    public void insertTest() {
        String vin = "JN1TAAT32A0XXXXYY";
        Mockito.doReturn(1).when(jdbcTemplate).update(preparedStatementCreator);

        vinDetailsDao.insert(FACTORY_DATA, vin);
        Assertions.assertEquals(1, jdbcTemplate.update(preparedStatementCreator));

    }

}
