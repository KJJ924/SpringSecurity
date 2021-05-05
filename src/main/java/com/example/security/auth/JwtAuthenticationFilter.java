package com.example.security.auth;

import com.auth0.jwt.exceptions.TokenExpiredException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {

    public final AuthenticationManager authenticationManager;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        try {
            if (httpServletRequest.getCookies() == null) {
                chain.doFilter(request, response);
                return;
            }

            Optional<Cookie> cookie = Arrays.stream(httpServletRequest.getCookies())
                .filter(c -> "AUTH_TOKEN".equals(c.getName())).findFirst();

            if (cookie.isEmpty()) {
                chain.doFilter(request, response);
                return;
            }
            NotYetJwtAuthToken yetJwtAuthToken = new NotYetJwtAuthToken(cookie.get().getValue());
            Authentication authenticate = authenticationManager.authenticate(yetJwtAuthToken);
            SecurityContextHolder.getContext().setAuthentication(authenticate);
            chain.doFilter(request, response);

        } catch (BadCredentialsException e) {
            // 이상한 토큰은 지워준다.
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            Cookie cookie = new Cookie("AUTH_TOKEN", null);
            cookie.setMaxAge(0);
            httpServletResponse.addCookie(cookie);
            request.setAttribute("exception",ErrorCode.INVALID);
            chain.doFilter(request, response);
        }catch (TokenExpiredException e){
            request.setAttribute("exception",ErrorCode.EXPIRED);
            chain.doFilter(request, response);
        } catch(IOException | ServletException e) {
            throw new BadCredentialsException("JwtAuthenticationFiler error");
        }
    }

}
