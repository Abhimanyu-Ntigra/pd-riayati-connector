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

    public AuthorizationRepositoryImpl(@Qualifier("riayatiJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<AuthorizationRequestDto> fetchPendingAuthorizations() {
        String sql =
                "SELECT " +
                        "    AH.Id AS authId, " +
                        "    AH.AuthRef AS authRef, " +
                        "    AH.RequestReferenceNumber AS requestRefNumber, " +
                        "    AH.CreatedAt AS createdAt, " +
                        "    AH.RetryCount AS retryCount, " +
                        "    AH.DateOrdered AS dateOrdered, " +
                        "    PID.MemberId AS memberId, " +
                        "    P.NationalId AS emiratesId, " +
                        "    P.FirstName + ' ' + P.LastName AS fullName, " +
                        "    P.Gender AS gender, " +
                        "    P.DOB AS dateOfBirth, " +
                        "    P.Mobile AS contactNumber, " +
                        "    P.Email AS email, " +
                        "    CM.LicenseNo AS senderLicense, " +
                        "    'PAYER_LICENSE_ID' AS receiverLicense, " +
                        "    'PAYER_LICENSE_ID' AS payerLicense, " +
                        "    CM.LicenseNo AS facilityLicense, " +
                        "    PV.EncounterType AS encounterType, " +
                        "    PV.FacilityId AS facilityId, " +
                        "    PV.EncounterStartTime AS encounterStart, " +
                        "    PV.EncounterEndTime AS encounterEnd, " +
                        "    AH.Weight AS weight " +
                        "FROM AuthHead AH " +
                        "INNER JOIN PatientVisits PV ON PV.Id = AH.PatientVisitId " +
                        "INNER JOIN Patients P ON P.Id = PV.PatientId " +
                        "LEFT JOIN ClientMaster CM ON CM.Id = AH.SenderId " +
                        "LEFT JOIN PatientInsuranceDetails PID ON PID.Id = PV.PatientInsuranceId " +
                        "WHERE AH.Status IN ('PENDING', 'RETRY') " +
                        "  AND AH.RetryCount < 3 " +
                        "ORDER BY AH.CreatedAt ASC";

        try {
            List<AuthorizationRequestDto> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
                AuthorizationRequestDto dto = new AuthorizationRequestDto();
                dto.setId(rs.getLong("authId"));
                dto.setTransactionId(rs.getString("authRef"));
                dto.setRequestReferenceNumber(rs.getString("requestRefNumber"));
                dto.setMemberId(rs.getString("memberId"));
                dto.setEmiratesId(rs.getString("emiratesId"));
                dto.setFullName(rs.getString("fullName"));
                dto.setGender(rs.getString("gender"));
                dto.setDateOfBirth(rs.getString("dateOfBirth"));
                dto.setContactNumber(rs.getString("contactNumber"));
                dto.setEmail(rs.getString("email"));
                dto.setSenderId(rs.getString("senderLicense"));
                dto.setReceiverId(rs.getString("receiverLicense"));
                dto.setPayerId(rs.getString("payerLicense"));
                dto.setFacilityId(rs.getString("facilityLicense"));
                dto.setEncounterType(rs.getInt("encounterType"));
                dto.setEncounterStart(rs.getString("encounterStart"));
                dto.setEncounterEnd(rs.getString("encounterEnd"));
                dto.setWeight(rs.getDouble("weight"));

                String dateOrdered = rs.getString("dateOrdered");
                if (dateOrdered == null) {
                    dateOrdered = rs.getString("createdAt");
                }
                dto.setDateOrdered(dateOrdered);

                dto.setRetryCount(rs.getInt("retryCount"));
                dto.setDispositionFlag("PRODUCTION");
                dto.setRecordCount(1);

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
        String sql = "SELECT Code, Type FROM AuthDiagnosis WHERE AuthId = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new DiagnosisDto(
                rs.getString("Code"),
                rs.getString("Type")
        ), authId);
    }

    private List<AuthorizationActivityDto> fetchActivities(String authId) {
        String sql =
                "SELECT " +
                        "    AA.Id AS activityId, " +
                        "    AA.ActivityReference AS activityRef, " +
                        "    AA.ServiceCode AS code, " +
                        "    AA.ServiceStartDate AS startDate, " +
                        "    AA.ServiceCategoryId AS type, " +
                        "    AA.Location AS location, " +
                        "    AA.Quantity AS quantity, " +
                        "    AA.Unit AS unit, " +
                        "    AA.NetAmount AS net, " +
                        "    AA.ClinicianLicense AS clinician, " +
                        "    AA.Duration AS duration, " +
                        "    AA.VisitOrderId AS visitOrderId " +
                        "FROM AuthActivities AA " +
                        "WHERE AA.AuthId = ? " +
                        "ORDER BY AA.ItemSequenceNo ASC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            AuthorizationActivityDto activity = new AuthorizationActivityDto();
            activity.setId(rs.getString("activityId"));
            activity.setActivityReference(rs.getString("activityRef"));
            activity.setCode(rs.getString("code"));
            activity.setStart(rs.getString("startDate"));
            activity.setType(String.valueOf(rs.getInt("type")));
            activity.setLocation(rs.getString("location"));
            activity.setQuantity(rs.getDouble("quantity"));
            activity.setUnit(rs.getString("unit"));
            activity.setNet(rs.getDouble("net"));
            activity.setClinician(rs.getString("clinician"));

            int duration = rs.getInt("duration");
            if (duration > 0) {
                activity.setDuration((double) duration);
            }

            // Fetch Observations
            String visitOrderId = rs.getString("visitOrderId");
            if (visitOrderId != null) {
                activity.setObservations(fetchActivityObservations(visitOrderId));
            }

            return activity;
        }, authId);
    }

    private List<ObservationDto> fetchActivityObservations(String visitOrderId) {
        String sql = "SELECT ObservationType, ObservationCode, ObservationValue, ObservationValueType FROM ActivityObservations WHERE OrderId = ?";

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
        String sql = "UPDATE AuthHead SET Status = 'SENT', EntityId = ?, SentAt = ? WHERE Id = ?";
        jdbcTemplate.update(sql, entityId, Timestamp.valueOf(LocalDateTime.now()), id);
        log.info("Authorization {} marked as SENT with EntityId: {}", id, entityId);
    }

    @Override
    public void updateAuthorizationAsFailed(Long id, String errorMessage) {
        String sql = "UPDATE AuthHead SET Status = 'FAILED', ErrorMessage = ? WHERE Id = ?";
        jdbcTemplate.update(sql, errorMessage, id);
        log.error("Authorization {} marked as FAILED: {}", id, errorMessage);
    }

    @Override
    public void updateRetryCount(Long id) {
        String sql = "UPDATE AuthHead SET RetryCount = RetryCount + 1, Status = 'RETRY' WHERE Id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Authorization {} retry count incremented", id);
    }

    @Override
    public void updateAuthorizationResponse(String transactionId, String result, String idPayer,
                                            String denialCode, String startDate, String endDate,
                                            Double limit, String responseData) {
        String sql =
                "UPDATE AuthHead " +
                        "SET Result = ?, IdPayer = ?, DenialCode = ?, " +
                        "    StartDate = ?, EndDate = ?, CoverageLimit = ?, " +
                        "    ResponseData = ?, Status = 'PROCESSED', ProcessedAt = ? " +
                        "WHERE AuthRef = ?";

        jdbcTemplate.update(sql, result, idPayer, denialCode, startDate, endDate, limit,
                responseData, Timestamp.valueOf(LocalDateTime.now()), transactionId);
        log.info("Authorization response updated for: {}, Result: {}", transactionId, result);
    }
}

