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
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceInfoDao.
 *
 * @author ayush agrahari
 */
public class DeviceInfoDaoTest {
    private static final String SQL = "select * from \"DeviceInfo\" where \"HarmanID\" = ?";
    private static final String HARMAN_ID = "H1";
    @InjectMocks
    private DeviceInfoDao deviceInfoDao;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void findDeviceInfo() {
        List<DeviceInfo> deviceInfoList = Collections.singletonList(new DeviceInfo("H1", "name1", "value1"));
        Mockito.doReturn(deviceInfoList).when(jdbcTemplate)
            .query(SQL, new Object[]{HARMAN_ID}, new DeviceInfoRowMapper());

        List<DeviceInfo> actualDeviceInfo = deviceInfoDao.findDeviceInfo(HARMAN_ID);
        assertNotNull(actualDeviceInfo);
    }
}
