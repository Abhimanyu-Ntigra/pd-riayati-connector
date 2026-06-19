package com.ntigra.riayati_middleware.scheduler.authorization;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.ApiResponseDto;
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
public class AuthorizationUploadBackgroundService {

    private final RiayatiRestClient riayatiClient;
    private final AuthorizationRepository authorizationRepository;
    private final AuthorizationRequestMapper authorizationMapper;
    private final RiayatiResponseProcessor responseProcessor;

    public void submitAuthorizationInBackground(AuthorizationRequestDto request) {
        try {
            AuthorizationSubmission submission = authorizationMapper.toAuthorizationSubmission(request);

            Map<String, Object> apiRequest = new HashMap<>();
            Map<String, Object> priorRequest = new HashMap<>();
            priorRequest.put("Header", submission.getHeader());
            priorRequest.put("Authorization", submission.getAuthorization());
            apiRequest.put("PriorRequest", priorRequest);

            ApiResponseDto response = riayatiClient.postAuthorizationRequest(apiRequest);
            responseProcessor.validateUploadResponse(response);
            authorizationRepository.updateAuthorizationAsSent(request.getId(), response.getEntityId());

        } catch (Exception e) {
            authorizationRepository.updateRetryCount(request.getId());
            if (request.getRetryCount() + 1 >= 3) {
                authorizationRepository.updateAuthorizationAsFailed(request.getId(), e.getMessage());
            }
        }
    }
}
