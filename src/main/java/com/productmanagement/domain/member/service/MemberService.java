package com.productmanagement.domain.member.service;

import com.productmanagement.common.exception.CustomException;
import com.productmanagement.common.exception.ErrorCode;
import com.productmanagement.domain.member.dto.LoginRequest;
import com.productmanagement.domain.member.dto.LoginResponse;
import com.productmanagement.domain.member.dto.SignupRequest;
import com.productmanagement.domain.member.dto.SignupResponse;
import com.productmanagement.domain.member.entity.Member;
import com.productmanagement.domain.member.repository.MemberRepository;
import com.productmanagement.common.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest request) {
        log.info("[로그인 시도] email={}", request.email());

        Member member = memberRepository.findByEmail(request.email())
            .orElseThrow(() -> {
                log.warn("[로그인 실패] 존재하지 않는 이메일: {}", request.email());
                return new CustomException(ErrorCode.UNAUTHORIZED);
            });

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            log.warn("[로그인 실패] 비밀번호 불일치 - email: {}", request.email());
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        log.info("[로그인 성공] memberId={}, email={}", member.getId(), member.getEmail());
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

        log.info("[회원가입 완료] memberId={}, email={}", newMember.getId(), newMember.getEmail());
        return new SignupResponse(savedMember.getEmail());
    }

}