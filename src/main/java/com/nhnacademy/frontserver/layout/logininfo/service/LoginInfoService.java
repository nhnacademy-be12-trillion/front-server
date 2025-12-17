package com.nhnacademy.frontserver.layout.logininfo.service;

import com.nhnacademy.frontserver.layout.logininfo.LoginInfo;
import com.nhnacademy.frontserver.member.MemberClient;
import com.nhnacademy.frontserver.member.MemberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginInfoService {
    private final MemberClient memberClient;

    public LoginInfo getLoginInfo(){
        MemberResponse memberResponse = memberClient.getMember();
        return LoginInfo.builder()
                .name(memberResponse.memberName())
                .grade(memberResponse.gradeName())
                .build();
    }
}
