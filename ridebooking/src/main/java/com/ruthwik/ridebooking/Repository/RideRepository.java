package com.ruthwik.ridebooking.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ruthwik.ridebooking.model.Ride;
import com.ruthwik.ridebooking.model.User;


public interface RideRepository extends JpaRepository<Ride, Long> {
	 List<Ride> findByDriver(User driver);
	
	 @Query("""
			    SELECT r
			    FROM Ride r
			    WHERE r.driver.id = :driverId

			    AND (
			        :source IS NULL
			        OR LOWER(r.source) = LOWER(:source)
			        OR EXISTS (
			            SELECT rs
			            FROM RideStop rs
			            WHERE rs.ride = r
			            AND LOWER(rs.stopName) = LOWER(:source)
			        )
			    )

			    AND (
			        :destination IS NULL
			        OR LOWER(r.destination) = LOWER(:destination)
			        OR EXISTS (
			            SELECT rs
			            FROM RideStop rs
			            WHERE rs.ride = r
			            AND LOWER(rs.stopName) = LOWER(:destination)
			        )
			    )

			    AND (
			        :rideDate IS NULL
			        OR r.rideDate = :rideDate
			    )

			    ORDER BY r.rideDate ASC, r.rideTime ASC
			    """)
			List<Ride> searchMyRides(
			        @Param("driverId") Long driverId,
			        @Param("source") String source,
			        @Param("destination") String destination,
			        @Param("rideDate") LocalDate rideDate
			);
	 
	 @Query("""
			 SELECT DISTINCT r
			 FROM Ride r
			 WHERE
			     r.rideDate >= CURRENT_DATE
			     AND (:rideDate IS NULL OR r.rideDate = :rideDate)
			     AND r.totalSeats >= :requiredSeats

			     AND (
			         LOWER(r.source) = LOWER(:source)
			         OR LOWER(r.destination) = LOWER(:source)
			         OR EXISTS (
			             SELECT rs
			             FROM RideStop rs
			             WHERE rs.ride = r
			             AND LOWER(rs.stopName) = LOWER(:source)
			         )
			     )

			     AND (
			         LOWER(r.source) = LOWER(:destination)
			         OR LOWER(r.destination) = LOWER(:destination)
			         OR EXISTS (
			             SELECT rs
			             FROM RideStop rs
			             WHERE rs.ride = r
			             AND LOWER(rs.stopName) = LOWER(:destination)
			         )
			     )

			 ORDER BY r.rideDate ASC, r.rideTime ASC
			 """)
			 List<Ride> availableRides(
			         @Param("source") String source,
			         @Param("destination") String destination,
			         @Param("rideDate") LocalDate rideDate,
			         @Param("requiredSeats") Integer requiredSeats
			 );
}
