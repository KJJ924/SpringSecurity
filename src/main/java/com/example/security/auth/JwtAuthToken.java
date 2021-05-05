package com.example.security.auth;


import com.example.security.auth.JwtService.JwtUser;
import com.example.security.member.domain.AuthUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;


public class JwtAuthToken extends AbstractAuthenticationToken {

	private final AuthUser authUser;

	public JwtAuthToken(JwtUser user) {
		super(null);
		this.authUser =  AuthUser.builder().email(user.getEmail()).name(user.getUserId())
			.build();
		setAuthenticated(true);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public AuthUser getPrincipal() {
		return authUser;
	}
}
