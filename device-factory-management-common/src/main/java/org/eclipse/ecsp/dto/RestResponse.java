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

import org.eclipse.ecsp.common.CommonConstants;
import org.eclipse.ecsp.common.HcpServicesBaseResponse;
import org.eclipse.ecsp.common.HcpServicesFailureResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * This class provides utility methods for creating REST responses.
 */
public class RestResponse {

    public static final String CONTENT_LOCATION = "Content-Location";
    private static final int STATUS_CODE_400 = 400;
    private static final int STATUS_CODE_404 = 404;
    private static final int STATUS_CODE_408 = 408;

    /**
     * Private constructor to prevent instantiation.
     */
    private RestResponse() {

    }

    /**
     * Creates a REST response for a successful creation request.
     *
     * @param resource    The resource that was created
     * @param resourceUrl The URL of the created resource
     * @param <T>         The type of the resource
     * @return The response entity
     */
    public static <T> ResponseEntity<T> created(T resource, String resourceUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_LOCATION, resourceUrl);
        return new ResponseEntity<T>(resource, headers, HttpStatus.CREATED);
    }

    /**
     * Creates a REST response for a successful request.
     *
     * @param resource The resource
     * @param <T>      The type of the resource
     * @return The response entity
     */
    public static <T> ResponseEntity<T> ok(T resource) {
        return new ResponseEntity<T>(resource, HttpStatus.OK);
    }

    /**
     * Creates a REST response for an accepted request.
     *
     * @param resource The resource
     * @param <T>      The type of the resource
     * @return The response entity
     */
    public static <T> ResponseEntity<T> accepted(T resource) {
        return new ResponseEntity<T>(resource, HttpStatus.ACCEPTED);
    }

    /**
     * Creates a ResponseEntity object with an ErrorRest object as the response body.
     *
     * @param status    the HTTP status code for the response
     * @param message   the error message
     * @param reference the reference for the error
     * @return a ResponseEntity object containing the ErrorRest object as the response body
     */
    private static ResponseEntity<ErrorRest> createErrorResponse(HttpStatus status, String message, String reference) {
        ErrorRest error = new ErrorRest();
        error.setMessage(message);
        error.setReference(reference);
        return new ResponseEntity<ErrorRest>(error, status);
    }

    /**
     * Creates a REST response for a "not found" error.
     *
     * @param message The error message
     * @return The response entity
     */
    public static ResponseEntity<ErrorRest> not_Found(String message) {
        return createErrorResponse(HttpStatus.NOT_FOUND, message, null);
    }

    /**
     * Creates a REST response for a "not found" error.
     *
     * @param resource The resource
     * @param <T>      The type of the resource
     * @return The response entity
     */
    public static <T> ResponseEntity<T> not_Found(T resource) {
        return new ResponseEntity<T>(resource, HttpStatus.NOT_FOUND);
    }

    /**
     * Creates a REST response for a "method not allowed" error.
     *
     * @return The response entity
     */
    public static ResponseEntity<ErrorRest> method_Not_Allowed() {
        return createErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method is not allowed for this resource", null);
    }

    /**
     * Creates a REST response for a "login failed" error.
     *
     * @return The response entity
     */
    public static ResponseEntity<ErrorRest> login_Fail() {
        return createErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid Credentials", null);
    }

    /**
     * Creates a REST response for a failure to get device information.
     *
     * @param harmanId The Harman ID
     * @return The response entity
     */
    public static ResponseEntity<ErrorRest> get_Device_Info_Failed(String harmanId) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Could Not Get Vehicle Specs for Harman ID:- " + harmanId
            + ". Please make sure you are using the correct HarmanID and it is present in the system.", null);
    }

    /**
     * Creates a REST response for a failure to get unique spec values for a spec name.
     *
     * @param specName The spec name
     * @return The response entity
     */
    public static ResponseEntity<ErrorRest> get_All_Unique_Spec_Simple_Values_Failed(String specName) {
        return createErrorResponse(HttpStatus.BAD_REQUEST,
            "Could Not Get All Unique Vehicle Spec for Spec:- " + specName
                + ". Please make sure you are using the correct specName and it is present in the system.", null);
    }

    /**
     * Creates a REST response for a failure to get unique spec values for a specs filter.
     *
     * @param specsFilter The specs filter
     * @return The response entity
     */
    public static ResponseEntity<ErrorRest> get_All_Unique_Spec_Values_Failed(String specsFilter) {
        return createErrorResponse(HttpStatus.BAD_REQUEST,
            "Could Not Get All Unique Vehicle Spec values for Specs Filter Combination:- "
                + specsFilter
                + ". Please make sure you are using the correct specName and it is present in the system.", null);
    }

    /**
     * Creates a REST response for a failure to get IDs based on a filter.
     *
     * @param filterFields The filter fields
     * @return The response entity
     */
    public static ResponseEntity<ErrorRest> get_Ids_Based_On_Filter_Failed(Map<String, List<String>> filterFields) {
        return createErrorResponse(HttpStatus.BAD_REQUEST,
            "Could Not Get Vehicle Specs values for the Filter Query:- " + filterFields.toString() + ". "
                + CommonConstants.USING_VALID_FILTER_LOGGING,
            null);
    }

    /**
     * Creates a REST response for a failure to get all names for a given ID.
     *
     * @param id The ID
     * @return The response entity
     */
    public static ResponseEntity<ErrorRest> get_All_Name_From_Id(String id) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Could Not Get Name for given ID:- " + id
            + ". Please make sure you are using the correct ID and it is present in the system.", null);

    }

    /**
     * Creates a REST response for a failure to get all fields.
     *
     * @return The response entity
     */
    public static ResponseEntity get_All_Fields() {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Could Not Get All Field names.", null);
    }

    /**
     * Creates a REST response for a failure in campaign manager device selection.
     *
     * @param filterFields The filter fields
     * @return The response entity
     */
    public static ResponseEntity get_Campaign_Mgr_Device_Selection_Info_Based_On_Filter_Failed(
        Map<String, List<String>> filterFields) {

        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
            "campaignMgrDeviceSelectionInfo():filterFields from JSON comes as:- " + filterFields.toString(), null);
    }

    /**
     * Creates a REST response for a failure to get the last connected time.
     *
     * @return The response entity
     */
    public static ResponseEntity<ErrorRest> get_Last_Connected_Time_Failure() {
        return createErrorResponse(HttpStatus.UNAUTHORIZED, "Internal Server Error : Can not fetch list of timestamps",
            null);
    }

    /**
     * Creates a REST response for a bad request error.
     *
     * @param message The error message
     * @return The response entity
     */
    public static ResponseEntity<ErrorRest> bad_Request(String message) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, message, null);
    }

    /**
     * Creates a REST response for a bad request error.
     *
     * @param resource The resource
     * @param <T>      The type of the resource
     * @return The response entity
     */
    public static <T> ResponseEntity<T> bad_Request(T resource) {
        return new ResponseEntity<T>(resource, HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates a REST response for a precondition failed error.
     *
     * @param message The error message
     * @return The response entity
     */
    public static ResponseEntity<ErrorRest> precondition_Failed(String message) {
        return createErrorResponse(HttpStatus.PRECONDITION_FAILED, message, null);
    }

    /**
     * Creates a REST response for a precondition failed error.
     *
     * @param resource The resource
     * @param <T>      The type of the resource
     * @return The response entity
     */
    public static <T> ResponseEntity<T> precondition_Failed(T resource) {
        return new ResponseEntity<T>(resource, HttpStatus.PRECONDITION_FAILED);
    }

    /**
     * Creates a REST response for an internal server error.
     *
     * @param message   The error message
     * @param reference The error reference
     * @return The response entity
     */
    public static ResponseEntity<ErrorRest> internal_Server_Error(String message, String reference) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, reference);
    }

    /**
     * Creates a REST response for an internal server error.
     *
     * @param message The error message
     * @return The response entity
     */
    public static ResponseEntity<ErrorRest> internal_Server_Error(String message) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
    }

    /**
     * Creates a REST response for an internal server error.
     *
     * @param resource The resource
     * @param <T>      The type of the resource
     * @return The response entity
     */
    public static <T> ResponseEntity<T> internal_Server_Error(T resource) {
        return new ResponseEntity<T>(resource, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a failure response.
     *
     * @param httpSatatusCode The HTTP status code
     * @param reason          The reason for the failure
     * @param message         The error message
     * @return The response entity
     */
    public static ResponseEntity<HcpServicesBaseResponse> createFailureResponse(int httpSatatusCode, String reason,
                                                                                String message) {
        HcpServicesFailureResponse failureResponse = new HcpServicesFailureResponse();
        failureResponse.setCode("HCP-001");
        failureResponse.setReason(reason);
        failureResponse.setHttpStatusCode(httpSatatusCode);
        failureResponse.setMessage(message);
        failureResponse.setRequestId("req1");
        if (httpSatatusCode == STATUS_CODE_408) {
            return new ResponseEntity<>(failureResponse, HttpStatus.REQUEST_TIMEOUT);
        }
        if (httpSatatusCode == STATUS_CODE_400) {
            return new ResponseEntity<>(failureResponse, HttpStatus.BAD_REQUEST);
        } else if (httpSatatusCode == STATUS_CODE_404) {
            return new ResponseEntity<>(failureResponse, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(failureResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
