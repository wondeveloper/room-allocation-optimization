package com.smarthost.allocationservice.service;

import com.smarthost.allocationservice.config.dto.RoomOccupancyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.stream.Stream;

public class PremiumRoomAllocator {

    private static final Logger logger = LoggerFactory.getLogger(PremiumRoomAllocator.class);

    public static RoomOccupancyHolder allocatePremium(final RoomOccupancyHolder roomOccupancyHolder, PriorityQueue<BigDecimal> demand){
        logger.debug("Premium room allocation started");
        RoomOccupancyHolder currentHolder = Stream.generate(demand::poll)
                //max rooms allocated must not be should be queue size
                .limit(Math.min(roomOccupancyHolder.totalPremiumRooms(), demand.size()))
                .filter(Objects::nonNull)
                .reduce(
                        roomOccupancyHolder,
                        RoomOccupancyHolder::withPremiumBooking,
                        (h1, h2) -> h2
                );
        logger.info("Premium room allocation completed with total remaining rooms: {}", currentHolder.totalPremiumRooms());
        return currentHolder;
    }
}
