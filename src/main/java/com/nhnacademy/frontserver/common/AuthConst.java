package com.nhnacademy.frontserver.common;

public class AuthConst {
    // [문법 설명] ${프로퍼티키:기본값}
    // 만약 app.auth.header.member-id 설정이 없으면 자동으로 "X-USER-ID"를 사용함
    public static final String HEADER_MEMBER_ID = "${app.auth.header.member-id:X-USER-ID}";
    public static final String HEADER_GUEST_ID = "${app.auth.header.guest-id:guestId}";
}
