package com.ntigra.riayati_middleware.dto.request.Authorization.request;

import lombok.Data;

@Data
public class RequestHeader {
    private String senderId;
    private String receiverId;
    private String transactionDate;
    private Integer recordCount;
    private String dispositionFlag;
    private String payerId;
}
