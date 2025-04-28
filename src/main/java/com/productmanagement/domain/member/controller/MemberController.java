package com.productmanagement.domain.member.controller;

import com.productmanagement.common.response.ApiResponse;
import com.productmanagement.domain.member.dto.LoginRequest;
import com.productmanagement.domain.member.dto.LoginResponse;
import com.productmanagement.domain.member.dto.SignupRequest;
import com.productmanagement.domain.member.dto.SignupResponse;
import com.productmanagement.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원 관련 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse loginResponse = memberService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(loginResponse));
    }

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@RequestBody @Valid SignupRequest request) {
        SignupResponse signupResponse = memberService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(signupResponse));
    }

}