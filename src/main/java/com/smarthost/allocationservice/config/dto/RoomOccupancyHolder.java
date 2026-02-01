package com.smarthost.allocationservice.config.dto;

import java.math.BigDecimal;

public record RoomOccupancyHolder(
        Long totalPremiumRooms,
        Long totalEconomyRooms,
        BigDecimal totalPremiumRevenue,
        BigDecimal totalEconomyRevenue
) {

    public static RoomOccupancyHolder of(Long premiumRooms, Long economyRooms) {
        return new RoomOccupancyHolder(
                premiumRooms,
                economyRooms,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    public RoomOccupancyHolder withPremiumBooking(BigDecimal price) {
        return new RoomOccupancyHolder(
                totalPremiumRooms - 1,
                totalEconomyRooms,
                totalPremiumRevenue.add(price),
                totalEconomyRevenue
        );
    }

    public RoomOccupancyHolder withEconomyBooking(BigDecimal price) {
        return new RoomOccupancyHolder(
                totalPremiumRooms,
                totalEconomyRooms - 1,
                totalPremiumRevenue,
                totalEconomyRevenue.add(price)
        );
    }
}
