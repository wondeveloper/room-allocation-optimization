package com.smarthost.allocationservice.config.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RoomQueryResponse(int usagePremium, BigDecimal revenuePremium,
                                int usageEconomy, BigDecimal revenueEconomy) {
}
