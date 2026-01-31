package com.smarthost.allocationservice.service;

import com.smarthost.allocationservice.config.dto.RoomQueryRequest;
import com.smarthost.allocationservice.config.dto.RoomQueryResponse;

public sealed interface RoomAllocationService permits RoomAllocationServiceImpl{

    RoomQueryResponse allocateRooms(RoomQueryRequest request);
}
