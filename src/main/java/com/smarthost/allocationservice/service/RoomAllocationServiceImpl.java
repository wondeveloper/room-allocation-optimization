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
        Map<RoomType, PriorityQueue<BigDecimal>> roomTypePriorityQueueMap = getRoomTypePriorityQueueMap(request);

        final RoomOccupancyHolder roomOccupancyHolder = RoomOccupancyHolder.of(request.premiumRooms(),request.economyRooms());

        //Premium room allocation
        RoomOccupancyHolder premiuimRoomOccupancyHolder = PremiumRoomAllocator.allocatePremium(roomOccupancyHolder, roomTypePriorityQueueMap.get(RoomType.PREMIUM));

        //Economy rooms allocation
        RoomOccupancyHolder economyRoomOccupancyHolder = EconomyRoomAllocator.allocateEconomy(premiuimRoomOccupancyHolder,roomTypePriorityQueueMap.get(RoomType.ECONOMY));

        logger.info("Smart upgrade started");
        //Smart upgrade
        RoomOccupancyHolder premiumRoomSmartUpgradeOccupancyHolder = PremiumRoomAllocator.allocatePremium(economyRoomOccupancyHolder,roomTypePriorityQueueMap.get(RoomType.ECONOMY));

        logger.debug("Rooms allocation finished for current request");
        return QueryResponseMapper.fromRoomOccupancyHolder(request.premiumRooms(), request.economyRooms(),premiumRoomSmartUpgradeOccupancyHolder);
    }

    private Map<RoomType, PriorityQueue<BigDecimal>> getRoomTypePriorityQueueMap(RoomQueryRequest request) {
        return request.potentialGuests().stream()
                .collect(Collectors.groupingBy(quote -> quote.compareTo(new BigDecimal(premiumAmountThreshold)) >= 0 ? RoomType.PREMIUM : RoomType.ECONOMY,
                        () -> {
                            Map<RoomType, PriorityQueue<BigDecimal>> initialMap = new EnumMap<>(RoomType.class);
                            initialMap.put(RoomType.PREMIUM, new PriorityQueue<>(Collections.reverseOrder()));
                            initialMap.put(RoomType.ECONOMY, new PriorityQueue<>(Collections.reverseOrder()));
                            return initialMap;
                        },
                        Collectors.toCollection(() ->new PriorityQueue<>(Collections.reverseOrder()))));
    }
}
