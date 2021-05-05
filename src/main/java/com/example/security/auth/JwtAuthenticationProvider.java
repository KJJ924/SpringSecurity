package com.example.security.auth;

import com.example.security.auth.JwtService.JwtUser;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        if (authentication == null || !supports(authentication.getClass()))
            return null;

        NotYetJwtAuthToken authToken = (NotYetJwtAuthToken) authentication;
        JwtUser decode = jwtService.decode(authToken.getPrincipal());
        return new JwtAuthToken(decode);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(NotYetJwtAuthToken.class);
    }

    private UserDetails retrieveUser(String name){
        return userDetailsService.loadUserByUsername(name);
    }

    private boolean passwordCheck(UserDetails userDetails , Authentication authentication){
        if(Objects.isNull(authentication.getCredentials())){
            throw new BadCredentialsException("asd");
        }
        String credentials = (String) authentication.getCredentials();
        return passwordEncoder.matches(credentials, userDetails.getPassword());
    }

}
