package com.example.security.member.controller;

import com.example.security.member.domain.AuthUser;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

/**
 * @author dkansk924@naver.com
 * @since 2021/05/14
 */
public class WithAuthUserSecurityContextFactory implements WithSecurityContextFactory<WithAuthUser> {

    @Override
    public SecurityContext createSecurityContext(WithAuthUser annotation) {
        String id = annotation.id();
        String role = annotation.role();

        AuthUser authUser = new AuthUser("testUserName", id);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            authUser, "password", List.of(new SimpleGrantedAuthority(role)));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        return context;
    }
}
