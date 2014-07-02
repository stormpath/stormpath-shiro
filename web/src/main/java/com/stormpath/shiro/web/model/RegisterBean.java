/*
 * Copyright 2014 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.shiro.web.model;

/**
 * Holder for registration data.
 *
 * @since 0.6.0
 */
public class RegisterBean {

    private String email;
    private String username;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
//    private String status;
//    private boolean suppressVerificationEmail;

    public String getEmail() {
        return email;
    }

    public RegisterBean setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public RegisterBean setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RegisterBean setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public RegisterBean setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getMiddleName() {
        return middleName;
    }

    public RegisterBean setMiddleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public RegisterBean setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

//    public String getStatus() {
//        return status;
//    }
//
//    public RegisterBean setStatus(String status) {
//        this.status = status;
//        return this;
//    }
//
//    public boolean isSuppressVerificationEmail() {
//        return suppressVerificationEmail;
//    }
//
//    public RegisterBean setSuppressVerificationEmail(boolean suppressVerificationEmail) {
//        this.suppressVerificationEmail = suppressVerificationEmail;
//        return this;
//    }
}
