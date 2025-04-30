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

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents a simple response message.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleResponseMessage {

    private String header;
    private String message;

    /**
     * Constructs a SimpleResponseMessage object with the given message.
     *
     * @param message the message to be set
     */
    public SimpleResponseMessage(String message) {
        super();
        this.message = message;
    }

    /**
     * Constructs a SimpleResponseMessage object with the given header and message.
     *
     * @param header  the header to be set
     * @param message the message to be set
     */
    public SimpleResponseMessage(String header, String message) {
        this.header = header;
        this.message = message;
    }

    /**
     * Retrieves the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message the message to be set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Retrieves the header.
     *
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * Sets the header.
     *
     * @param header the header to be set
     */
    public void setHeader(String header) {
        this.header = header;
    }

}
