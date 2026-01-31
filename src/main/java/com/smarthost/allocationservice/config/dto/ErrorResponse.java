package com.smarthost.allocationservice.config.dto;

import java.util.List;

public record ErrorResponse(String message,
                            List<FieldErrorResponse> errorResponses) {
}
