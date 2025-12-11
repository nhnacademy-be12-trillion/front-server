package com.nhnacademy.frontserver.member;

import com.nhnacademy.frontserver.PageResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "gateway-member",
        url = "${gateway.url}")
public interface MemberClient {

    @GetMapping("/api/members")
    MemberResponse getMember();

    @GetMapping("/api/members/addresses")
    List<AddressResponse> getAddresses();

    @GetMapping("/api/members/reviews")
    PageResponse<MemberReviewResponse> getReviews(@RequestParam("page") int page,
                                                  @RequestParam("size") int size,
                                                  @RequestParam(name = "sort", defaultValue = "reviewId,desc") String sort);

    //TODO 멤버 업데이트 시 업데이트 된 유저정보 반환 필요
    @PostMapping("/api/members")
    MemberResponse updateMember(@RequestBody MemberUpdateRequest memberUpdateRequest);

    @PostMapping("/api/auth/login")
    void login(@RequestBody LoginRequest loginRequest);

    @PostMapping("/api/members/findEmail")
    String findId(@RequestBody String email);

    @PostMapping("/api/members/signup")
    String signup(@RequestBody MemberSignupRequest memberSignupRequest);

}
