package com.example.security.member.dto;

import com.example.security.member.domain.Member;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RequestMember {

    @NotNull
    private String id;
    private String pw;
    private String email;

    public Member toEntity() {
        return Member.builder()
            .id(id)
            .pw(pw)
            .email(email)
            .build();
    }
}
