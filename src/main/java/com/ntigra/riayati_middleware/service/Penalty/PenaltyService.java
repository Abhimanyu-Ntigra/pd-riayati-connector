package com.ntigra.riayati_middleware.service.Penalty;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.ApiResponseDto;
import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.dto.entity.Penalty.PenaltyResponse;
import com.ntigra.riayati_middleware.dto.entity.Penalty.PenaltyResponseData;
import com.ntigra.riayati_middleware.dto.entity.Penalty.PenaltySubmission;
import com.ntigra.riayati_middleware.dto.response.PenaltyResponseDto;
import com.ntigra.riayati_middleware.dto.request.PenaltyRequestDto;
import com.ntigra.riayati_middleware.dto.response.PenaltyResponseDto;
import com.ntigra.riayati_middleware.mapper.PenaltyMapper;
import com.ntigra.riayati_middleware.respository.PenaltyRepository;
import com.ntigra.riayati_middleware.service.RiayatiResponseProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PenaltyService {

    private final RiayatiRestClient riayatiClient;
    private final PenaltyRepository penaltyRepository;
    private final PenaltyMapper penaltyMapper;
    private final RiayatiResponseProcessor responseProcessor;
    private final ObjectMapper objectMapper;

    // ==================== UPLOAD METHODS ====================

    public PenaltyResponseDto sendPenalty(PenaltyRequestDto request) {
        log.info("Sending penalty for claim: {}", request.getClaimId());

        try {
            PenaltySubmission submission = penaltyMapper.toPenaltySubmission(request);

            Map<String, Object> apiRequest = new HashMap<>();
            Map<String, Object> penaltyMap = new HashMap<>();
            penaltyMap.put("Header", submission.getHeader());
            penaltyMap.put("ClaimPenalty", submission.getClaimPenalty());
            apiRequest.put("PenaltySubmission", penaltyMap);

            ApiResponseDto response = riayatiClient.submitPenalty(apiRequest);
            responseProcessor.validateUploadResponse(response);

            String responseJson = objectMapper.writeValueAsString(response);
            penaltyRepository.updatePenaltyAsSentWithResponse(
                    request.getClaimId(), response.getEntityId(), responseJson
            );

            log.info("Penalty sent successfully for claim: {}, EntityID: {}",
                    request.getClaimId(), response.getEntityId());

            return PenaltyResponseDto.builder()
                    .success(true)
                    .entityId(response.getEntityId())
                    .message(response.getMessage())
                    .build();

        } catch (Exception e) {
            log.error("Penalty failed for claim: {}", request.getClaimId(), e);
            penaltyRepository.updatePenaltyAsFailed(request.getClaimId(), e.getMessage());

            return PenaltyResponseDto.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    public void submitPenaltyInBackground(PenaltyRequestDto request) {
        log.info("Background penalty for claim: {}", request.getClaimId());

        try {
            // Save initial penalty response record
            PenaltyResponse entity = new PenaltyResponse();
            entity.setClaimId(request.getClaimId());
            entity.setStatus("PENDING");
            entity.setRetryCount(0);
            penaltyRepository.savePenaltyResponse(entity);

            PenaltySubmission submission = penaltyMapper.toPenaltySubmission(request);

            Map<String, Object> apiRequest = new HashMap<>();
            Map<String, Object> penaltyMap = new HashMap<>();
            penaltyMap.put("Header", submission.getHeader());
            penaltyMap.put("ClaimPenalty", submission.getClaimPenalty());
            apiRequest.put("PenaltySubmission", penaltyMap);

            ApiResponseDto response = riayatiClient.submitPenalty(apiRequest);
            responseProcessor.validateUploadResponse(response);

            String responseJson = objectMapper.writeValueAsString(response);
            penaltyRepository.updatePenaltyAsSentWithResponse(
                    request.getClaimId(), response.getEntityId(), responseJson
            );

            log.info("Background penalty sent successfully for claim: {}", request.getClaimId());

        } catch (Exception e) {
            log.error("Background penalty failed for claim: {}", request.getClaimId(), e);
            penaltyRepository.updateRetryCount(request.getClaimId());

            PenaltyResponse existing = penaltyRepository
                    .findPenaltyResponseByClaimId(request.getClaimId())
                    .orElse(null);

            if (existing != null && existing.getRetryCount() != null && existing.getRetryCount() + 1 >= 3) {
                penaltyRepository.updatePenaltyAsFailed(request.getClaimId(), e.getMessage());
            }
        }
    }

    // ==================== DOWNLOAD METHODS ====================

    public void processPenaltyResponseInBackground(TransactionEntityDto transaction) {
        log.info("Processing penalty response for transaction: {}", transaction.getId());

        try {
            ApiResponseDto viewResponse = riayatiClient.viewPenalty(transaction.getId(), 0);
            responseProcessor.validateDownloadResponse(viewResponse);

            PenaltyResponseData responseData = parsePenaltyResponse(viewResponse);

            if (responseData.getClaimId() != null) {
                String responseJson = objectMapper.writeValueAsString(viewResponse);
                penaltyRepository.updatePenaltyResponse(
                        responseData.getClaimId(),
                        responseData.getResult(),
                        responseData.getIdPayer(),
                        responseData.getDenialCode(),
                        responseData.getReferenceNumber(),
                        responseData.getPenaltyDateSettlement(),
                        responseData.getPenaltyPaymentAmount(),
                        responseData.getComments(),
                        responseJson
                );
                log.info("Penalty response updated for claim: {}, Result: {}",
                        responseData.getClaimId(), responseData.getResult());
            }

            riayatiClient.setPenaltyDownloaded(transaction.getId());
            log.info("Penalty response processed successfully: {}", transaction.getId());

        } catch (Exception e) {
            log.error("Failed to process penalty response: {}", transaction.getId(), e);
        }
    }

    // ==================== HELPER METHODS ====================

    private PenaltyResponseData parsePenaltyResponse(ApiResponseDto response) {
        PenaltyResponseData data = new PenaltyResponseData();

        try {
            String responseJson = objectMapper.writeValueAsString(response);
            JsonNode rootNode = objectMapper.readTree(responseJson);

            JsonNode entityNode = rootNode.path("Entity");
            JsonNode penaltyNode = entityNode.path("PenaltyResponse");
            JsonNode claimPenaltyNode = penaltyNode.path("ClaimPenaltyPayment");

            data.setClaimId(claimPenaltyNode.path("ID").asText(null));
            data.setResult(claimPenaltyNode.path("Result").asText(null));
            data.setIdPayer(claimPenaltyNode.path("IDPayer").asText(null));
            data.setDenialCode(claimPenaltyNode.path("DenialCode").asText(null));
            data.setReferenceNumber(claimPenaltyNode.path("ReferenceNumber").asText(null));
            data.setPenaltyDateSettlement(claimPenaltyNode.path("PenaltyDateSettlement").asText(null));
            data.setPenaltyPaymentAmount(claimPenaltyNode.path("PenaltyPaymentAmount").asDouble(0.0));
            data.setComments(claimPenaltyNode.path("Comments").asText(null));

            log.info("Parsed penalty response: ClaimId={}, Result={}",
                    data.getClaimId(), data.getResult());

        } catch (Exception e) {
            log.error("Error parsing penalty response", e);
        }

        return data;
    }
}
