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

package org.eclipse.ecsp.dao.read.device;

import org.eclipse.ecsp.dto.DeviceInfoFactoryData;

import java.util.List;
import java.util.Map;

/**
 * This interface represents a data access object for retrieving device details.
 */
public interface IdeviceDetailsDaoV1 {

    /**
     * Constructs and fetches factory data based on the provided request parameters.
     *
     * @param requestParams the map of request parameters
     * @return the list of DeviceInfoFactoryData objects
     */
    List<DeviceInfoFactoryData> constructAndFetchFactoryData(Map<String, String> requestParams);
}
