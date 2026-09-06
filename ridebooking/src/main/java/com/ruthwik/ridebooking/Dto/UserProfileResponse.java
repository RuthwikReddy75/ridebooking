package com.ruthwik.ridebooking.Dto;

public class UserProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String profilePhoto;
    private String vehicleName;
    private String vehicleNumber;
    private String vehicleType;
    private Integer totalRidesCreated;
    private Integer totalBookingsDone;

    public UserProfileResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
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

    public Integer getTotalRidesCreated() {
        return totalRidesCreated;
    }

    public void setTotalRidesCreated(Integer totalRidesCreated) {
        this.totalRidesCreated = totalRidesCreated;
    }

    public Integer getTotalBookingsDone() {
        return totalBookingsDone;
    }

    public void setTotalBookingsDone(Integer totalBookingsDone) {
        this.totalBookingsDone = totalBookingsDone;
    }
}