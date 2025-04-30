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

package org.eclipse.ecsp.service.swm;

import org.eclipse.ecsp.common.exception.ApiTechnicalException;

/**
 * The IswmCrudService interface provides methods for creating, updating, and deleting vehicles.
 *
 * @param <I> the input type for the vehicle operations
 */
public interface IswmCrudService<I> {

    /**
     * Creates a new vehicle.
     *
     * @param input the input object representing the vehicle to be created
     * @return true if the vehicle is created successfully, false otherwise
     * @throws ApiTechnicalException if an error occurs during the creation process
     */
    boolean createVehicle(I input) throws ApiTechnicalException;

    /**
     * Updates an existing vehicle.
     *
     * @param input the input object representing the vehicle to be updated
     * @return true if the vehicle is updated successfully, false otherwise
     * @throws ApiTechnicalException if an error occurs during the update process
     */
    boolean updateVehicle(I input) throws ApiTechnicalException;

    /**
     * Deletes a vehicle.
     *
     * @param input the input object representing the vehicle to be deleted
     * @return true if the vehicle is deleted successfully, false otherwise
     * @throws ApiTechnicalException if an error occurs during the deletion process
     */
    boolean deleteVehicle(I input) throws ApiTechnicalException;
}
