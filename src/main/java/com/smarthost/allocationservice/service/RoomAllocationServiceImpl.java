package com.smarthost.allocationservice.service;

import com.smarthost.allocationservice.config.dto.RoomOccupancyHolder;
import com.smarthost.allocationservice.config.dto.RoomQueryRequest;
import com.smarthost.allocationservice.config.dto.RoomQueryResponse;
import com.smarthost.allocationservice.config.dto.RoomType;
import com.smarthost.allocationservice.config.mapper.QueryResponseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
public non-sealed class RoomAllocationServiceImpl implements RoomAllocationService {

    private static final Logger logger = LoggerFactory.getLogger(RoomAllocationServiceImpl.class);

    @Value("${premium.room.threshold.amount}")
    private Long premiumAmountThreshold;

    @Override
    public RoomQueryResponse allocateRooms(RoomQueryRequest request) {
        logger.debug("Allocating rooms for premium and economy client requests with premium room cut off amount: {}", premiumAmountThreshold);
        Map<RoomType, PriorityQueue<BigDecimal>> map = request.potentialGuests().stream()
                .collect(Collectors.groupingBy(quote -> quote.compareTo(new BigDecimal(premiumAmountThreshold)) >= 0 ? RoomType.PREMIUM : RoomType.ECONOMY,
                        Collectors.toCollection(() ->new PriorityQueue<>(Collections.reverseOrder()))));

        final RoomOccupancyHolder roomOccupancyHolder = new RoomOccupancyHolder(request.premiumRooms(),request.economyRooms());

        //Premium room allocation
        PremiumRoomAllocator.allocatePremium(roomOccupancyHolder, map.get(RoomType.PREMIUM));

        //Economy rooms allocation
        EconomyRoomAllocator.allocateEconomy(roomOccupancyHolder,map.get(RoomType.ECONOMY));

        logger.info("Smart upgrade started");
        //Smart upgrade
        PremiumRoomAllocator.allocatePremium(roomOccupancyHolder,map.get(RoomType.ECONOMY));

        logger.debug("Rooms allocation finished for current request");
        return QueryResponseMapper.fromRoomOccupancyHolder(request.premiumRooms(), request.economyRooms(),roomOccupancyHolder);
    }
}
