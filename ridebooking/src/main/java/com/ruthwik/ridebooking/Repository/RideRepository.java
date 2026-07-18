package com.ruthwik.ridebooking.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ruthwik.ridebooking.model.Ride;
import com.ruthwik.ridebooking.model.User;


public interface RideRepository extends JpaRepository<Ride, Long> {
	 List<Ride> findByDriver(User driver);

}
