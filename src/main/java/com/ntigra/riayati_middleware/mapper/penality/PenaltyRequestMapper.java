package com.ntigra.riayati_middleware.mapper.penality;

import com.ntigra.riayati_middleware.config.RiayatiProperties;
import com.ntigra.riayati_middleware.dto.request.erx.request.Observation;
import com.ntigra.riayati_middleware.dto.request.penality.request.ClaimPenalty;
import com.ntigra.riayati_middleware.dto.request.penality.request.PenaltyRequest;
import com.ntigra.riayati_middleware.dto.request.penality.request.PenaltyRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PenaltyRequestMapper {

    private final RiayatiProperties properties;
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PenaltyRequest map(PenaltyRequestDto dto) {
        ClaimPenalty claimPenalty = buildClaimPenalty(dto);

        PenaltyRequest request = new PenaltyRequest();
        request.setClaimPenalty(new ArrayList<>());
        request.getClaimPenalty().add(claimPenalty);

        return request;
    }

    private ClaimPenalty buildClaimPenalty(PenaltyRequestDto dto) {
        ClaimPenalty claimPenalty = new ClaimPenalty();
        claimPenalty.setId(dto.getPenaltyId());
        claimPenalty.setClaimId(dto.getClaimId());
        claimPenalty.setRemittedIdPayer(dto.getRemittedIdPayer());
        claimPenalty.setClaimSubmittedDate(dto.getClaimSubmittedDate());
        claimPenalty.setRemittanceReceivedDate(dto.getRemittanceReceivedDate());
        claimPenalty.setRemittedPaymentReference(dto.getRemittedPaymentReference());
        claimPenalty.setClaimedNetAmount(dto.getClaimedNetAmount());
        claimPenalty.setPenaltyNetAmount(dto.getPenaltyNetAmount() != null ? dto.getPenaltyNetAmount() : 100.0);
        claimPenalty.setPenaltyReasonCode(dto.getPenaltyReasonCode() != null ? dto.getPenaltyReasonCode() : "LATE_PAYMENT");

        // Build Observation
        List<Observation> observations = new ArrayList<>();
        Observation observation = new Observation();
        observation.setType("Text");
        observation.setCode("Description");
        observation.setValue(dto.getObservationText() != null ? dto.getObservationText() : "Penalty for late payment");
        observation.setValueType("Other");
        observations.add(observation);
        claimPenalty.setObservation(observations);

        return claimPenalty;
    }

    private String now() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }
}