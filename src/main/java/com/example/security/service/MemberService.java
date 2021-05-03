package com.example.security.service;

import com.example.security.config.UserAccount;
import com.example.security.member.dao.MemberRepository;
import com.example.security.member.domain.Member;
import com.example.security.member.dto.RequestMember;
import com.example.security.member.dto.ResponseMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;


    public ResponseMember save(RequestMember member) {
        String encode = passwordEncoder.encode(member.getPw());
        member.setPw(encode);
        Member save = memberRepository.save(member.toEntity());
        login(save);
        SecurityContextHolder.getContext().getAuthentication();
        return modelMapper.map(save,ResponseMember.class);
    }

    private void login(Member member) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            new UserAccount(member),
            member.getPw(),
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username);
        if(member == null){
            throw new UsernameNotFoundException(username);
        }
        return new UserAccount(member);
    }
}
