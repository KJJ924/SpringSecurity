package com.example.security.member.controller;

import com.example.security.config.UserAccount;
import com.example.security.member.dto.RequestMember;
import com.example.security.member.dto.ResponseMember;
import com.example.security.service.MemberService;
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
    public ResponseEntity<ResponseMember> save(@RequestBody RequestMember member) {
        ResponseMember responseMember = memberService.save(member);
        return ResponseEntity.ok(responseMember);
    }

    @GetMapping("/hi")
    public ResponseEntity<Object> hi(@AuthenticationPrincipal UserAccount userAccount) {
        System.out.println("userAccount.getUsername() = " + userAccount.getUsername());
        return ResponseEntity.ok("Hi !   " + userAccount.getUsername());
    }

}
