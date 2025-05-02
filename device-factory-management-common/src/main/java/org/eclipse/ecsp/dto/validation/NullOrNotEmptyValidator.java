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

/**
 * Validator implementation for the custom annotation {@link NullOrNotEmpty}.
 * This validator ensures that a given string is either null or not empty.
 *
 * <p>Validation logic:
 * <ul>
 *   <li>If the string is null, it is considered valid.</li>
 *   <li>If the string is empty, it is considered invalid.</li>
 *   <li>Any non-empty string is considered valid.</li>
 * </ul>
 *
 * <p>Usage:
 * This validator is typically used in conjunction with the {@link NullOrNotEmpty} annotation
 * to enforce the validation rule on string fields in a Java Bean.
 *
 * <p>Example:
 * <pre>
 * {@code
 * @NullOrNotEmpty
 * private String exampleField;
 * }
 * </pre>
 *
 * @see NullOrNotEmpty
 * @see jakarta.validation.ConstraintValidator
 */
public class NullOrNotEmptyValidator implements ConstraintValidator<NullOrNotEmpty, String> {

    @Override
    public void initialize(NullOrNotEmpty parameters) {
        //Nothing to do here
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        } else if (value.isEmpty()) {
            return false;
        }
        return true;
    }
}
