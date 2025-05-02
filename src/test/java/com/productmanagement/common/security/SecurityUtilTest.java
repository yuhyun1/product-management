package com.productmanagement.common.security;

import com.productmanagement.common.exception.CustomException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityUtilTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentMemberId_정상반환() {
        // given
        Long expectedId = 123L;
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(expectedId, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        Long actualId = SecurityUtil.getCurrentMemberId();

        // then
        assertThat(actualId).isEqualTo(expectedId);
    }

    @Test
    void getCurrentMemberId_비정상컨텍스트_예외발생() {
        // given
        SecurityContextHolder.clearContext();

        // expect
        assertThatThrownBy(SecurityUtil::getCurrentMemberId)
            .isInstanceOf(CustomException.class)
            .hasMessage("인증이 필요합니다.");
    }

    @Test
    void getCurrentMemberId_형식이잘못된Principal_예외발생() {
        // given
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken("not-a-number", null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // expect
        assertThatThrownBy(SecurityUtil::getCurrentMemberId)
            .isInstanceOf(CustomException.class)
            .hasMessage("유효하지 않은 토큰입니다.");
    }

}
