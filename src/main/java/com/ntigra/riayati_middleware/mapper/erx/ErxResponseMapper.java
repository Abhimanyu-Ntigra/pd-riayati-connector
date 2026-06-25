package com.ntigra.riayati_middleware.mapper.erx;

import com.ntigra.riayati_middleware.dto.request.erx.response.ErxAuthorization;
import com.ntigra.riayati_middleware.dto.request.erx.response.ErxResponse;
import com.ntigra.riayati_middleware.dto.request.erx.response.ErxResultDto;
import org.springframework.stereotype.Component;

@Component
public class ErxResponseMapper {

    public ErxResultDto map(ErxResponse response) {
        if (response == null || response.getAuthorization() == null) {
            return ErxResultDto.builder()
                    .authorizationStatus("ERROR")
                    .build();
        }

        ErxAuthorization auth = response.getAuthorization();

        return ErxResultDto.builder()
                .prescriptionId(auth.getId())
                .payerAuthorizationId(auth.getIdPayer())
                .authorizationStatus(mapResult(auth.getResult()))
                .denialCode(auth.getDenialCode())
                .startDate(auth.getStart())
                .endDate(auth.getEnd())
                .limitAmount(auth.getLimit())
                .build();
    }

    private String mapResult(String result) {
        if ("Yes".equalsIgnoreCase(result)) {
            return "APPROVED";
        } else if ("No".equalsIgnoreCase(result)) {
            return "REJECTED";
        }
        return "PENDING";
    }
}