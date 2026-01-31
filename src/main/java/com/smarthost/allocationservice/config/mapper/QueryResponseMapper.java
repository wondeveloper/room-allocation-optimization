package com.smarthost.allocationservice.config.mapper;

import com.smarthost.allocationservice.config.dto.RoomOccupancyHolder;
import com.smarthost.allocationservice.config.dto.RoomQueryResponse;

public class QueryResponseMapper {

    public static RoomQueryResponse fromRoomOccupancyHolder(Long premiumRooms,
                                                            Long economyRooms,
                                                            RoomOccupancyHolder roomOccupancyHolder){
        return RoomQueryResponse.builder()
                .usagePremium(premiumRooms - roomOccupancyHolder.getTotalPremiumRooms())
                .usageEconomy(economyRooms - roomOccupancyHolder.getTotalEconomyRooms())
                .revenueEconomy(roomOccupancyHolder.getTotalEconomyRevenue())
                .revenuePremium(roomOccupancyHolder.getTotalPremiumRevenue())
                .build();
    }
}
