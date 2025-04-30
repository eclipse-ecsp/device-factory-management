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

/**
 * Represents a user post request.
 */
public class UserPostRequest {

    private UserCreationDetails userPost;

    /**
     * Gets the user creation details for the user post request.
     *
     * @return the user creation details
     */
    public UserCreationDetails getUserPost() {
        return userPost;
    }

    /**
     * Sets the user creation details for the user post request.
     *
     * @param userPost the user creation details to be set
     */
    public void setUserPost(UserCreationDetails userPost) {
        this.userPost = userPost;
    }

}
