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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Represents the data for a device information aggregate factory.
 * This class is used to store information about the count and state count of devices.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfoAggregateFactoryData implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long count;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private StateCount stateCount;

    /**
     * Represents the state count of a device.
     * This class is used to store the count of devices in different states.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StateCount implements Serializable {

        private Long active;
        private Long provisioned;
        private Long stolen;
        private Long faulty;
    }
}
