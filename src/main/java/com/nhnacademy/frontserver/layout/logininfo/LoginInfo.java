package com.nhnacademy.frontserver.layout.logininfo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginInfo {
    private String name;
    private String grade;
}