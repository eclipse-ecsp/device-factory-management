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

import org.eclipse.ecsp.dto.read.device.DeviceDetailsDtoV1;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for StateDetailsDeviceInfoQueryFindServiceV1.
 */
public class StateDetailsDeviceInfoQueryFindServiceV1Test {

    @InjectMocks
    private StateDetailsDeviceInfoQueryFindServiceV1 stateDetailsDeviceInfoQueryFindServiceV1;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void findFactoryDataTest() {
        DeviceDetailsDtoV1 dto = new DeviceDetailsDtoV1(null, null, null, null, null, null, null, null);
        stateDetailsDeviceInfoQueryFindServiceV1.findFactoryData(dto);
        Assertions.assertNotNull(dto);
    }
}
