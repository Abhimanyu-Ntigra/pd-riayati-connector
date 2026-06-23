package com.ntigra.riayati_middleware.dto.entity.Penalty;

import com.ntigra.riayati_middleware.dto.entity.Header;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PenaltySubmission {
    private Header header;
    private List<ClaimPenalty> claimPenalty;
}
