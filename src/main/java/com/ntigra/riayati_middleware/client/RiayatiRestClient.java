package com.ntigra.riayati_middleware.client;

import com.ntigra.riayati_middleware.config.RiayatiProperties;
import com.ntigra.riayati_middleware.dto.ApiResponseDto;
import com.ntigra.riayati_middleware.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RiayatiRestClient {

    private final RestTemplate riayatiRestTemplate;
    private final RiayatiProperties properties;

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("username", properties.getUsername());
        headers.set("password", properties.getPassword());
        return headers;
    }

    private <T> HttpEntity<T> createHttpEntity(T body) {
        return new HttpEntity<>(body, createAuthHeaders());
    }

    private HttpEntity<Void> createHttpEntity() {
        return new HttpEntity<>(createAuthHeaders());
    }

    // ==================== ATTACHMENT APIs ====================

    /**
     * Upload attachment PDF
     * Returns EntityID to use in claim
     */
    public String uploadAttachment(byte[] fileContent, String fileName) {
        String url = properties.getAttachmentUrl() + "/api/Attachments/UploadAttachment";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("username", properties.getUsername());
        headers.set("password", properties.getPassword());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(fileContent) {
            @Override
            public String getFilename() {
                return fileName;
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                ApiResponseDto.class
        );

        ApiResponseDto apiResponse = response.getBody();
        if (apiResponse != null && apiResponse.getSuccess() != null && apiResponse.getSuccess()) {
            return apiResponse.getEntityId();
        }

        log.error("Failed to upload attachment: {}", apiResponse != null ? apiResponse.getMessage() : "Unknown");
        return null;
    }

    /**
     * Download attachment by EntityID
     */
    public byte[] downloadAttachment(String attachmentId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(properties.getAttachmentUrl() + "/api/Attachments/DownloadAttachment")
                .queryParam("id", attachmentId)
                .build()
                .toUriString();

        HttpHeaders headers = createAuthHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = riayatiRestTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                byte[].class
        );

        return response.getBody();
    }


    // ==================== CLAIM APIs ====================

    /**
     * CLAIM: Submit a claim to Riayati (POST /api/Claim/PostSubmission)
     * Called by ClaimService.submitClaim() and ClaimService.submitClaimInBackground()
     */
    public ApiResponseDto postClaimSubmission(Map<String, Object> claimJson) {
        String url = properties.getBaseUrl() + "/api/Claim/PostSubmission";

        log.debug("Posting claim submission to: {}", url);

        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url,
                HttpMethod.POST,
                createHttpEntity(claimJson),
                ApiResponseDto.class
        );

        return response.getBody();
    }

    /**
     * CLAIM: Get new claims/remittances (GET /api/Claim/GetNew)
     * Called by ClaimService.downloadRemittances()
     * Provider gets RemittanceAdvice, Payer gets ClaimSubmission
     */
    public ApiResponseDto getNewClaim() {
        String url = properties.getBaseUrl() + "/api/Claim/GetNew";

        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url,
                HttpMethod.GET,
                createHttpEntity(),
                ApiResponseDto.class
        );

        return response.getBody();
    }

    /**
     * CLAIM: View specific claim/remittance (GET /api/Claim/View)
     * Called by ClaimService.processRemittance()
     */
    public ApiResponseDto viewClaim(String transactionId, Integer direction) {
        String url = UriComponentsBuilder
                .fromHttpUrl(properties.getBaseUrl() + "/api/Claim/View")
                .queryParam("id", transactionId)
                .queryParam("direction", direction != null ? direction : 0)
                .build()
                .toUriString();

        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url,
                HttpMethod.GET,
                createHttpEntity(),
                ApiResponseDto.class
        );

        return response.getBody();
    }

    /**
     * CLAIM: Mark transaction as downloaded (POST /api/Claim/SetDownloaded)
     * Called by ClaimService.processRemittance()
     */
    public ApiResponseDto setClaimDownloaded(String transactionId) {
        String url = properties.getBaseUrl() + "/api/Claim/SetDownloaded";

        Map<String, String> body = Collections.singletonMap("id", transactionId);

        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url,
                HttpMethod.POST,
                createHttpEntity(body),
                ApiResponseDto.class
        );

        return response.getBody();
    }

    /**
     * CLAIM: Search claims (GET /api/Claim/Search) - Optional
     */
    public ApiResponseDto searchClaims(Map<String, Object> searchParams) {
        String url = UriComponentsBuilder
                .fromHttpUrl(properties.getBaseUrl() + "/api/Claim/Search")
                .build()
                .toUriString();

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(searchParams, createAuthHeaders());

        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                ApiResponseDto.class
        );

        return response.getBody();
    }



    /**
     * ELIGIBILITY: Send Eligibility Request (GET /getNew/SetDownloaded)
     */
    public ApiResponseDto postEligibilityRequest(Map<String, Object> requestBody) {
        String url = properties.getBaseUrl() + "/getNew/SetDownloaded";

        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url,
                HttpMethod.GET,
                createHttpEntity(requestBody),
                ApiResponseDto.class
        );

        return response.getBody();
    }

    /**
     * ELIGIBILITY: Get new Eligibility responses (GET /api/Authorization/GetNew)
     */
    public ApiResponseDto getNewEligibility() {
        String url = properties.getBaseUrl() + "/api/Authorization/GetNew";

        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url,
                HttpMethod.GET,
                createHttpEntity(),
                ApiResponseDto.class
        );

        return response.getBody();
    }

    /**
     * ELIGIBILITY: View specific Eligibility transaction (GET /api/Authorization/View)
     */
    public ApiResponseDto viewEligibility(String transactionId, Integer direction) {
        String url = UriComponentsBuilder
                .fromHttpUrl(properties.getBaseUrl() + "/api/Authorization/View")
                .queryParam("id", transactionId)
                .queryParam("direction", direction != null ? direction : 0)
                .build()
                .toUriString();

        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url,
                HttpMethod.GET,
                createHttpEntity(),
                ApiResponseDto.class
        );

        return response.getBody();
    }

    /**
     * ELIGIBILITY: Mark Eligibility as downloaded (POST /api/Authorization/SetDownloaded)
     */
    public ApiResponseDto setEligibilityDownloaded(String transactionId) {
        String url = properties.getBaseUrl() + "/api/Authorization/SetDownloaded";

        Map<String, String> body = Collections.singletonMap("id", transactionId);

        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url,
                HttpMethod.POST,
                createHttpEntity(body),
                ApiResponseDto.class
        );

        return response.getBody();
    }

    // ==================== AUTHORIZATION APIs ====================

    /**
     * AUTHORIZATION: Send Authorization Request (POST /api/Authorization/PostRequest)
     */
    public ApiResponseDto postAuthorizationRequest(Map<String, Object> requestBody) {
        String url = properties.getBaseUrl() + "/api/Authorization/PostRequest";

        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url,
                HttpMethod.POST,
                createHttpEntity(requestBody),
                ApiResponseDto.class
        );

        return response.getBody();
    }

    /**
     * AUTHORIZATION: Get new Authorization responses (GET /api/Authorization/GetNew)
     */
    public ApiResponseDto getNewAuthorization() {
        String url = properties.getBaseUrl() + "/api/Authorization/GetNew";

        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url,
                HttpMethod.GET,
                createHttpEntity(),
                ApiResponseDto.class
        );

        return response.getBody();
    }

    /**
     * AUTHORIZATION: View specific Authorization transaction (GET /api/Authorization/View)
     */
    public ApiResponseDto viewAuthorization(String transactionId, Integer direction) {
        String url = UriComponentsBuilder
                .fromHttpUrl(properties.getBaseUrl() + "/api/Authorization/View")
                .queryParam("id", transactionId)
                .queryParam("direction", direction != null ? direction : 0)
                .build()
                .toUriString();

        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url,
                HttpMethod.GET,
                createHttpEntity(),
                ApiResponseDto.class
        );

        return response.getBody();
    }

    /**
     * AUTHORIZATION: Mark Authorization as downloaded (POST /api/Authorization/SetDownloaded)
     */
    public ApiResponseDto setAuthorizationDownloaded(String transactionId) {
        String url = properties.getBaseUrl() + "/api/Authorization/SetDownloaded";

        Map<String, String> body = Collections.singletonMap("id", transactionId);

        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url,
                HttpMethod.POST,
                createHttpEntity(body),
                ApiResponseDto.class
        );

        return response.getBody();
    }


    // ==================== ERX APIs ====================

    public ApiResponseDto postErxRequest(Map<String, Object> requestBody) {
        String url = properties.getBaseUrl() + "/api/ERX/PostRequest";
        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url, HttpMethod.POST, createHttpEntity(requestBody), ApiResponseDto.class);
        return response.getBody();
    }

    public ApiResponseDto getNewErx() {
        String url = properties.getBaseUrl() + "/api/ERX/GetNew";
        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url, HttpMethod.GET, createHttpEntity(), ApiResponseDto.class);
        return response.getBody();
    }

    public ApiResponseDto viewErx(String transactionId, Integer direction) {
        String url = UriComponentsBuilder
                .fromHttpUrl(properties.getBaseUrl() + "/api/ERX/View")
                .queryParam("id", transactionId)
                .queryParam("direction", direction != null ? direction : 0)
                .build().toUriString();
        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url, HttpMethod.GET, createHttpEntity(), ApiResponseDto.class);
        return response.getBody();
    }

    public ApiResponseDto setErxDownloaded(String transactionId) {
        String url = properties.getBaseUrl() + "/api/ERX/SetDownloaded";
        Map<String, String> body = Collections.singletonMap("id", transactionId);
        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url, HttpMethod.POST, createHttpEntity(body), ApiResponseDto.class);
        return response.getBody();
    }

    // ==================== DISPENSE APIs ====================

    public ApiResponseDto getNewDispense() {
        String url = properties.getBaseUrl() + "/api/Dispense/GetNew";

        // Dispense GetNew requires type parameter
        String fullUrl = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("type", "Prescription")
                .build().toUriString();

        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                fullUrl, HttpMethod.GET, createHttpEntity(), ApiResponseDto.class);
        return response.getBody();
    }

    public ApiResponseDto viewDispense(String transactionId, Integer direction) {
        String url = UriComponentsBuilder
                .fromHttpUrl(properties.getBaseUrl() + "/api/Dispense/View")
                .queryParam("id", transactionId)
                .queryParam("direction", direction != null ? direction : 0)
                .build().toUriString();
        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url, HttpMethod.GET, createHttpEntity(), ApiResponseDto.class);
        return response.getBody();
    }

    public ApiResponseDto postErxDispense(Map<String, Object> requestBody) {
        String url = properties.getBaseUrl() + "/api/Dispense/ErxDispense";
        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url, HttpMethod.POST, createHttpEntity(requestBody), ApiResponseDto.class);
        return response.getBody();
    }

    public ApiResponseDto setDispenseDownloaded(String transactionId) {
        String url = properties.getBaseUrl() + "/api/Dispense/SetDownloaded";
        Map<String, String> body = new HashMap<>();
        body.put("id", transactionId);
        body.put("type", "Prescription");
        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url, HttpMethod.POST, createHttpEntity(body), ApiResponseDto.class);
        return response.getBody();
    }


    // ==================== PENALTY APIs ====================

    public ApiResponseDto submitPenalty(Map<String, Object> requestBody) {
        String url = properties.getBaseUrl() + "/api/Penalty/PenaltySubmission";
        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url, HttpMethod.POST, createHttpEntity(requestBody), ApiResponseDto.class);
        return response.getBody();
    }

    public ApiResponseDto getNewPenalty() {
        String url = properties.getBaseUrl() + "/api/Penalty/GetNew";
        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url, HttpMethod.GET, createHttpEntity(), ApiResponseDto.class);
        return response.getBody();
    }

    public ApiResponseDto viewPenalty(String transactionId, Integer direction) {
        String url = UriComponentsBuilder
                .fromHttpUrl(properties.getBaseUrl() + "/api/Penalty/View")
                .queryParam("id", transactionId)
                .queryParam("direction", direction != null ? direction : 0)
                .build().toUriString();
        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url, HttpMethod.GET, createHttpEntity(), ApiResponseDto.class);
        return response.getBody();
    }

    public ApiResponseDto setPenaltyDownloaded(String transactionId) {
        String url = properties.getBaseUrl() + "/api/Penalty/SetDownloaded";
        Map<String, String> body = Collections.singletonMap("id", transactionId);
        ResponseEntity<ApiResponseDto> response = riayatiRestTemplate.exchange(
                url, HttpMethod.POST, createHttpEntity(body), ApiResponseDto.class);
        return response.getBody();
    }
}