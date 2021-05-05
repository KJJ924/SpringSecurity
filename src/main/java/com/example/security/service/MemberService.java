package com.example.security.service;

import com.example.security.auth.JwtAuthToken;
import com.example.security.auth.JwtService;
import com.example.security.auth.JwtService.JwtUser;
import com.example.security.config.UserAccount;
import com.example.security.member.dao.MemberRepository;
import com.example.security.member.domain.Member;
import com.example.security.member.dto.RequestLogin;
import com.example.security.member.dto.RequestMember;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final JwtService jwtService;


    public String save(RequestMember member) {
        String encode = passwordEncoder.encode(member.getPw());
        member.setPw(encode);
        Member save = memberRepository.save(member.toEntity());
        return login(save);
    }

    private String login(Member member) {
        JwtUser jwtUser = JwtUser.of(member.getId(),
            member.getEmail(),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(30));

        String jwtToken = jwtService.encode(jwtUser);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthToken(jwtUser));
        return jwtToken;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username);
        if(member == null){
            throw new RuntimeException();
        }
        return new UserAccount(member);
    }

    public String login(RequestLogin login) {
        UserDetails userDetails = loadUserByUsername(login.getId());
        UserAccount account = (UserAccount) userDetails;
        Member member = account.getMember();
        boolean result = passwordCheck(member, login);
        if(!result){
            throw new RuntimeException();
        }
        return login(account.getMember());
    }

    private boolean passwordCheck(Member account , RequestLogin login){
        return passwordEncoder.matches(login.getPw(), account.getPw());
    }
}
