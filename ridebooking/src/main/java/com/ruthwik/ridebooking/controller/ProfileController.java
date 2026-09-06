package com.ruthwik.ridebooking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruthwik.ridebooking.Dto.UpdateProfileRequest;
import com.ruthwik.ridebooking.Dto.UserProfileResponse;
import com.ruthwik.ridebooking.service.ProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/profile")
public class ProfileController {
	
	
	private ProfileService profileService;
	public  ProfileController(ProfileService profileService)
	{
		this.profileService=profileService;
	}
	@GetMapping("/details")
	public ResponseEntity<UserProfileResponse> getDetails()
	{
		return new ResponseEntity<>(profileService.getProfile(),HttpStatus.OK);
	}

	  @PutMapping("/update")
	    public ResponseEntity<String> updateProfile(
	            @Valid @RequestBody UpdateProfileRequest request) {
	
	        String response = profileService.updateProfile(request);
	
	        return ResponseEntity.ok(response);
	    }
	

}
