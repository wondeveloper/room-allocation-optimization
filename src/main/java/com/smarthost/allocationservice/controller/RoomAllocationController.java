package com.smarthost.allocationservice.controller;

import com.smarthost.allocationservice.config.dto.RoomQueryRequest;
import com.smarthost.allocationservice.config.dto.RoomQueryResponse;
import com.smarthost.allocationservice.service.RoomAllocationServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/rooms")
public class RoomAllocationController {

    @Autowired
    private RoomAllocationServiceImpl roomAllocationService;

    @GetMapping("/")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello");
    }

    @PostMapping(value = "/occupancy", produces = "application/json")
    public ResponseEntity<RoomQueryResponse> checkOccupancy(@Valid @RequestBody RoomQueryRequest request){
        RoomQueryResponse roomQueryResponse = roomAllocationService.allocateRooms(request);
        return ResponseEntity.ok(roomQueryResponse);
    }
}
