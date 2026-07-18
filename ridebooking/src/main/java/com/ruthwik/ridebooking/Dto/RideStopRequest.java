package com.ruthwik.ridebooking.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class RideStopRequest {

    @NotBlank(message = "Stop name is required")
    private String stopName;

    @Min(value = 1, message = "Stop order must be at least 1")
    private int stopOrder;

    // Getters and Setters

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(int stopOrder) {
        this.stopOrder = stopOrder;
    }
}
