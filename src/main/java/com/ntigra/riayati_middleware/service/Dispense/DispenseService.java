package com.ntigra.riayati_middleware.service.Dispense;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.ApiResponseDto;
import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.dto.entity.Dispense.DispenseResponseData;
import com.ntigra.riayati_middleware.dto.entity.Dispense.DispenseSubmission;
import com.ntigra.riayati_middleware.dto.request.DispenseRequestDto;
import com.ntigra.riayati_middleware.dto.response.DispenseResponseDto;
import com.ntigra.riayati_middleware.mapper.DispenseMapper;
import com.ntigra.riayati_middleware.respository.DispenseRepository;
import com.ntigra.riayati_middleware.service.RiayatiResponseProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispenseService {

    private final RiayatiRestClient riayatiClient;
    private final DispenseRepository dispenseRepository;
    private final DispenseMapper dispenseMapper;
    private final RiayatiResponseProcessor responseProcessor;
    private final ObjectMapper objectMapper;


    /**
     * Send dispense to Riayati (Called by Controller)
     */
    public DispenseResponseDto sendDispense(DispenseRequestDto request) {
        log.info("Sending dispense: {}", request.getDispenseId());

        try {
            DispenseSubmission submission = dispenseMapper.toDispenseSubmission(request);

            Map<String, Object> apiRequest = new HashMap<>();
            Map<String, Object> dispenseMap = new HashMap<>();
            dispenseMap.put("Header", submission.getHeader());
            dispenseMap.put("Dispense", submission.getDispense());
            apiRequest.put("PrescriptionDispense", dispenseMap);

            ApiResponseDto response = riayatiClient.postErxDispense(apiRequest);
            responseProcessor.validateUploadResponse(response);

            String responseJson = objectMapper.writeValueAsString(response);
            dispenseRepository.updateDispenseAsSentWithResponse(
                    request.getId(), response.getEntityId(), responseJson
            );

            log.info("Dispense sent successfully: {}, EntityID: {}",
                    request.getDispenseId(), response.getEntityId());

            return DispenseResponseDto.builder()
                    .success(true)
                    .entityId(response.getEntityId())
                    .message(response.getMessage())
                    .build();

        } catch (Exception e) {
            log.error("Dispense failed: {}", request.getDispenseId(), e);
            dispenseRepository.updateDispenseAsFailed(request.getId(), e.getMessage());

            return DispenseResponseDto.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    /**
     * Submit dispense in background (Called by Upload Thread)
     */
    public void submitDispenseInBackground(DispenseRequestDto request) {
        log.info("Background dispense: {}", request.getDispenseId());

        try {
            DispenseSubmission submission = dispenseMapper.toDispenseSubmission(request);

            Map<String, Object> apiRequest = new HashMap<>();
            Map<String, Object> dispenseMap = new HashMap<>();
            dispenseMap.put("Header", submission.getHeader());
            dispenseMap.put("Dispense", submission.getDispense());
            apiRequest.put("PrescriptionDispense", dispenseMap);

            ApiResponseDto response = riayatiClient.postErxDispense(apiRequest);
            responseProcessor.validateUploadResponse(response);

            String responseJson = objectMapper.writeValueAsString(response);
            dispenseRepository.updateDispenseAsSentWithResponse(
                    request.getId(), response.getEntityId(), responseJson
            );

            log.info("Background dispense sent successfully: {}, EntityID: {}",
                    request.getDispenseId(), response.getEntityId());

        } catch (Exception e) {
            log.error("Background dispense failed: {}", request.getDispenseId(), e);
            dispenseRepository.updateRetryCount(request.getId());
            if (request.getRetryCount() != null && request.getRetryCount() + 1 >= 3) {
                dispenseRepository.updateDispenseAsFailed(request.getId(), e.getMessage());
            }
        }
    }

    // ==================== DOWNLOAD METHODS ====================

    /**
     * Process dispense response in background (Called by Download Thread)
     */
    public void processDispenseResponseInBackground(TransactionEntityDto transaction) {
        log.info("Processing dispense response for transaction: {}", transaction.getId());

        try {
            ApiResponseDto viewResponse = riayatiClient.viewDispense(transaction.getId(), 0);
            responseProcessor.validateDownloadResponse(viewResponse);

            DispenseResponseData responseData = parseDispenseResponse(viewResponse);

            if (responseData.getDispenseId() != null) {
                String responseJson = objectMapper.writeValueAsString(viewResponse);
                dispenseRepository.updateDispenseResponse(
                        responseData.getDispenseId(),
                        responseData.getResult(),
                        responseData.getIdPayer(),
                        responseData.getDenialCode(),
                        responseData.getStartDate(),
                        responseData.getEndDate(),
                        responseData.getLimit(),
                        responseJson
                );
                log.info("Dispense response updated for: {}, Result: {}",
                        responseData.getDispenseId(), responseData.getResult());
            }

            riayatiClient.setDispenseDownloaded(transaction.getId());
            log.info("Dispense response processed successfully: {}", transaction.getId());

        } catch (Exception e) {
            log.error("Failed to process dispense response: {}", transaction.getId(), e);
        }
    }

    // ==================== HELPER METHODS ====================

    private DispenseResponseData parseDispenseResponse(ApiResponseDto response) {
        DispenseResponseData data = new DispenseResponseData();

        try {
            String responseJson = objectMapper.writeValueAsString(response);
            JsonNode rootNode = objectMapper.readTree(responseJson);

            JsonNode entityNode = rootNode.path("Entity");
            JsonNode dispenseNode = entityNode.path("PrescriptionDispense");
            JsonNode dispense = dispenseNode.path("Dispense");

            data.setDispenseId(dispense.path("ID").asText(null));
            data.setResult(dispense.path("Result").asText(null));
            data.setIdPayer(dispense.path("IDPayer").asText(null));
            data.setDenialCode(dispense.path("DenialCode").asText(null));
            data.setStartDate(dispense.path("Start").asText(null));
            data.setEndDate(dispense.path("End").asText(null));
            data.setLimit(dispense.path("Limit").asDouble(0.0));

            log.info("Parsed dispense response: DispenseId={}, Result={}",
                    data.getDispenseId(), data.getResult());

        } catch (Exception e) {
            log.error("Error parsing dispense response", e);
        }

        return data;
    }
}
