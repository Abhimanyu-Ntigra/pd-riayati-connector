package com.ntigra.riayati_middleware.dto.entity.Authorization;

import com.ntigra.riayati_middleware.dto.entity.Header;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationResponseData {

    private String transactionId;
    private String result;
    private String idPayer;
    private String denialCode;
    private String startDate;
    private String endDate;
    private Double coverageLimit;
    private String errorMessage;

}
