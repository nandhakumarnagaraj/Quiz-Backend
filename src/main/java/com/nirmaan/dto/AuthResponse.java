package com.nirmaan.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
	
	private String token;
	private String username;
}
