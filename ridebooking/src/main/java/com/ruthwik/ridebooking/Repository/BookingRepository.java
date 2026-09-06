package com.ruthwik.ridebooking.Repository;

import com.ruthwik.ridebooking.model.Booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface  BookingRepository extends JpaRepository<Booking,Long>{
	List<Booking> findByRideId(Long rideId);
	
	 List<Booking> findByPassengerIdOrderByBookedAtDesc(Long passengerId);
	 @Query("""
			    SELECT b
			    FROM Booking b
			    WHERE b.passenger.id = :userId

			    AND (
			        :source IS NULL
			        OR LOWER(b.source) LIKE LOWER(CONCAT('%', :source, '%'))
			    )

			    AND (
			        :destination IS NULL
			        OR LOWER(b.destination) LIKE LOWER(CONCAT('%', :destination, '%'))
			    )

			    AND (
			        :rideDate IS NULL
			        OR b.rideDate = :rideDate
			    )

			    AND (
			        :fromDate IS NULL
			        OR b.bookedAt >= :fromDate
			    )

			    ORDER BY b.bookedAt DESC
			""")
			List<Booking> searchBookings(
			        @Param("userId") Long userId,
			        @Param("source") String source,
			        @Param("destination") String destination,
			        @Param("rideDate") LocalDate rideDate,
			        @Param("fromDate") LocalDateTime fromDate
			);
}
