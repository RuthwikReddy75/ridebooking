package com.ruthwik.ridebooking.Config;
import com.ruthwik.ridebooking.service.JwtService;
import com.ruthwik.ridebooking.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter  {
	
	 @Autowired
     private JwtService jwtService;
	 
	 @Autowired
	 ApplicationContext context;
	 
	 @Override
	 protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
	//  Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJraWxsIiwiaWF0IjoxNzIzMTgzNzExLCJleHAiOjE3MjMxODM4MTl9.5nf7dRzKRiuGurN2B9dHh_M5xiu73ZzWPr6rbhOTTHs
	     System.out.println("in jwt filter");   
		 String authHeader = request.getHeader("Authorization");
	        String token = null;
	        String username = null;
	        System.out.println("auth header"+authHeader);
	        if (authHeader != null && authHeader.startsWith("Bearer ")) {
	            token = authHeader.substring(7);
	            username = jwtService.extractUserName(token);
	            System.out.println("username"+username);
	        }

	        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	            UserDetails userDetails = context.getBean(CustomUserDetailsService.class).loadUserByUsername(username);
	            System.out.println("user details "+userDetails.getUsername());
	            try {
	            	 if (jwtService.validateToken(token,username)) {
	 	            	System.out.println("Validated succesfully");
	 	                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	 	                authToken.setDetails(new WebAuthenticationDetailsSource()
	 	                        .buildDetails(request));
	 	                SecurityContextHolder.getContext().setAuthentication(authToken);
	 	                System.out.println("stored succesfully");
	 	                
	 	            }
	            	
	            }
	            catch(Exception e)
	            {
	            	System.out.println(e.getMessage());
	            }
	            	
	            
	           
	        }

	        filterChain.doFilter(request, response);
	 }
}
