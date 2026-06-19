package com.ntigra.riayati_middleware.dto;

import lombok.Data;

@Data
public class TransactionEntityDto {
    private String id;
    private String senderId;
    private String receiverId;
    private Integer recordCount;
    private String transactionDate;
    private String creationDate;
    private Boolean downloaded;
}
