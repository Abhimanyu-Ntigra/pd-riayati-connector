package com.ntigra.riayati_middleware.respository.Impl;

import com.ntigra.riayati_middleware.dto.request.DispenseActivityDto;
import com.ntigra.riayati_middleware.dto.request.DispenseRequestDto;
import com.ntigra.riayati_middleware.respository.DispenseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class DispenseRepositoryImpl implements DispenseRepository {

    private final JdbcTemplate jdbcTemplate;

    public DispenseRepositoryImpl(@Qualifier("db1JdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<DispenseRequestDto> fetchPendingDispenses() {
        String sql =
                "SELECT " +
                        "    PH.Id AS dispenseId, " +
                        "    PH.PreAuthRef AS dispenseRef, " +
                        "    PH.ParentAuthRef AS referenceNumber, " +
                        "    PH.PayerAuthRef AS authorizationId, " +
                        "    PH.CreatedAt AS createdAt, " +
                        "    PH.RetryCount AS retryCount, " +
                        "    " +
                        "    PID.MemberId AS memberId, " +
                        "    P.NationalId AS emiratesId, " +
                        "    P.FirstName + ' ' + P.LastName AS fullName, " +
                        "    P.Gender AS gender, " +
                        "    P.DOB AS dateOfBirth, " +
                        "    P.Mobile AS contactNumber, " +
                        "    P.Email AS email, " +
                        "    " +
                        "    CM.LicenseNo AS senderLicense, " +
                        "    'PAYER_LICENSE_ID' AS receiverLicense, " +
                        "    'PAYER_LICENSE_ID' AS payerLicense, " +
                        "    CM.LicenseNo AS facilityLicense, " +
                        "    " +
                        "    PV.EncounterType AS encounterType, " +
                        "    " +
                        "    PAO.ItemSequenceNo AS activityId, " +
                        "    PAO.ServiceCode AS code, " +
                        "    PAO.Quantity AS quantity, " +
                        "    PAO.ServiceStartDate AS serviceDate " +
                        "FROM PreAuthHead PH " +
                        "INNER JOIN PatientVisits PV ON PV.Id = PH.PatientVisitId " +
                        "INNER JOIN Patients P ON P.Id = PV.PatientId " +
                        "LEFT JOIN ClientMaster CM ON CM.Id = PH.SenderId " +
                        "LEFT JOIN PatientInsuranceDetails PID ON PID.Id = PV.PatientInsuranceId " +
                        "LEFT JOIN PreAuthOrders PAO ON PAO.PreAuthId = PH.Id " +
                        "WHERE PH.Status = 1 " +
                        "  AND PH.TransactionType = 'DISPENSE' " +
                        "  AND ISNULL(PH.IsDeleted, 0) = 0 " +
                        "ORDER BY PH.CreatedAt ASC";

        try {
            List<DispenseRequestDto> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
                DispenseRequestDto dto = new DispenseRequestDto();

                // ========== FROM PREAUTHHEAD TABLE ==========
                dto.setId(rs.getLong("dispenseId"));
                dto.setDispenseId(rs.getString("dispenseRef"));
                dto.setReferenceNumber(rs.getString("referenceNumber"));
                dto.setAuthorizationId(rs.getString("authorizationId"));
                dto.setRetryCount(rs.getInt("retryCount"));

                // ========== FROM PATIENTS TABLE ==========
                dto.setMemberId(rs.getString("memberId"));
                dto.setEmiratesId(rs.getString("emiratesId"));
                dto.setFullName(rs.getString("fullName"));
                dto.setGender(rs.getString("gender") != null ? rs.getString("gender") : "Male");
                dto.setDateOfBirth(rs.getString("dateOfBirth"));
                dto.setContactNumber(rs.getString("contactNumber"));
                dto.setEmail(rs.getString("email"));

                // ========== FROM CLIENTMASTER TABLE ==========
                dto.setSenderId(rs.getString("senderLicense"));
                dto.setFacilityId(rs.getString("facilityLicense"));

                // ========== CONSTANTS ==========
                dto.setReceiverId("PAYER_LICENSE_ID");
                dto.setPayerId("PAYER_LICENSE_ID");
                dto.setDispositionFlag("PRODUCTION");
                dto.setRecordCount(1);
                dto.setDispenseDate(rs.getString("createdAt"));  // Use CreatedAt as DispenseDate

                // ========== FROM PATIENTVISITS TABLE ==========
                Integer encounterType = rs.getInt("encounterType");
                dto.setEncounterType(rs.wasNull() ? 1 : encounterType);

                // ========== BUILD ACTIVITY ==========
                DispenseActivityDto activity = new DispenseActivityDto();
                activity.setId(rs.getString("activityId"));
                activity.setType("5");  // Constant: Drug
                activity.setCode(rs.getString("code"));
                activity.setActivityReference(rs.getString("activityId"));  // Same as activity ID
                activity.setQuantity(rs.getDouble("quantity"));
                activity.setDispensedQuantity(rs.getDouble("quantity"));   // Same as quantity
                activity.setLocation("2");  // Constant: Outpatient
                activity.setPerformerName("Pharmacy Name");  // Constant
                activity.setAuthorizationId(rs.getString("authorizationId"));
                activity.setComments("Dispensed");  // Constant
                dto.setActivity(activity);

                return dto;
            });

            log.info("Found {} pending dispense requests", results.size());
            return results;

        } catch (Exception e) {
            log.error("Database error while fetching pending dispenses", e);
            throw new RuntimeException("Failed to fetch pending dispenses", e);
        }
    }

    @Override
    public void updateDispenseAsSent(Long id, String entityId) {
        String sql = "UPDATE PreAuthHead SET Status = 2, EntityId = ?, SentAt = GETDATE() WHERE Id = ?";
        jdbcTemplate.update(sql, entityId, id);
        log.info("Dispense {} marked as SENT with EntityId: {}", id, entityId);
    }

    @Override
    public void updateDispenseAsSentWithResponse(Long id, String entityId, String responseJson) {
        String sql = "UPDATE PreAuthHead SET Status = 2, EntityId = ?, SentAt = GETDATE(), ResponseData = ? WHERE Id = ?";
        jdbcTemplate.update(sql, entityId, responseJson, id);
        log.info("Dispense {} marked as SENT with EntityId: {}", id, entityId);
    }

    @Override
    public void updateDispenseAsFailed(Long id, String errorMessage) {
        String sql = "UPDATE PreAuthHead SET Status = 3, ErrorMessage = ? WHERE Id = ?";
        jdbcTemplate.update(sql, errorMessage, id);
        log.error("Dispense {} marked as FAILED: {}", id, errorMessage);
    }

    @Override
    public void updateRetryCount(Long id) {
        String sql = "UPDATE PreAuthHead SET RetryCount = ISNULL(RetryCount, 0) + 1 WHERE Id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Dispense {} retry count incremented", id);
    }

    //
    @Override
    public void updateDispenseResponse(String dispenseId, String result, String idPayer,
                                       String denialCode, String startDate, String endDate,
                                       Double limit, String responseData) {
        String sql =
                "UPDATE PreAuthHead " +
                        "SET Result = ?, IdPayer = ?, DenialCode = ?, " +
                        "    StartDate = ?, EndDate = ?, CoverageLimit = ?, " +
                        "    ResponseData = ?, Status = 4, ProcessedAt = GETDATE() " +
                        "WHERE PreAuthRef = ?";
        jdbcTemplate.update(sql, result, idPayer, denialCode, startDate, endDate, limit,
                responseData, dispenseId);
        log.info("Dispense response updated for: {}, Result: {}", dispenseId, result);
    }
}