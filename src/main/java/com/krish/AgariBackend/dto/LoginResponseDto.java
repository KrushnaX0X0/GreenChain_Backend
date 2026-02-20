package com.krish.AgariBackend.dto;

public class LoginResponseDto {
	public String token;
	public String username;

	public java.util.Set<String> roles;

	public LoginResponseDto(String token, String username, java.util.Set<String> roles) {
		this.token = token;
		this.username = username;
		this.roles = roles;
	}

	public java.util.Set<String> getRoles() {
		return roles;
	}

	public void setRoles(java.util.Set<String> roles) {
		this.roles = roles;
	}
}
