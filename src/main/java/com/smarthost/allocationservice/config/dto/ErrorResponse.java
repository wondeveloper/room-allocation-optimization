package com.smarthost.allocationservice.config.dto;

import java.util.List;

/**
 * Error response can be returned from the controller in case the validation fails.
 * @param message
 * @param errorResponses
 */
public record ErrorResponse(String message,
                            List<FieldErrorResponse> errorResponses) {
}
