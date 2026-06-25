package com.ntigra.riayati_middleware.service.Authorization;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.request.Authorization.request.AuthorizationRequest;
import com.ntigra.riayati_middleware.dto.request.Authorization.request.AuthorizationRequestDto;
import com.ntigra.riayati_middleware.mapper.authorization.AuthorizationRequestMapper;
import com.ntigra.riayati_middleware.service.Eligibility.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final AuthorizationRequestMapper mapper;
    private final RiayatiRestClient client;
    private final TransactionHistoryService historyService;

    public String submit(AuthorizationRequestDto dto) {
        log.info("Submitting authorization request for member: {}", dto.getMemberId());

        try {
            AuthorizationRequest request = mapper.map(dto);
            String response = client.submitAuthorization(request);

            // Log the transaction
            historyService.log("AUTHORIZATION_REQUEST", request, response);

            return response;

        } catch (Exception e) {
            log.error("Authorization submission failed", e);
            return "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    public String getStatus(String transactionId) {
        log.info("Getting authorization status for: {}", transactionId);

        try {
            return client.getAuthorizationStatus(transactionId);
        } catch (Exception e) {
            log.error("Failed to get authorization status", e);
            return "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}";
        }
    }
}