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
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Custom annotation to validate that a field is either null or not empty.
 * This annotation can be applied to methods, fields, annotation types, 
 * constructors, parameters, and type use.
 * 
 * <p>
 * The validation logic is implemented in the {@code NullOrNotEmptyValidator} class.
 * </p>
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * @NullOrNotEmpty
 * private String exampleField;
 * }
 * </pre>
 * 
 *
 * <p>Attributes:
 * <ul>
 *   <li><b>message:</b> Custom error message to be used when validation fails. 
 *       Defaults to "{jakarta.validation.constraints.NullOrNotEmpty.message}".</li>
 *   <li><b>groups:</b> Allows grouping of constraints.</li>
 *   <li><b>payload:</b> Can be used by clients to assign custom payload objects 
 *       to a constraint.</li>
 * </ul>
 *
 * <p>This annotation is retained at runtime and is documented in the generated Javadoc.
 *
 * @see NullOrNotEmptyValidator
 * @author Your Name
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = NullOrNotEmptyValidator.class)
public @interface NullOrNotEmpty {
    /**
     * Specifies the default validation message to be used when the annotated 
     * element does not satisfy the NullOrNotEmpty constraint. The message can 
     * be customized using a message key in a resource bundle.
     *
     * @return the default validation message
     */
    String message() default "{jakarta.validation.constraints.NullOrNotEmpty.message}";

    /**
     * Specifies the validation groups for which this constraint is applicable.
     *
     * @return an array of classes representing the validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Specifies an array of custom payload classes that can be used by clients of the
     * Bean Validation API to assign custom metadata to a constraint. This metadata
     * can be used by validation clients to interpret or process the constraint in
     * a specific way.
     *
     * @return an array of {@code Class} objects representing the custom payload types.
     */
    Class<? extends Payload>[] payload() default {};
}
