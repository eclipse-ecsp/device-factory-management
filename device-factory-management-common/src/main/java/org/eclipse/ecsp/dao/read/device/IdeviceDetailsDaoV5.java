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

import org.eclipse.ecsp.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.dto.DeviceInfoAggregateFactoryData;
import org.eclipse.ecsp.dto.DeviceInfoFactoryDataWithSubscription;

import java.util.List;

/**
 * This interface provides methods for fetching device details from the factory data.
 */
public interface IdeviceDetailsDaoV5 {

    /**
     * Fetches the total factory data for device details.
     *
     * @param inputType      the input type
     * @param inputTypeValue the input type value
     * @return the count of factory data
     */
    Long constructFetchTotalFactoryDataForDeviceDetails(DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType,
                                                        String inputTypeValue);

    /**
     * Constructs and fetches factory data based on the specified parameters.
     *
     * @param type        the type of device details input
     * @param searchKey   the search key for filtering the data
     * @param sizeValue   the size value for pagination
     * @param pageValue   the page value for pagination
     * @param sortBy      the field to sort the data by
     * @param orderBy     the order in which to sort the data (ascending or descending)
     * @return a list of DeviceInfoFactoryDataWithSubscription objects containing the fetched factory data
     */
    List<DeviceInfoFactoryDataWithSubscription> constructFetchFactoryData(
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum type,
        String searchKey, int sizeValue, int pageValue, String sortBy, String orderBy);

    /**
     * Constructs and fetches the aggregate factory data for device information.
     *
     * @param type      the input type of the device details
     * @param searchKey the search key for filtering the device details
     * @return the state count of the device information aggregate factory data
     */
    DeviceInfoAggregateFactoryData.StateCount constructFetchAggregateFactoryData(
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum type,
        String searchKey);
}
