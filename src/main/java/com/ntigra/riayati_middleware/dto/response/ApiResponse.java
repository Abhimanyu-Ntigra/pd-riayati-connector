package com.ntigra.riayati_middleware.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ntigra.riayati_middleware.dto.entity.Claim.TransactionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

    @JsonProperty("StatusCode")
    private Integer statusCode;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Success")
    private Boolean success;

    @JsonProperty("UserMessage")
    private String userMessage;

    @JsonProperty("MemberValidation")
    private Integer memberValidation; // 0=Disabled, 1=Failed, 2=Working

    @JsonProperty("EntityID")
    private String entityId;

    @JsonProperty("ReferenceNumber")
    private String referenceNumber;

    @JsonProperty("Error")
    private List<Map<String, Object>> error;

    private List<TransactionEntity> entities;  // For GetNew responses

    public boolean isSuccessful() {
        return success != null && success;
    }
}
