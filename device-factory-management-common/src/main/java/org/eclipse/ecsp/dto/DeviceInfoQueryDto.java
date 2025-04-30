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
 * It contains the record statistics and a list of data objects.
 *
 * @param <T> the type of data objects in the list
 */
public class DeviceInfoQueryDto<T> {
    private RecordStats recordStats;
    private List<T> data;

    /**
     * Default constructor for the DeviceInfoQueryDto class.
     */
    public DeviceInfoQueryDto() {
        super();
    }

    /**
     * Retrieves the record statistics.
     *
     * @return the record statistics
     */
    public RecordStats getRecordStats() {
        return recordStats;
    }

    /**
     * Sets the record statistics.
     *
     * @param recordStats the record statistics to set
     */
    public void setRecordStats(RecordStats recordStats) {
        this.recordStats = recordStats;
    }

    /**
     * Retrieves the list of data objects.
     *
     * @return the list of data objects
     */
    public List<T> getData() {
        return data;
    }

    /**
     * Sets the list of data objects.
     *
     * @param data the list of data objects to set
     */
    public void setData(List<T> data) {
        this.data = data;
    }
}
