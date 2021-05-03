package com.example.security.config;

import com.example.security.member.domain.Member;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class UserAccount extends User {

    private final Member member;

    public UserAccount(Member member) {
        super(member.getId(), member.getPw(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.member = member;
    }
}
