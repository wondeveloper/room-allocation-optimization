package com.smarthost.allocationservice.config.dto;

public record FieldErrorResponse(String fieldName,
                                 String code,
                                 String message) {
}
