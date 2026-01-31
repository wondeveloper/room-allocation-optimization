package com.smarthost.allocationservice.config.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomOccupancyHolder {

    private Long totalPremiumRooms;
    private Long totalEconomyRooms;
    private BigDecimal totalPremiumRevenue;
    private BigDecimal totalEconomyRevenue;

    public RoomOccupancyHolder(Long totalPremiumRooms, Long totalEconomyRooms) {
        this.totalPremiumRooms = totalPremiumRooms;
        this.totalEconomyRooms = totalEconomyRooms;
        this.totalPremiumRevenue = new BigDecimal(0);
        this.totalEconomyRevenue = new BigDecimal(0);
    }

    public void decrementPremiumRooms(){
        this.totalPremiumRooms -=1;
    }

    public void decrementEconomyRooms(){
        this.totalEconomyRooms -=1;
    }

    public void incrementPremiumRevenue(BigDecimal value){
        this.totalPremiumRevenue = this.totalPremiumRevenue.add(value);
    }

    public void incrementEconomyRevenue(BigDecimal value){
        this.totalEconomyRevenue = this.totalEconomyRevenue.add(value);
    }
}
