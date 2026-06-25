package com.ntigra.riayati_middleware.mapper.penality;

import com.ntigra.riayati_middleware.dto.request.penality.response.PenaltyAuthorization;
import com.ntigra.riayati_middleware.dto.request.penality.response.PenaltyResponse;
import com.ntigra.riayati_middleware.dto.request.penality.response.PenaltyResultDto;
import org.springframework.stereotype.Component;

@Component
public class PenaltyResponseMapper {

    public PenaltyResultDto map(PenaltyResponse response) {
        if (response == null || response.getAuthorization() == null) {
            return PenaltyResultDto.builder()
                    .penaltyStatus("ERROR")
                    .build();
        }

        PenaltyAuthorization auth = response.getAuthorization();

        return PenaltyResultDto.builder()
                .claimId(auth.getId())
                .payerPenaltyId(auth.getIdPayer())
                .penaltyStatus(mapResult(auth.getResult()))
                .denialCode(auth.getDenialCode())
                .settlementDate(auth.getPenaltyDateSettlement())
                .paymentAmount(auth.getPenaltyPaymentAmount())
                .comments(auth.getComments())
                .build();
    }

    private String mapResult(String result) {
        if ("Approved".equalsIgnoreCase(result)) {
            return "APPROVED";
        } else if ("Rejected".equalsIgnoreCase(result)) {
            return "REJECTED";
        }
        return "PENDING";
    }
}
