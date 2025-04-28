package com.productmanagement.domain.member.service;

import com.productmanagement.common.exception.CustomException;
import com.productmanagement.common.exception.ErrorCode;
import com.productmanagement.domain.member.dto.LoginRequest;
import com.productmanagement.domain.member.dto.LoginResponse;
import com.productmanagement.domain.member.dto.SignupRequest;
import com.productmanagement.domain.member.dto.SignupResponse;
import com.productmanagement.domain.member.entity.Member;
import com.productmanagement.domain.member.repository.MemberRepository;
import com.productmanagement.domain.member.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
            .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String accessToken = jwtProvider.createToken(member.getId(), member.getEmail());
        return new LoginResponse(accessToken);
    }

    public SignupResponse signup(SignupRequest request) {
        if (memberRepository.findByEmail(request.email()).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS);
        }

        Member newMember = Member.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .build();

        Member savedMember = memberRepository.save(newMember);

        return new SignupResponse(savedMember.getEmail());
    }

}