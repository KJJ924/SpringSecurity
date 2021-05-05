package com.example.security.member.controller;

import com.example.security.member.domain.AuthUser;
import com.example.security.member.dto.RequestLogin;
import com.example.security.member.dto.RequestMember;
import com.example.security.service.MemberService;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member")
    public ResponseEntity<String> save(@RequestBody RequestMember member, HttpServletResponse httpServletResponse) {
        String authToken = memberService.save(member);
        setAuthToken(httpServletResponse,authToken);
        return ResponseEntity.ok(authToken);
    }

    @GetMapping("/hi")
    public ResponseEntity<Object> hi(@AuthenticationPrincipal AuthUser user) {
        return ResponseEntity.ok("Hi !   " +user.getEmail());
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody RequestLogin login ,HttpServletResponse httpServletResponse){
        String token = memberService.login(login);
        setAuthToken(httpServletResponse, token);
        return ResponseEntity.ok(token);
    }



    private void setAuthToken(HttpServletResponse httpServletResponse, String authToken) {
        Cookie cookie = new Cookie("AUTH_TOKEN", authToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        // cookie.setSecure(true); FIXME https only
        httpServletResponse.addCookie(cookie);
    }

}
