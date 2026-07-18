package com.ruthwik.ridebooking.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruthwik.ridebooking.Dto.LoginRequest;
import com.ruthwik.ridebooking.Dto.RegisterRequest;
import com.ruthwik.ridebooking.Dto.RegisterResponse;
import com.ruthwik.ridebooking.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private  AuthService authService;

	@Autowired
    private AuthenticationManager authenticationManager;
    // Registration API
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest request) {
    	String message;
    	
		try {
			message = authService.register(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity
	                .badRequest()
	                .body(e.getMessage());
		}

        return ResponseEntity.ok(message);
        

       
    }

    // Login API
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest){
         System.out.println(loginRequest.getEmail());
         System.out.println(loginRequest.getPassword());
        return authService.login(loginRequest);
        }
 
    // Test API
    @GetMapping("/test")
    public String testApi() {

        return "Auth Controller Working";
    }
}