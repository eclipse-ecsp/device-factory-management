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
import org.eclipse.ecsp.dto.validation.NullOrNotEmptyValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for the {@link NullOrNotEmptyValidator} class.
 * This test class verifies the behavior of the isValid method in various scenarios.
 * 
 * <p>Test Scenarios:</p>
 * <ul>
 *   <li>{@code isValidTestWithNullValue}: Ensures that the validator returns true when the input value is null.</li>
 *   <li>{@code isValidTestWithNotEmptyValue}: Ensures that the validator returns true when the input value is 
 *   a non-empty string.</li>
 *   <li>{@code isValidTestWithEmptyValue}: Ensures that the validator returns false when the input value is 
 *   an empty string.</li>
 * </ul>
 * 
 * <p>Dependencies:</p>
 * <ul>
 *   <li>{@link NullOrNotEmptyValidator}: The class being tested.</li>
 *   <li>{@link ConstraintValidatorContext}: Mocked context used during validation.</li>
 * </ul>
 * 
 * <p>Setup:</p>
 * <ul>
 *   <li>The {@code setUp} method initializes the mocks using {@code initMocks}.</li>
 * </ul>
 */
public class NullOrNotEmptyValidatorTest {

    @InjectMocks
    private NullOrNotEmptyValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void isValidTestWithNullValue() {
        boolean result = validator.isValid(null, context);
        Assert.assertTrue(result);
    }

    @Test
    public void isValidTestWithNotEmptyValue() {
        boolean result = validator.isValid("xyz", context);
        Assert.assertTrue(result);
    }

    @Test
    public void isValidTestWithEmptyValue() {
        boolean result = validator.isValid("", context);
        Assert.assertFalse(result);
    }

}
