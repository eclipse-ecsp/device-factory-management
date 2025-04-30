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

import com.fasterxml.jackson.annotation.JsonInclude;
import org.eclipse.ecsp.common.HcpServicesBaseResponse;
import org.eclipse.ecsp.common.RecordStats;

import java.util.List;

/**
 * Represents a successful response for a device information query.
 * This class extends the {@link HcpServicesBaseResponse} class and provides additional properties for record
 * statistics and data.
 *
 * @param <T> The type of data contained in the response.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceInfoQuerySuccessResponse<T> extends HcpServicesBaseResponse {
    private RecordStats recordStats;
    private List<T> data;

    /**
     * Constructs a new DeviceInfoQuerySuccessResponse object.
     */
    public DeviceInfoQuerySuccessResponse() {
        super();
    }

    /**
     * Retrieves the record statistics of the query success response.
     *
     * @return The record statistics.
     */
    public RecordStats getRecordStats() {
        return recordStats;
    }

    /**
     * Sets the record statistics of the query success response.
     *
     * @param recordStats The record statistics to be set.
     */
    public void setRecordStats(RecordStats recordStats) {
        this.recordStats = recordStats;
    }

    /**
     * Retrieves the data of the query success response.
     *
     * @return The data.
     */
    public List<T> getData() {
        return data;
    }

    /**
     * Sets the data of the query success response.
     *
     * @param data The data to be set.
     */
    public void setData(List<T> data) {
        this.data = data;
    }

}
