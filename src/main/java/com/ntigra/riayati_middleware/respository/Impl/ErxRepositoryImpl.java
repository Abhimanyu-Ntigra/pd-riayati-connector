package com.ntigra.riayati_middleware.respository.Impl;

import com.ntigra.riayati_middleware.dto.request.*;
import com.ntigra.riayati_middleware.dto.request.Authorization.request.DiagnosisDto;
import com.ntigra.riayati_middleware.dto.request.erx.response.ErxActivityResponse;
import com.ntigra.riayati_middleware.respository.ErxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@Slf4j
public class ErxRepositoryImpl implements ErxRepository {

    private final JdbcTemplate jdbcTemplate;

    public ErxRepositoryImpl(@Qualifier("db1JdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ErxRequestDto> fetchPendingErxRequests() {
        String sql =
                "SELECT " +
                        "    CAST(PH.PreAuthRef AS VARCHAR(36)) AS PreAuthRef, " +
                        "    CAST(PH.Id AS VARCHAR(36)) AS preAuthHeadId, " +
                        "    CAST(PH.PatientVisitId AS VARCHAR(36)) AS visitId, " +
                        "    PH.ParentAuthRef AS ParentAuthRef, " +
                        "    PH.CreatedAt AS authorizationCreatedAt, " +
                        "    PH.Status AS status, " +
                        "    PH.IsProceed AS isProceed, " +
                        "    PH.PollingAttempts AS retryCount, " +
                        "    PH.SenderId AS SenderId, " +
                        "    " +
                        "    PV.VisitNo AS VisitNo, " +
                        "    PV.IsNewborn AS IsNewborn, " +
                        "    PV.EncounterStartTime AS EncounterStartTime, " +
                        "    ISNULL(PV.EncounterEndTime, PV.EncounterStartTime) AS EncounterEndTime, " +
                        "    PV.EncounterType AS encounterType, " +
                        "    " +
                        "    P.FirstName AS firstName, " +
                        "    P.LastName AS lastName, " +
                        "    P.NationalId AS EmiratesIDNumber, " +
                        "    P.Gender AS gender, " +
                        "    P.DOB AS birthDate, " +
                        "    P.Mobile AS mobile, " +
                        "    P.Email AS email, " +
                        "    " +
                        "    PM.ItemSequenceNo AS activityId, " +
                        "    PM.DrugCode AS code, " +
                        "    PM.Quantity AS quantity, " +
                        "    PM.DaysSupply AS duration, " +
                        "    PM.Unit AS unitId, " +
                        "    PM.OrderingDate AS startDate, " +
                        "    PM.OrderType AS instructions " +
                        "FROM PreAuthHead PH " +
                        "INNER JOIN PatientVisits PV ON PV.Id = PH.PatientVisitId " +
                        "INNER JOIN Patients P ON P.Id = PV.PatientId " +
                        "LEFT JOIN PreAuthMedications PM ON PM.PreAuthId = PH.Id " +
                        "WHERE PH.Status = 1 " +
                        "  AND ISNULL(PH.IsDeleted, 0) = 0 " +
                        "ORDER BY PH.CreatedAt ASC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ErxRequestDto dto = new ErxRequestDto();

            // ========== FROM PREAUTHHEAD ==========
            dto.setId(rs.getLong("preAuthHeadId"));
            dto.setPrescriptionId(rs.getString("PreAuthRef"));
            dto.setRequestReferenceNumber(rs.getString("ParentAuthRef"));
            dto.setDateOrdered(rs.getString("authorizationCreatedAt"));
            dto.setRetryCount(rs.getInt("retryCount"));
            dto.setSenderId(rs.getString("SenderId"));
            dto.setFacilityId(rs.getString("SenderId"));

            // ========== FROM PATIENTVISITS ==========
            dto.setEncounterType(rs.getInt("encounterType"));
            dto.setEncounterStart(rs.getString("EncounterStartTime"));
            dto.setEncounterEnd(rs.getString("EncounterEndTime"));

            // ========== FROM PATIENTS ==========
            String firstName = rs.getString("firstName");
            String lastName = rs.getString("lastName");
            dto.setFullName((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
            dto.setEmiratesId(rs.getString("EmiratesIDNumber"));
            dto.setGender(rs.getString("gender") != null ? rs.getString("gender") : "Male");
            dto.setDateOfBirth(rs.getString("birthDate"));
            dto.setContactNumber(rs.getString("mobile"));
            dto.setEmail(rs.getString("email"));

            // ========== CONSTANTS ==========
            dto.setMemberId("CONSTANT_MEMBER_ID");
            dto.setWeight(50.0);
            dto.setReceiverId("PAYER_LICENSE_ID");
            dto.setPayerId("PAYER_LICENSE_ID");
            dto.setDispositionFlag("PRODUCTION");
            dto.setRecordCount(1);
            dto.setClinician("CONSTANT_CLINICIAN");
            dto.setTransactionType("eRxRequest");

            // ========== DIAGNOSIS - USING CONSTANTS FROM SAMPLE JSON ==========
            List<DiagnosisDto> diagnoses = new ArrayList<>();
            DiagnosisDto diag = new DiagnosisDto();
            diag.setType("Principal");
            diag.setCode("R12");
            diagnoses.add(diag);
            dto.setDiagnoses(diagnoses);

            // ========== ACTIVITIES ==========
            List<ErxActivityDto> activities = new ArrayList<>();
            String activityId = rs.getString("activityId");
            if (activityId != null) {
                ErxActivityDto activity = new ErxActivityDto();
                activity.setId(activityId);
                activity.setType("5");
                activity.setCode(rs.getString("code"));
                activity.setQuantity(rs.getDouble("quantity"));
                activity.setDuration(rs.getDouble("duration"));
                activity.setUnitId(rs.getInt("unitId"));
                activity.setRefills(0);
                activity.setRouteOfAdmin("001");
                activity.setInstructions(rs.getString("instructions"));
                activity.setStart(rs.getString("startDate"));

                // Additional constants
                activity.setActivityReference("12451242");
                activity.setDispensedQuantity("15.00");
                activity.setLocation("3");
                activity.setPerformerName("Performer name");
                activity.setAuthorizationId("34589435");
                activity.setComments("Test Comment Example");

                activities.add(activity);
            }
            dto.setActivities(activities);

            return dto;
        });
    }

    private List<DiagnosisDto> fetchDiagnoses(String preAuthId) {
        String sql = "SELECT Code, Type FROM PreAuthDiagnosis WHERE PreAuthId = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            DiagnosisDto dto = new DiagnosisDto();
            dto.setCode(rs.getString("Code"));
            dto.setType(rs.getString("Type"));
            return dto;
        }, preAuthId);
    }

    private List<ErxActivityDto> fetchActivities(String preAuthId) {
        String sql =
                "SELECT " +
                        "    ItemSequenceNo AS id, " +
                        "    ServiceCode AS code, " +
                        "    Quantity AS quantity, " +
                        "    ServiceStartDate AS start " +
                        "FROM PreAuthOrders WHERE PreAuthId = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ErxActivityDto activity = new ErxActivityDto();
            activity.setId(rs.getString("id"));
            activity.setCode(rs.getString("code"));
            activity.setQuantity(rs.getDouble("quantity"));
            activity.setStart(rs.getString("start"));

            // Constants
            activity.setType("5");
            activity.setActivityReference("12451242");
            activity.setDispensedQuantity("15.00");
            activity.setLocation("3");
            activity.setPerformerName("Performer name");
            activity.setAuthorizationId("34589435");
            activity.setComments("Test Comment Example");

            return activity;
        }, preAuthId);
    }

    @Override
    public void updateErxAsSent(Long id, String entityId, String referenceNumber) {
        String sql = "UPDATE PreAuthHead SET Status = 2, UpdatedAt = GETDATE() WHERE Id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void updateErxAsSentWithResponse(Long id, String entityId, String referenceNumber, String responseJson) {
        String sql = "UPDATE PreAuthHead SET Status = 2, UpdatedAt = GETDATE(), PreAuthRef = ?, ResponseIdentifier = ?, ResponseComment = ? WHERE Id = ?";
        jdbcTemplate.update(sql, referenceNumber, entityId, responseJson, id);
        log.info("ERX marked as SENT for ID: {}, ReferenceNumber: {}, EntityID: {}", id, referenceNumber, entityId);
    }

    @Override
    public void updateErxAsFailed(Long id, String errorMessage) {
        String sql = "UPDATE PreAuthHead SET Status = 8, UpdatedAt = GETDATE(), ResponseComment = ? WHERE Id = ?";
        jdbcTemplate.update(sql, errorMessage, id);
        log.error("ERX marked as FAILED for ID: {}, Error: {}", id, errorMessage);
    }

    @Override
    public void updateRetryCount(Long id) {
        String sql = "UPDATE PreAuthHead SET PollingAttempts = ISNULL(PollingAttempts, 0) + 1 WHERE Id = ?";
        jdbcTemplate.update(sql, id);
    }


    //PreAuthOrders   DenialCode is for PreAuthOrders
//    @Override
//    public void updateErxResponse(String prescriptionId, String result, String idPayer,
//                                  String denialCode, String startDate, String endDate,
//                                  Double limit, String responseData) {
//        String sql =
//                "UPDATE PreAuthHead " +
//                        "SET Result = ?, IdPayer = ?, DenialCode = ?, " +
//                        "    StartDate = ?, EndDate = ?, CoverageLimit = ?, " +
//                        "    ResponseData = ?, Status = 4, ProcessedAt = GETDATE() " +
//                        "WHERE PreAuthRef = ?";
//        jdbcTemplate.update(sql, result, idPayer, denialCode, startDate, endDate, limit,
//                responseData, prescriptionId);
//    }

    @Override
    public void updateErxOrders(String preAuthRef, ErxActivityResponse item) {
        try {
            Integer approvalStatus = 2;
            Double netAmount = 0.0;
            Double paymentAmount = 0.0;

            if (item.getNet() != null && item.getPaymentAmount() != null) {
                netAmount = item.getNet();
                paymentAmount = item.getPaymentAmount();

                if (netAmount.equals(paymentAmount)) {
                    approvalStatus = 3;
                } else if (netAmount > paymentAmount && paymentAmount > 0) {
                    approvalStatus = 2;
                } else if (paymentAmount == 0) {
                    approvalStatus = 4;
                }
            } else if (item.getPaymentAmount() == null || item.getPaymentAmount() == 0) {
                approvalStatus = 4;
            }

            String denialCode = item.getDenialCode();
            Integer denialCodeId = null;

            if (denialCode != null && !denialCode.isEmpty()) {
                try {
                    String sql = "SELECT TOP 1 Id FROM DenialCodes WHERE DenialCode = ?";
                    denialCodeId = jdbcTemplate.queryForObject(sql, Integer.class, denialCode);
                } catch (Exception e) {
                    log.warn("DenialCode not found: {}", denialCode);
                }
            }

            String updateSql =
                    "UPDATE PAO " +
                            "SET PAO.ApprovedAmount = ?, " +
                            "    PAO.ApprovalStatus = ?, " +
                            "    PAO.DenialCode = ?, " +
                            "    PAO.UpdatedAt = GETDATE() " +
                            "FROM PreAuthOrders PAO " +
                            "INNER JOIN PreAuthHead PH ON PAO.PreAuthId = PH.Id " +
                            "WHERE PH.PreAuthRef = ? " +
                            "  AND PAO.ServiceCode = ?";

            jdbcTemplate.update(
                    updateSql,
                    paymentAmount,
                    approvalStatus,
                    denialCodeId,
                    preAuthRef,
                    item.getCode()
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to update ErxOrders", e);
        }
    }

    @Override
    public void updateErxStatus(String preAuthRef, Integer status, Integer isProceed) {
        String sql =
                "UPDATE PreAuthHead " +
                        "SET Status = ?, " +
                        "    IsProceed = ?, " +
                        "    UpdatedAt = GETDATE() " +
                        "WHERE PreAuthRef = ?";
        jdbcTemplate.update(sql, status, isProceed, preAuthRef);
    }

    @Override
    public String findPreAuthHeadIdByPreAuthRef(String preAuthRef) {
        String preAuthId = null;
        try {
            String sql = "SELECT TOP 1 Id FROM PreAuthHead WHERE PreAuthRef = ?";
            preAuthId = jdbcTemplate.queryForObject(sql, String.class, preAuthRef);
        } catch (Exception e) {
            log.warn("PreAuthRef not found: {}", preAuthRef);
        }
        return preAuthId;
    }
}
