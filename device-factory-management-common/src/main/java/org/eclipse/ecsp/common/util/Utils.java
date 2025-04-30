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

package org.eclipse.ecsp.common.util;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.common.ResponseConstants;
import org.eclipse.ecsp.common.exception.InputParamValidationException;

/**
 * Utility class for common operations.
 */
public final class Utils {
    public static final int LENGTH = 3;

    /**
     * Private constructor to prevent instantiation of the {@code Utils} class.
     */
    private Utils() {
    }

    /**
     * Validates if the given allowed type is present in the allowed types array.
     *
     * @param allowedType  the allowed type to validate
     * @param allowedTypes the array of allowed types
     * @return true if the allowed type is present, false otherwise
     */
    public static boolean validateAllowedType(String allowedType, String[] allowedTypes) {
        for (String type : allowedTypes) {
            if (type.equals(allowedType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates the IMEI (International Mobile Equipment Identity) number.
     *
     * @param imei the IMEI number to validate
     * @throws InputParamValidationException if the IMEI is invalid
     */
    public static void validateImei(String imei) throws InputParamValidationException {
        if (StringUtils.isNotEmpty(imei)) {
            if (imei.length() < LENGTH) {
                throw new InputParamValidationException(ResponseConstants.INVALID_IMEI_LENGTH_ERROR_MSG);
            }
            // This is additional validation also we have added validation in
            // API Gateway component
            if (!StringUtils.isNumeric(imei)) {
                throw new InputParamValidationException(ResponseConstants.INVALID_IMEI_ERROR_MSG);
            }
        }
    }

    /**
     * Validates the serial number, ensuring it is alphanumeric and has a minimum length of 3.
     *
     * @param serialNumber the serial number to validate
     * @throws IllegalArgumentException if the serial number is invalid
     */
    public static void validateSerialNumber(String serialNumber) throws IllegalArgumentException {
        if (StringUtils.isNotEmpty(serialNumber)) {
            if (serialNumber.length() < LENGTH) {
                throw new IllegalArgumentException(ResponseConstants.INVALID_SERIAL_NUMBER_LENGTH_ERROR_MSG);
            }
            if (!StringUtils.isAlphanumeric(serialNumber)) {
                throw new IllegalArgumentException(ResponseConstants.INVALID_SERIAL_NUMBER_ERROR_MSG);
            }
        }
    }
}
