package com.smarthost.allocationservice.service;

import com.smarthost.allocationservice.config.dto.RoomOccupancyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.PriorityQueue;

public class EconomyRoomAllocator {

    private static final Logger logger = LoggerFactory.getLogger(EconomyRoomAllocator.class);

    public static void allocateEconomy(RoomOccupancyHolder roomOccupancyHolder, PriorityQueue<BigDecimal> demand){
        logger.info("Economy room allocation started");
        Long totalEconomyRoomsRemaining = roomOccupancyHolder.getTotalEconomyRooms();
        while (totalEconomyRoomsRemaining > 0 && !demand.isEmpty()){
            roomOccupancyHolder.decrementEconomyRooms();
            roomOccupancyHolder.incrementEconomyRevenue(demand.poll());
            totalEconomyRoomsRemaining--;
        }
        logger.info("Economy room allocation completed with total remaining rooms: {}", totalEconomyRoomsRemaining);

    }
}
