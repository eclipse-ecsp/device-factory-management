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

package org.eclipse.ecsp.service.read.device.impl;

import org.eclipse.ecsp.dto.read.device.DeviceDetailsDtoV2;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for AllDeviceDetailsServiceV3.
 */
public class AllDeviceDetailsServiceV3Test {

    @InjectMocks
    private AllDeviceDetailsServiceV3 allDeviceDetailsServiceV3;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test(expected = RuntimeException.class)
    public void findAllFactoryDataTest() {

        DeviceDetailsDtoV2 dto = new DeviceDetailsDtoV2("1", "1", null, null, "true", "12234", "34566");
        allDeviceDetailsServiceV3.findAllFactoryData(dto, "");
    }
}
