package com.ntigra.riayati_middleware.mapper;

import com.ntigra.riayati_middleware.config.RiayatiProperties;
import com.ntigra.riayati_middleware.dto.entity.Header;
import com.ntigra.riayati_middleware.dto.entity.Observation;
import com.ntigra.riayati_middleware.dto.entity.Penalty.ClaimPenalty;
import com.ntigra.riayati_middleware.dto.entity.Penalty.PenaltySubmission;
import com.ntigra.riayati_middleware.dto.request.PenaltyRequestDto;
import com.ntigra.riayati_middleware.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PenaltyMapper {

    private final RiayatiProperties properties;
    private final DateUtil dateUtil;

    public PenaltySubmission toPenaltySubmission(PenaltyRequestDto dto) {
        String currentDate = dateUtil.getCurrentDateTime();

        // ==================== BUILD HEADER (REUSE) ====================
        Header header = Header.builder()
                .senderId(dto.getSenderId() != null ? dto.getSenderId() : properties.getSenderId())
                .receiverId(dto.getReceiverId() != null ? dto.getReceiverId() : properties.getReceiverId())
                .transactionDate(currentDate)
                .recordCount(1)
                .dispositionFlag("PRODUCTION")
                .payerId(dto.getPayerId() != null ? dto.getPayerId() : properties.getPayerId())
                .build();

        // ==================== BUILD OBSERVATION ====================
        List<Observation> observations = new ArrayList<>();
        observations.add(Observation.builder()
                .type("Text")
                .code("Description")
                .value("Penalty for late payment")  // Constant
                .valueType("Other")
                .build());

        // ==================== BUILD CLAIM PENALTY ====================
        ClaimPenalty claimPenalty = ClaimPenalty.builder()
                .id(dto.getPenaltyId())
                .claimId(dto.getClaimId())
                .remittedIdPayer(dto.getRemittedIdPayer())
                .claimSubmittedDate(dto.getClaimSubmittedDate())
                .remittanceReceivedDate(dto.getRemittanceReceivedDate())
                .remittedPaymentReference(dto.getRemittedPaymentReference())
                .claimedNetAmount(dto.getClaimedNetAmount())
                .penaltyNetAmount(100.0)  // Constant
                .penaltyReasonCode("LATE_PAYMENT")  // Constant
                .observation(observations)
                .build();

        List<ClaimPenalty> claimPenalties = new ArrayList<>();
        claimPenalties.add(claimPenalty);

        // ==================== BUILD SUBMISSION ====================
        return PenaltySubmission.builder()
                .header(header)
                .claimPenalty(claimPenalties)
                .build();
    }
}