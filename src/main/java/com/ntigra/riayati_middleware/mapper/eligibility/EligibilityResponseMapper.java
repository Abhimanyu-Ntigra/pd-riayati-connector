package com.ntigra.riayati_middleware.mapper.eligibility;


import com.ntigra.riayati_middleware.dto.request.eligibility.response.AuthorizationResponse;
import com.ntigra.riayati_middleware.dto.request.eligibility.response.EligibilityResponse;
import com.ntigra.riayati_middleware.dto.request.eligibility.response.EligibilityResultDto;
import org.springframework.stereotype.Component;

@Component
public class EligibilityResponseMapper {

    public EligibilityResultDto map(
            EligibilityResponse response) {

        AuthorizationResponse authorization =
                response.getPriorAuthorization()
                        .getAuthorization();

        return EligibilityResultDto.builder()
                .requestId(
                        authorization.getId())
                .payerAuthorizationId(
                        authorization.getIdPayer())
                .eligibilityStatus(
                        mapResult(
                                authorization.getResult()))
                .denialCode(
                        authorization.getDenialCode())
                .comments(
                        authorization.getComments())
                .startDate(
                        authorization.getStart())
                .endDate(
                        authorization.getEnd())
                .limitAmount(
                        authorization.getLimit())
                .build();
    }

    private String mapResult(
            String result) {

        if ("Yes".equalsIgnoreCase(result)) {
            return "ELIGIBLE";
        }

        return "NOT_ELIGIBLE";
    }
}