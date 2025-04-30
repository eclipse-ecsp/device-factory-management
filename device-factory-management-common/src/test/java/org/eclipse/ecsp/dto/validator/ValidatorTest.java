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

package org.eclipse.ecsp.dto.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.eclipse.ecsp.dto.validation.Validator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit test class for the {@link Validator} class.
 * This class tests the functionality of the Validator's `isValid` method.
 * 
 * <p>Test Scenarios:</p>
 * <ul>
 *   <li>isValidTestWithAllowedValue: Verifies that the `isValid` method returns true when the input value is null.</li>
 * </ul>
 * 
 * <p>Annotations:</p>
 * <ul>
 *   <li>{@code @InjectMocks}: Injects the mock dependencies into the Validator instance.</li>
 *   <li>{@code @Mock}: Mocks the {@link ConstraintValidatorContext} dependency.</li>
 *   <li>{@code @Before}: Sets up the test environment by initializing mocks before each test.</li>
 *   <li>{@code @Test}: Marks the method as a test case.</li>
 * </ul>
 * 
 * <p>Dependencies:</p>
 * <ul>
 *   <li>{@link Validator}: The class under test.</li>
 *   <li>{@link ConstraintValidatorContext}: Mocked dependency used in the validation process.</li>
 * </ul>
 */
public class ValidatorTest {

    @InjectMocks
    Validator validator;

    private String propName;
    private String message;
    private List<String> allowable;

    @Mock
    private ConstraintValidatorContext context;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void isValidTestWithAllowedValue() {
        String value = null;
        boolean result = validator.isValid(value, context);
        Assert.assertTrue(result);
    }

}
