package com.ntigra.riayati_middleware.dto.entity.Penalty;

import com.ntigra.riayati_middleware.dto.entity.Observation;
import lombok.Builder;
import lombok.Data;

import java.util.*;

@Data
@Builder
public class ClaimPenalty {

    private String id;
    private String claimId;
    private String remittedIdPayer;
    private String claimSubmittedDate;
    private String remittanceReceivedDate;
    private String remittedPaymentReference;
    private Double claimedNetAmount;
    private Double penaltyNetAmount;
    private String penaltyReasonCode;
    private List<Observation> observation;
}
