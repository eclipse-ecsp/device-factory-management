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

import org.eclipse.ecsp.common.exception.InputParamValidationException;
import org.eclipse.ecsp.common.util.Utils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for Utils.
 *
 * @author ayush agrahari
 */
public class UtilsTest {

    @Test
    public void validateType() {
        Assert.assertFalse(Utils.validateAllowedType("dashcam1", new String[]{"dashcam"}));
    }

    @Test
    public void validateTypeSuccess() {
        Assert.assertTrue(Utils.validateAllowedType("dashcam", new String[]{"dashcam"}));
    }

    @Test(expected = InputParamValidationException.class)
    public void validateImei_Empty() {
        Utils.validateImei(" ");
    }

    @Test(expected = InputParamValidationException.class)
    public void validateImei_NonNumeric() {
        Utils.validateImei("abcdef");
    }

    @Test(expected = InputParamValidationException.class)
    public void validateImeiInvalidLengthTest() {
        Utils.validateImei("93");
    }

    @Test(expected = InputParamValidationException.class)
    public void validateImeiInvalidTypeTest() {
        Utils.validateImei("9374abCD75537");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateSerialNumber_Empty() {
        Utils.validateSerialNumber(" ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateSerialNumber_NonNumeric() {
        Utils.validateSerialNumber("$$$$$");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateSerialNumberInvalidLengthTest() {
        Utils.validateSerialNumber("12");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateSerialNumberInvalidTypeTest() {
        Utils.validateSerialNumber("120 7");
    }
}
