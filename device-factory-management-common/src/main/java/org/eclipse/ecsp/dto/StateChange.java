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

import jakarta.validation.constraints.NotNull;
import org.eclipse.ecsp.dto.validation.NullOrNotEmpty;
import java.io.Serializable;

/**
 * Represents a state change of a device in the factory management system.
 * This class contains information about the factory ID, device state, and IMEI number.
 */
public class StateChange implements Serializable {

    /**
     * Factory id.
     */
    @NotNull(message = "factoryId is not allowed to be null")
    private Long factoryId;

    /**
     * Device state.
     */
    @NotNull(message = "state is required and not allowed to be null")
    private DeviceState state;

    @NullOrNotEmpty(message = "imei is not allowed be empty")
    private String imei;

    /**
     * Gets the factory id.
     *
     * @return The factory id.
     */
    public Long getFactoryId() {
        return factoryId;
    }

    /**
     * Sets the factory id.
     *
     * @param factoryId The factory id to set.
     */
    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    /**
     * Gets the device state.
     *
     * @return The device state.
     */
    public DeviceState getState() {
        return state;
    }

    /**
     * Sets the device state.
     *
     * @param state The device state to set.
     */
    public void setState(DeviceState state) {
        this.state = state;
    }

    /**
     * Gets the IMEI number of the device.
     *
     * @return The IMEI number.
     */
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI number of the device.
     *
     * @param imei The IMEI number to set.
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Returns the hash code value for this object. The hash code is computed based on the values of the `factoryId`,
     * `state`, and `imei` properties.
     *
     * @return the hash code value for this object
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((factoryId == null) ? 0 : factoryId.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((imei == null) ? 0 : imei.hashCode());
        return result;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        StateChange other = (StateChange) obj;
        if (factoryId == null) {
            if (other.factoryId != null) {
                return false;
            }
        } else if (!factoryId.equals(other.factoryId)) {
            return false;
        }

        if (imei == null) {
            if (other.imei != null) {
                return false;
            }
        } else if (!imei.equals(other.imei)) {
            return false;
        }

        if (state != other.state) {
            return false;
        }
        return true;
    }

    /**
     * Returns a string representation of the StateChange object.
     *
     * @return a string representation of the StateChange object
     */
    @Override
    public String toString() {
        return "StateChange [factoryId=" + factoryId + ", state=" + state + ", imei=" + imei + "]";
    }

}
