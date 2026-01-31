package com.smarthost.allocationservice.config.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record RoomQueryRequest (

    @NotNull
    @PositiveOrZero
    Long premiumRooms,

    @NotNull
    @PositiveOrZero
    Long economyRooms,

    @NotEmpty List<@NotNull @PositiveOrZero BigDecimal> potentialGuests){
}
