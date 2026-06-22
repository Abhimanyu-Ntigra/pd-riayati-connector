package com.ntigra.riayati_middleware.respository.Impl;

import com.ntigra.riayati_middleware.dto.entity.Penalty.PenaltyResponse;
import com.ntigra.riayati_middleware.dto.request.PenaltyRequestDto;
import com.ntigra.riayati_middleware.respository.PenaltyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class PenaltyRepositoryImpl implements PenaltyRepository {

    private final JdbcTemplate jdbcTemplate;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PenaltyRepositoryImpl(@Qualifier("db1JdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<PenaltyRequestDto> fetchPendingPenalties() {
        String sql =
                "SELECT " +
                        "    C.Id AS penaltyId, " +
                        "    C.ClaimRef AS claimId, " +
                        "    C.ResponseIdentifier AS remittedIdPayer, " +
                        "    C.SubmissionDate AS claimSubmittedDate, " +
                        "    C.PayerResponseDate AS remittanceReceivedDate, " +
                        "    C.ClaimAmount AS claimedNetAmount, " +
                        "    C.ApprovalCode AS remittedPaymentReference, " +
                        "    C.ResponseComment AS observationText, " +
                        "    " +
                        "    CM.LicenseNo AS senderLicense, " +
                        "    INS.LicenseNo AS receiverLicense, " +
                        "    INS.LicenseNo AS payerLicense, " +
                        "    CM.LicenseNo AS facilityLicense " +
                        "FROM Claims C " +
                        "LEFT JOIN ClientMaster CM ON CM.Id = C.ClientId " +
                        "LEFT JOIN Insurances INS ON INS.Id = C.InsuranceID " +
                        "LEFT JOIN PenaltyResponses PR ON PR.ClaimId = C.ClaimRef " +
                        "WHERE C.Status = 8 " +  // PROCESSED
                        "  AND C.PayerResponseDate IS NOT NULL " +
                        "  AND C.SubmissionDate IS NOT NULL " +
                        "  AND ISNULL(C.IsDeleted, 0) = 0 " +
                        "  AND (PR.Id IS NULL OR PR.Status IN ('PENDING', 'RETRY')) " +
                        "ORDER BY C.CreatedAt ASC";

        try {
            List<PenaltyRequestDto> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
                PenaltyRequestDto dto = new PenaltyRequestDto();

                // From Claims Table
                dto.setId(rs.getLong("penaltyId"));
                dto.setClaimId(rs.getString("claimId"));
                dto.setPenaltyId("PEN-" + rs.getString("claimId"));  // Constant format
                dto.setRemittedIdPayer(rs.getString("remittedIdPayer"));
                dto.setClaimSubmittedDate(formatDate(rs.getTimestamp("claimSubmittedDate")));
                dto.setRemittanceReceivedDate(formatDate(rs.getTimestamp("remittanceReceivedDate")));
                dto.setClaimedNetAmount(rs.getDouble("claimedNetAmount"));
                dto.setRemittedPaymentReference(rs.getString("remittedPaymentReference"));
                dto.setObservationText(rs.getString("observationText"));

                // Constants
                dto.setPenaltyNetAmount(100.0);  // Constant
                dto.setPenaltyReasonCode("LATE_PAYMENT");  // Constant
                dto.setSenderId(rs.getString("senderLicense"));
                dto.setReceiverId("PAYER_LICENSE_ID");
                dto.setPayerId("PAYER_LICENSE_ID");
                dto.setFacilityId(rs.getString("facilityLicense"));
                dto.setDispositionFlag("PRODUCTION");
                dto.setRecordCount(1);
                dto.setRetryCount(0);

                return dto;
            });

            log.info("Found {} claims eligible for penalty", results.size());
            return results;

        } catch (Exception e) {
            log.error("Database error while fetching pending penalties", e);
            throw new RuntimeException("Failed to fetch pending penalties", e);
        }
    }

    private String formatDate(Timestamp timestamp) {
        if (timestamp == null) return null;
        return timestamp.toLocalDateTime().format(dateFormatter);
    }

    @Override
    public Optional<PenaltyResponse> findPenaltyResponseByClaimId(String claimId) {
        String sql = "SELECT * FROM PenaltyResponses WHERE ClaimId = ?";
        try {
            PenaltyResponse entity = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                PenaltyResponse e = new PenaltyResponse();
                e.setId(rs.getLong("Id"));
                e.setClaimId(rs.getString("ClaimId"));
                e.setEntityId(rs.getString("EntityId"));
                e.setResult(rs.getString("Result"));
                e.setIdPayer(rs.getString("IdPayer"));
                e.setDenialCode(rs.getString("DenialCode"));
                e.setReferenceNumber(rs.getString("ReferenceNumber"));
                e.setPenaltyPaymentAmount(rs.getDouble("PenaltyPaymentAmount"));
                e.setComments(rs.getString("Comments"));
                e.setResponseData(rs.getString("ResponseData"));
                e.setStatus(rs.getString("Status"));
                e.setRetryCount(rs.getInt("RetryCount"));
                return e;
            }, claimId);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void savePenaltyResponse(PenaltyResponse response) {
        String sql =
                "INSERT INTO PenaltyResponses (" +
                        "    ClaimId, EntityId, Result, IdPayer, DenialCode, " +
                        "    ReferenceNumber, PenaltyPaymentAmount, Comments, " +
                        "    ResponseData, Status, RetryCount, CreatedAt" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"Id"});
            ps.setString(1, response.getClaimId());
            ps.setString(2, response.getEntityId());
            ps.setString(3, response.getResult());
            ps.setString(4, response.getIdPayer());
            ps.setString(5, response.getDenialCode());
            ps.setString(6, response.getReferenceNumber());
            ps.setDouble(7, response.getPenaltyPaymentAmount() != null ? response.getPenaltyPaymentAmount() : 0.0);
            ps.setString(8, response.getComments());
            ps.setString(9, response.getResponseData());
            ps.setString(10, response.getStatus() != null ? response.getStatus() : "PENDING");
            ps.setInt(11, response.getRetryCount() != null ? response.getRetryCount() : 0);
            ps.setObject(12, response.getCreatedAt() != null ? response.getCreatedAt() : LocalDateTime.now());
            return ps;
        });

        log.info("Penalty response saved for claim: {}", response.getClaimId());
    }

    @Override
    public void updatePenaltyAsSent(String claimId, String entityId) {
        String sql = "UPDATE PenaltyResponses SET Status = 'SENT', EntityId = ?, ProcessedAt = GETDATE() WHERE ClaimId = ?";
        jdbcTemplate.update(sql, entityId, claimId);
        log.info("Penalty {} marked as SENT with EntityId: {}", claimId, entityId);
    }

    @Override
    public void updatePenaltyAsSentWithResponse(String claimId, String entityId, String responseJson) {
        String sql = "UPDATE PenaltyResponses SET Status = 'SENT', EntityId = ?, ProcessedAt = GETDATE(), ResponseData = ? WHERE ClaimId = ?";
        jdbcTemplate.update(sql, entityId, responseJson, claimId);
        log.info("Penalty {} marked as SENT with EntityId: {}", claimId, entityId);
    }

    @Override
    public void updatePenaltyAsFailed(String claimId, String errorMessage) {
        String sql = "UPDATE PenaltyResponses SET Status = 'FAILED', ResponseData = ? WHERE ClaimId = ?";
        jdbcTemplate.update(sql, errorMessage, claimId);
        log.error("Penalty {} marked as FAILED: {}", claimId, errorMessage);
    }

    @Override
    public void updateRetryCount(String claimId) {
        String sql = "UPDATE PenaltyResponses SET RetryCount = ISNULL(RetryCount, 0) + 1, Status = 'RETRY' WHERE ClaimId = ?";
        jdbcTemplate.update(sql, claimId);
        log.info("Penalty {} retry count incremented", claimId);
    }

    @Override
    public void updatePenaltyResponse(String claimId, String result, String idPayer,
                                      String denialCode, String referenceNumber,
                                      String penaltyDateSettlement, Double penaltyPaymentAmount,
                                      String comments, String responseData) {
        String sql =
                "UPDATE PenaltyResponses " +
                        "SET Result = ?, IdPayer = ?, DenialCode = ?, " +
                        "    ReferenceNumber = ?, PenaltyDateSettlement = ?, " +
                        "    PenaltyPaymentAmount = ?, Comments = ?, " +
                        "    ResponseData = ?, Status = 'PROCESSED', ProcessedAt = GETDATE() " +
                        "WHERE ClaimId = ?";

        jdbcTemplate.update(sql, result, idPayer, denialCode, referenceNumber,
                penaltyDateSettlement, penaltyPaymentAmount, comments,
                responseData, claimId);

        log.info("Penalty response updated for claim: {}, Result: {}", claimId, result);
    }
}
