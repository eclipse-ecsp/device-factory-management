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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.Valid;

/**
 * Represents a request to update a device.
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceUpdateRequest {
    @Valid
    private DeviceData currentValue;
    @Valid
    private DeviceData replaceWith;

    /**
     * Gets the current value of the device.
     *
     * @return The current value of the device.
     */
    public DeviceData getCurrentValue() {
        return currentValue;
    }

    /**
     * Sets the current value of the device.
     *
     * @param currentValue The current value of the device.
     */
    public void setCurrentValue(DeviceData currentValue) {
        this.currentValue = currentValue;
    }

    /**
     * Gets the value to replace the current value of the device with.
     *
     * @return The value to replace the current value of the device with.
     */
    public DeviceData getReplaceWith() {
        return replaceWith;
    }

    /**
     * Sets the value to replace the current value of the device with.
     *
     * @param replaceWith The value to replace the current value of the device with.
     */
    public void setReplaceWith(DeviceData replaceWith) {
        this.replaceWith = replaceWith;
    }

    /**
     * Returns a string representation of the DeviceUpdateRequest object.
     *
     * @return A string representation of the DeviceUpdateRequest object.
     */
    @Override
    public String toString() {
        return "currentValue:{" + currentValue + "}, replaceValue:{" + replaceWith + "}";
    }
}
