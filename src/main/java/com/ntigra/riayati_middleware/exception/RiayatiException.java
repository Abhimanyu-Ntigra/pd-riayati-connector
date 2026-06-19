package com.ntigra.riayati_middleware.exception;

import lombok.Getter;

@Getter
public class RiayatiException extends RuntimeException {

    private final String errorCode;

    public RiayatiException(String message) {
        super(message);
        this.errorCode = "RIAYATI_001";
    }

    public RiayatiException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "RIAYATI_002";
    }

    public RiayatiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
