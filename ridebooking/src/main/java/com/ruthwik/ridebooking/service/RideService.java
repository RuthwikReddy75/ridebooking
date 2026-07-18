package com.ruthwik.ridebooking.service;

import com.ruthwik.ridebooking.Dto.CreateRideRequest;
import com.ruthwik.ridebooking.Dto.RideStopRequest;
import com.ruthwik.ridebooking.model.Ride;
import com.ruthwik.ridebooking.model.RideStop;
import com.ruthwik.ridebooking.model.User;
import com.ruthwik.ridebooking.Repository.RideRepository;
import com.ruthwik.ridebooking.Repository.UserRepository;

import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RideService {
	   private final RideRepository rideRepository;
	   
	   private final UserRepository userRepository;

	    public RideService(RideRepository rideRepository,UserRepository userRepository) {
	    	
	        this.rideRepository = rideRepository;
	        this.userRepository=userRepository;
	    }

	    public ResponseEntity<String> createRide(CreateRideRequest request) {

	     try {
	        // Create Ride Entity
	        Ride ride = new Ride();
	        org.springframework.security.core.@Nullable Authentication authentication =
			        SecurityContextHolder.getContext().getAuthentication();
			System.out.println(authentication.getName());
			Optional<User> user=userRepository.findByName(authentication.getName());
			
			ride.setDriver(user.get());

	        ride.setSource(request.getSource());
	        ride.setDestination(request.getDestination());
	        ride.setRideTime(request.getRideTime());
	        ride.setRideDate(request.getRideDate());
	        ride.setPricePerSeat(request.getPricePerSeat());
	        ride.setStatus("Active");
	        ride.setTotalSeats(request.getTotalSeats());
	        
	        // Convert RideStopRequest DTOs to RideStop Entities
	        for (RideStopRequest stopRequest : request.getRideStops()) {

	            RideStop rideStop = new RideStop();

	            rideStop.setStopName(stopRequest.getStopName());
	            rideStop.setStopOrder(stopRequest.getStopOrder());

	            // Set Parent Ride
	            rideStop.setRide(ride);

	            // Add stop to Ride
	            ride.getRideStops().add(rideStop);
	        }

	        // Save Ride (RideStops are saved automatically because of CascadeType.ALL)
	         System.out.println("in try block of service of create ride method ");
	         rideRepository.save(ride);
	         return new ResponseEntity("Creted succesfully",HttpStatus.OK);
	         
	     }
	     catch(Exception e)
	     {
	    	 System.out.println("in catch block of create ride in service"+e.getMessage());
	    	   return new ResponseEntity("failed",HttpStatus.BAD_REQUEST);
	     }
	    }

}
