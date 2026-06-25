package com.ntigra.riayati_middleware.service.Penalty;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.request.penality.request.PenaltyRequest;
import com.ntigra.riayati_middleware.dto.request.penality.request.PenaltyRequestDto;
import com.ntigra.riayati_middleware.mapper.penality.PenaltyRequestMapper;
import com.ntigra.riayati_middleware.service.Eligibility.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PenaltyService {

    private final PenaltyRequestMapper mapper;
    private final RiayatiRestClient client;
    private final TransactionHistoryService historyService;

    public String submit(PenaltyRequestDto dto) {
        log.info("Submitting penalty request for claim: {}", dto.getClaimId());

        try {
            PenaltyRequest request = mapper.map(dto);
            String response = client.submitPenalty(request);

            historyService.log("PENALTY_REQUEST", request, response);

            return response;

        } catch (Exception e) {
            log.error("Penalty submission failed", e);
            return "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    public String getStatus(String claimId) {
        log.info("Getting penalty status for claim: {}", claimId);

        try {
            return client.getPenaltyStatus(claimId);
        } catch (Exception e) {
            log.error("Failed to get penalty status", e);
            return "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}";
        }
    }
}