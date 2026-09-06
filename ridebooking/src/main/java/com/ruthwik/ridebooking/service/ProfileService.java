package com.ruthwik.ridebooking.service;

import org.springframework.stereotype.Service;

import com.ruthwik.ridebooking.Dto.UpdateProfileRequest;
import com.ruthwik.ridebooking.Dto.UserProfileResponse;
import com.ruthwik.ridebooking.Repository.UserRepository;
import com.ruthwik.ridebooking.model.User;
import com.ruthwik.ridebooking.utilities.UserDetailsUtility;

@Service
public class ProfileService {
	 private final UserRepository userRepository;
	 
	 private final UserDetailsUtility userDetailsUtility;

	    public ProfileService(UserRepository userRepository,UserDetailsUtility userDetailsUtility) {
	        this.userRepository = userRepository;
	        this.userDetailsUtility=userDetailsUtility;
	    }
	    
	    public UserProfileResponse getProfile() {

	        User user=userDetailsUtility.getUser();
	        UserProfileResponse response = new UserProfileResponse();

	        response.setId(user.getId());
	        response.setName(user.getName());
	        response.setEmail(user.getEmail());
	        response.setPhone(user.getPhone());
	        response.setProfilePhoto(user.getProfilePhoto());
	        response.setVehicleName(user.getVehicleName());
	        response.setVehicleNumber(user.getVehicleNumber());
	        response.setVehicleType(user.getVehicleType());
	        response.setTotalRidesCreated(user.getTotalRidesCreated());
	        response.setTotalBookingsDone(user.getTotalBookingsDone());

	        return response;
	    }
	    
	    public String updateProfile(UpdateProfileRequest request) {


	    
	        User user = userDetailsUtility.getUser();
	        user.setName(request.getName());
	        user.setPhone(request.getPhone());
	        user.setProfilePhoto(request.getProfilePhoto());
	        user.setVehicleName(request.getVehicleName());
	        user.setVehicleNumber(request.getVehicleNumber());
	        user.setVehicleType(request.getVehicleType());

	        User updatedUser = userRepository.save(user);

	        return "Updated succesfully";
	    }
	    
	    
	    


}
