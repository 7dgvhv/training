package com.dorm.dto;

import lombok.Data;

@Data
public class RepairOrderRequest {
    private String deviceType;
    private String description;
}