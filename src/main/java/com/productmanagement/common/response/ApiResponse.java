package com.productmanagement.common.response;

import com.productmanagement.common.exception.ErrorCode;
import com.productmanagement.common.exception.SuccessCode;

public record ApiResponse<T>(
    boolean success,
    int status,
    T data,
    String message
) {
    public static <T> ApiResponse<T> success(SuccessCode successCode, T data) {
        return new ApiResponse<>(true, successCode.getHttpStatus().value(), data, successCode.getMessage());
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getStatus().value(), null, errorCode.getMessage());
    }
}