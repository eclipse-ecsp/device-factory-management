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
 * The {@code ApiValidationFailedException} class represents an exception that is thrown when API validation fails.
 * It extends the {@link RuntimeException} class.
 */
public class ApiValidationFailedException extends RuntimeException {
    private String code;
    private String message;
    private String errorMessage;
    private Throwable throwable;

    /**
     * Constructs a new {@code ApiValidationFailedException} with the specified detail message.
     *
     * @param message the detail message
     */
    public ApiValidationFailedException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * Constructs a new {@code ApiValidationFailedException} with the specified code and message.
     *
     * @param code    the error code
     * @param message the error message
     */
    public ApiValidationFailedException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * Constructs a new {@code ApiValidationFailedException} with the specified code, message, and error message.
     *
     * @param code         the error code
     * @param message      the error message
     * @param errorMessage the detailed error message
     */
    public ApiValidationFailedException(String code, String message, String errorMessage) {
        super(message);
        this.code = code;
        this.message = message;
        this.errorMessage = errorMessage;
    }

    /**
     * Constructs a new {@code ApiValidationFailedException} with the specified code, message, error message, and cause.
     *
     * @param code         the error code
     * @param message      the error message
     * @param errorMessage the detailed error message
     * @param e            the cause of the exception
     */
    public ApiValidationFailedException(String code, String message, String errorMessage, Throwable e) {
        super(message, e);
        this.code = code;
        this.message = message;
        this.errorMessage = errorMessage;
        this.throwable = e;
    }

    /**
     * Returns the error code associated with this exception.
     *
     * @return the error code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the error code for this exception.
     *
     * @param code the error code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Returns the error message associated with this exception.
     *
     * @return the error message
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * Sets the error message for this exception.
     *
     * @param message the error message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the detailed error message associated with this exception.
     *
     * @return the detailed error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the detailed error message for this exception.
     *
     * @param errorMessage the detailed error message to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the cause of this exception.
     *
     * @return the cause of this exception
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * Sets the cause of this exception.
     *
     * @param throwable the cause to set
     */
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
