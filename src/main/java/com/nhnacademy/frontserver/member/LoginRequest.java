package com.nhnacademy.frontserver.member;

record LoginRequest(
        String memberEmail,
        String memberPassword
) {}