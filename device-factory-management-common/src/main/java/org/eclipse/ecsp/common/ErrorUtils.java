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

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Map;

/**
 * The ErrorUtils class provides utility methods for handling errors.
 */
public final class ErrorUtils {

    /**
     * A constant key used to represent the error code in error handling mechanisms.
     * This key is typically used to store or retrieve error codes from a data structure
     * such as a map or JSON object.
     */
    public static final String ERROR_CODE_KEY = "errorCode";
    /**
     * The maximum capacity value used in the application.
     * This constant represents a predefined limit of 150.
     */
    public static final int CAPACITY = 150;

    /**
     * Private constructor to prevent instantiation of the ErrorUtils class.
     */
    private ErrorUtils() {
    }

    /**
     * Builds an error message with the given general error message, exception, and request input.
     *
     * @param generalErrorMessage The general error message to include in the error.
     * @param e The exception that occurred.
     * @param requestInput The map containing the request input.
     * @return The built error message as a string.
     */
    public static String buildError(String generalErrorMessage, Exception e, Map<Object, Object> requestInput) {
        StringBuilder errorSb = new StringBuilder(CAPACITY);
        errorSb.append("\n").append(generalErrorMessage).append("\n");
        errorSb.append("Request input:").append("\n");
        Object errorCode = requestInput.remove(ERROR_CODE_KEY);
        for (Map.Entry<Object, Object> m : requestInput.entrySet()) {
            Object key = m.getKey();
            Object value = m.getValue();
            errorSb.append("  ").append(key).append(": ").append(value).append("\n");
        }
        errorSb.append("Error Code: ").append(errorCode).append("\n");
        errorSb.append("Error Message: ").append(e.getMessage()).append("\n");
        errorSb.append("RootCause Message: ").append(ExceptionUtils.getRootCauseMessage(e)).append("\n");
        errorSb.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
            .append("\n");
        errorSb.append("Error StackTrace:").append("\n");
        errorSb.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
            .append("\n");
        errorSb.append(ExceptionUtils.getStackTrace(e)).append("\n");
        return errorSb.toString();
    }
}
