package com.ntigra.riayati_middleware.mapper.authorization;

import com.ntigra.riayati_middleware.dto.request.Authorization.response.Authorization;
import com.ntigra.riayati_middleware.dto.request.Authorization.response.AuthorizationResponse;
import com.ntigra.riayati_middleware.dto.request.Authorization.response.AuthorizationResultDto;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationResponseMapper {

    public AuthorizationResultDto map(AuthorizationResponse response) {
        if (response == null || response.getPriorAuthorization() == null) {
            return AuthorizationResultDto.builder()
                    .authorizationStatus("ERROR")
                    .build();
        }

        Authorization auth = response.getPriorAuthorization().getAuthorization();

        return AuthorizationResultDto.builder()
                .requestId(auth.getId())
                .payerAuthorizationId(auth.getIdPayer())
                .authorizationStatus(mapResult(auth.getResult()))
                .denialCode(auth.getDenialCode())
                .comments(auth.getComments())
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