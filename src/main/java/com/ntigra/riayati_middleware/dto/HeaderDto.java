package com.ntigra.riayati_middleware.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HeaderDto {
    private String senderId;
    private String receiverId;
    private String transactionDate;
    private Integer recordCount;
    private String dispositionFlag;
    private String payerId;
}