package com.ntigra.riayati_middleware.dto.entity;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@Builder
public class Header {
    private String senderId;
    private String receiverId;
    private String transactionDate;
    private Integer recordCount;
    private String dispositionFlag;
    private String payerId;
}
