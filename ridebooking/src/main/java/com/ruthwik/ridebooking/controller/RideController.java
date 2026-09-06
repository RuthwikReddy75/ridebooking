package com.ruthwik.ridebooking.controller;

import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Optional;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruthwik.ridebooking.Dto.AvailableRideResponse;
import com.ruthwik.ridebooking.Dto.AvailableRidesRequest;
import com.ruthwik.ridebooking.Dto.CreateRideRequest;
import com.ruthwik.ridebooking.Dto.RideResponse;
import com.ruthwik.ridebooking.Dto.SearchRidesAndBookingsRequest;
import com.ruthwik.ridebooking.Dto.UpdateRideRequest;
import com.ruthwik.ridebooking.Repository.UserRepository;
import com.ruthwik.ridebooking.model.Ride;
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
	
	 @GetMapping("/myrides")
	    public ResponseEntity<List<RideResponse>> getMyCreatedRides() {
		 
	        List<RideResponse> rides =
	                rideService.getRides();
	        return ResponseEntity.ok(rides);
	    }

	@PostMapping("/createride")
	public ResponseEntity<String> getRides(@Valid @RequestBody CreateRideRequest createRideRequest)
	{
		
		System.out.println("in create ride method in controller");
		return rideService.createRide(createRideRequest);
	}
	@PutMapping("/edit/{rideId}")
	public ResponseEntity<String> updateRide(
	        @PathVariable Long rideId,
	        @Valid @RequestBody UpdateRideRequest request
	       ) {

	    rideService.updateRide(rideId, request);

	    return ResponseEntity.ok("Ride updated successfully");
	}
	@DeleteMapping("/cancel/{id}")
	 public ResponseEntity<String> cancelRide(@PathVariable Long id) {
       try {
       
        return ResponseEntity.ok( rideService.cancelRide(id));
       } 
       catch(Exception e)
       {
    	   return new ResponseEntity<String>("cancelling ride failed",HttpStatus.BAD_REQUEST);
       }

    }
	@GetMapping("/search")
	public ResponseEntity<List<RideResponse>> searchMyRides(
	        @RequestBody SearchRidesAndBookingsRequest request) {

	    List<RideResponse> rides = rideService.searchMyRides(request);

	    return ResponseEntity.ok(rides);
	}
	
	@GetMapping("/available")
	public ResponseEntity<List<AvailableRideResponse>> availableRides(
	        @Valid @ModelAttribute AvailableRidesRequest request) {

	    List<AvailableRideResponse> rides = rideService.availableRides(request);
	    
	    return ResponseEntity.ok(rides);
	}
	
}
