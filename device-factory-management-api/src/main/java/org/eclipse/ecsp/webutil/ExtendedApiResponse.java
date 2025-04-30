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

package org.eclipse.ecsp.webutil;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Represents an extended API response.
 *
 * @param <D> the type of data in the response
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class ExtendedApiResponse<D> {
    private final String requestId;
    private final String code;
    private final String reason;
    private final String message;
    private final Meta meta;
    private final D data;
    private HttpStatus httpStatusCodeObj;
    private int httpStatusCode;
    private List<InterComponentError> errors;

    /**
     * Constructs a new ExtendedApiResponse object.
     *
     * @param builder The builder object used to construct the ExtendedApiResponse.
     */
    public ExtendedApiResponse(Builder<D> builder) {
        this.httpStatusCodeObj = builder.httpStatusCodeObj;
        this.httpStatusCode = builder.httpStatusCode;
        this.requestId = builder.requestId;
        this.code = builder.code;
        this.reason = builder.reason;
        this.message = builder.message;
        this.meta = builder.meta;
        this.data = builder.data;
        this.errors = builder.errors;
    }

    /**
     * Get the HTTP status code.
     *
     * @return The HTTP status code.
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     * Set the HTTP status code.
     *
     * @param httpStatusCode The HTTP status code to set.
     */
    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * Get the HTTP status code object.
     *
     * @return The HTTP status code object.
     */
    public HttpStatus getHttpStatusCodeObj() {
        return httpStatusCodeObj;
    }

    /**
     * Set the HTTP status code object.
     *
     * @param httpStatusCodeObj The HTTP status code object to set.
     */
    public void setHttpStatusCodeObj(HttpStatus httpStatusCodeObj) {
        this.httpStatusCodeObj = httpStatusCodeObj;
    }

    /**
     * Get the request ID.
     *
     * @return The request ID.
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Get the code.
     *
     * @return The code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Get the reason.
     *
     * @return The reason.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Get the message.
     *
     * @return The message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the meta data.
     *
     * @return The meta data.
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * Get the data.
     *
     * @return The data.
     */
    public D getData() {
        return data;
    }

    /**
     * Get the list of inter-component errors.
     *
     * @return The list of inter-component errors.
     */
    public List<InterComponentError> getErrors() {
        return errors;
    }

    /**
     * Set the list of inter-component errors.
     *
     * @param errors The list of inter-component errors to set.
     */
    public void setErrors(List<InterComponentError> errors) {
        this.errors = errors;
    }

    /**
     * Builder class for constructing ExtendedApiResponse objects.
     *
     * @param <D> The type of data.
     */
    public static class Builder<D> {
        private final HttpStatus httpStatusCodeObj;
        private final int httpStatusCode;
        private final String requestId;
        private final String code;
        private final String reason;
        private final String message;
        private Meta meta;
        private D data;
        private List<InterComponentError> errors;

        /**
         * Constructor for the Builder class.
         *
         * @param requestId     The request ID.
         * @param httpStatus    The HTTP status.
         * @param code          The code.
         * @param reason        The reason.
         * @param message       The message.
         */
        public Builder(String requestId, HttpStatus httpStatus, String code, String reason, String message) {
            this.httpStatusCodeObj = httpStatus;
            this.httpStatusCode = httpStatus.value();
            this.code = code;
            this.requestId = requestId;
            this.reason = reason;
            this.message = message;
        }

        /**
         * Set the meta data.
         *
         * @param meta The meta data to set.
         * @return The Builder object.
         */
        public Builder<D> withMeta(Meta meta) {
            this.meta = meta;
            return this;
        }

        /**
         * Set the data.
         *
         * @param data The data to set.
         * @return The Builder object.
         */
        public Builder<D> withData(D data) {
            this.data = data;
            return this;
        }

        /**
         * Set the list of inter-component errors.
         *
         * @param interComponentErrors The list of inter-component errors to set.
         * @return The Builder object.
         */
        public Builder<D> withError(List<InterComponentError> interComponentErrors) {
            this.errors = interComponentErrors;
            return this;
        }

        /**
         * Build the ExtendedApiResponse object.
         *
         * @return The built ExtendedApiResponse object.
         */
        public ExtendedApiResponse<D> build() {
            return new ExtendedApiResponse<>(this);
        }
    }

    /**
     * Meta class for storing meta data.
     */
    public static class Meta {
        private final int firstPage;
        private final int lastPage;
        private final int count;

        /**
         * Constructor for the Meta class.
         *
         * @param firstPage The first page.
         * @param lastPage  The last page.
         * @param count     The count.
         */
        public Meta(int firstPage, int lastPage, int count) {
            this.firstPage = firstPage;
            this.lastPage = lastPage;
            this.count = count;
        }

        /**
         * Get the first page.
         *
         * @return The first page.
         */
        public int getFirstPage() {
            return firstPage;
        }

        /**
         * Get the last page.
         *
         * @return The last page.
         */
        public int getLastPage() {
            return lastPage;
        }

        /**
         * Get the count.
         *
         * @return The count.
         */
        public int getCount() {
            return count;
        }
    }

    /**
     * InterComponentError class for storing inter-component error details.
     */
    public static class InterComponentError {
        private String code;
        private String reason;
        private String message;

        /**
         * Get the error code.
         *
         * @return The error code.
         */
        public String getCode() {
            return code;
        }

        /**
         * Set the error code.
         *
         * @param code The error code to set.
         */
        public void setCode(String code) {
            this.code = code;
        }

        /**
         * Get the error reason.
         *
         * @return The error reason.
         */
        public String getReason() {
            return reason;
        }

        /**
         * Set the error reason.
         *
         * @param reason The error reason to set.
         */
        public void setReason(String reason) {
            this.reason = reason;
        }

        /**
         * Get the error message.
         *
         * @return The error message.
         */
        public String getMessage() {
            return message;
        }

        /**
         * Set the error message.
         *
         * @param message The error message to set.
         */
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
