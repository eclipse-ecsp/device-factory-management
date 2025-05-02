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

package org.eclipse.ecsp.common;

/**
 * The {@code DatabaseConstants} class contains constants related to the database.
 */
public final class DatabaseConstants {
    /**
     * The timestamp format used for device factory data.
     * This format follows the pattern "yyyy/MM/dd", where:
     * - "yyyy" represents the year in four digits.
     * - "MM" represents the month in two digits.
     * - "dd" represents the day in two digits.
     */
    public static final String DEVICEINFOFACTORYDATA_TIMESTAMP_FORMAT = "yyyy/MM/dd";

    /**
     * The `DatabaseConstants` class contains constants related to the database used in the device factory management
     * system.
     * This class cannot be instantiated as it only provides constants and does not have any instance variables or
     * methods.
     */
    private DatabaseConstants() {

    }
}
