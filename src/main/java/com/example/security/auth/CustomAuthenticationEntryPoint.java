package com.example.security.auth;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {

        Optional<Cookie> cookie = Arrays.stream(request.getCookies())
            .filter(c -> "AUTH_TOKEN".equals(c.getName()))
            .findFirst();
        ErrorCode errorCode = (ErrorCode) request.getAttribute("exception");
        if (errorCode == ErrorCode.INVALID) {
            response.addCookie(cookie.get());
        }
        setResponse(response, errorCode);
    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().println("{ \"message\" : \"" + errorCode.getMessage()
            + "\", \"code\" : \"" + errorCode.getCode());

    }
}
