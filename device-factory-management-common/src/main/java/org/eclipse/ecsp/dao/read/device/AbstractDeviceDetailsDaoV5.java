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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an abstract class that serves as a base for implementing the IdeviceDetailsDaoV5 interface.
 * It provides common functionality and properties for device details data access objects.
 */
public abstract class AbstractDeviceDetailsDaoV5 implements IdeviceDetailsDaoV5 {

    /**
     * Logger instance for logging messages related to the AbstractDeviceDetailsDaoV5 class.
     * This logger is used to log debug, info, warning, and error messages to assist in
     * monitoring and troubleshooting the application's behavior.
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractDeviceDetailsDaoV5.class);

}
