package com.ntigra.riayati_middleware.service.Claim;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.ApiResponseDto;
import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.dto.entity.Claim.ActivityDetail;
import com.ntigra.riayati_middleware.dto.entity.Claim.ClaimSubmission;
import com.ntigra.riayati_middleware.dto.entity.Claim.RemittanceData;
import com.ntigra.riayati_middleware.dto.request.ClaimRequestDto;
import com.ntigra.riayati_middleware.dto.response.ClaimSubmissionResponseDto;
import com.ntigra.riayati_middleware.mapper.ClaimMapper;
import com.ntigra.riayati_middleware.respository.ClaimRepository;
import com.ntigra.riayati_middleware.service.RiayatiResponseProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimService {

    private final RiayatiRestClient riayatiClient;
    private final ClaimRepository claimRepository;
    private final ClaimMapper claimMapper;
    private final RiayatiResponseProcessor responseProcessor;
    private final ObjectMapper objectMapper;

    // ==================== UPLOAD METHODS ====================

    /**
     * Submit a claim to Riayati (Called by Controller)
     */
    public ClaimSubmissionResponseDto submitClaim(ClaimRequestDto request) {
        log.info("Submitting claim: {}", request.getClaimId());

        try {
            // Step 1: Upload attachment if present
            String attachmentId = null;
            if (request.getFileContent() != null && request.getFileContent().length > 0) {
                attachmentId = riayatiClient.uploadAttachment(
                        request.getFileContent(),
                        request.getFileName() != null ? request.getFileName() : "claim.pdf"
                );
                log.info("Attachment uploaded with ID: {}", attachmentId);
            }

            // Step 2: Convert to Riayati JSON model
            ClaimSubmission submission = claimMapper.toClaimSubmission(request, attachmentId);

            // Step 3: Build API request
            Map<String, Object> apiRequest = new HashMap<>();
            Map<String, Object> submissionMap = new HashMap<>();
            submissionMap.put("Header", submission.getHeader());
            submissionMap.put("Claim", submission.getClaim());
            apiRequest.put("Submission", submissionMap);

            // Step 4: Send to Riayati
            ApiResponseDto response = riayatiClient.postClaimSubmission(apiRequest);

            // Step 5: Validate response
            responseProcessor.validateUploadResponse(response);

            // Step 6: Update database with success
            //String responseJson = objectMapper.writeValueAsString(response);
            //claimRepository.updateClaimAsSentWithResponse(request.getId(), response.getEntityId(), responseJson);

            log.info("Claim submitted successfully: {}, EntityID: {}", request.getClaimId(), response.getEntityId());

            return ClaimSubmissionResponseDto.builder()
                    .success(true)
                    .entityId(response.getEntityId())
                    .statusCode(response.getStatusCode())
                    .message(response.getMessage())
                    .build();

        } catch (Exception e) {
            log.error("Claim submission failed: {}", request.getClaimId(), e);
            claimRepository.updateClaimAsFailed(request.getId(), e.getMessage());

            return ClaimSubmissionResponseDto.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    /**
     * Submit claim in background (Called by Upload Thread)
     */
    public void submitClaimInBackground(ClaimRequestDto request) {
        log.info("Background claim upload: {}", request.getClaimId());

        try {
            // Step 1: Upload attachment if present
            String attachmentId = null;
            if (request.getFileContent() != null && request.getFileContent().length > 0) {
                attachmentId = riayatiClient.uploadAttachment(
                        request.getFileContent(),
                        request.getFileName() != null ? request.getFileName() : "claim.pdf"
                );
                log.info("Attachment uploaded with ID: {}", attachmentId);
            }

            // Step 2: Convert to Riayati JSON model
            ClaimSubmission submission = claimMapper.toClaimSubmission(request, attachmentId);

            // Step 3: Build API request
            Map<String, Object> apiRequest = new HashMap<>();
            Map<String, Object> submissionMap = new HashMap<>();
            submissionMap.put("Header", submission.getHeader());
            submissionMap.put("Claim", submission.getClaim());
            apiRequest.put("Submission", submissionMap);

            // Step 4: Send to Riayati
            ApiResponseDto response = riayatiClient.postClaimSubmission(apiRequest);

            // Step 5: Validate response
            responseProcessor.validateUploadResponse(response);

            // Step 6: Update database with success
            //String responseJson = objectMapper.writeValueAsString(response);
            //claimRepository.updateClaimAsSentWithResponse(request.getId(), response.getEntityId(), responseJson);

            log.info("Background claim uploaded successfully: {}, EntityID: {}", request.getClaimId(), response.getEntityId());

        } catch (Exception e) {
            log.error("Background claim upload failed: {}", request.getClaimId(), e);

            // Update retry count
            claimRepository.updateRetryCount(request.getId());

            // If max retries reached, mark as failed
            if (request.getRetryCount() != null && request.getRetryCount() + 1 >= 3) {
                claimRepository.updateClaimAsFailed(request.getId(), e.getMessage());
            }
        }
    }

    // ==================== DOWNLOAD METHODS ====================

    /**
     * Download and process remittances from Riayati (Called by Download Scheduler)
     */
//    public void downloadRemittances() {
//        log.info("Checking for pending remittances...");
//
//        try {
//            // Step 1: Get new remittances from Riayati
//            ApiResponseDto response = riayatiClient.getNewClaim();
//
//            if (response.getEntities() == null || response.getEntities().isEmpty()) {
//                log.debug("No pending remittances found");
//                return;
//            }
//
//            log.info("Found {} pending remittances", response.getEntities().size());
//
//            // Step 2: Process each remittance
//            for (TransactionEntityDto transaction : response.getEntities()) {
//                processRemittance(transaction);
//            }
//
//        } catch (Exception e) {
//            log.error("Failed to download remittances", e);
//        }
//    }

    /**
     * Process a single remittance
     */
//    private void processRemittance(TransactionEntityDto transaction) {
//        try {
//            // Step 1: View full transaction details
//            ApiResponseDto viewResponse = riayatiClient.viewClaim(transaction.getId(), 0);
//            responseProcessor.validateDownloadResponse(viewResponse);
//
//            // Step 2: Extract payment information
//            String claimId = extractClaimId(viewResponse);
//            Double paymentAmount = extractPaymentAmount(viewResponse);
//            String paymentReference = extractPaymentReference(viewResponse);
//
//            // Step 3: Update database with payment info
//            if (claimId != null) {
//                String remittanceJson = objectMapper.writeValueAsString(viewResponse);
//                claimRepository.updatePaymentInfo(claimId, paymentAmount, paymentReference, remittanceJson);
//                log.info("Payment updated for claim: {}, amount: {}", claimId, paymentAmount);
//            }
//
//            // Step 4: Mark as downloaded in Riayati
//            riayatiClient.setClaimDownloaded(transaction.getId());
//
//            log.info("Remittance processed successfully: {}", transaction.getId());
//
//        } catch (Exception e) {
//            log.error("Failed to process remittance for transaction: {}", transaction.getId(), e);
//        }
//    }

    // ==================== DOWNLOAD METHODS ====================

    public void processRemittanceInBackground(TransactionEntityDto transaction) {
        log.info("Processing remittance for transaction: {}", transaction.getId());

        try {
            // Step 1: View full transaction details
            ApiResponseDto viewResponse = riayatiClient.viewClaim(transaction.getId(), 0);
            responseProcessor.validateDownloadResponse(viewResponse);

            // Step 2: Parse the full response
            RemittanceData remittanceData = parseRemittanceResponse(viewResponse);

            // Step 3: Update database with payment info
            if (remittanceData.getClaimId() != null) {
                String remittanceJson = objectMapper.writeValueAsString(viewResponse);
                /*claimRepository.updatePaymentInfo(
                        remittanceData.getClaimId(),
                        remittanceData.getTotalPaymentAmount(),
                        remittanceData.getPaymentReference(),
                        remittanceData.getDenialCode(),
                        remittanceJson
                );*/

                claimRepository.updateRemittance(
                        remittanceData.getClaimId(),
                        remittanceData.getTotalPaymentAmount(),
                        remittanceData.getDateSettlement(),
                        remittanceData.getPaymentReference(),
                        remittanceData.getPaymentReference(),
                        remittanceData.getDenialCode()
                );
                log.info("Payment updated for claim: {}, amount: {}",
                        remittanceData.getClaimId(), remittanceData.getTotalPaymentAmount());
            }

            // Step 4: Mark as downloaded in Riayati
            riayatiClient.setClaimDownloaded(transaction.getId());

            log.info("Remittance processed successfully: {}", transaction.getId());

        } catch (Exception e) {
            log.error("Failed to process remittance for transaction: {}", transaction.getId(), e);
        }
    }

    // ==================== HELPER METHODS - FULL PARSING ====================

    /**
     * Parse the full remittance response from Riayati
     */
    private RemittanceData parseRemittanceResponse(ApiResponseDto response) {
        RemittanceData data = new RemittanceData();

        try {
            // Convert response to JSON node for parsing
            String responseJson = objectMapper.writeValueAsString(response);
            JsonNode rootNode = objectMapper.readTree(responseJson);

            // Navigate to Remittance → Claim
            JsonNode entityNode = rootNode.path("Entity");
            JsonNode remittanceNode = entityNode.path("Remittance");
            JsonNode claimNode = remittanceNode.path("Claim");

            // Parse Claim level fields
            data.setClaimId(claimNode.path("ID").asText(null));
            data.setIdPayer(claimNode.path("IDPayer").asText(null));
            data.setProviderId(claimNode.path("ProviderID").asText(null));
            data.setDenialCode(claimNode.path("DenialCode").asText(null));
            data.setPaymentReference(claimNode.path("PaymentReference").asText(null));
            data.setDateSettlement(claimNode.path("DateSettlement").asText(null));

            // Parse Activities and calculate total payment
            JsonNode activitiesNode = claimNode.path("Activity");
            double totalPaymentAmount = 0.0;
            double totalGross = 0.0;
            double totalPatientShare = 0.0;

            data.setActivityDetails(new ArrayList<ActivityDetail>());
            if (activitiesNode.isArray()) {
                for (JsonNode activity : activitiesNode) {
                    double paymentAmount = activity.path("PaymentAmount").asDouble(0.0);
                    double gross = activity.path("Gross").asDouble(0.0);
                    double patientShare = activity.path("PatientShare").asDouble(0.0);

                    totalPaymentAmount += paymentAmount;
                    totalGross += gross;
                    totalPatientShare += patientShare;

                    ActivityDetail activityDetail = new ActivityDetail();
                    activityDetail.setPaymentAmount(activity.path("PaymentAmount").asDouble(0.0));
                    activityDetail.setActivityId(activity.path("ID").asText(null));
                    activityDetail.setCode(activity.path("Code").asText(null));
                    activityDetail.setDenialCode(activity.path("DenialCode").asText(null));
                    activityDetail.setCode(activity.path("Comments").asText(null));

                    data.getActivityDetails().add(activityDetail);
                    // Store activity level details if needed
//                    data.addActivityDetail(
//                            activity.path("ID").asText(null),
//                            activity.path("Code").asText(null),
//                            paymentAmount,
//                            activity.path("DenialCode").asText(null)
//                    );
                }
            }

            data.setTotalPaymentAmount(totalPaymentAmount);
            data.setTotalGross(totalGross);
            data.setTotalPatientShare(totalPatientShare);

            log.info("Parsed remittance: ClaimId={}, TotalPayment={}, Activities={}",
                    data.getClaimId(), totalPaymentAmount, activitiesNode.size());

        } catch (Exception e) {
            log.error("Error parsing remittance response", e);
        }

        return data;
    }
}