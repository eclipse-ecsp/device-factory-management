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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Represents the state of a device.
 */
public enum DeviceState {

    PROVISIONED("PROVISIONED") {
        @Override
        public boolean isValidTransition(DeviceState newState) {
            LOGGER.debug("checking  - is Transition allowed from PROVISIONED to {} ", newState.getValue());
            if (newState.equals(DeviceState.STOLEN) || newState.equals(DeviceState.FAULTY)) {
                return true;
            }
            return false;
        }

    },
    READY_TO_ACTIVATE("READY_TO_ACTIVATE") {
        @Override
        public boolean isValidTransition(DeviceState newState) {
            LOGGER.debug("checking - is transition allowed from READY_TO_ACTIVATE to {} ", newState.getValue());
            return false;
        }

    },
    ACTIVE("ACTIVE") {
        @Override
        public boolean isValidTransition(DeviceState newState) {
            LOGGER.debug("checking - is Transition allowed from ACTIVE to {} ", newState.getValue());
            if (newState.equals(DeviceState.STOLEN) || newState.equals(DeviceState.FAULTY)) {
                return true;
            }
            return false;
        }
    },
    STOLEN("STOLEN") {
        @Override
        public boolean isValidTransition(DeviceState newState) {
            LOGGER.debug("checking - is Transition allowed from STOLEN to {} ", newState.getValue());
            if (newState.equals(DeviceState.ACTIVE) || newState.equals(DeviceState.PROVISIONED)) {
                return true;
            }
            return false;
        }

    },
    FAULTY("FAULTY") {
        @Override
        public boolean isValidTransition(DeviceState newState) {
            LOGGER.debug("checking - is Transition allowed from FAULTY to {} ", newState.getValue());
            if (newState.equals(DeviceState.STOLEN) || newState.equals(DeviceState.ACTIVE)
                || newState.equals(DeviceState.PROVISIONED)) {
                return true;
            }
            return false;
        }

    },
    DEACTIVATED("DEACTIVATED") {
        @Override
        public boolean isValidTransition(DeviceState newState) {
            LOGGER.debug("checking - is Transition allowed from DEACTIVATED to {} ", newState.getValue());
            return false;
        }

    },
    PROVISIONED_ALIVE("PROVISIONED_ALIVE") {
        @Override
        public boolean isValidTransition(DeviceState newState) {
            return false;
        }

    };

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceState.class);
    private String value;

    /**
     * Constructs a new {@code DeviceState} with the specified value.
     *
     * @param value the value of the device state
     */
    private DeviceState(String value) {
        this.value = value;
    }

    /**
     * Converts the state count of device states to the corresponding fields in the given
     * {@link DeviceInfoAggregateFactoryData.StateCount} object.
     *
     * @param stateCount      the {@link DeviceInfoAggregateFactoryData.StateCount} object to update with the state
     *                        counts
     * @param deviceStateMap  the list of {@link DeviceStateAggregateData} objects containing the device states and
     *                        their counts
     */
    public static void convertStateCount(DeviceInfoAggregateFactoryData.StateCount stateCount,
                                         List<DeviceStateAggregateData> deviceStateMap) {

        for (DeviceStateAggregateData deviceState : deviceStateMap) {
            String state = deviceState.getState().toUpperCase();
            if (PROVISIONED.getValue().equals(state)) {
                stateCount.setProvisioned(deviceState.getCount());
            } else if (ACTIVE.getValue().equals(state)) {
                stateCount.setActive(deviceState.getCount());
            } else if (FAULTY.getValue().equals(state)) {
                stateCount.setFaulty(deviceState.getCount());
            } else if (STOLEN.getValue().equals(state)) {
                stateCount.setStolen(deviceState.getCount());
            }
        }
    }

    /**
     * Checks if the transition to the specified device state is valid.
     *
     * @param newState the new device state to transition to
     * @return true if the transition is valid, false otherwise
     */
    public abstract boolean isValidTransition(DeviceState newState);

    /**
     * Gets the value of the device state.
     *
     * @return The value of the device state.
     */
    public String getValue() {
        return this.value;
    }
}
