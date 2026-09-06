package com.ruthwik.ridebooking.Dto;

import java.time.LocalDate;

import com.ruthwik.ridebooking.model.Duration;

public class SearchRidesAndBookingsRequest {

    private String source;
    private String destination;
    public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	private LocalDate rideDate;
    private Duration duration;

    // Default Constructor
    public SearchRidesAndBookingsRequest() {
    }

    // Parameterized Constructor
    public SearchRidesAndBookingsRequest(String source, String destination, LocalDate rideDate) {
        this.source = source;
        this.destination = destination;
        this.rideDate = rideDate;
    }

    // Getter for source
    public String getSource() {
        return source;
    }

    // Setter for source
    public void setSource(String source) {
        this.source = source;
    }

    // Getter for destination
    public String getDestination() {
        return destination;
    }

    // Setter for destination
    public void setDestination(String destination) {
        this.destination = destination;
    }

    // Getter for rideDate
    public LocalDate getRideDate() {
        return rideDate;
    }

    // Setter for rideDate
    public void setRideDate(LocalDate rideDate) {
        this.rideDate = rideDate;
    }
}