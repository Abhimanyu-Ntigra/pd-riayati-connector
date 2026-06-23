package com.ntigra.riayati_middleware.mapper.eligibility;


import com.ntigra.riayati_middleware.config.RiayatiProperties;
import com.ntigra.riayati_middleware.dto.request.eligibility.request.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EligibilityRequestMapper {

    private final RiayatiProperties properties;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public EligibilityRequest map(
            EligibilityRequestDto dto) {

        RequestHeader header = buildHeader(dto);

        EncounterRequest encounter =
                buildEncounter(dto);

        AuthorizationRequest authorization =
                buildAuthorization(
                        dto,
                        encounter);

        PriorRequest priorRequest =
                new PriorRequest();

        priorRequest.setHeader(header);
        priorRequest.setAuthorization(authorization);

        EligibilityRequest request =
                new EligibilityRequest();

        request.setPriorRequest(priorRequest);

        return request;
    }

    private RequestHeader buildHeader(
            EligibilityRequestDto dto) {

        RequestHeader header =
                new RequestHeader();

        header.setSenderId(
                dto.getSenderId());

        header.setReceiverId(
                dto.getReceiverId());

        header.setTransactionDate(
                now());

        header.setRecordCount(1);

        header.setDispositionFlag(
                dto.getDispositionFlag());

        header.setPayerId(
                dto.getPayerId());

        return header;
    }

    private EncounterRequest buildEncounter(
            EligibilityRequestDto dto) {

        EncounterRequest encounter =
                new EncounterRequest();

        encounter.setFacilityId(
                dto.getSenderId());

        encounter.setType(
                dto.getEncounterType());

        return encounter;
    }

    private AuthorizationRequest buildAuthorization(
            EligibilityRequestDto dto,
            EncounterRequest encounter) {

        AuthorizationRequest authorization =
                new AuthorizationRequest();

        authorization.setType(
                "Eligibility");

        authorization.setRequestType(
                "New");

        authorization.setId(
                generateRequestId());

        authorization.setGender(
                dto.getGender());

        authorization.setMemberId(
                dto.getMemberId());

        authorization.setEmiratesIdNumber(
                dto.getEmiratesId());

        authorization.setDateOrdered(
                now());

        authorization.setDateOfBirth(
                dto.getDateOfBirth()
                        .format(DATE_TIME_FORMATTER));

        authorization.setEncounter(
                encounter);

        authorization.setDiagnosis(
                Collections.emptyList());

        authorization.setActivity(
                Collections.emptyList());

        return authorization;
    }

    private String generateRequestId() {

        return "ELG-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12);
    }

    private String now() {

        return LocalDateTime.now()
                .format(DATE_TIME_FORMATTER);
    }
}