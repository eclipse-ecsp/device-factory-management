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

package org.eclipse.ecsp.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code Validator} class is a custom implementation of the {@link ConstraintValidator}
 * interface, used for validating whether a given string value is part of a predefined set
 * of allowable values. This validator is typically used in conjunction with the {@code @ValuesAllowed}
 * annotation to enforce constraints on string properties.
 *
 * <p>Key Features:</p>
 * <ul>
 *   <li>Validates that a string value is either null or part of a predefined list of allowable values.</li>
 *   <li>Customizes validation error messages to include the property name and allowable values.</li>
 * </ul>
 *
 * <p>Usage:</p>
 * <pre>
 * {@code
 * @ValuesAllowed(
 *     propName = "status",
 *     values = {"ACTIVE", "INACTIVE"},
 *     message = "Invalid status. Allowed values are: "
 * )
 * private String status;
 * }
 * </pre>
 *
 * <p>Implementation Details:</p>
 * <ul>
 *   <li>The {@code initialize} method initializes the validator with the annotation's properties.</li>
 *   <li>The {@code isValid} method performs the actual validation and constructs a custom error message
 *       if the validation fails.</li>
 * </ul>
 *
 * @author Akshay
 * @see ConstraintValidator
 * @see ValuesAllowed
 */
public class Validator implements ConstraintValidator<ValuesAllowed, String> {

    private String propName;
    private String message;
    private List<String> allowable;

    @Override
    public void initialize(ValuesAllowed requiredIfChecked) {
        this.propName = requiredIfChecked.propName();
        this.message = requiredIfChecked.message();
        this.allowable = Arrays.asList(requiredIfChecked.values());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean valid = value == null || this.allowable.contains(value);

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message.concat(this.allowable.toString()))
                    .addPropertyNode(this.propName).addConstraintViolation();
        }
        return valid;
    }
}
