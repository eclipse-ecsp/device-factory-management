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

package org.eclipse.ecsp.service.read.device;

import org.eclipse.ecsp.dto.DeviceInfoFactoryData;

import java.util.List;

/**
 * This interface provides methods to retrieve device details.
 *
 * @param <T> the type of input parameter for finding factory data
 */
public interface IdeviceDetailsService<T> {
    /**
     * Finds the factory data for the given input parameter.
     *
     * @param t the input parameter for finding factory data
     * @return a list of DeviceInfoFactoryData objects representing the factory data
     */
    List<DeviceInfoFactoryData> findFactoryData(T t);
}
