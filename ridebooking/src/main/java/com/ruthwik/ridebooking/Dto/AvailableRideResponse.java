package com.ruthwik.ridebooking.Dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AvailableRideResponse {

    private Long rideId;

    private String source;

    private String destination;

    private LocalDate rideDate;

    private LocalDateTime rideTime;

    private Double pricePerSeat;

    private Integer availableSeats;

    private Long driverId;

    private String driverName;

    private String driverMobileNumber;

    private String vehicleName;

    private String vehicleNumber;

    private String vehicleType;

    private String description;

    private List<RideStopResponse> rideStops;
    
    private String  yourSource;
    
    private String yourDestination;

    public String getYourSource() {
		return yourSource;
	}

	public void setYourSource(String yourSource) {
		this.yourSource = yourSource;
	}

	public String getYourDestination() {
		return yourDestination;
	}

	public void setYourDestination(String yourDestination) {
		this.yourDestination = yourDestination;
	}

	public AvailableRideResponse() {
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
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

    public LocalDateTime getRideTime() {
        return rideTime;
    }

    public void setRideTime(LocalDateTime rideTime) {
        this.rideTime = rideTime;
    }

    public Double getPricePerSeat() {
        return pricePerSeat;
    }

    public void setPricePerSeat(Double pricePerSeat) {
        this.pricePerSeat = pricePerSeat;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverMobileNumber() {
        return driverMobileNumber;
    }

    public void setDriverMobileNumber(String driverMobileNumber) {
        this.driverMobileNumber = driverMobileNumber;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<RideStopResponse> getRideStops() {
        return rideStops;
    }

    public void setRideStops(List<RideStopResponse> rideStops) {
        this.rideStops = rideStops;
    }
}