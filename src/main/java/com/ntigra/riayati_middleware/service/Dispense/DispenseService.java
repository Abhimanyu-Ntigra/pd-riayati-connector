package com.ntigra.riayati_middleware.service.Dispense;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.request.dispense.request.DispenseRequest;
import com.ntigra.riayati_middleware.dto.request.dispense.request.DispenseRequestDto;
import com.ntigra.riayati_middleware.mapper.dispense.DispenseRequestMapper;
import com.ntigra.riayati_middleware.service.Eligibility.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispenseService {

    private final DispenseRequestMapper mapper;
    private final RiayatiRestClient client;
    private final TransactionHistoryService historyService;

    public String submit(DispenseRequestDto dto) {
        log.info("Submitting dispense request for: {}", dto.getDispenseId());

        try {
            DispenseRequest request = mapper.map(dto);
            String response = client.submitDispense(request);

            historyService.log("DISPENSE_REQUEST", request, response);

            return response;

        } catch (Exception e) {
            log.error("Dispense submission failed", e);
            return "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    public String getStatus(String dispenseId) {
        log.info("Getting dispense status for: {}", dispenseId);

        try {
            return client.getDispenseStatus(dispenseId);
        } catch (Exception e) {
            log.error("Failed to get dispense status", e);
            return "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}";
        }
    }
}
