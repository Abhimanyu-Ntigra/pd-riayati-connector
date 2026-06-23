package com.ntigra.riayati_middleware.respository;

import com.ntigra.riayati_middleware.dto.entity.Claim.ClaimEntity;
import com.ntigra.riayati_middleware.dto.request.ClaimRequestDto;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository {

    // For UPLOAD: Fetch pending claims from DB (Like fetchPendingAuthorizations)
    List<ClaimRequestDto> fetchPendingClaimsForUpload();

    // Update claim after successful upload
    void updateClaimAsSent(Long id, String entityId);

    // Update claim as failed
    void updateClaimAsFailed(Long id, String errorMessage);

    // Update retry count
    void updateRetryCount(Long id);

    // Update payment info after downloading remittance
    void updatePaymentInfo(String claimId, Double paymentAmount, String paymentReference,
                           String denialCode, String remittanceData);

    // Find by ID
    Optional<ClaimEntity> findById(Long id);

    // Find by Claim ID
    Optional<ClaimEntity> findByClaimId(String claimId);

    // Find by Entity ID (Riayati's ID)
    Optional<ClaimEntity> findByEntityId(String entityId);

    // Save new claim
    ClaimEntity save(ClaimEntity entity);

    // Get claims waiting for payment (for download scheduler)
    List<ClaimEntity> fetchClaimsWaitingForPayment();

    // Mark claim as processed after payment received
    void markAsProcessed(String entityId);

    void updateRemittance(String claimRef, Double transactionPaidAmount, String paymentDate, String paymentRef, String responseIdentifier, String note);
    void updateRemittanceStatus(int status, Double paidAmount, String remittancesId);
    void insertRemittanceTransaction(String RemittanceId, Double paidAmount,
                                     String paymentDate, String paymentRef, String note, String responseIdentifier);
}