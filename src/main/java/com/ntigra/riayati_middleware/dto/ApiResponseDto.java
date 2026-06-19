package com.ntigra.riayati_middleware.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ApiResponseDto {
    private Integer statusCode;
    private String message;
    private Boolean success;
    private String userMessage;
    private Integer memberValidation;
    private String entityId;
    private String referenceNumber;
    private List<Map<String, Object>> error;
    private List<TransactionEntityDto> entities;
}
