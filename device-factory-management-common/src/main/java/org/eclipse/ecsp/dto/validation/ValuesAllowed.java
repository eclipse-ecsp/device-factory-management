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

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to validate that a field or parameter contains a value from a predefined set of allowed values.
 * 
 * <p>This annotation can be applied to fields or method parameters to ensure that their values
 * match one of the specified allowed values. The validation logic is implemented in the associated
 * {@code Validator} class.
 * 
 * <p>Example usage:
 * <pre>
 * {@code
 * @ValuesAllowed(propName = "status", values = {"ACTIVE", "INACTIVE"})
 * private String status;
 * }
 * </pre>
 * 
 * <p>Attributes:
 * <ul>
 *   <li>{@code message}: Custom error message to be displayed when validation fails. Defaults to
 *       "{jakarta.validation.constraints.ValuesAllowed.message}".</li>
 *   <li>{@code groups}: Groups for categorizing constraints. Defaults to an empty array.</li>
 *   <li>{@code payload}: Payload for clients to specify additional metadata. Defaults to an empty array.</li>
 *   <li>{@code propName}: The name of the property being validated. This is used for error reporting.</li>
 *   <li>{@code values}: The array of allowed values for the property.</li>
 * </ul>
 * 
 * <p>Note: This annotation requires a corresponding {@code Validator} implementation to perform
 * the actual validation logic.
 *
 * @see jakarta.validation.Constraint
 * @see jakarta.validation.Payload
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = Validator.class)
public @interface ValuesAllowed {

    /**
     * Specifies the default validation message to be used when the constraint is violated.
     * The message can be customized by providing a different value or using a message key
     * for internationalization purposes.
     *
     * @return The default validation message or a message key for localization.
     */
    String message() default "{jakarta.validation.constraints.ValuesAllowed.message}";
    
    
    /**
     * Specifies the validation groups the constraint belongs to.
     * This can be used to apply the constraint to specific validation groups.
     *
     * @return an array of classes representing the validation groups
     */
    Class<?>[] groups() default { };


    /**
     * Specifies an array of custom payload classes that can be used by clients of the 
     * Bean Validation API to assign custom payload objects to a constraint. 
     * These payload objects are typically used to carry metadata information 
     * consumed by a validation client.
     *
     * @return an array of {@code Class} objects extending {@code Payload}.
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Specifies the name of the property being validated. This is used for error reporting.
     *
     * @return The name of the property being validated.
     */
    String propName();

    /**
     * Specifies the array of allowed values for the property. The value of the property being validated
     * must match one of these allowed values for the validation to pass.
     *
     * @return An array of allowed values for the property.
     */
    String[] values();
}
