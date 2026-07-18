package com.ruthwik.ridebooking.service;
import com.ruthwik.ridebooking.Dto.LoginRequest;
import com.ruthwik.ridebooking.Dto.RegisterRequest;
import com.ruthwik.ridebooking.model.User;
import com.ruthwik.ridebooking.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
	     private final UserRepository userRepository;
	    private final PasswordEncoder passwordEncoder;
	    
	    @Autowired
	    private AuthenticationManager authenticationManager;
	    
	    @Autowired
	    private JwtService jwtService;

	    public AuthService(UserRepository userRepository,
	                       PasswordEncoder passwordEncoder) {
	        this.userRepository = userRepository;
	        this.passwordEncoder = passwordEncoder;
	    }

	    public String register(RegisterRequest request) {

	        if (userRepository.existsByEmail(request.getEmail())) {
	            throw new RuntimeException("Email already registered");
	        }

//	        User user = User.builder()
//	                .name(request.getName())
//	                .email(request.getEmail())
//	                .password(passwordEncoder.encode(request.getPassword()))
//	                .phone(request.getPhone())
//	                .build();
	        User user = new User();
	        user.setName(request.getName());
	        user.setEmail(request.getEmail());
	        user.setPassword(passwordEncoder.encode(request.getPassword()));
	        user.setPhone(request.getPhone());

	        userRepository.save(user);

	        return "User registered successfully";
	    }
	    
	    public ResponseEntity<String> login(LoginRequest loginRequest)
	    {
	    	try
	    	{
	    	Authentication auth =
        	        authenticationManager.authenticate(
        	            new UsernamePasswordAuthenticationToken(
        	                loginRequest.getEmail(),
        	                loginRequest.getPassword()
        	            )
        	        );
		    	if(auth.isAuthenticated())
		    	{
		    		return new ResponseEntity<>(jwtService.generateToken(loginRequest.getEmail()),HttpStatus.OK);
		    	}
	    	}
	    	catch(Exception e){
	    		System.out.println("exception occured");
	    		return  new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
	    		
	    	}
	    	
	    	return  new ResponseEntity<>("failed",HttpStatus.BAD_REQUEST);
	    }

}
