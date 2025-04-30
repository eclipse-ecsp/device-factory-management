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

package org.eclipse.ecsp.util;

import org.eclipse.ecsp.common.ApiResponse;
import org.eclipse.ecsp.webutil.WebUtils;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.eclipse.ecsp.common.enums.ApiMessageEnum.FACTORY_DATA_CREATE_SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Test class for WebUtils.
 */
public class WebUtilsTest {
    private static final int STATUS_CODE_200 = 200;

    @Test
    public void getResponseEntity() {
        ApiResponse<Object> apiResponse = new ApiResponse.Builder<>(FACTORY_DATA_CREATE_SUCCESS.getCode(),
            FACTORY_DATA_CREATE_SUCCESS.getMessage(),
            HttpStatus.OK).build();
        ResponseEntity<ApiResponse<Object>> responseEntity = WebUtils.getResponseEntity(apiResponse);

        assertNotNull(responseEntity);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCode().value());
        assertEquals(FACTORY_DATA_CREATE_SUCCESS.getCode(), Objects.requireNonNull(responseEntity.getBody()).getCode());
        assertEquals(FACTORY_DATA_CREATE_SUCCESS.getMessage(), responseEntity.getBody().getMessage());
    }

    @Test
    public void getResponseEntityWithData() {
        ApiResponse<Object> apiResponse = new ApiResponse.Builder<>(FACTORY_DATA_CREATE_SUCCESS.getCode(),
            FACTORY_DATA_CREATE_SUCCESS.getMessage(),
            HttpStatus.OK).withData("Hello").build();
        ResponseEntity<ApiResponse<Object>> responseEntity = WebUtils.getResponseEntity(apiResponse);

        assertNotNull(responseEntity);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCode().value());
        assertEquals(FACTORY_DATA_CREATE_SUCCESS.getCode(), Objects.requireNonNull(responseEntity.getBody()).getCode());
        assertEquals(FACTORY_DATA_CREATE_SUCCESS.getMessage(), responseEntity.getBody().getMessage());
        assertEquals("Hello", responseEntity.getBody().getData());
    }
}
