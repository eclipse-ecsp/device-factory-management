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

package org.eclipse.ecsp.dto;

import org.eclipse.ecsp.common.RecordStats;

import java.util.List;

/**
 * This class represents a query DTO (Data Transfer Object) for device information.
 * It contains information about the device's record statistics and factory details.
 *
 * @param <T> the type of factory details
 */
public class DeviceInfoQueryDtoV2<T> {
    private RecordStats recordStats;
    private List<T> factoryDetails;

    /**
     * Default constructor for the DeviceInfoQueryDtoV2 class.
     */
    public DeviceInfoQueryDtoV2() {
        super();
    }

    /**
     * Retrieves the record statistics of the device.
     *
     * @return the record statistics
     */
    public RecordStats getRecordStats() {
        return recordStats;
    }

    /**
     * Sets the record statistics of the device.
     *
     * @param recordStats the record statistics to set
     */
    public void setRecordStats(RecordStats recordStats) {
        this.recordStats = recordStats;
    }

    /**
     * Retrieves the factory details of the device.
     *
     * @return the factory details
     */
    public List<T> getFactoryData() {
        return factoryDetails;
    }

    /**
     * Sets the factory details of the device.
     *
     * @param data the factory details to set
     */
    public void setFactoryData(List<T> data) {
        this.factoryDetails = data;
    }
}
