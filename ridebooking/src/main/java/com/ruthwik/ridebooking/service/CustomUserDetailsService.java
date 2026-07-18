package com.ruthwik.ridebooking.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ruthwik.ridebooking.model.User;
import com.ruthwik.ridebooking.Dto.UserPrincipal;
import com.ruthwik.ridebooking.Repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;
	
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
    	Optional<User> user=userRepo.findByEmail(email);
    	if(!user.isPresent())
    	{
    		System.out.println("user not found");
    		throw new UsernameNotFoundException("username not found");
    	}
    	System.out.println("user found");
    	 return new UserPrincipal(user.get());
    	
    }
}