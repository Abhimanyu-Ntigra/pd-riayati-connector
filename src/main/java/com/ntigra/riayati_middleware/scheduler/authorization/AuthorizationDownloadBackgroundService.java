package com.ntigra.riayati_middleware.scheduler.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.ApiResponseDto;
import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.dto.entity.Authorization.AuthorizationSubmission;
import com.ntigra.riayati_middleware.dto.request.AuthorizationRequestDto;
import com.ntigra.riayati_middleware.dto.response.AuthorizationResponseDto;
import com.ntigra.riayati_middleware.mapper.AuthorizationRequestMapper;
import com.ntigra.riayati_middleware.respository.AuthorizationRepository;
import com.ntigra.riayati_middleware.service.RiayatiResponseProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationDownloadBackgroundService {

    private final RiayatiRestClient riayatiClient;
    private final AuthorizationRepository authorizationRepository;
    private final RiayatiResponseProcessor responseProcessor;
    private final ObjectMapper objectMapper;

    public void processAuthorizationResponse(TransactionEntityDto transaction) {
        try {
            ApiResponseDto response = riayatiClient.viewAuthorization(transaction.getId(), 0);
            responseProcessor.validateDownloadResponse(response);

            String responseJson = objectMapper.writeValueAsString(response);

            // Parse response data
            String transactionId = response.getEntityId();
            String result = "Yes";
            String idPayer = null;
            String denialCode = null;
            String startDate = null;
            String endDate = null;
            Double limit = null;

            authorizationRepository.updateAuthorizationResponse(
                    transactionId, result, idPayer, denialCode, startDate, endDate, limit, responseJson
            );

            riayatiClient.setAuthorizationDownloaded(transaction.getId());

        } catch (Exception e) {
            log.error("Failed to process authorization response: {}", transaction.getId(), e);
        }
    }
}