package com.ntigra.riayati_middleware.service.Eligibility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.ApiResponseDto;
import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.dto.entity.Eligibility.EligibilityResponseData;
import com.ntigra.riayati_middleware.dto.entity.Eligibility.EligibilitySubmission;
import com.ntigra.riayati_middleware.dto.request.EligibilityRequestDto;
import com.ntigra.riayati_middleware.dto.response.EligibilityResponseDto;
import com.ntigra.riayati_middleware.mapper.EligibilityMapper;
import com.ntigra.riayati_middleware.respository.EligibilityRepository;
import com.ntigra.riayati_middleware.service.RiayatiResponseProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EligibilityServiceOld {

    private final RiayatiRestClient riayatiClient;
    private final EligibilityRepository eligibilityRepository;
    private final EligibilityMapper eligibilityMapper;
    private final RiayatiResponseProcessor responseProcessor;
    private final ObjectMapper objectMapper;

    // ==================== UPLOAD METHODS ====================

    /**
     * Check eligibility (Called by Controller)
     */
    public EligibilityResponseDto checkEligibility(EligibilityRequestDto request) {
        log.info("Checking eligibility for member: {}", request.getMemberId());

        try {
            EligibilitySubmission submission = eligibilityMapper.toEligibilitySubmission(request);

            Map<String, Object> apiRequest = new HashMap<>();
            Map<String, Object> priorRequest = new HashMap<>();
            priorRequest.put("Header", submission.getHeader());
            priorRequest.put("Authorization", submission.getAuthorization());
            apiRequest.put("PriorRequest", priorRequest);

            ApiResponseDto response = riayatiClient.postEligibilityRequest(apiRequest);
            responseProcessor.validateUploadResponse(response);

            String responseJson = objectMapper.writeValueAsString(response);
            eligibilityRepository.updateEligibilityAsSentWithResponse(request.getId(), response.getEntityId(), responseJson);
            // where to save the response ??

            log.info("Eligibility check sent successfully: {}", request.getTransactionId());

            return EligibilityResponseDto.builder()
                    .success(true)
                    .entityId(response.getEntityId())
                    .message(response.getMessage())
                    .build();

        } catch (Exception e) {
            log.error("Eligibility check failed: {}", request.getTransactionId(), e);
            eligibilityRepository.updateEligibilityAsFailed(request.getId(), e.getMessage());

            return EligibilityResponseDto.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    /**
     * Submit eligibility in background (Called by Upload Thread)
     */
    public void submitEligibilityInBackground(EligibilityRequestDto request) {
        log.info("Background eligibility check: {}", request.getTransactionId());

        try {
            EligibilitySubmission submission = eligibilityMapper.toEligibilitySubmission(request);

            Map<String, Object> apiRequest = new HashMap<>();
            Map<String, Object> priorRequest = new HashMap<>();
            priorRequest.put("Header", submission.getHeader());
            priorRequest.put("Authorization", submission.getAuthorization());
            apiRequest.put("PriorRequest", priorRequest);

            ApiResponseDto response = riayatiClient.postEligibilityRequest(apiRequest);
            responseProcessor.validateUploadResponse(response);

            String responseJson = objectMapper.writeValueAsString(response);
            eligibilityRepository.updateEligibilityAsSentWithResponse(request.getId(), response.getEntityId(), responseJson);
            // where to save the response ??

            log.info("Background eligibility sent successfully: {}", request.getTransactionId());

        } catch (Exception e) {
            log.error("Background eligibility failed: {}", request.getTransactionId(), e);

            eligibilityRepository.updateRetryCount(request.getId());

            if (request.getRetryCount() != null && request.getRetryCount() + 1 >= 3) {
                eligibilityRepository.updateEligibilityAsFailed(request.getId(), e.getMessage());
            }
        }
    }

    // ==================== DOWNLOAD METHODS ====================

    /**
     * Process eligibility response in background (Called by Download Thread)
     */
    public void processEligibilityResponseInBackground(TransactionEntityDto transaction) {
        log.info("Processing eligibility response for transaction: {}", transaction.getId());

        try {
            // Step 1: View full transaction details
            ApiResponseDto viewResponse = riayatiClient.viewEligibility(transaction.getId(), 0);
            responseProcessor.validateDownloadResponse(viewResponse);

            // Step 2: Parse response data
            EligibilityResponseData responseData = parseEligibilityResponse(viewResponse);

            // Step 3: Update database
            if (responseData.getTransactionId() != null) {
                String responseJson = objectMapper.writeValueAsString(viewResponse);
                eligibilityRepository.updateEligibilityResponse(
                        responseData.getTransactionId(),
                        responseData.getResult(),
                        responseData.getIdPayer(),
                        responseData.getDenialCode(),
                        responseData.getStartDate(),
                        responseData.getEndDate(),
                        responseData.getLimit(),
                        responseJson
                );
                log.info("Eligibility response updated for: {}, Result: {}",
                        responseData.getTransactionId(), responseData.getResult());
            }

            // Step 4: Mark as downloaded in Riayati
            riayatiClient.setEligibilityDownloaded(transaction.getId());

            log.info("Eligibility response processed successfully: {}", transaction.getId());

        } catch (Exception e) {
            log.error("Failed to process eligibility response: {}", transaction.getId(), e);
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Parse eligibility response from Riayati
     */
    private EligibilityResponseData parseEligibilityResponse(ApiResponseDto response) {
        EligibilityResponseData data = new EligibilityResponseData();

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
            data.setLimit(authNode.path("Limit").asDouble(0.0));

            log.info("Parsed eligibility response: TransactionId={}, Result={}",
                    data.getTransactionId(), data.getResult());

        } catch (Exception e) {
            log.error("Error parsing eligibility response", e);
        }

        return data;
    }

}