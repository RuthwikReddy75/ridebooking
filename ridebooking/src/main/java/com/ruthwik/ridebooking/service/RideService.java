package com.ruthwik.ridebooking.service;

import com.ruthwik.ridebooking.Dto.AvailableRideResponse;
import com.ruthwik.ridebooking.Dto.AvailableRidesRequest;
import com.ruthwik.ridebooking.Dto.CreateRideRequest;
import com.ruthwik.ridebooking.Dto.RideResponse;
import com.ruthwik.ridebooking.Dto.RideStopRequest;
import com.ruthwik.ridebooking.Dto.RideStopResponse;
import com.ruthwik.ridebooking.Dto.SearchRidesAndBookingsRequest;
import com.ruthwik.ridebooking.Dto.UpdateRideRequest;
import com.ruthwik.ridebooking.model.Booking;
import com.ruthwik.ridebooking.model.BookingStatus;
import com.ruthwik.ridebooking.model.Ride;
import com.ruthwik.ridebooking.model.RideStatus;
import com.ruthwik.ridebooking.model.RideStop;
import com.ruthwik.ridebooking.model.User;
import com.ruthwik.ridebooking.utilities.UserDetailsUtility;

import jakarta.transaction.Transactional;

import com.ruthwik.ridebooking.Repository.BookingRepository;
import com.ruthwik.ridebooking.Repository.RideRepository;
import com.ruthwik.ridebooking.Repository.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
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
	   
	   private final BookingRepository bookingRepository;
	   

	    public RideService(RideRepository rideRepository,UserRepository userRepository,UserDetailsUtility userDetailsUtility,BookingRepository bookingRepository) {
	    	
	        this.rideRepository = rideRepository;
	        this.userRepository=userRepository;
	        this.userDetailsUtility=userDetailsUtility;
	        this.bookingRepository=bookingRepository;
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
	        ride.setStatus(RideStatus.ACTIVE);
	        ride.setTotalSeats(request.getTotalSeats());
	        ride.setVehicleNumber(request.getVehicleNumber());
	        
	        // Convert RideStopRequest DTOs to RideStop Entities
	        for (RideStopRequest stopRequest : request.getRideStops()) {

	            RideStop rideStop = new RideStop();

	            rideStop.setStopName(stopRequest.getStopName());
	            rideStop.setStopOrder(stopRequest.getStopOrder());
	            rideStop.setPrice(stopRequest.getPrice());

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
	    	            stopDto.setPrice(stop.getPrice());

	    	            stopDtos.add(stopDto);
	    	        }

	    	        dto.setRideStops(stopDtos);

	    	        response.add(dto);
	    	 }
	    	 return response;
	    	
	    }
	    @Transactional
	    public ResponseEntity<String> updateRide(Long rideId,
	                           UpdateRideRequest request) {
	      try {

	        Ride ride = rideRepository.findById(rideId)
	                .orElseThrow(() -> new RuntimeException("Ride not found"));

	       
	        // Update ride details
	        ride.setSource(request.getSource());
	        ride.setDestination(request.getDestination());
	        ride.setRideDate(request.getRideDate());
	        ride.setRideTime(request.getRideTime());
	        ride.setTotalSeats(request.getTotalSeats());
	        ride.setPricePerSeat(request.getPricePerSeat());
	        ride.setDescription(request.getDescription());
	        ride.setVehicleNumber(request.getVehicleNumber());

	        // Replace ride stops
	        ride.getRideStops().clear();

	       

	        for (RideStopRequest stop : request.getRideStops()) {

	            RideStop rideStop = new RideStop();
	            rideStop.setRide(ride);
	            rideStop.setStopName(stop.getStopName());
	            rideStop.setStopOrder(stop.getStopOrder());
	            rideStop.setPrice(stop.getPrice());

	            ride.getRideStops().add(rideStop);
	        }

	        rideRepository.save(ride);
	        return new ResponseEntity<String>("updated succesfully",HttpStatus.ACCEPTED);
	      }
	      catch(Exception e)
	      {
	    	  System.out.println("exception occured while updating(ride service--->update ride)");
	    	  System.out.println(e.getMessage());
	    	  return new ResponseEntity<String>("failed to update",HttpStatus.BAD_REQUEST);
	      }
	    }
	    public String cancelRide(Long rideId) {

	        Ride ride = rideRepository.findById(rideId)
	                .orElseThrow(() -> new RuntimeException("Ride not found."));

	        ride.setStatus(RideStatus.CANCELLED);
	        rideRepository.save(ride);
	        List<Booking> bookings=bookingRepository.findByRideId(rideId);
	        for(Booking booking:bookings)
	        {
	           booking.setBookingStatus(BookingStatus.RIDECANCELLED);	
	           bookingRepository.save(booking);
		        
	        }
	       
	        
	        return "ride cancelled succesfully";
	    }
	    
	    public List<RideResponse> searchMyRides(SearchRidesAndBookingsRequest request)
	    {
	    	
	    	 User user=userDetailsUtility.getUser();
	    	 List<Ride> rides = rideRepository.searchMyRides(
	    	            user.getId(),
	    	            request.getSource(),
	    	            request.getDestination(),
	    	            request.getRideDate());

	    	    List<RideResponse> responseList = new ArrayList<>();

	    	    for (Ride ride : rides) {

	    	        RideResponse response = new RideResponse();

	    	        response.setId(ride.getId());
	    	        response.setSource(ride.getSource());
	    	        response.setDestination(ride.getDestination());
	    	        response.setRideDate(ride.getRideDate());
	    	        response.setRideTime(ride.getRideTime());
	    	        response.setTotalSeats(ride.getTotalSeats());
	    	        response.setPricePerSeat(ride.getPricePerSeat());
	    	        response.setVehicleNumber(ride.getVehicleNumber());
	    	        response.setDescription(ride.getDescription());
	    	        response.setStatus(ride.getStatus());
	    	        response.setCreatedAt(ride.getCreatedAt());

	    	        // Convert RideStop entities to RideStopResponse DTOs
	    	        List<RideStopResponse> stopResponses = new ArrayList<>();

	    	        for (RideStop stop : ride.getRideStops()) {

	    	            RideStopResponse stopResponse = new RideStopResponse();

	    	            stopResponse.setId(stop.getId());
	    	            stopResponse.setStopName(stop.getStopName());
	    	            stopResponse.setStopOrder(stop.getStopOrder());
	    	            stopResponse.setPrice(stop.getPrice());
	    	            stopResponses.add(stopResponse);
	    	        }

	    	        response.setRideStops(stopResponses);

	    	        responseList.add(response);
	    	    }

	    	    return responseList;
	    	}
	    
        
		public List<AvailableRideResponse> availableRides(AvailableRidesRequest request) {
			List<AvailableRideResponse> result = new ArrayList<>();
		 try {
		    
			 User luser=userDetailsUtility.getUser();
		    
		    List<Ride> rides = rideRepository.availableRides(
		            request.getSource(),
		            request.getDestination(),
		            request.getRideDate(),
		            request.getRequiredSeats()
		            );
		    System.out.println("matched rides"+rides.size());
		    
		
		    
		
		    for (Ride ride : rides) {
		
		    	List<String> route = new ArrayList<>();
		    	User user=ride.getDriver();
		    	if(user.equals(luser)) continue;

		    	// Add ride source first
		    	route.add(ride.getSource());

		    	// Get all stops
		    	List<RideStop> stops = new ArrayList<>(ride.getRideStops());

		    	// Sort by stopOrder
		    	stops.sort(Comparator.comparing(RideStop::getStopOrder));

		    	// Add stops in the correct order
		    	for (RideStop stop : stops) {
		    	    route.add(stop.getStopName());
		    	}

		    	// Add ride destination at the end
		    	route.add(ride.getDestination());
		        int sourceIndex = -1;
		        int destinationIndex = -1;
		
		        for (int i = 0; i < route.size(); i++) {
		
		            if (route.get(i).equalsIgnoreCase(request.getSource())) {
		                sourceIndex = i;
		            }
		
		            if (route.get(i).equalsIgnoreCase(request.getDestination())) {
		                destinationIndex = i;
		            }
		        }
		
		        if (sourceIndex != -1
		                && destinationIndex != -1
		                && sourceIndex < destinationIndex) {
		        	ride.setPricePerSeat(calculatePrice(ride,request));
		            RideResponse rideResponse=new RideResponse();
		           
		            result.add(convertToResponse(ride,user,request));
		        }
		        
		    }
		    return result;
		    
		   
		   }
			catch (Exception e) {
				System.out.println(e.getMessage());
                return result;
			}
		     
		}
		private AvailableRideResponse convertToResponse(Ride ride,User user,AvailableRidesRequest request) {

		    AvailableRideResponse response = new AvailableRideResponse();

		    response.setRideId(ride.getId());
		    response.setSource(ride.getSource());
		    response.setDestination(ride.getDestination());
		    response.setRideDate(ride.getRideDate());
		    response.setRideTime(ride.getRideTime());
		    response.setAvailableSeats(ride.getTotalSeats());
		    response.setPricePerSeat(ride.getPricePerSeat());
		    response.setVehicleNumber(ride.getVehicleNumber());
		    response.setDescription(ride.getDescription());
		    response.setDriverId(user.getId());
		    response.setDriverName(user.getName());
		    response.setDriverMobileNumber(user.getPhone());
		    response.setYourSource(request.getSource());
		    response.setYourDestination(request.getDestination());
		      

		    List<RideStopResponse> stopResponses = new ArrayList<>();

		    for (RideStop stop : ride.getRideStops()) {

		        RideStopResponse stopResponse = new RideStopResponse();

		        stopResponse.setId(stop.getId());
		        stopResponse.setStopName(stop.getStopName());
		        stopResponse.setStopOrder(stop.getStopOrder());
		        stopResponse.setPrice(stop.getPrice());

		        stopResponses.add(stopResponse);
		    }

		    response.setRideStops(stopResponses);
		    

		    return response;
		}
		
		private double calculatePrice(Ride ride,AvailableRidesRequest request)
		{
			if(request.getSource().equalsIgnoreCase(ride.getSource()) && request.getDestination().equalsIgnoreCase(ride.getDestination()))
			{
				return ride.getPricePerSeat();
			}
			else if(request.getSource().equalsIgnoreCase(ride.getSource()))
			{
				for(RideStop stop:ride.getRideStops())
				{
					if(stop.getStopName().equalsIgnoreCase(request.getDestination()))
					{
						return stop.getPrice();
					}
				}
			}
			else if(request.getDestination().equalsIgnoreCase(ride.getDestination()))
			{
			    for(RideStop stop:ride.getRideStops())
			    {
			    	if(stop.getStopName().equalsIgnoreCase(request.getSource()))
			    	{
			    		return ride.getPricePerSeat()-stop.getPrice();
			    	}
			    }
			}
			else
			{
				double src=0;
				double des=0;
				for(RideStop stop:ride.getRideStops())
				{
					if(stop.getStopName().equalsIgnoreCase(request.getSource()))
					{
						src=stop.getPrice();
					}
					if(stop.getStopName().equalsIgnoreCase(request.getDestination()))
					{
						des=stop.getPrice();
						break;
					}
				}
				return des-src;
			
						
				
				
			}
			return 0;
		}
		
}
	      


