package com.nhnacademy.frontserver.member.controller;

import com.nhnacademy.frontserver.member.LoginRequest;
import com.nhnacademy.frontserver.member.MemberClient;
import com.nhnacademy.frontserver.member.MemberSignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final MemberClient memberClient;


    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @PostMapping("/login")
    public String doLogin(@RequestBody LoginRequest loginRequest) {
        memberClient.login(loginRequest);
        return "redirect:/";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("/signup")
    public String doSignup(@RequestBody MemberSignupRequest memberSignupRequest) {
        memberClient.signup(memberSignupRequest);
        return "redirect:/login";
    }
}
