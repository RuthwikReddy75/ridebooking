package com.ruthwik.ridebooking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ruthwik.ridebooking.Dto.AvailableRidesRequest;
import com.ruthwik.ridebooking.Dto.BookingRequest;
import com.ruthwik.ridebooking.Dto.BookingResponse;
import com.ruthwik.ridebooking.Dto.RideBookingResponse;
import com.ruthwik.ridebooking.Dto.SearchRidesAndBookingsRequest;
import com.ruthwik.ridebooking.model.Booking;
import com.ruthwik.ridebooking.model.BookingStatus;
import com.ruthwik.ridebooking.model.Ride;
import com.ruthwik.ridebooking.model.RideStatus;
import com.ruthwik.ridebooking.model.RideStop;
import com.ruthwik.ridebooking.model.User;
import com.ruthwik.ridebooking.utilities.UserDetailsUtility;
import com.ruthwik.ridebooking.Repository.BookingRepository;
import com.ruthwik.ridebooking.Repository.RideRepository;
import com.ruthwik.ridebooking.Repository.UserRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final UserDetailsUtility userDetailsUtility;
    

    public BookingService(BookingRepository bookingRepository,
                          RideRepository rideRepository,
                          UserRepository userRepository,UserDetailsUtility userDetailsUtility) {

        this.bookingRepository = bookingRepository;
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.userDetailsUtility=userDetailsUtility;
    }

    public void bookRide(BookingRequest request) {

        User passenger=userDetailsUtility.getUser();
        

        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // Driver cannot book his own ride
        if (ride.getDriver().getId().equals(passenger.getId())) {
            throw new RuntimeException("You cannot book your own ride");
        }

        // Check available seats
        if (ride.getTotalSeats() < request.getNumberOfSeats()) {
            throw new RuntimeException("Not enough seats available");
        }

        Booking booking = new Booking();

        booking.setRide(ride);
        booking.setPassenger(passenger);

        booking.setSource(request.getSource());
        booking.setDestination(request.getDestination());

        booking.setNumberOfSeats(request.getNumberOfSeats());

        booking.setTotalPrice(request.getNumberOfSeats()*calculatePrice(ride,request));

        booking.setRideDate(ride.getRideDate());
        booking.setRideTime(ride.getRideTime());

        booking.setPassengerName(passenger.getName());
        booking.setPassengerPhone(passenger.getPhone());

        booking.setDriverName(ride.getDriver().getName());
        booking.setDriverPhone(ride.getDriver().getPhone());

        booking.setVehicleNumber(ride.getVehicleNumber());

        booking.setBookingStatus(BookingStatus.BOOKED);

        booking.setBookedAt(LocalDateTime.now());

        booking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        // Reduce available seats
        ride.setTotalSeats(
                ride.getTotalSeats() - request.getNumberOfSeats());
        if(ride.getTotalSeats()==0)
        {
          ride.setStatus(RideStatus.FILLED);
         }

        rideRepository.save(ride);
    }
    private double calculatePrice(Ride ride,BookingRequest request)
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
    public List<RideBookingResponse> getBookingsOfRide(Long rideId) {

        
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        
        List<Booking> bookings = bookingRepository.findByRideId(rideId);

        List<RideBookingResponse> responses = new ArrayList<>();

        for (Booking booking : bookings) {

            RideBookingResponse response = new RideBookingResponse();

            response.setBookingId(booking.getId());

            response.setSource(booking.getRide().getSource());

            response.setDestination(booking.getRide().getDestination());

            response.setSeatsBooked(booking.getNumberOfSeats());

            response.setPrice(booking.getTotalPrice());

            response.setBookedAt(booking.getBookedAt());

            response.setStatus(booking.getBookingStatus());

            response.setPassengerName(
                    booking.getPassenger().getName());

            response.setPassengerPhone(
                    booking.getPassenger().getPhone());

            responses.add(response);
        }

        return responses;
    }
    
    public List<BookingResponse> getMyBookings() {

       

    	  User passenger=userDetailsUtility.getUser();

        List<Booking> bookings =
                bookingRepository.findByPassengerIdOrderByBookedAtDesc(
                        passenger.getId());

        List<BookingResponse> responseList = new ArrayList<>();

        for (Booking booking : bookings) {

            BookingResponse response = new BookingResponse();
         

            response.setBookingId(booking.getId());

            response.setSource(
                    booking.getSource());

            response.setDestination(
                    booking.getDestination());

            response.setRideDate(
                    booking.getRideDate());

            response.setRideTime(
                    booking.getRideTime());

            response.setSeatsBooked(
                    booking.getNumberOfSeats());

            response.setPrice(
                    booking.getTotalPrice());

            response.setBookedAt(
                    booking.getBookedAt());

            response.setStatus(
                    booking.getBookingStatus());

            response.setDriverName(
                    booking.getDriverName());

            response.setVehicleNumber(
                    booking.getVehicleNumber());

            responseList.add(response);
        }

        return responseList;
    }
    
    public ResponseEntity<String> cancelBooking(Long id)
    {
      try {
    	 Optional<Booking> opt=bookingRepository.findById(id);
    	 
    	 Booking booking=opt.get();
    	 booking.setBookingStatus(BookingStatus.CANCELLED);
    	 bookingRepository.save(booking);
    	 Ride ride=booking.getRide();
    	 ride.setTotalSeats(ride.getTotalSeats()+booking.getNumberOfSeats());
    	 if(ride.getStatus()==RideStatus.FILLED) ride.setStatus(RideStatus.ACTIVE);
    	 rideRepository.save(ride);
    	 
    	 return new ResponseEntity<>("Cancelled succesfully",HttpStatus.OK);  
      }
      catch(Exception e)
      {
    	 return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST); 
      }
    }
    
    public List<BookingResponse> searchBookings(SearchRidesAndBookingsRequest request) {

        

       User user=userDetailsUtility.getUser();

        LocalDateTime fromDate = null;

        if(request.getDuration()!=null){

            switch(request.getDuration()){

                case ONE_MONTH:
                    fromDate = LocalDateTime.now().minusMonths(1);
                    break;

                case THREE_MONTHS:
                    fromDate = LocalDateTime.now().minusMonths(3);
                    break;

                case SIX_MONTHS:
                    fromDate = LocalDateTime.now().minusMonths(6);
                    break;
            }
        }

        List<Booking> bookings = bookingRepository.searchBookings(
                user.getId(),
                request.getSource(),
                request.getDestination(),
                request.getRideDate(),
                fromDate
        );

        List<BookingResponse> response = new ArrayList<>();

        for(Booking booking : bookings){
            response.add(convertToResponse(booking));
        }

        return response;
    }

    private BookingResponse convertToResponse(Booking booking) {

        BookingResponse response = new BookingResponse();

        response.setBookingId(booking.getId());

        response.setSource(booking.getSource());

        response.setDestination(booking.getDestination());

        response.setRideDate(booking.getRideDate());

        response.setRideTime(booking.getRideTime());

        response.setSeatsBooked(booking.getNumberOfSeats());

        response.setPrice(booking.getTotalPrice());

        response.setBookedAt(booking.getBookedAt());

        response.setStatus(booking.getBookingStatus());

        response.setDriverName(booking.getDriverName());

        response.setDriverPhone(booking.getDriverPhone());

        response.setVehicleNumber(booking.getVehicleNumber());

        return response;
    }


}