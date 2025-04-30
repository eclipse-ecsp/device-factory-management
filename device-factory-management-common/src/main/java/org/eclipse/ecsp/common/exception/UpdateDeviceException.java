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

package org.eclipse.ecsp.common.exception;

/**
 * Exception thrown when an error occurs during the update of a device.
 */
public class UpdateDeviceException extends RuntimeException {

    private static final long serialVersionUID = 3826992573014071356L;

    /**
     * Constructs a new UpdateDeviceException with the specified detail message.
     *
     * @param message the detail message
     */
    public UpdateDeviceException(String message) {
        super(message);
    }

    /**
     * Constructs a new UpdateDeviceException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param e the cause
     */
    public UpdateDeviceException(String message, Exception e) {
        super(message, e);
    }

}
