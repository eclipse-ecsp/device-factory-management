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
 * This interface provides methods to fetch device details from the factory data.
 */
public interface IdeviceDetailsDaoV4 {

    /**
     * Fetches the total factory data for device details based on the input type and input type value.
     *
     * @param inputType      the input type
     * @param inputTypeValue the input type value
     * @return the count of total factory data
     */
    Long constructFetchTotalFactoryDataForDeviceDetails(DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType,
                                                        String inputTypeValue);

    /**
     * Fetches the factory data based on the type, search key, size value, page value, sort by, and order by.
     *
     * @param type        the type
     * @param searchKey   the search key
     * @param sizeValue   the size value
     * @param pageValue   the page value
     * @param sortBy      the sort by
     * @param orderBy     the order by
     * @return the list of DeviceInfoFactoryDataWithSubscription
     */
    List<DeviceInfoFactoryDataWithSubscription> constructFetchFactoryData(
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum type, String searchKey, int sizeValue,
        int pageValue, String sortBy, String orderBy);

    /**
     * Fetches the aggregate factory data based on the type and search key.
     *
     * @param type      the type
     * @param searchKey the search key
     * @return the state count of aggregate factory data
     */
    DeviceInfoAggregateFactoryData.StateCount constructFetchAggregateFactoryData(
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum type, String searchKey);
}
