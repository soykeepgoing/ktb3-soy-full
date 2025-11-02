package com.soy.springcommunity.dto;

import com.soy.springcommunity.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UsersApiResponse {
    public static <T> ResponseEntity<CommonResponse<T>> ok(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status)
                .body(CommonResponse.success(status.value(), message, data));
    }

    public static <T> ResponseEntity<CommonResponse<T>> created(HttpStatus status, String message, T data){
        return ResponseEntity.status(status)
                .body(CommonResponse.success(status.value(), message, data));
    }

    public static <T> ResponseEntity<CommonResponse<T>> fail(BusinessException e) {
        return ResponseEntity.status(e.getStatus())
                .body(CommonResponse.fail(e.getStatus().value(), e.getMessage()));
    }
}
