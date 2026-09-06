package com.ruthwik.ridebooking.Dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AvailableRidesRequest {

    @NotBlank(message = "Source is required")
    private String source;

    @NotBlank(message = "Destination is required")
    private String destination;

    
    private LocalDate rideDate;

    @NotNull(message = "Required seats is required")
    @Min(value = 1, message = "Required seats must be at least 1")
    private Integer requiredSeats;
    

    // Default Constructor
    public AvailableRidesRequest() {
    }

    // Parameterized Constructor
    public AvailableRidesRequest(String source, String destination,
                                 LocalDate rideDate, Integer requiredSeats) {
        this.source = source;
        this.destination = destination;
        this.rideDate = rideDate;
        this.requiredSeats = requiredSeats;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getRideDate() {
        return rideDate;
    }

    public void setRideDate(LocalDate rideDate) {
        this.rideDate = rideDate;
    }

    public Integer getRequiredSeats() {
        return requiredSeats;
    }

    public void setRequiredSeats(Integer requiredSeats) {
        this.requiredSeats = requiredSeats;
    }
}