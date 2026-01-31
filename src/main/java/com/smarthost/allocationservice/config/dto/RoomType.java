package com.smarthost.allocationservice.config.dto;

import lombok.Getter;

@Getter
public enum RoomType {

    PREMIUM("Available for EUR 100 and more"),
    ECONOMY("Available for less than EUR 100");

    private final String details;

    RoomType(String details) {
        this.details = details;
    }

}
