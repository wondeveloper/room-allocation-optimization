package com.smarthost.allocationservice.service;

import com.smarthost.allocationservice.config.dto.RoomOccupancyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.PriorityQueue;

public class PremiumRoomAllocator {

    private static final Logger logger = LoggerFactory.getLogger(PremiumRoomAllocator.class);

    public static void allocatePremium(final RoomOccupancyHolder roomOccupancyHolder, PriorityQueue<BigDecimal> demand){
        logger.debug("Premium room allocation started");
        Long totalPremiumRoomsRemaining = roomOccupancyHolder.getTotalPremiumRooms();
        while (totalPremiumRoomsRemaining > 0 && !demand.isEmpty()){
            roomOccupancyHolder.decrementPremiumRooms();
            roomOccupancyHolder.incrementPremiumRevenue(demand.poll());
            totalPremiumRoomsRemaining--;
        }
        logger.info("Premium room allocation completed with total remaining rooms: {}", totalPremiumRoomsRemaining);
    }
}
