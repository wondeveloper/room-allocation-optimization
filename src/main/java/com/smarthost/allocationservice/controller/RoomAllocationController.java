package com.smarthost.allocationservice.controller;

import com.smarthost.allocationservice.config.dto.RoomQueryRequest;
import com.smarthost.allocationservice.config.dto.RoomQueryResponse;
import com.smarthost.allocationservice.service.RoomAllocationServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomAllocationController {

    private static final Logger logger = LoggerFactory.getLogger(RoomAllocationController.class);

    @Autowired
    private RoomAllocationServiceImpl roomAllocationService;

    @GetMapping("/")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello");
    }

    @PostMapping(value = "/occupancy", produces = "application/json")
    public ResponseEntity<RoomQueryResponse> checkOccupancy(@Valid @RequestBody RoomQueryRequest request){
        logger.info("Request received to allocate rooms and calculate revenue at {}", LocalDateTime.now());
        RoomQueryResponse roomQueryResponse = roomAllocationService.allocateRooms(request);
        logger.debug("Request completed at {}", LocalDateTime.now());
        return ResponseEntity.ok(roomQueryResponse);
    }
}
