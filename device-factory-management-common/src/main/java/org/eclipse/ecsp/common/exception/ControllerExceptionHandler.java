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

package org.eclipse.ecsp.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.eclipse.ecsp.common.ResponseConstants;
import org.eclipse.ecsp.dto.ErrorRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;


/**
 * This class handles exceptions that occur within controllers and provides a consistent way to handle and respond to
 * them.
 * It implements the HandlerExceptionResolver interface to resolve exceptions thrown during request handling.
 */
public class ControllerExceptionHandler implements HandlerExceptionResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * Resolves the exception thrown during request processing and returns a ModelAndView object.
     * This method is responsible for handling exceptions and generating appropriate error responses.
     *
     * @param request  the HttpServletRequest object representing the current request
     * @param response the HttpServletResponse object representing the current response
     * @param handler  the handler object that handled the request
     * @param ex       the exception that was thrown during request processing
     * @return a ModelAndView object representing the error response
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                         Exception ex) {
        ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
        ExcMessage excMessage = new ExcMessage(new ErrorRest(), HttpStatus.INTERNAL_SERVER_ERROR);
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (isSpringHttpConverterError(stackTrace)) {
            excMessage.getError().setMessage("Unable to parse request (" + ex.getMessage() + ")");
            excMessage.setHttpStatus(HttpStatus.BAD_REQUEST);
        } else {
            excMessage = parseException(ex);
        }
        mv.addObject("error", excMessage.getError());
        response.setStatus(excMessage.getHttpStatus().value());
        LOGGER.error("HttpStatus(" + excMessage.getHttpStatus() + ") " + excMessage.getError().getMessage(), ex);
        return mv;
    }

    /**
     * Parses the given exception and returns an ExcMessage object containing the appropriate error message and HTTP
     * status code.
     *
     * @param e the exception to be parsed
     * @return an ExcMessage object containing the error message and HTTP status code
     */
    private ExcMessage parseException(Exception e) {
        ErrorRest error = new ErrorRest();
        if (e instanceof ServletRequestBindingException) {
            if (e.getMessage().contains(ResponseConstants.MISSING_USERID_MSG)) {
                error.setMessage(ResponseConstants.MISSING_USERID_RESPONSE);
                return new ExcMessage(error, HttpStatus.BAD_REQUEST);
            }
        } else if (e instanceof HttpMessageNotReadableException) {
            error.setMessage(ResponseConstants.INVALID_PAYLOAD_RESPONSE);
            return new ExcMessage(error, HttpStatus.BAD_REQUEST);
        } else if (e instanceof DuplicateKeyException) {
            error.setMessage(ResponseConstants.DUPLICATE_ENTRY_RESPONSE);
            return new ExcMessage(error, HttpStatus.BAD_REQUEST);
        } else if (e instanceof DataIntegrityViolationException) {
            error.setMessage(ResponseConstants.INVALID_PAYLOAD_NULL_RESPONSE);
            return new ExcMessage(error, HttpStatus.BAD_REQUEST);
        } else if (e instanceof DeleteDeviceException) {
            if (e.getMessage().contains(ResponseConstants.INVALID_CURRENT_FACTORY_DATA)) {
                error.setMessage(ResponseConstants.INVALID_CURRENT_FACTORY_DATA);
                return new ExcMessage(error, HttpStatus.NOT_FOUND);
            }
        } else if (e instanceof InvalidDeviceStateTransitionException || e instanceof InvalidDeviceStateException) {
            error.setMessage(e.getMessage());
            return new ExcMessage(error, HttpStatus.BAD_REQUEST);
        } else if (e instanceof ReplaceDeviceException) {
            ExcMessage excMessage = verifyReplaceDevice(e, error);
            if (excMessage != null) {
                return excMessage;
            }
        } else if (e instanceof UncategorizedSQLException
            && e.getMessage().contains(ResponseConstants.INVALID_DATE_FORMAT_MSG)) {
            error.setMessage(ResponseConstants.INVALID_DATE_FORMAT_RESPONSE);
            return new ExcMessage(error, HttpStatus.BAD_REQUEST);
        }
        error.setMessage("Internal Server Error (" + e.getMessage() + ")");
        return new ExcMessage(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Verifies the exception and creates an ExcMessage object based on the error message.
     *
     * @param e     The exception that occurred.
     * @param error The ErrorRest object to be populated with the error message.
     * @return An ExcMessage object representing the error and the corresponding HTTP status.
     */
    private ExcMessage verifyReplaceDevice(Exception e, ErrorRest error) {
        if (e.getMessage().contains(ResponseConstants.INVALID_CURRENT_FACTORY_DATA_FOR_REPLACE)) {
            error.setMessage(e.getMessage());
            return new ExcMessage(error, HttpStatus.NOT_FOUND);
        } else if (e.getMessage().contains(ResponseConstants.INVALID_DEVICE_REPLACEMENT_CURRENT_DATA_STATE)
            || e.getMessage().contains(ResponseConstants.INVALID_DEVICE_REPLACEMENT_REPLACE_DATA_STATE)
            || e.getMessage().contains(ResponseConstants.INVALID_REPLACE_REQUEST_DATA)
            || e.getMessage().contains(ResponseConstants.INVALID_INACTIVATED_DEVICE_FOR_REPLACEMENT)) {
            error.setMessage(e.getMessage());
            return new ExcMessage(error, HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    /**
     * Checks if the given stack trace contains any element from the Spring HTTP converter class.
     *
     * @param stackTrace the stack trace to be checked
     * @return true if the stack trace contains an element from the Spring HTTP converter class, false otherwise
     */
    private boolean isSpringHttpConverterError(StackTraceElement[] stackTrace) {
        for (StackTraceElement stackTraceElement : stackTrace) {
            if ("org.springframework.http.converter.AbstractHttpMessageConverter".equals(
                stackTraceElement.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Represents an exception message containing an error and an HTTP status.
     */
    @AllArgsConstructor
    @Data
    private class ExcMessage {
        private ErrorRest error;
        private HttpStatus httpStatus;
    }
}