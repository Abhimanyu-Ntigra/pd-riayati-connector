package com.ntigra.riayati_middleware.service.Authorization;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.ApiResponseDto;
import com.ntigra.riayati_middleware.dto.entity.Authorization.AuthorizationSubmission;
import com.ntigra.riayati_middleware.dto.request.AuthorizationRequestDto;
import com.ntigra.riayati_middleware.dto.response.AuthorizationResponseDto;
import com.ntigra.riayati_middleware.mapper.AuthorizationRequestMapper;
import com.ntigra.riayati_middleware.service.RiayatiResponseProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final RiayatiRestClient riayatiClient;
    private final AuthorizationRequestMapper authorizationMapper;
    private final RiayatiResponseProcessor responseProcessor;

    public AuthorizationResponseDto sendAuthorization(AuthorizationRequestDto request) {
        try {
            AuthorizationSubmission submission = authorizationMapper.toAuthorizationSubmission(request);

            Map<String, Object> apiRequest = new HashMap<>();
            Map<String, Object> priorRequest = new HashMap<>();
            priorRequest.put("Header", submission.getHeader());
            priorRequest.put("Authorization", submission.getAuthorization());
            apiRequest.put("PriorRequest", priorRequest);

            ApiResponseDto response = riayatiClient.postAuthorizationRequest(apiRequest);
            responseProcessor.validateUploadResponse(response);

            return AuthorizationResponseDto.builder()
                    .success(true)
                    .entityId(response.getEntityId())
                    .message(response.getMessage())
                    .build();

        } catch (Exception e) {
            log.error("Authorization request failed", e);
            return AuthorizationResponseDto.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
}