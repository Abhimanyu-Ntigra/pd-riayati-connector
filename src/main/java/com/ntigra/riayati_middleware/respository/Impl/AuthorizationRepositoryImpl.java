package com.ntigra.riayati_middleware.respository.Impl;

import com.ntigra.riayati_middleware.dto.request.AuthorizationActivityDto;
import com.ntigra.riayati_middleware.dto.request.AuthorizationRequestDto;
import com.ntigra.riayati_middleware.dto.request.DiagnosisDto;
import com.ntigra.riayati_middleware.dto.request.ObservationDto;
import com.ntigra.riayati_middleware.respository.AuthorizationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@Slf4j
public class AuthorizationRepositoryImpl implements AuthorizationRepository {

    private final JdbcTemplate jdbcTemplate;

    public AuthorizationRepositoryImpl(@Qualifier("db1JdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<AuthorizationRequestDto> fetchPendingAuthorizations() {
        String sql =
                "SELECT " +
                        "    PH.Id AS authId, " +
                        "    PH.PreAuthRef AS authRef, " +
                        "    PH.ParentAuthRef AS requestRefNumber, " +
                        "    PH.CreatedAt AS createdAt, " +
                        "    PH.PollingAttempts AS retryCount, " +
                        "    PH.CreatedAt AS dateOrdered, " +
                        "    " +
                        "    PID.MemberId AS memberId, " +
                        "    P.NationalId AS emiratesId, " +
                        "    P.FirstName + ' ' + P.LastName AS fullName, " +
                        "    P.Gender AS gender, " +
                        "    P.DOB AS dateOfBirth, " +
                        "    P.Mobile AS contactNumber, " +
                        "    P.MRN AS mrn, " +
                        "    " +
                        "    CM.LicenseNo AS senderLicense, " +
                        "    CM.LicenseNo AS facilityLicense, " +
                        "    " +
                        "    PV.EncounterType AS encounterType, " +
                        "    PV.FacilityId AS facilityId, " +
                        "    PV.EncounterStartTime AS encounterStart, " +
                        "    PV.EncounterEndTime AS encounterEnd, " +
                        "    " +
                        "    PH.Weight AS weight, " +
                        "    PH.ClaimAmount AS coverageLimit " +
                        "FROM PreAuthHead PH " +
                        "INNER JOIN PatientVisits PV ON PV.Id = PH.PatientVisitId " +
                        "INNER JOIN Patients P ON P.Id = PV.PatientId " +
                        "LEFT JOIN ClientMaster CM ON CM.Id = PH.SenderId " +
                        "LEFT JOIN PatientInsuranceDetails PID ON PID.Id = PV.PatientInsuranceId " +
                        "WHERE PH.Status = 1 " +
                        "  AND ISNULL(PH.IsDeleted, 0) = 0 " +
                        "ORDER BY PH.CreatedAt ASC";

        // status = 1 means NEW, 2 means PENDING
        try {
            List<AuthorizationRequestDto> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
                AuthorizationRequestDto dto = new AuthorizationRequestDto();

                // ========== FROM PREAUTHHEAD TABLE ==========
                dto.setId(rs.getLong("authId"));
                dto.setTransactionId(rs.getString("authRef"));
                dto.setRequestReferenceNumber(rs.getString("requestRefNumber"));
                dto.setRetryCount(rs.getInt("retryCount"));
                dto.setCoverageLimit(rs.getDouble("coverageLimit"));

                // Weight - use DB value or constant 50.0
                double weight = rs.getDouble("weight");
                dto.setWeight(rs.wasNull() ? 50.0 : weight);

                // ========== FROM PATIENTS TABLE ==========
                dto.setMemberId(rs.getString("memberId"));
                dto.setEmiratesId(rs.getString("emiratesId"));
                dto.setFullName(rs.getString("fullName"));
                dto.setGender(rs.getString("gender") != null ? rs.getString("gender") : "Male");
                dto.setDateOfBirth(rs.getString("dateOfBirth"));
                dto.setContactNumber(rs.getString("contactNumber"));
                dto.setMrn(rs.getString("mrn"));

                // ========== FROM CLIENTMASTER TABLE ==========
                dto.setSenderId(rs.getString("senderLicense"));
                dto.setFacilityId(rs.getString("facilityLicense"));

                // ========== FROM PATIENTVISITS TABLE ==========
                Integer encounterType = rs.getInt("encounterType");
                dto.setEncounterType(rs.wasNull() ? 1 : encounterType);
                dto.setEncounterStart(rs.getString("encounterStart"));
                dto.setEncounterEnd(rs.getString("encounterEnd"));
                dto.setFacilityId(rs.getString("facilityId") != null ? rs.getString("facilityId") : dto.getFacilityId());

                // ========== CONSTANTS ==========
                dto.setReceiverId("PAYER_LICENSE_ID");
                dto.setPayerId("PAYER_LICENSE_ID");
                dto.setDispositionFlag("PRODUCTION");
                dto.setRecordCount(1);

                // DateOrdered - use CreatedAt
                String dateOrdered = rs.getString("dateOrdered");
                if (dateOrdered == null) {
                    dateOrdered = rs.getString("createdAt");
                }
                dto.setDateOrdered(dateOrdered);

                // Fetch diagnoses
                dto.setDiagnoses(fetchDiagnoses(dto.getTransactionId()));

                // Fetch activities
                dto.setActivities(fetchActivities(dto.getTransactionId()));

                return dto;
            });

            log.info("Found {} pending authorization requests", results.size());
            return results;

        } catch (Exception e) {
            log.error("Database error while fetching pending authorizations", e);
            throw new RuntimeException("Failed to fetch pending authorizations", e);
        }
    }

    private List<DiagnosisDto> fetchDiagnoses(String authId) {
        String sql = "SELECT Code, Type FROM PreAuthDiagnosis WHERE PreAuthId = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            DiagnosisDto dto = new DiagnosisDto();
            dto.setCode(rs.getString("Code"));
            dto.setType(rs.getString("Type"));
            return dto;
        }, authId);
    }

    private List<AuthorizationActivityDto> fetchActivities(String authId) {
        String sql =
                "SELECT " +
                        "    PAO.ItemSequenceNo AS seq_no, " +
                        "    PAO.ServiceCode AS code, " +
                        "    PAO.ServiceName AS codeDisplay, " +
                        "    PAO.ServiceStartDate AS servicedDate, " +
                        "    ISNULL(PAO.Quantity, 0.0) AS quantity, " +
                        "    ISNULL(PAO.InsuranceGrossAmount, 0.0) AS net, " +
                        "    ISNULL(PAO.CopayAmount, 0.0) AS patientShare, " +
                        "    ISNULL(PAO.ServiceCategoryId, 1) AS type, " +
                        "    PAO.VisitOrderId AS visitOrderId, " +
                        "    PAO.Id AS itemId " +
                        "FROM PreAuthOrders PAO " +
                        "WHERE PAO.PreAuthId = ? " +
                        "ORDER BY PAO.ItemSequenceNo ASC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            AuthorizationActivityDto activity = new AuthorizationActivityDto();
            activity.setId(rs.getString("seq_no"));
            activity.setCode(rs.getString("code"));
            activity.setStart(rs.getString("servicedDate"));
            activity.setQuantity(rs.getDouble("quantity"));
            activity.setNet(rs.getDouble("net"));
            activity.setPatientShare(rs.getDouble("patientShare"));
            activity.setType(String.valueOf(rs.getInt("type")));
            activity.setLocation("2"); // Constant: Outpatient
            activity.setClinician("CONSTANT_CLINICIAN"); // Constant for now

            // Fetch Observations
            String visitOrderId = rs.getString("visitOrderId");
            if (visitOrderId != null) {
                activity.setObservations(fetchActivityObservations(visitOrderId));
            }

            return activity;
        }, authId);
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

    @Override
    public void updateAuthorizationAsSent(Long id, String entityId) {
        String sql = "UPDATE PreAuthHead SET Status = 2, EntityId = ?, SentAt = GETDATE() WHERE Id = ?";
        jdbcTemplate.update(sql, entityId, id);
        log.info("Authorization {} marked as SENT with EntityId: {}", id, entityId);
    }

    @Override
    public void updateAuthorizationAsSentWithResponse(Long id, String entityId, String responseJson) {
        String sql = "UPDATE PreAuthHead SET Status = 2, EntityId = ?, SentAt = GETDATE(), ResponseData = ? WHERE Id = ?";
        jdbcTemplate.update(sql, entityId, responseJson, id);
        log.info("Authorization {} marked as SENT with EntityId: {}", id, entityId);
    }

    @Override
    public void updateAuthorizationAsFailed(Long id, String errorMessage) {
        String sql = "UPDATE PreAuthHead SET Status = 3, ErrorMessage = ? WHERE Id = ?";
        jdbcTemplate.update(sql, errorMessage, id);
        log.error("Authorization {} marked as FAILED: {}", id, errorMessage);
    }

    @Override
    public void updateRetryCount(Long id) {
        String sql = "UPDATE PreAuthHead SET PollingAttempts = ISNULL(PollingAttempts, 0) + 1 WHERE Id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Authorization {} retry count incremented", id);
    }

    @Override
    public void updateAuthorizationResponse(String transactionId, String result, String denialCode,
                                            String responseComment, String idPayer,
                                            String startDate, String endDate,
                                            Double coverageLimit, String responseData) {
        String sql =
                "UPDATE PreAuthHead " +
                        "SET Result = ?, " +
                        "    DenialCode = ?, " +
                        "    ResponseComment = ?, " +
                        "    IdPayer = ?, " +
                        "    StartDate = ?, " +
                        "    EndDate = ?, " +
                        "    ClaimAmount = ?, " +
                        "    ResponseData = ?, " +
                        "    Status = 4, " +
                        "    ProcessedAt = GETDATE() " +
                        "WHERE PreAuthRef = ?";

        jdbcTemplate.update(sql, result, denialCode, responseComment, idPayer,
                startDate, endDate, coverageLimit, responseData, transactionId);
        log.info("Authorization response updated for: {}, Result: {}", transactionId, result);
    }
}