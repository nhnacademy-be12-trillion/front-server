package com.nhnacademy.frontserver.layout.member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentMember {
    private String name;
    private String role; // ROLE_MEMBER, ROLE_ADMIN
}