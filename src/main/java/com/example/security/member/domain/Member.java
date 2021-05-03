package com.example.security.member.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@EqualsAndHashCode(of = "no")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long no;

    @Column(nullable = false)
    private String id;

    @Column(nullable = false)
    private String pw;

    @Column(nullable = false)
    private String email;

    @Builder
    public Member(String id, String pw, String email) {
        this.id = id;
        this.pw = pw;
        this.email = email;
    }
}
