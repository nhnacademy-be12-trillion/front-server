package com.nhnacademy.frontserver.member;

import java.time.LocalDate;

record SocialSignupRequest(
        String email,
        String name,
        LocalDate birthDate,
        String contact,
        String memberOauthId,
        AddressCreateRequest address
) {}