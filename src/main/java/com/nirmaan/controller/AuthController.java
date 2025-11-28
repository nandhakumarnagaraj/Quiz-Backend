package com.nirmaan.controller;

import com.nirmaan.dto.AuthRequest;
import com.nirmaan.dto.AuthResponse;
import com.nirmaan.dto.RegisterRequest;
import com.nirmaan.entity.User;
import com.nirmaan.security.JwtUtil;
import com.nirmaan.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
		} catch (BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
		}

		final UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
		final String jwt = jwtUtil.generateToken(userDetails);

		return ResponseEntity.ok(new AuthResponse(jwt, userDetails.getUsername()));
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
		try {
			User user = new User();
			user.setUsername(registerRequest.getUsername());
			user.setPassword(registerRequest.getPassword());
			user.setEmail(registerRequest.getEmail());

			User registeredUser = userService.registerUser(user);

			return ResponseEntity.status(HttpStatus.CREATED)
					.body("User registered successfully: " + registeredUser.getUsername());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/register-admin")
	public ResponseEntity<?> registerAdmin(@Valid @RequestBody RegisterRequest registerRequest) {
		try {
			User user = new User();
			user.setUsername(registerRequest.getUsername());
			user.setPassword(registerRequest.getPassword());
			user.setEmail(registerRequest.getEmail());

			User registeredUser = userService.registerAdmin(user);

			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Admin registered successfully: " + registeredUser.getUsername());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}