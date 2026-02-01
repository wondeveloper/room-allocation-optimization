package com.smarthost.allocationservice.service;

import com.smarthost.allocationservice.config.dto.RoomOccupancyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.stream.Stream;

public class EconomyRoomAllocator {

    private static final Logger logger = LoggerFactory.getLogger(EconomyRoomAllocator.class);

    public static RoomOccupancyHolder allocateEconomy(final RoomOccupancyHolder roomOccupancyHolder, PriorityQueue<BigDecimal> demand){
        logger.debug("Economy room allocation started");
        RoomOccupancyHolder currentHolder = Stream.generate(demand::poll)
                //max rooms allocated must not be should be queue size
                .limit(Math.min(roomOccupancyHolder.totalEconomyRooms(), demand.size()))
                .filter(Objects::nonNull)
                .reduce(
                        roomOccupancyHolder,
                        RoomOccupancyHolder::withEconomyBooking,
                        (h1, h2) -> h2
                );
        logger.info("Economy room allocation completed with total remaining rooms: {}", currentHolder.totalEconomyRooms());
        return currentHolder;
    }
}
