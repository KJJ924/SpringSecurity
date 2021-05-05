package com.example.security.auth;

import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;


@EqualsAndHashCode
public class NotYetJwtAuthToken extends AbstractAuthenticationToken {

    private final String authToken;

    public NotYetJwtAuthToken(String authToken) {
        super(null);
        this.authToken = authToken;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public String getPrincipal() {
        return authToken;
    }
}
