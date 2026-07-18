package com.ruthwik.ridebooking.Repository;

import com.ruthwik.ridebooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    
    
    boolean existsByEmail(String email);
    
    Optional<User> findByName(String username);
}