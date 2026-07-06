package com.ntigra.riayati_middleware.service.ERX;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.ApiResponseDto;
import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.dto.entity.ERX.ErxResponseData;
import com.ntigra.riayati_middleware.dto.entity.ERX.ErxSubmission;
import com.ntigra.riayati_middleware.dto.request.ErxRequestDto;
import com.ntigra.riayati_middleware.dto.response.ErxResponseDto;
import com.ntigra.riayati_middleware.mapper.ErxMapper;
import com.ntigra.riayati_middleware.respository.ErxRepository;
import com.ntigra.riayati_middleware.service.RiayatiResponseProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErxServiceOld {

    private final RiayatiRestClient riayatiClient;
    private final ErxRepository erxRepository;
    private final ErxMapper erxMapper;
    private final RiayatiResponseProcessor responseProcessor;
    private final ObjectMapper objectMapper;

    // ==================== UPLOAD METHODS ====================

    public ErxResponseDto sendErxRequest(ErxRequestDto request) {
        log.info("Sending ERX request: {}", request.getPrescriptionId());

        try {
            // Upload attachment if present
            String attachmentId = null;
            if (request.getFileContent() != null && request.getFileContent().length > 0) {
                attachmentId = riayatiClient.uploadAttachment(
                        request.getFileContent(),
                        request.getFileName() != null ? request.getFileName() : "prescription.pdf"
                );
                log.info("Attachment uploaded with ID: {}", attachmentId);
            }

            ErxSubmission submission = erxMapper.toErxSubmission(request, attachmentId);

            Map<String, Object> apiRequest = new HashMap<>();
            Map<String, Object> erxRequestMap = new HashMap<>();
            erxRequestMap.put("Header", submission.getHeader());
            erxRequestMap.put("Prescription", submission.getPrescription());
            apiRequest.put("ErxRequest", erxRequestMap);

            ApiResponseDto response = riayatiClient.postErxRequest(apiRequest);
            responseProcessor.validateUploadResponse(response);

            String responseJson = objectMapper.writeValueAsString(response);
            erxRepository.updateErxAsSentWithResponse(
                    request.getId(), response.getEntityId(), response.getReferenceNumber(), responseJson
            );

            log.info("ERX sent successfully: {}, EntityID: {}, ReferenceNumber: {}",
                    request.getPrescriptionId(), response.getEntityId(), response.getReferenceNumber());

            return ErxResponseDto.builder()
                    .success(true)
                    .entityId(response.getEntityId())
                    .referenceNumber(response.getReferenceNumber())
                    .message(response.getMessage())
                    .build();

        } catch (Exception e) {
            log.error("ERX request failed: {}", request.getPrescriptionId(), e);
            erxRepository.updateErxAsFailed(request.getId(), e.getMessage());

            return ErxResponseDto.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    public void submitErxInBackground(ErxRequestDto request) {
        log.info("Background ERX upload: {}", request.getPrescriptionId());

        try {
            String attachmentId = null;
            if (request.getFileContent() != null && request.getFileContent().length > 0) {
                attachmentId = riayatiClient.uploadAttachment(
                        request.getFileContent(),
                        request.getFileName() != null ? request.getFileName() : "prescription.pdf"
                );
                log.info("Attachment uploaded with ID: {}", attachmentId);
            }

            ErxSubmission submission = erxMapper.toErxSubmission(request, attachmentId);

            Map<String, Object> apiRequest = new HashMap<>();
            Map<String, Object> erxRequestMap = new HashMap<>();
            erxRequestMap.put("Header", submission.getHeader());
            erxRequestMap.put("Prescription", submission.getPrescription());
            apiRequest.put("ErxRequest", erxRequestMap);

            ApiResponseDto response = riayatiClient.postErxRequest(apiRequest);
            responseProcessor.validateUploadResponse(response);

            String responseJson = objectMapper.writeValueAsString(response);
            erxRepository.updateErxAsSentWithResponse(
                    request.getId(), response.getEntityId(), response.getReferenceNumber(), responseJson
            );
            // where to save response ??

            log.info("Background ERX sent successfully: {}, EntityID: {}, ReferenceNumber: {}",
                    request.getPrescriptionId(), response.getEntityId(), response.getReferenceNumber());

        } catch (Exception e) {
            log.error("Background ERX failed: {}", request.getPrescriptionId(), e);
            erxRepository.updateRetryCount(request.getId());
            if (request.getRetryCount() != null && request.getRetryCount() + 1 >= 3) {
                erxRepository.updateErxAsFailed(request.getId(), e.getMessage());
            }
        }
    }
//
//    // ==================== DOWNLOAD METHODS ====================
//
//    public void processErxResponseInBackground(TransactionEntityDto transaction) {
//        log.info("Processing ERX response for transaction: {}", transaction.getId());
//
//        try {
//            ApiResponseDto viewResponse = riayatiClient.viewErx(transaction.getId(), 0);
//            responseProcessor.validateDownloadResponse(viewResponse);
//
//            ErxResponseData responseData = parseErxResponse(viewResponse);
//
//            if (responseData.getPrescriptionId() != null) {
//                String responseJson = objectMapper.writeValueAsString(viewResponse);
//                erxRepository.updateErxResponse(
//                        responseData.getPrescriptionId(),
//                        responseData.getResult(),
//                        responseData.getIdPayer(),
//                        responseData.getDenialCode(),
//                        responseData.getStartDate(),
//                        responseData.getEndDate(),
//                        responseData.getLimit(),
//                        responseJson
//                );
//                log.info("ERX response updated for: {}, Result: {}",
//                        responseData.getPrescriptionId(), responseData.getResult());
//            }
//
//            riayatiClient.setErxDownloaded(transaction.getId());
//            log.info("ERX response processed successfully: {}", transaction.getId());
//
//        } catch (Exception e) {
//            log.error("Failed to process ERX response: {}", transaction.getId(), e);
//        }
//    }
//
//    // ==================== HELPER METHODS ====================
//
//    private ErxResponseData parseErxResponse(ApiResponseDto response) {
//        ErxResponseData data = new ErxResponseData();
//
//        try {
//            String responseJson = objectMapper.writeValueAsString(response);
//            JsonNode rootNode = objectMapper.readTree(responseJson);
//
//            JsonNode entityNode = rootNode.path("Entity");
//            JsonNode erxNode = entityNode.path("ErxAuthorization");
//            JsonNode authNode = erxNode.path("Authorization");
//
//            data.setPrescriptionId(authNode.path("ID").asText(null));
//            data.setResult(authNode.path("Result").asText(null));
//            data.setIdPayer(authNode.path("IDPayer").asText(null));
//            data.setDenialCode(authNode.path("DenialCode").asText(null));
//            data.setStartDate(authNode.path("Start").asText(null));
//            data.setEndDate(authNode.path("End").asText(null));
//            data.setLimit(authNode.path("Limit").asDouble(0.0));
//
//            log.info("Parsed ERX response: PrescriptionId={}, Result={}",
//                    data.getPrescriptionId(), data.getResult());
//
//        } catch (Exception e) {
//            log.error("Error parsing ERX response", e);
//        }
//
//        return data;
//    }
}

