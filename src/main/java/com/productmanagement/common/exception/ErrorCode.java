package com.productmanagement.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 리소스를 찾을 수 없습니다."),
    ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),
    PRODUCT_OPTION_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "상품 옵션은 최대 3개까지만 등록할 수 있습니다."),
    INVALID_OPTION_VALUES(HttpStatus.BAD_REQUEST, "선택형 옵션은 값 목록을 필수로 입력해야 합니다."),
    OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상품 옵션입니다."),
    PRODUCT_OPTION_MISMATCH(HttpStatus.BAD_REQUEST, "상품과 옵션이 일치하지 않습니다."),
    OPTION_VALUE_MISMATCH(HttpStatus.BAD_REQUEST, "상품 옵션과 옵션 값이 일치하지 않습니다."),
    OPTION_VALUE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 옵션 값입니다."),
    NO_AUTHORIZATION(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다.");

    private final HttpStatus status;
    private final String message;

}