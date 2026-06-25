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
                        "    PH.Id AS erxId, " +
                        "    PH.PreAuthRef AS prescriptionId, " +
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
                        "    CM.LicenseNo AS facilityLicense, " +
                        "    " +
                        "    PV.EncounterType AS encounterType, " +
                        "    PV.EncounterStartTime AS encounterStart, " +
                        "    PV.EncounterEndTime AS encounterEnd " +
                        "FROM PreAuthHead PH " +
                        "INNER JOIN PatientVisits PV ON PV.Id = PH.PatientVisitId " +
                        "INNER JOIN Patients P ON P.Id = PV.PatientId " +
                        "LEFT JOIN ClientMaster CM ON CM.Id = PH.SenderId " +
                        "LEFT JOIN PatientInsuranceDetails PID ON PID.Id = PV.PatientInsuranceId " +
                        "WHERE PH.Status = 1 " +
                        "  AND ISNULL(PH.IsDeleted, 0) = 0 " +
                        "ORDER BY PH.CreatedAt ASC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ErxRequestDto dto = new ErxRequestDto();
            dto.setId(rs.getLong("erxId"));
            dto.setPrescriptionId(rs.getString("prescriptionId"));
            dto.setRetryCount(rs.getInt("retryCount"));

            dto.setMemberId(rs.getString("memberId"));
            dto.setEmiratesId(rs.getString("emiratesId"));
            dto.setFullName(rs.getString("fullName"));
            dto.setGender(rs.getString("gender") != null ? rs.getString("gender") : "Male");
            dto.setDateOfBirth(rs.getString("dateOfBirth"));
            dto.setContactNumber(rs.getString("contactNumber"));
            dto.setEmail(rs.getString("email"));

            dto.setSenderId(rs.getString("senderLicense"));
            dto.setFacilityId(rs.getString("facilityLicense"));

            Integer encounterType = rs.getInt("encounterType");
            dto.setEncounterType(rs.wasNull() ? 1 : encounterType);
            dto.setEncounterStart(rs.getString("encounterStart"));
            dto.setEncounterEnd(rs.getString("encounterEnd"));

            // Constants
            dto.setReceiverId("PAYER_LICENSE_ID");
            dto.setPayerId("PAYER_LICENSE_ID");
            dto.setDispositionFlag("PRODUCTION");
            dto.setRecordCount(1);
            dto.setClinician("CONSTANT_CLINICIAN");
            dto.setTransactionType("eRxRequest");
            dto.setWeight(50.0);

            // Fetch diagnoses (reuse from PreAuthDiagnosis)
            dto.setDiagnoses(fetchDiagnoses(dto.getPrescriptionId()));

            // Fetch activities (reuse from PreAuthOrders)
            dto.setActivities(fetchActivities(dto.getPrescriptionId()));

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
        String sql = "UPDATE PreAuthHead SET Status = 2, EntityId = ?, ReferenceNumber = ?, SentAt = GETDATE() WHERE Id = ?";
        jdbcTemplate.update(sql, entityId, referenceNumber, id);
    }

    @Override
    public void updateErxAsSentWithResponse(Long id, String entityId, String referenceNumber, String responseJson) {
        String sql = "UPDATE PreAuthHead SET Status = 2, EntityId = ?, ReferenceNumber = ?, SentAt = GETDATE(), ResponseData = ? WHERE Id = ?";
        jdbcTemplate.update(sql, entityId, referenceNumber, responseJson, id);
    }

    @Override
    public void updateErxAsFailed(Long id, String errorMessage) {
        String sql = "UPDATE PreAuthHead SET Status = 3, ErrorMessage = ? WHERE Id = ?";
        jdbcTemplate.update(sql, errorMessage, id);
    }

    @Override
    public void updateRetryCount(Long id) {
        String sql = "UPDATE PreAuthHead SET RetryCount = ISNULL(RetryCount, 0) + 1 WHERE Id = ?";
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
