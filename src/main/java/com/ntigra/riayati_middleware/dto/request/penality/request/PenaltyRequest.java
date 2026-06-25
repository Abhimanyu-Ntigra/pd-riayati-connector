package com.ntigra.riayati_middleware.dto.request.penality.request;

import lombok.Data;
import java.util.List;

@Data
public class PenaltyRequest {
    private List<ClaimPenalty> claimPenalty;
}
