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

import java.util.Set;

/**
 * Represents the details required for user creation with Spring Security authentication.
 */
public class UserCreationDetailsSpringAuth {

    private String userName;
    private String password;
    private Set<String> roles;
    private String aud;
    private String email;

    /**
     * Get the user name.
     *
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set the user name.
     *
     * @param userName the user name to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Get the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the roles.
     *
     * @return the roles
     */
    public Set<String> getRoles() {
        return roles;
    }

    /**
     * Set the roles.
     *
     * @param roles the roles to set
     */
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    /**
     * Get the aud.
     *
     * @return the aud
     */
    public String getAud() {
        return aud;
    }

    /**
     * Set the aud.
     *
     * @param aud the aud to set
     */
    public void setAud(String aud) {
        this.aud = aud;
    }

    /**
     * Get the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
