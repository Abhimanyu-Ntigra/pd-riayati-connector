package com.ntigra.riayati_middleware.dto.polling;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetNewEntity {

    @JsonProperty("ID")
    private String id;

    @JsonProperty("SenderID")
    private String senderId;

    @JsonProperty("ReceiverID")
    private String receiverId;

    @JsonProperty("RecordCount")
    private Integer recordCount;

    @JsonProperty("TransactionDate")
    private String transactionDate;

    @JsonProperty("CreationDate")
    private String creationDate;

    @JsonProperty("Downloaded")
    private Boolean downloaded;

    @JsonProperty("DownloadedDateGenerated")
    private String downloadedDateGenerated;
}