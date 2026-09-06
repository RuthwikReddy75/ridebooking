package com.ruthwik.ridebooking.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ruthwik.ridebooking.Dto.BookingRequest;
import com.ruthwik.ridebooking.Dto.BookingResponse;
import com.ruthwik.ridebooking.Dto.RideBookingResponse;
import com.ruthwik.ridebooking.Dto.SearchRidesAndBookingsRequest;
import com.ruthwik.ridebooking.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/book")
    public ResponseEntity<String> bookRide(@Valid @RequestBody BookingRequest request) {
    	try {

        bookingService.bookRide(request);

        return new ResponseEntity<>("Ride booked successfully", HttpStatus.CREATED);
    	}
    	catch(Exception e)
    	{
    		return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
    	}
    }
    @GetMapping("/rides/{rideid}")
    public ResponseEntity<List<RideBookingResponse>> getBookingsOfRide(@PathVariable long rideid){
    	List<RideBookingResponse> bookings=new ArrayList<>();
      try {
    	 bookings= bookingService.getBookingsOfRide(rideid);
    	  
      }
      catch(Exception e)
      {
    	  System.out.println(e.getMessage());

    	  
      }
      return ResponseEntity.ok(bookings);
      
    }
    
    @GetMapping("/mybookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings() {

        List<BookingResponse> response =
                bookingService.getMyBookings();

        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId) {

        return bookingService.cancelBooking(bookingId);
    }
    
    @PostMapping("mybookings/search")
    public ResponseEntity<List<BookingResponse>> searchBookings(
            @RequestBody SearchRidesAndBookingsRequest request){

        return ResponseEntity.ok(
                bookingService.searchBookings(request)
        );
    }
    
    
}