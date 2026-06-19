package com.ntigra.riayati_middleware.dto.entity.Claim;

import lombok.Data;

@Data
public class TransactionEntity {

    private String id;           // Auto-generated
    private String senderId;
    private String receiverId;
    private Integer recordCount;
    private String transactionDate;
    private String creationDate;
    private Boolean downloaded;
    private String downloadedDateGeneratedString;
}