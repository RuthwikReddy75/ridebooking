package com.ruthwik.ridebooking.controller;

import org.springframework.context.ApplicationContext;

import java.util.Optional;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruthwik.ridebooking.Dto.CreateRideRequest;
import com.ruthwik.ridebooking.Repository.UserRepository;
import com.ruthwik.ridebooking.model.User;
import com.ruthwik.ridebooking.service.CustomUserDetailsService;
import com.ruthwik.ridebooking.service.RideService;

import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
@RestController
@RequestMapping("/rides")
public class RideController {
	
	@Autowired
	private RideService rideService;
	
	 @Autowired
	 private ApplicationContext context;


	@Autowired
	private UserRepository userRepo;
	
	
  
	@PostMapping("/createride")
	public ResponseEntity<String> getRides(@Valid @RequestBody CreateRideRequest createRideRequest)
	{
		
		System.out.println("in create ride method in controller");
		return rideService.createRide(createRideRequest);
	}
    
}
