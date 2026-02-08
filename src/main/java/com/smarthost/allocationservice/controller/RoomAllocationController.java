package com.smarthost.allocationservice.controller;

import com.smarthost.allocationservice.config.constant.Constants;
import com.smarthost.allocationservice.config.dto.RoomQueryRequest;
import com.smarthost.allocationservice.config.dto.RoomQueryResponse;
import com.smarthost.allocationservice.service.RoomAllocationServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@AllArgsConstructor
public class RoomAllocationController {

    private static final Logger logger = LoggerFactory.getLogger(RoomAllocationController.class);

    private RoomAllocationServiceImpl roomAllocationService;

    @GetMapping
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello Vivek");
    }

    @PostMapping(value = Constants.POST_OCCUPANCY, produces = "application/json")
    public ResponseEntity<RoomQueryResponse> checkOccupancy(@Valid @RequestBody RoomQueryRequest request){
        logger.info("Request {} received to allocate rooms and calculate revenue at {}",request, LocalDateTime.now());
        RoomQueryResponse roomQueryResponse = roomAllocationService.allocateRooms(request);
        logger.debug("Request: {} completed at {} with response: {}", request, LocalDateTime.now(), roomQueryResponse);
        return ResponseEntity.ok(roomQueryResponse);
    }
}
