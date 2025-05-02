package com.productmanagement.domain.member.service;

import com.productmanagement.common.exception.CustomException;
import com.productmanagement.common.security.JwtProvider;
import com.productmanagement.domain.member.dto.LoginRequest;
import com.productmanagement.domain.member.dto.LoginResponse;
import com.productmanagement.domain.member.dto.SignupRequest;
import com.productmanagement.domain.member.dto.SignupResponse;
import com.productmanagement.domain.member.entity.Member;
import com.productmanagement.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;


    @Test
    void 로그인_성공() {
        // given
        String rawPassword = "password123";
        Member member = Member.builder()
            .id(1L)
            .email("user@email.com")
            .password("encoded_pw")
            .build();

        LoginRequest request = new LoginRequest("user@email.com", rawPassword);

        given(memberRepository.findByEmail(request.email())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(rawPassword, "encoded_pw")).willReturn(true);
        given(jwtProvider.createToken(member.getId(), member.getEmail())).willReturn("mocked-jwt-token");

        // when
        LoginResponse response = memberService.login(request);

        // then
        assertThat(response.accessToken()).isEqualTo("mocked-jwt-token");
    }

    @Test
    void 로그인_실패_비밀번호불일치() {
        // given
        Member member = Member.builder()
            .email("user@email.com")
            .password("encoded_pw")
            .build();
        LoginRequest request = new LoginRequest("user@email.com", "wrong_pw");

        given(memberRepository.findByEmail(request.email())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // expect
        assertThatThrownBy(() -> memberService.login(request))
            .isInstanceOf(CustomException.class);
    }

    @Test
    void 회원가입_성공() {
        // given
        SignupRequest request = new SignupRequest("test@email.com", "password123");
        Member savedMember = Member.builder()
            .id(1L)
            .email(request.email())
            .password("encoded_pw")
            .build();

        given(memberRepository.existsByEmail(request.email())).willReturn(false);
        given(passwordEncoder.encode(request.password())).willReturn("encoded_pw");
        given(memberRepository.save(any(Member.class))).willReturn(savedMember);

        // when
        SignupResponse response = memberService.signup(request);

        // then
        assertThat(response.email()).isEqualTo("test@email.com");
    }

    @Test
    void 회원가입_실패_중복이메일() {
        // given
        SignupRequest request = new SignupRequest("test@email.com", "password123");

        given(memberRepository.existsByEmail(request.email())).willReturn(true);

        // expect
        assertThatThrownBy(() -> memberService.signup(request))
            .isInstanceOf(CustomException.class);
    }
}
