package com.ruthwik.ridebooking.utilities;

import java.util.Optional;


import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ruthwik.ridebooking.Repository.UserRepository;
import com.ruthwik.ridebooking.model.User;

@Component
public class UserDetailsUtility {
	
	 @Autowired
	 private UserRepository userRepository;
	 
	 public User getUser()
	 {
		    Authentication authentication =
			        SecurityContextHolder.getContext().getAuthentication();
			Optional<User> user=userRepository.findByName(authentication.getName());
			return user.get();
			
	 }
	

}
