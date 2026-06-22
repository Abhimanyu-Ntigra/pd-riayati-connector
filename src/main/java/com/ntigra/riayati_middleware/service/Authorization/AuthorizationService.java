package com.ntigra.riayati_middleware.service.Authorization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.ApiResponseDto;
import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.dto.entity.Authorization.AuthorizationResponseData;
import com.ntigra.riayati_middleware.dto.entity.Authorization.AuthorizationSubmission;
import com.ntigra.riayati_middleware.dto.request.AuthorizationRequestDto;
import com.ntigra.riayati_middleware.dto.response.AuthorizationResponseDto;
import com.ntigra.riayati_middleware.mapper.AuthorizationMapper;
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
public class AuthorizationService {

    private final RiayatiRestClient riayatiClient;
    private final AuthorizationRepository authorizationRepository;
    private final AuthorizationMapper authorizationMapper;
    private final RiayatiResponseProcessor responseProcessor;
    private final ObjectMapper objectMapper;

    // ==================== UPLOAD METHODS ====================

    /**
     * Send authorization request (Called by Controller)
     */
    public AuthorizationResponseDto sendAuthorization(AuthorizationRequestDto request) {
        log.info("Sending authorization request: {}", request.getTransactionId());

        try {
            AuthorizationSubmission submission = authorizationMapper.toAuthorizationSubmission(request);

            Map<String, Object> apiRequest = new HashMap<>();
            Map<String, Object> priorRequest = new HashMap<>();
            priorRequest.put("Header", submission.getHeader());
            priorRequest.put("Authorization", submission.getAuthorization());
            apiRequest.put("PriorRequest", priorRequest);

            ApiResponseDto response = riayatiClient.postAuthorizationRequest(apiRequest);
            responseProcessor.validateUploadResponse(response);

            String responseJson = objectMapper.writeValueAsString(response);
            authorizationRepository.updateAuthorizationAsSentWithResponse(
                    request.getId(), response.getEntityId(), responseJson
            );

            log.info("Authorization sent successfully: {}, EntityID: {}",
                    request.getTransactionId(), response.getEntityId());

            return AuthorizationResponseDto.builder()
                    .success(true)
                    .entityId(response.getEntityId())
                    .message(response.getMessage())
                    .build();

        } catch (Exception e) {
            log.error("Authorization request failed: {}", request.getTransactionId(), e);
            authorizationRepository.updateAuthorizationAsFailed(request.getId(), e.getMessage());

            return AuthorizationResponseDto.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    /**
     * Submit authorization in background (Called by Upload Thread)
     */
    public void submitAuthorizationInBackground(AuthorizationRequestDto request) {
        log.info("Background authorization upload: {}", request.getTransactionId());

        try {
            AuthorizationSubmission submission = authorizationMapper.toAuthorizationSubmission(request);

            Map<String, Object> apiRequest = new HashMap<>();
            Map<String, Object> priorRequest = new HashMap<>();
            priorRequest.put("Header", submission.getHeader());
            priorRequest.put("Authorization", submission.getAuthorization());
            apiRequest.put("PriorRequest", priorRequest);

            ApiResponseDto response = riayatiClient.postAuthorizationRequest(apiRequest);
            responseProcessor.validateUploadResponse(response);

            String responseJson = objectMapper.writeValueAsString(response);
            authorizationRepository.updateAuthorizationAsSentWithResponse(
                    request.getId(), response.getEntityId(), responseJson
            );

            log.info("Background authorization sent successfully: {}, EntityID: {}",
                    request.getTransactionId(), response.getEntityId());

        } catch (Exception e) {
            log.error("Background authorization failed: {}", request.getTransactionId(), e);

            authorizationRepository.updateRetryCount(request.getId());

            if (request.getRetryCount() != null && request.getRetryCount() + 1 >= 3) {
                authorizationRepository.updateAuthorizationAsFailed(request.getId(), e.getMessage());
            }
        }
    }

    // ==================== DOWNLOAD METHODS ====================

    /**
     * Process authorization response in background (Called by Download Thread)
     */
    public void processAuthorizationResponseInBackground(TransactionEntityDto transaction) {
        log.info("Processing authorization response for transaction: {}", transaction.getId());

        try {
            // Step 1: View full transaction details
            ApiResponseDto viewResponse = riayatiClient.viewAuthorization(transaction.getId(), 0);
            responseProcessor.validateDownloadResponse(viewResponse);

            // Step 2: Parse the response
            AuthorizationResponseData responseData = parseAuthorizationResponse(viewResponse);

            // Step 3: Build ResponseComment (concat of result, error, responseData)
            String responseComment = buildResponseComment(
                    responseData.getResult(),
                    responseData.getErrorMessage(),
                    viewResponse
            );

            // Step 4: Update database
            if (responseData.getTransactionId() != null) {
                String responseJson = objectMapper.writeValueAsString(viewResponse);

                authorizationRepository.updateAuthorizationResponse(
                        responseData.getTransactionId(),
                        responseData.getResult(),
                        responseData.getDenialCode(),
                        responseComment,
                        responseData.getIdPayer(),
                        responseData.getStartDate(),
                        responseData.getEndDate(),
                        responseData.getCoverageLimit(),
                        responseJson
                );

                log.info("Authorization response updated for: {}, Result: {}",
                        responseData.getTransactionId(), responseData.getResult());
            }

            // Step 5: Mark as downloaded in Riayati
            riayatiClient.setAuthorizationDownloaded(transaction.getId());

            log.info("Authorization response processed successfully: {}", transaction.getId());

        } catch (Exception e) {
            log.error("Failed to process authorization response: {}", transaction.getId(), e);
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Build ResponseComment - concat of Result, ErrorMessage, ResponseData
     */
    private String buildResponseComment(String result, String errorMessage, ApiResponseDto response) {
        StringBuilder comment = new StringBuilder();

        if (result != null) {
            comment.append("Result: ").append(result);
        }

        if (errorMessage != null) {
            if (comment.length() > 0) comment.append(" | ");
            comment.append("Error: ").append(errorMessage);
        }

        if (response != null) {
            if (comment.length() > 0) comment.append(" | ");
            try {
                comment.append("ResponseData: ").append(objectMapper.writeValueAsString(response));
            } catch (Exception e) {
                comment.append("ResponseData: ").append(response.toString());
            }
        }

        return comment.toString();
    }

    /**
     * Parse authorization response from Riayati
     */
    private AuthorizationResponseData parseAuthorizationResponse(ApiResponseDto response) {
        AuthorizationResponseData data = new AuthorizationResponseData();

        try {
            String responseJson = objectMapper.writeValueAsString(response);
            JsonNode rootNode = objectMapper.readTree(responseJson);

            // Navigate to PriorAuthorization → Authorization
            JsonNode entityNode = rootNode.path("Entity");
            JsonNode priorAuthNode = entityNode.path("PriorAuthorization");
            JsonNode authNode = priorAuthNode.path("Authorization");

            data.setTransactionId(authNode.path("ID").asText(null));
            data.setResult(authNode.path("Result").asText(null));
            data.setIdPayer(authNode.path("IDPayer").asText(null));
            data.setDenialCode(authNode.path("DenialCode").asText(null));
            data.setStartDate(authNode.path("Start").asText(null));
            data.setEndDate(authNode.path("End").asText(null));
            data.setCoverageLimit(authNode.path("Limit").asDouble(0.0));

            // Check for error in response
            if (response.getError() != null && !response.getError().isEmpty()) {
                data.setErrorMessage(response.getMessage());
            }

            log.info("Parsed authorization response: TransactionId={}, Result={}",
                    data.getTransactionId(), data.getResult());

        } catch (Exception e) {
            log.error("Error parsing authorization response", e);
            data.setErrorMessage(e.getMessage());
        }

        return data;
    }
}