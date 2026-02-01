package com.smarthost.allocationservice.config.mapper;

import com.smarthost.allocationservice.config.dto.RoomOccupancyHolder;
import com.smarthost.allocationservice.config.dto.RoomQueryResponse;

public class QueryResponseMapper {

    public static RoomQueryResponse fromRoomOccupancyHolder(int premiumRooms,
                                                            int economyRooms,
                                                            RoomOccupancyHolder roomOccupancyHolder){
        return RoomQueryResponse.builder()
                .usagePremium(premiumRooms - roomOccupancyHolder.totalPremiumRooms())
                .usageEconomy(economyRooms - roomOccupancyHolder.totalEconomyRooms())
                .revenueEconomy(roomOccupancyHolder.totalEconomyRevenue())
                .revenuePremium(roomOccupancyHolder.totalPremiumRevenue())
                .build();
    }
}
