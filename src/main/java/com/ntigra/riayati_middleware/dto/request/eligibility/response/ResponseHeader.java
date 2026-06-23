package com.ntigra.riayati_middleware.dto.request.eligibility.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseHeader {

    @JsonProperty("SenderID")
    private String senderId;

    @JsonProperty("ReceiverID")
    private String receiverId;

    @JsonProperty("TransactionDate")
    private String transactionDate;

    @JsonProperty("RecordCount")
    private Integer recordCount;

    @JsonProperty("DispositionFlag")
    private String dispositionFlag;

    @JsonProperty("PayerID")
    private String payerId;
}