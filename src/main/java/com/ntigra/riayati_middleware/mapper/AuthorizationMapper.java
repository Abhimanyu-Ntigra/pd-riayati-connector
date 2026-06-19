package com.ntigra.riayati_middleware.mapper;

import com.ntigra.riayati_middleware.dto.request.AuthorizationRequestDto;
import com.ntigra.riayati_middleware.dto.response.AuthorizationResponseDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AuthorizationMapper {

    /**
     * Map internal Authorization Request to Riayati API format
     */
    public Map<String, Object> toApiRequest(AuthorizationRequestDto request) {
        Map<String, Object> apiRequest = new HashMap<>();
        Map<String, Object> priorRequest = new HashMap<>();

        // Map Header
        Map<String, Object> header = new HashMap<>();
        header.put("SenderID", request.getPriorRequest().getHeader().getSenderId());
        header.put("ReceiverID", request.getPriorRequest().getHeader().getReceiverId());
        header.put("TransactionDate", request.getPriorRequest().getHeader().getTransactionDate());
        header.put("RecordCount", request.getPriorRequest().getHeader().getRecordCount());
        header.put("DispositionFlag", request.getPriorRequest().getHeader().getDispositionFlag());
        header.put("PayerID", request.getPriorRequest().getHeader().getPayerId());
        priorRequest.put("Header", header);

        // Map Authorization
        Map<String, Object> authorization = new HashMap<>();
        authorization.put("Type", request.getPriorRequest().getAuthorization().getType());
        authorization.put("ID", request.getPriorRequest().getAuthorization().getId());
        authorization.put("RequestType", request.getPriorRequest().getAuthorization().getRequestType());
        authorization.put("RequestReferenceNumber", request.getPriorRequest().getAuthorization().getRequestReferenceNumber());
        authorization.put("MemberID", request.getPriorRequest().getAuthorization().getMemberId());
        authorization.put("EmiratesIDNumber", request.getPriorRequest().getAuthorization().getEmiratesIdNumber());
        authorization.put("DateOrdered", request.getPriorRequest().getAuthorization().getDateOrdered());
        authorization.put("Weight", request.getPriorRequest().getAuthorization().getWeight());
        authorization.put("DateOfBirth", request.getPriorRequest().getAuthorization().getDateOfBirth());
        authorization.put("Gender", request.getPriorRequest().getAuthorization().getGender());
        authorization.put("FullName", request.getPriorRequest().getAuthorization().getFullName());
        authorization.put("ContactNumber", request.getPriorRequest().getAuthorization().getContactNumber());
        authorization.put("Email", request.getPriorRequest().getAuthorization().getEmail());

        // Map Encounter
        if (request.getPriorRequest().getAuthorization().getEncounter() != null) {
            Map<String, Object> encounter = new HashMap<>();
            encounter.put("FacilityID", request.getPriorRequest().getAuthorization().getEncounter().getFacilityId());
            encounter.put("Type", request.getPriorRequest().getAuthorization().getEncounter().getType());
            encounter.put("PatientID", request.getPriorRequest().getAuthorization().getEncounter().getPatientId());
            encounter.put("Start", request.getPriorRequest().getAuthorization().getEncounter().getStart());
            encounter.put("End", request.getPriorRequest().getAuthorization().getEncounter().getEnd());
            authorization.put("Encounter", encounter);
        }

        // Map Diagnosis
        if (request.getPriorRequest().getAuthorization().getDiagnosis() != null) {
            List<Map<String, Object>> diagnosisList = request.getPriorRequest().getAuthorization().getDiagnosis()
                    .stream()
                    .map(diag -> {
                        Map<String, Object> diagMap = new HashMap<>();
                        diagMap.put("Type", diag.getType());
                        diagMap.put("Code", diag.getCode());
                        if (diag.getDxInfo() != null) {
                            Map<String, Object> dxInfo = new HashMap<>();
                            dxInfo.put("Type", diag.getDxInfo().getType());
                            dxInfo.put("Code", diag.getDxInfo().getCode());
                            diagMap.put("DxInfo", dxInfo);
                        }
                        return diagMap;
                    })
                    .collect(Collectors.toList());
            authorization.put("Diagnosis", diagnosisList);
        }

        // Map Activities
        if (request.getPriorRequest().getAuthorization().getActivity() != null) {
            List<Map<String, Object>> activities = request.getPriorRequest().getAuthorization().getActivity()
                    .stream()
                    .map(act -> {
                        Map<String, Object> actMap = new HashMap<>();
                        actMap.put("ID", act.getId());
                        actMap.put("ActivityReference", act.getActivityReference());
                        actMap.put("Start", act.getStart());
                        actMap.put("Type", act.getType());
                        actMap.put("Location", act.getLocation());
                        actMap.put("Code", act.getCode());
                        actMap.put("Quantity", act.getQuantity());
                        actMap.put("Unit", act.getUnit());
                        actMap.put("Net", act.getNet());
                        actMap.put("Clinician", act.getClinician());
                        actMap.put("Duration", act.getDuration());

                        if (act.getObservation() != null) {
                            List<Map<String, Object>> observations = act.getObservation()
                                    .stream()
                                    .map(obs -> {
                                        Map<String, Object> obsMap = new HashMap<>();
                                        obsMap.put("Type", obs.getType());
                                        obsMap.put("Code", obs.getCode());
                                        obsMap.put("Value", obs.getValue());
                                        obsMap.put("ValueType", obs.getValueType());
                                        return obsMap;
                                    })
                                    .collect(Collectors.toList());
                            actMap.put("Observation", observations);
                        }
                        return actMap;
                    })
                    .collect(Collectors.toList());
            authorization.put("Activity", activities);
        }

        priorRequest.put("Authorization", authorization);
        apiRequest.put("PriorRequest", priorRequest);

        return apiRequest;
    }

    /**
     * Map internal Authorization Response to Riayati API format
     */
    public Map<String, Object> toApiResponse(AuthorizationResponseDto response) {
        Map<String, Object> apiResponse = new HashMap<>();
        Map<String, Object> priorAuthorization = new HashMap<>();

        // Map Header
        Map<String, Object> header = new HashMap<>();
        header.put("SenderID", response.getPriorAuthorization().getHeader().getSenderId());
        header.put("ReceiverID", response.getPriorAuthorization().getHeader().getReceiverId());
        header.put("TransactionDate", response.getPriorAuthorization().getHeader().getTransactionDate());
        header.put("RecordCount", response.getPriorAuthorization().getHeader().getRecordCount());
        header.put("DispositionFlag", response.getPriorAuthorization().getHeader().getDispositionFlag());
        header.put("PayerID", response.getPriorAuthorization().getHeader().getPayerId());
        priorAuthorization.put("Header", header);

        // Map Authorization
        Map<String, Object> authorization = new HashMap<>();
        authorization.put("Result", response.getPriorAuthorization().getAuthorization().getResult());
        authorization.put("ID", response.getPriorAuthorization().getAuthorization().getId());
        authorization.put("IDPayer", response.getPriorAuthorization().getAuthorization().getIdPayer());
        authorization.put("DenialCode", response.getPriorAuthorization().getAuthorization().getDenialCode());
        authorization.put("Start", response.getPriorAuthorization().getAuthorization().getStart());
        authorization.put("End", response.getPriorAuthorization().getAuthorization().getEnd());
        authorization.put("Limit", response.getPriorAuthorization().getAuthorization().getLimit());
        authorization.put("Comments", response.getPriorAuthorization().getAuthorization().getComments());

        // Map Activities
        if (response.getPriorAuthorization().getAuthorization().getActivity() != null) {
            List<Map<String, Object>> activities = response.getPriorAuthorization().getAuthorization().getActivity()
                    .stream()
                    .map(act -> {
                        Map<String, Object> actMap = new HashMap<>();
                        actMap.put("ID", act.getId());
                        actMap.put("Type", act.getType());
                        actMap.put("Code", act.getCode());
                        actMap.put("Quantity", act.getQuantity());
                        actMap.put("Net", act.getNet());
                        actMap.put("List", act.getList());
                        actMap.put("PatientShare", act.getPatientShare());
                        actMap.put("PaymentAmount", act.getPaymentAmount());
                        actMap.put("DenialCode", act.getDenialCode());
                        actMap.put("Comments", act.getComments());

                        if (act.getObservation() != null) {
                            List<Map<String, Object>> observations = act.getObservation()
                                    .stream()
                                    .map(obs -> {
                                        Map<String, Object> obsMap = new HashMap<>();
                                        obsMap.put("Type", obs.getType());
                                        obsMap.put("Code", obs.getCode());
                                        obsMap.put("Value", obs.getValue());
                                        obsMap.put("ValueType", obs.getValueType());
                                        return obsMap;
                                    })
                                    .collect(Collectors.toList());
                            actMap.put("Observation", observations);
                        }
                        return actMap;
                    })
                    .collect(Collectors.toList());
            authorization.put("Activity", activities);
        }

        priorAuthorization.put("Authorization", authorization);
        apiResponse.put("PriorAuthorization", priorAuthorization);

        return apiResponse;
    }

    /**
     * Map Riayati API response to internal AuthorizationResponseDto
     */
    public AuthorizationResponseDto toAuthorizationResponse(Map<String, Object> apiResponse) {
        if (apiResponse == null || !apiResponse.containsKey("PriorAuthorization")) {
            return null;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> priorAuth = (Map<String, Object>) apiResponse.get("PriorAuthorization");

        AuthorizationResponseDto response = new AuthorizationResponseDto();
        AuthorizationResponseDto.PriorAuthorization priorAuthorization = new AuthorizationResponseDto.PriorAuthorization();

        // Map Header
        if (priorAuth.containsKey("Header")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> headerMap = (Map<String, Object>) priorAuth.get("Header");
            com.healthcare.riayati.dto.common.HeaderDto header = com.healthcare.riayati.dto.common.HeaderDto.builder()
                    .senderId((String) headerMap.get("SenderID"))
                    .receiverId((String) headerMap.get("ReceiverID"))
                    .transactionDate((String) headerMap.get("TransactionDate"))
                    .recordCount((Integer) headerMap.get("RecordCount"))
                    .dispositionFlag((String) headerMap.get("DispositionFlag"))
                    .payerId((String) headerMap.get("PayerID"))
                    .build();
            priorAuthorization.setHeader(header);
        }

        // Map Authorization
        if (priorAuth.containsKey("Authorization")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> authMap = (Map<String, Object>) priorAuth.get("Authorization");
            AuthorizationResponseDto.Authorization authorization = new AuthorizationResponseDto.Authorization();
            authorization.setResult((String) authMap.get("Result"));
            authorization.setId((String) authMap.get("ID"));
            authorization.setIdPayer((String) authMap.get("IDPayer"));
            authorization.setDenialCode((String) authMap.get("DenialCode"));
            authorization.setStart((String) authMap.get("Start"));
            authorization.setEnd((String) authMap.get("End"));
            authorization.setLimit((Double) authMap.get("Limit"));
            authorization.setComments((String) authMap.get("Comments"));

            // Map Activities
            if (authMap.containsKey("Activity")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> activityList = (List<Map<String, Object>>) authMap.get("Activity");
                List<AuthorizationResponseDto.Activity> activities = activityList.stream()
                        .map(actMap -> {
                            AuthorizationResponseDto.Activity activity = new AuthorizationResponseDto.Activity();
                            activity.setId((String) actMap.get("ID"));
                            activity.setType((String) actMap.get("Type"));
                            activity.setCode((String) actMap.get("Code"));
                            activity.setQuantity((Double) actMap.get("Quantity"));
                            activity.setNet((Double) actMap.get("Net"));
                            activity.setList((Double) actMap.get("List"));
                            activity.setPatientShare((Double) actMap.get("PatientShare"));
                            activity.setPaymentAmount((Double) actMap.get("PaymentAmount"));
                            activity.setDenialCode((String) actMap.get("DenialCode"));
                            activity.setComments((String) actMap.get("Comments"));
                            return activity;
                        })
                        .collect(Collectors.toList());
                authorization.setActivity(activities);
            }
            priorAuthorization.setAuthorization(authorization);
        }

        response.setPriorAuthorization(priorAuthorization);
        return response;
    }
}
