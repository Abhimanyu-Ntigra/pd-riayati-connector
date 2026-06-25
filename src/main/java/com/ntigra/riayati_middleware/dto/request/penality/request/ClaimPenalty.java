package com.ntigra.riayati_middleware.dto.request.penality.request;

import com.ntigra.riayati_middleware.dto.request.erx.request.Observation;
import lombok.Data;
import java.util.List;

@Data
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
