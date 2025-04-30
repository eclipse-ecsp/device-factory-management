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
 * This is an abstract class that provides a base implementation for the {@link IdeviceDetailsDaoV1} interface.
 * It contains common functionality and constants that can be used by concrete implementations of the interface.
 */
public abstract class AbstractDeviceDetailsDaoV1 implements IdeviceDetailsDaoV1 {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractDeviceDetailsDaoV1.class);
}
