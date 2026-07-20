package com.ruthwik.ridebooking.service;

import com.ruthwik.ridebooking.Dto.CreateRideRequest;
import com.ruthwik.ridebooking.Dto.RideResponse;
import com.ruthwik.ridebooking.Dto.RideStopRequest;
import com.ruthwik.ridebooking.Dto.RideStopResponse;
import com.ruthwik.ridebooking.model.Ride;
import com.ruthwik.ridebooking.model.RideStop;
import com.ruthwik.ridebooking.model.User;
import com.ruthwik.ridebooking.utilities.UserDetailsUtility;
import com.ruthwik.ridebooking.Repository.RideRepository;
import com.ruthwik.ridebooking.Repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RideService {
	   private final RideRepository rideRepository;
	   
	   private final UserRepository userRepository;
	   
	   
	   private final UserDetailsUtility userDetailsUtility;

	    public RideService(RideRepository rideRepository,UserRepository userRepository,UserDetailsUtility userDetailsUtility) {
	    	
	        this.rideRepository = rideRepository;
	        this.userRepository=userRepository;
	        this.userDetailsUtility=userDetailsUtility;
	    }

	    public ResponseEntity<String> createRide(CreateRideRequest request) {

	     try {
	        // Create Ride Entity
	        Ride ride = new Ride();
	        User user=userDetailsUtility.getUser();
			
			ride.setDriver(user);

	        ride.setSource(request.getSource());
	        ride.setDestination(request.getDestination());
	        ride.setRideTime(request.getRideTime());
	        ride.setRideDate(request.getRideDate());
	        ride.setPricePerSeat(request.getPricePerSeat());
	        ride.setStatus("Active");
	        ride.setTotalSeats(request.getTotalSeats());
	        ride.setVehicleNumber(request.getVehicleNumber());
	        
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
	    
	    public List<RideResponse> getRides()
	    {
	    	User user=userDetailsUtility.getUser();
	    	 List<Ride> Rides= rideRepository.findByDriver(user);
	    	 List<RideResponse> response = new ArrayList<>();
	    	 for(Ride ride:Rides)
	    	 {

	    	        RideResponse dto = new RideResponse();

	    	        dto.setId(ride.getId());
	    	        dto.setSource(ride.getSource());
	    	        dto.setDestination(ride.getDestination());
	    	        dto.setRideDate(ride.getRideDate());
	    	        dto.setRideTime(ride.getRideTime());
	    	        dto.setTotalSeats(ride.getTotalSeats());
	    	        dto.setPricePerSeat(ride.getPricePerSeat());
	    	        dto.setVehicleNumber(ride.getVehicleNumber());
	    	        dto.setDescription(ride.getDescription());
	    	        dto.setStatus(ride.getStatus());
	    	        dto.setCreatedAt(ride.getCreatedAt());

	    	        List<RideStopResponse> stopDtos = new ArrayList<>();

	    	        for (RideStop stop : ride.getRideStops()) {

	    	            RideStopResponse stopDto = new RideStopResponse();

	    	            stopDto.setId(stop.getId());
	    	            stopDto.setStopName(stop.getStopName());
	    	            stopDto.setStopOrder(stop.getStopOrder());

	    	            stopDtos.add(stopDto);
	    	        }

	    	        dto.setRideStops(stopDtos);

	    	        response.add(dto);
	    	 }
	    	 return response;
	    	
	    }

}
