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

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a request to replace factory data.
 */
@NoArgsConstructor
@Data
public class ReplaceFactoryDataRequest {

    DeviceInfoFactoryData currentValue;

    DeviceInfoFactoryData replaceWith;

    /**
     * Returns a string representation of the ReplaceFactoryDataRequest object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "ReplaceFactoryDataRequest [currentValue=" + currentValue + ", replaceWith=" + replaceWith + "]";
    }

}
