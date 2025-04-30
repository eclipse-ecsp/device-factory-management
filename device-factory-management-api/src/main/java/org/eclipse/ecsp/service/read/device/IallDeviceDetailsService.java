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

package org.eclipse.ecsp.service.read.device;

/**
 * This interface defines the contract for retrieving all factory data for a device.
 *
 * @param <I> the input type for retrieving factory data
 * @param <O> the output type for retrieving factory data
 */
public interface IallDeviceDetailsService<I, O> {

    /**
     * Retrieves all factory data for a device based on the provided input and input type.
     *
     * @param t the input object for retrieving factory data
     * @param inputType the input type(s) for retrieving factory data
     * @return the output object containing the retrieved factory data
     */
    O findAllFactoryData(I t, String... inputType);
}
