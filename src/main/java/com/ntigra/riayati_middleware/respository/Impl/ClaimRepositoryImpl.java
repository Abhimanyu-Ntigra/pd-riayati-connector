package com.ntigra.riayati_middleware.respository.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntigra.riayati_middleware.dto.entity.Claim.ClaimEntity;
import com.ntigra.riayati_middleware.dto.request.ClaimActivityDto;
import com.ntigra.riayati_middleware.dto.request.ClaimRequestDto;
import com.ntigra.riayati_middleware.dto.request.DiagnosisDto;
import com.ntigra.riayati_middleware.dto.request.ObservationDto;
import com.ntigra.riayati_middleware.respository.ClaimRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@Slf4j
public class ClaimRepositoryImpl implements ClaimRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public ClaimRepositoryImpl(@Qualifier("db1JdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<ClaimRequestDto> fetchPendingClaimsForUpload() {
        String sql =
                "SELECT " +
                        "    CAST(C.Id AS VARCHAR(36)) AS claimId, " +
                        "    C.ClaimRef AS claimRef, " +
                        "    C.ClaimAmount AS claimAmount, " +
                        "    C.Status AS claimStatus, " +
                        "    " +
                        "    PID.MemberId AS memberId, " +
                        "    P.NationalId AS emiratesId, " +
                        "    P.FirstName + ' ' + P.LastName AS patientName, " +
                        "    P.Gender AS patientGender, " +
                        "    P.DOB AS patientDob, " +
                        "    " +
                        "    CM.LicenseNo AS senderLicense, " +
                        "    INS.LicenseNo AS receiverLicense, " +
                        "    INS.LicenseNo AS payerLicense, " +
                        "    CM.LicenseNo AS providerLicense, " +
                        "    CM.LicenseNo AS facilityLicense, " +
                        "    " +
                        "    PV.EncounterType AS encounterType, " +
                        "    PV.EncounterStartTime AS encounterStart, " +
                        "    ISNULL(PV.EncounterEndTime, PV.EncounterStartTime) AS encounterEnd, " +
                        "    PV.FacilityId AS facilityId, " +
                        "    P.MRN AS mrn, " +
                        "    " +
                        "    PH.PayerAuthRef AS payerAuthRef " +
                        "FROM Claims C " +
                        "INNER JOIN PatientVisits PV ON PV.Id = C.VisitId " +
                        "INNER JOIN Patients P ON P.Id = PV.PatientId " +
                        "LEFT JOIN ClientMaster CM ON CM.Id = C.ClientId " +
                        "LEFT JOIN Insurances INS ON INS.Id = C.InsuranceID " +
                        "LEFT JOIN PatientInsuranceDetails PID ON PID.Id = PV.PatientInsuranceId " +
                        "LEFT JOIN PreAuthHead PH ON PH.PatientVisitId = PV.Id " +
                        "WHERE C.Status = 6 " +
                        "  AND C.SubmissionBatchId IS NULL " +
                        "  AND ISNULL(C.IsDeleted, 0) = 0 " +
                        "ORDER BY C.CreatedAt ASC";

        try {
            List<ClaimRequestDto> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
                ClaimRequestDto dto = new ClaimRequestDto();

                // Claim Identification
                dto.setId(rs.getLong("claimId"));
                dto.setClaimId(rs.getString("claimRef"));
                dto.setGross(rs.getDouble("claimAmount"));

                // Patient Information
                dto.setMemberId(rs.getString("memberId"));
                dto.setEmiratesId(rs.getString("emiratesId"));
                dto.setPatientName(rs.getString("patientName"));
                dto.setPatientGender(rs.getString("patientGender"));
                dto.setPatientDateOfBirth(rs.getString("patientDob"));

                // Header Information
                dto.setSenderId(rs.getString("senderLicense"));
                dto.setReceiverId(rs.getString("receiverLicense"));
                dto.setPayerId(rs.getString("payerLicense"));
                dto.setProviderId(rs.getString("providerLicense"));
                dto.setFacilityId(rs.getString("facilityLicense"));

                // Encounter Information
                dto.setEncounterType(rs.getInt("encounterType"));
                dto.setEncounterStart(rs.getString("encounterStart"));
                dto.setEncounterEnd(rs.getString("encounterEnd"));
                dto.setMrn(rs.getString("mrn"));

                // Authorization Reference
                dto.setPayerAuthRef(rs.getString("payerAuthRef"));

                // Constants
                dto.setWeight(50.0);  // Constant
                dto.setDispositionFlag("PRODUCTION");
                dto.setRecordCount(1);
                dto.setRetryCount(0);

                // Fetch Diagnoses
                dto.setDiagnoses(fetchDiagnoses(dto.getClaimId()));

                // Fetch Activities
                dto.setActivities(fetchActivities(dto.getClaimId()));

                // Calculate totals from activities
                double totalNet = 0.0;
                double totalPatientShare = 0.0;
                if (dto.getActivities() != null && !dto.getActivities().isEmpty()) {
                    for (ClaimActivityDto act : dto.getActivities()) {
                        totalNet += act.getNet() != null ? act.getNet() : 0.0;
                        totalPatientShare += act.getPatientShare() != null ? act.getPatientShare() : 0.0;
                    }
                }

                // If activities have values, use them; otherwise use ClaimAmount
                if (totalNet > 0) {
                    dto.setNet(totalNet);
                    dto.setGross(totalNet);
                } else {
                    dto.setNet(rs.getDouble("claimAmount"));
                    dto.setGross(rs.getDouble("claimAmount"));
                }
                dto.setPatientShare(totalPatientShare);

                // Fetch Attachment
                dto.setFileContent(fetchAttachment(dto.getClaimId()));
                dto.setFileName("claim_" + dto.getClaimId() + ".pdf");

                return dto;
            });

            log.info("Found {} pending claims for upload", results.size());
            return results;

        } catch (Exception e) {
            log.error("Database error while fetching pending claims", e);
            throw new RuntimeException("Failed to fetch pending claims", e);
        }
    }

    // ==================== HELPER METHODS ====================

    private List<DiagnosisDto> fetchDiagnoses(String claimId) {
        String sql = "SELECT Code, Type FROM ClaimDiagnosis WHERE ClaimId = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            DiagnosisDto dto = new DiagnosisDto();
            dto.setCode(rs.getString("Code"));
            dto.setType(rs.getString("Type"));

            // Constants for DxInfo
            dto.setDxInfoType("POA");
            dto.setDxInfoCode("Y");

            return dto;
        }, claimId);
    }

    private List<ClaimActivityDto> fetchActivities(String claimId) {
        String sql =
                "SELECT " +
                        "    CO.ItemSequenceNo AS seq_no, " +
                        "    CO.ServiceCode AS code, " +
                        "    CO.ServiceName AS codeDisplay, " +
                        "    CO.ServiceStartDate AS servicedDate, " +
                        "    ISNULL(CO.Quantity, 0.0) AS quantity, " +
                        "    ISNULL(CO.InsuranceNetAmount, 0.0) AS net, " +
                        "    ISNULL(CO.CopayAmount, 0.0) AS patientShare, " +
                        "    ISNULL(CO.ServiceCategoryId, 1) AS type, " +
                        "    CLIN.LicenseNo AS clinician, " +
                        "    CO.VisitOrderId AS visitOrderId, " +
                        "    CO.Id AS itemId " +
                        "FROM ClaimOrders CO " +
                        "LEFT JOIN Clinicians CLIN ON CLIN.Id = CO.OrderingClinicianId " +
                        "WHERE CO.ClaimId = ? " +
                        " AND ISNULL(CO.IsDeleted,0) = 0 AND ISNULL(CO.ApprovalStatus, 0) <> 4 AND ISNULL(CO.CoverageStatus, 0) <>2" +
                        "ORDER BY CO.ItemSequenceNo ASC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ClaimActivityDto activity = new ClaimActivityDto();
            activity.setId(rs.getString("seq_no"));
            activity.setCode(rs.getString("code"));
            activity.setStart(rs.getString("servicedDate"));
            activity.setQuantity(rs.getDouble("quantity"));
            activity.setNet(rs.getDouble("net"));
            activity.setPatientShare(rs.getDouble("patientShare"));
            activity.setType(String.valueOf(rs.getInt("type")));
            activity.setClinician(rs.getString("clinician"));

            // Constants
            activity.setDuration(0);  // Constant

            // Fetch Observations
            String visitOrderId = rs.getString("visitOrderId");
            if (visitOrderId != null) {
                activity.setObservations(fetchActivityObservations(visitOrderId));
            }

            return activity;
        }, claimId);
    }

    private List<ObservationDto> fetchActivityObservations(String visitOrderId) {
        String sql = "SELECT ObservationType, ObservationCode, ObservationValue, ObservationValueType FROM PatientVisitOrderObservations WHERE OrderId = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ObservationDto obs = new ObservationDto();
            obs.setType(rs.getString("ObservationType"));
            obs.setCode(rs.getString("ObservationCode"));
            obs.setValue(rs.getString("ObservationValue"));
            obs.setValueType(rs.getString("ObservationValueType"));
            return obs;
        }, visitOrderId);
    }

    private byte[] fetchAttachment(String claimId) {
        try {
            String sql = "SELECT FileContent FROM ClaimAttachments WHERE ClaimId = ? AND FileType = 'INVOICE'";
            return jdbcTemplate.queryForObject(sql, byte[].class, claimId);
        } catch (Exception e) {
            log.debug("No attachment found for claim: {}", claimId);
            return null;
        }
    }

    // ==================== UPDATE METHODS ====================

    @Override
    public void updateClaimAsSent(Long id, String entityId) {
        String sql = "UPDATE Claims SET Status = 7, EntityId = ?, SentAt = GETDATE(), RiayatiResponseJson = ? WHERE Id = ?";
        jdbcTemplate.update(sql, entityId, "{\"status\":\"SENT\",\"entityId\":\"" + entityId + "\"}", id);
        log.info("Claim {} marked as SENT with EntityId: {}", id, entityId);
    }


    @Override
    public void updateClaimAsFailed(Long id, String errorMessage) {
        String sql = "UPDATE Claims SET Status = 6, ErrorMessage = ? WHERE Id = ?";
        jdbcTemplate.update(sql, errorMessage, id);
        log.error("Claim {} marked as FAILED: {}", id, errorMessage);
    }

    @Override
    public void updateRetryCount(Long id) {
        String sql = "UPDATE Claims SET RetryCount = ISNULL(RetryCount, 0) + 1 WHERE Id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Claim {} retry count incremented", id);
    }

    //payment and remittance =

    @Override
    public void updatePaymentInfo(String claimId, Double paymentAmount, String paymentReference,
                                  String denialCode, String remittanceData) {
        String sql =
                "UPDATE Claims " +
                        "SET PaymentAmount = ?, PaymentReference = ?, DenialCode = ?, " +
                        "    RemittanceData = ?, Status = 8, ProcessedAt = GETDATE() " +
                        "WHERE ClaimRef = ?";

        jdbcTemplate.update(sql, paymentAmount, paymentReference, denialCode, remittanceData, claimId);
        log.info("Payment info updated for claim: {}, amount: {}, denialCode: {}", claimId, paymentAmount, denialCode);
    }

    @Override
    public Optional<ClaimEntity> findById(Long id) {
        String sql = "SELECT Id, ClaimRef, EntityId, Status, PaymentAmount FROM Claims WHERE Id = ?";
        try {
            ClaimEntity entity = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                ClaimEntity e = new ClaimEntity();
                e.setId(rs.getLong("Id"));
                e.setClaimId(rs.getString("ClaimRef"));
                e.setEntityId(rs.getString("EntityId"));
                e.setStatus(String.valueOf(rs.getInt("Status")));
                e.setPaymentAmount(rs.getDouble("PaymentAmount"));
                return e;
            }, id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ClaimEntity> findByClaimId(String claimId) {
        String sql = "SELECT Id, ClaimRef, EntityId, Status FROM Claims WHERE ClaimRef = ?";
        try {
            ClaimEntity entity = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                ClaimEntity e = new ClaimEntity();
                e.setId(rs.getLong("Id"));
                e.setClaimId(rs.getString("ClaimRef"));
                e.setEntityId(rs.getString("EntityId"));
                e.setStatus(String.valueOf(rs.getInt("Status")));
                return e;
            }, claimId);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ClaimEntity> findByEntityId(String entityId) {
        String sql = "SELECT Id, ClaimRef, EntityId, Status FROM Claims WHERE EntityId = ?";
        try {
            ClaimEntity entity = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                ClaimEntity e = new ClaimEntity();
                e.setId(rs.getLong("Id"));
                e.setClaimId(rs.getString("ClaimRef"));
                e.setEntityId(rs.getString("EntityId"));
                e.setStatus(String.valueOf(rs.getInt("Status")));
                return e;
            }, entityId);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public ClaimEntity save(ClaimEntity entity) {
        // Claims already exist in the system. This method is for cases where we need to create
        // a new claim record. Usually not needed as claims come from the existing system.
        String sql =
                "INSERT INTO Claims (" +
                        "    ClaimRef, MemberId, EmiratesId, PatientName, PatientGender, PatientDob, " +
                        "    SenderId, ReceiverId, PayerId, ProviderId, FacilityId, EncounterType, " +
                        "    Gross, Net, PatientShare, Status, CreatedAt " +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                entity.getClaimId(),
                entity.getMemberId(),
                entity.getEmiratesId(),
                entity.getPatientName(),
                entity.getPatientGender(),
                entity.getPatientDob(),
                entity.getSenderId(),
                entity.getReceiverId(),
                entity.getPayerId(),
                entity.getProviderId(),
                entity.getFacilityId(),
                entity.getEncounterType(),
                entity.getGross(),
                entity.getNet(),
                entity.getPatientShare(),
                6,  // PENDING status
                LocalDateTime.now()
        );

        return entity;
    }

    @Override
    public List<ClaimEntity> fetchClaimsWaitingForPayment() {
        String sql =
                "SELECT Id, ClaimRef, EntityId, MemberId, ProviderId, ClaimAmount AS Net, Status " +
                        "FROM Claims " +
                        "WHERE Status = 7 " +
                        "ORDER BY SentAt ASC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ClaimEntity entity = new ClaimEntity();
            entity.setId(rs.getLong("Id"));
            entity.setClaimId(rs.getString("ClaimRef"));
            entity.setEntityId(rs.getString("EntityId"));
            entity.setMemberId(rs.getString("MemberId"));
            entity.setProviderId(rs.getString("ProviderId"));
            entity.setNet(rs.getDouble("Net"));
            entity.setStatus(String.valueOf(rs.getInt("Status")));
            return entity;
        });
    }

    @Override
    public void markAsProcessed(String entityId) {
        String sql = "UPDATE Claims SET Status = 8, ProcessedAt = GETDATE() WHERE EntityId = ?";
        jdbcTemplate.update(sql, entityId);
        log.info("Claim with EntityId {} marked as PROCESSED", entityId);
    }

    @Override
    public void updateRemittance(String claimRef, Double transactionPaidAmount, String paymentDate, String paymentRef, String responseIdentifier, String note){

        try{
            // get the master remittance by claim ref

            String sql = """
                        SELECT R.ClaimAmount AS ClaimAmount, R.ClaimId AS ClaimId, R.Id AS  RemittancesID, 
                        R.RemittancesRef AS RemittancesRef, R.PaidAmount AS PaidAmount
                        FROM [PowerDoc].[dbo].[Remittances] R
                        INNER JOIN [PowerDoc].[dbo].[Claims] C ON C.Id = R.ClaimId
                        WHERE C.ClaimRef = ?
                        """;

            Map<String, Object> row = jdbcTemplate.query(sql, rs -> {
                if (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("RemittancesID", rs.getString("RemittancesID"));
                    m.put("ClaimAmount", rs.getObject("ClaimAmount"));
                    m.put("PaidAmount", rs.getObject("PaidAmount"));
                    m.put("ClaimId", rs.getString("ClaimId"));
                    m.put("RemittancesRef", rs.getString("RemittancesRef"));
                    return m;
                }
                return null;
            }, claimRef);

            String remittancesID = row != null ? (String) row.get("RemittancesID") : null;
            String claimId = row != null ? (String) row.get("ClaimId") : null;
            Double claimAmount = (row != null && row.get("ClaimAmount") != null)
                    ? ((Number) row.get("ClaimAmount")).doubleValue() : null;
            Double paidAmount = (row != null && row.get("PaidAmount") != null)
                    ? ((Number) row.get("PaidAmount")).doubleValue() : null;
            String remittancesRef = row != null ? (String) row.get("RemittancesRef") : null;

            Double rejectedAmount = claimAmount;
            int status = 1;

            // insert into Remittance transaction table


            insertRemittanceTransaction(remittancesID, transactionPaidAmount, paymentDate, paymentRef, note, responseIdentifier);


            // update Remittance master table    -- status and paid amount

            Double newPaidAmount = (paidAmount != null ? paidAmount : 0.0) + transactionPaidAmount;

            Double newPaidAmountWithOutFess = newPaidAmount;
            // check Remittance new status

            Double statusResult = newPaidAmountWithOutFess;

            int remittanceStatus = 1;
            if (statusResult != null && statusResult == 0){
                remittanceStatus = 4;
            }
            if (statusResult != null && statusResult >= claimAmount) {
                remittanceStatus = 2;
            }
            if (statusResult != null && statusResult > 0 && statusResult < claimAmount) {
                remittanceStatus = 3;
            }



            updateRemittanceStatus(remittanceStatus, newPaidAmountWithOutFess, remittancesID);

        } catch (Exception e) {
            log.error("Error updating remittance record for ClaimRef: {}", claimRef, e);
            throw new RuntimeException("Failed to update remittance record", e);
        }
    }

    @Override
    public void updateRemittanceStatus(int status, Double paidAmount, String remittancesId) {
        String sql = """
        UPDATE R 
        SET R.Status = ?,
            R.PaidAmount = ?,
            R.UpdatedAt = GETDATE(),
            R.UpdatedBy = 1
        FROM [PowerDoc].[dbo].[Remittances] R
        WHERE R.Id = ?
        """;

        try {
            int result = jdbcTemplate.update(sql, status, paidAmount, remittancesId);
            log.info("Updated remittance status to {} for ClaimRef: {}, RemittancesRef: {}. Rows affected: {}",
                    status, result);
        } catch (Exception e) {
            log.error("Error updating remittance status for remittancesId: {}", remittancesId, e);
            throw new RuntimeException("Failed to update remittance status", e);
        }
    }

    @Override
    public void insertRemittanceTransaction(
            String RemittanceId, Double paidAmount,
            String paymentDate, String paymentRef, String note, String responseIdentifier) {

        String sql = """
            INSERT INTO [PowerDoc].[dbo].[RemittancesPaymentTransaction]
            (Id, RemittanceId, PaidAmount, PaymentDate, PaymentRef, Note, CreatedAt, CreatedBy,UpdatedAt,UpdatedBy, IsDeleted, ResponseIdentifier)
            VALUES (?, ?, ?, ?, ?, ?, GETDATE(),1,GETDATE(),1, 0, ?)
            """;

        try {
            UUID id = UUID.randomUUID();

            int result = jdbcTemplate.update(sql,
                    id.toString(),
                    RemittanceId,
                    paidAmount,
                    paymentDate,
                    paymentRef,
                    note,
                    responseIdentifier
            );

            log.info("Inserted remittance record. Rows affected: {}", result);

        } catch (Exception e) {
            log.error("SQL Error Details:", e);
        }
    }


}