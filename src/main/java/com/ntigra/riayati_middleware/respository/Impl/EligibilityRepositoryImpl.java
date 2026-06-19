package com.ntigra.riayati_middleware.respository.Impl;

import com.ntigra.riayati_middleware.dto.request.DiagnosisDto;
import com.ntigra.riayati_middleware.dto.request.EligibilityActivityDto;
import com.ntigra.riayati_middleware.dto.request.EligibilityRequestDto;
import com.ntigra.riayati_middleware.respository.EligibilityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@Slf4j
public class EligibilityRepositoryImpl implements EligibilityRepository {

    private final JdbcTemplate jdbcTemplate;

    public EligibilityRepositoryImpl(@Qualifier("db1JdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<EligibilityRequestDto> fetchPendingEligibilityRequests() {
        String sql =
                "SELECT " +
                        "    E.Id AS eligibilityId, " +
                        "    E.ReferenceNo AS eligibilityRef, " +
                        "    E.NationalID AS emiratesId, " +
                        "    E.CreatedAt AS createdAt, " +
                        "    E.Status AS eligibilityStatus, " +
                        "    " +
                        "    P.FirstName + ' ' + P.LastName AS fullName, " +
                        "    P.Gender AS gender, " +
                        "    P.DOB AS dateOfBirth, " +
                        "    " +
                        "    PID.MemberId AS memberId, " +
                        "    " +
                        "    CM.LicenseNo AS senderLicense, " +
                        "    CM.LicenseNo AS facilityLicense, " +
                        "    " +
                        "    PV.EncounterType AS encounterType, " +
                        "    PV.FacilityId AS facilityId, " +
                        "    PV.EncounterStartTime AS encounterDate " +
                        "FROM Eligibility E " +
                        "LEFT JOIN Patients P ON P.NationalId = E.NationalID " +
                        "LEFT JOIN PatientInsuranceDetails PID ON PID.PatientId = P.Id " +
                        "LEFT JOIN ClientMaster CM ON CM.Id = E.CreatedBy " +
                        "LEFT JOIN PatientVisits PV ON PV.PatientId = P.Id " +
                        "WHERE (E.Status IS NULL OR E.Status = 'PENDING') " +
                        "  AND E.IsDeleted = 0 " +
                        "ORDER BY E.CreatedAt ASC";

        try {
            List<EligibilityRequestDto> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
                EligibilityRequestDto dto = new EligibilityRequestDto();

                // ========== FROM ELIGIBILITY TABLE ==========
                dto.setId(rs.getLong("eligibilityId"));
                dto.setTransactionId(rs.getString("eligibilityRef"));
                dto.setEmiratesId(rs.getString("emiratesId"));

                // ========== FROM PATIENTS TABLE ==========
                dto.setFullName(rs.getString("fullName"));
                dto.setGender(rs.getString("gender") != null ? rs.getString("gender") : "Male");
                dto.setDateOfBirth(rs.getString("dateOfBirth"));

                // ========== FROM PATIENTINSURANCEDETAILS TABLE ==========
                dto.setMemberId(rs.getString("memberId"));

                // ========== FROM CLIENTMASTER TABLE ==========
                dto.setSenderId(rs.getString("senderLicense"));
                dto.setFacilityId(rs.getString("facilityLicense"));

                // ========== FROM PATIENTVISITS TABLE ==========
                Integer encounterType = rs.getInt("encounterType");
                dto.setEncounterType(rs.wasNull() ? 1 : encounterType);
                dto.setFacilityId(rs.getString("facilityId") != null ? rs.getString("facilityId") : dto.getFacilityId());

                // ========== DATE ORDERED ==========
                String dateOrdered = rs.getString("encounterDate");
                if (dateOrdered == null) {
                    dateOrdered = rs.getString("createdAt");
                }
                dto.setDateOrdered(dateOrdered);

                // ========== CONSTANTS ==========
                dto.setReceiverId("PAYER_LICENSE_ID");  // Constant - from Config
                dto.setPayerId("PAYER_LICENSE_ID");      // Constant - from Config
                dto.setRetryCount(0);
                dto.setDispositionFlag("PRODUCTION");
                dto.setRecordCount(1);
                dto.setWeight(50.0);                     // Constant

                // Fetch diagnoses (if any)
                //dto.setDiagnoses(fetchDiagnoses(dto.getTransactionId()));

                // Fetch activities (usually empty for eligibility)
                //dto.setActivities(fetchActivities(dto.getTransactionId()));

                return dto;
            });

            log.info("Found {} pending eligibility requests", results.size());
            return results;

        } catch (Exception e) {
            log.error("Database error while fetching pending eligibility requests", e);
            throw new RuntimeException("Failed to fetch pending eligibility requests", e);
        }
    }

    private List<DiagnosisDto> fetchDiagnoses(String eligibilityId) {
        // diagnosis table
        return new ArrayList<>();
    }

    private List<EligibilityActivityDto> fetchActivities(String eligibilityId) {
        // activities table
        return new ArrayList<>();
    }

    @Override
    public void updateEligibilityAsSent(Long id, String entityId) {
        String sql = "UPDATE Eligibility SET Status = 'SENT', EntityId = ?, SentAt = GETDATE() WHERE Id = ?";
        jdbcTemplate.update(sql, entityId, id);
        log.info("Eligibility {} marked as SENT with EntityId: {}", id, entityId);
    }

    @Override
    public void updateEligibilityAsSentWithResponse(Long id, String entityId, String responseJson) {
        String sql = "UPDATE Eligibility SET Status = 'SENT', EntityId = ?, SentAt = GETDATE(), ResponseData = ? WHERE Id = ?";
        jdbcTemplate.update(sql, entityId, responseJson, id);
        log.info("Eligibility {} marked as SENT with EntityId: {}", id, entityId);
    }

    @Override
    public void updateEligibilityAsFailed(Long id, String errorMessage) {
        String sql = "UPDATE Eligibility SET Status = 'FAILED', ErrorMessage = ? WHERE Id = ?";
        jdbcTemplate.update(sql, errorMessage, id);
        log.error("Eligibility {} marked as FAILED: {}", id, errorMessage);
    }

    @Override
    public void updateRetryCount(Long id) {
        String sql = "UPDATE Eligibility SET RetryCount = ISNULL(RetryCount, 0) + 1 WHERE Id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Eligibility {} retry count incremented", id);
    }

    @Override
    public void updateEligibilityResponse(String transactionId, String result, String idPayer,
                                          String denialCode, String startDate, String endDate,
                                          Double limit, String responseData) {
        String sql =
                "UPDATE Eligibility " +
                        "SET Result = ?, IdPayer = ?, DenialCode = ?, " +
                        "    StartDate = ?, EndDate = ?, CoverageLimit = ?, " +
                        "    ResponseData = ?, Status = 'PROCESSED', ProcessedAt = GETDATE() " +
                        "WHERE ReferenceNo = ?";

        jdbcTemplate.update(sql, result, idPayer, denialCode, startDate, endDate, limit,
                responseData, transactionId);
        log.info("Eligibility response updated for: {}, Result: {}", transactionId, result);
    }
}