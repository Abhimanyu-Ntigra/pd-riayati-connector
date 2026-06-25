package com.ntigra.riayati_middleware.mapper.dispense;

import com.ntigra.riayati_middleware.dto.request.dispense.response.DispenseAuthorization;
import com.ntigra.riayati_middleware.dto.request.dispense.response.DispenseResponse;
import com.ntigra.riayati_middleware.dto.request.dispense.response.DispenseResultDto;
import org.springframework.stereotype.Component;

@Component
public class DispenseResponseMapper {

    public DispenseResultDto map(DispenseResponse response) {
        if (response == null || response.getAuthorization() == null) {
            return DispenseResultDto.builder()
                    .authorizationStatus("ERROR")
                    .build();
        }

        DispenseAuthorization auth = response.getAuthorization();

        return DispenseResultDto.builder()
                .dispenseId(auth.getId())
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
