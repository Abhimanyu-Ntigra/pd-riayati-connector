package com.ntigra.riayati_middleware.mapper;

import com.ntigra.riayati_middleware.config.RiayatiProperties;
import com.ntigra.riayati_middleware.dto.entity.Dispense.Dispense;
import com.ntigra.riayati_middleware.dto.entity.Dispense.DispenseActivity;
import com.ntigra.riayati_middleware.dto.entity.Dispense.DispenseSubmission;
import com.ntigra.riayati_middleware.dto.entity.Encounter;
import com.ntigra.riayati_middleware.dto.entity.Header;
import com.ntigra.riayati_middleware.dto.request.DispenseRequestDto;
import com.ntigra.riayati_middleware.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DispenseMapper {

    private final RiayatiProperties properties;
    private final DateUtil dateUtil;

    public DispenseSubmission toDispenseSubmission(DispenseRequestDto dto) {
        String currentDate = dateUtil.getCurrentDateTime();

        // ==================== BUILD HEADER (REUSE) ====================
        Header header = Header.builder()
                .senderId(dto.getSenderId() != null ? dto.getSenderId() : properties.getSenderId())
                .receiverId(dto.getReceiverId() != null ? dto.getReceiverId() : properties.getReceiverId())
                .transactionDate(currentDate)
                .recordCount(1)
                .dispositionFlag("PRODUCTION")
                .payerId(dto.getPayerId() != null ? dto.getPayerId() : properties.getPayerId())
                .build();

        // ==================== BUILD ENCOUNTER (REUSE) ====================
        Encounter encounter = Encounter.builder()
                .facilityId(dto.getFacilityId())
                .type(dto.getEncounterType() != null ? dto.getEncounterType() : 1)
                .build();

        // ==================== BUILD DISPENSE ACTIVITY ====================
        DispenseActivity activity = null;
        if (dto.getActivity() != null) {
            activity = DispenseActivity.builder()
                    .id(dto.getActivity().getId())
                    .type(dto.getActivity().getType() != null ? dto.getActivity().getType() : "5")
                    .code(dto.getActivity().getCode())
                    .activityReference(dto.getActivity().getActivityReference())
                    .quantity(dto.getActivity().getQuantity())
                    .dispensedQuantity(dto.getActivity().getDispensedQuantity())
                    .location(dto.getActivity().getLocation() != null ? dto.getActivity().getLocation() : "2")
                    .performerName(dto.getActivity().getPerformerName())
                    .authorizationId(dto.getActivity().getAuthorizationId())
                    .comments(dto.getActivity().getComments())
                    .build();
        }

        // ==================== BUILD DISPENSE ====================
        Dispense dispense = Dispense.builder()
                .id(dto.getDispenseId())
                .type("Dispense")
                .referenceNumber(dto.getReferenceNumber())
                .dispenseDate(dto.getDispenseDate() != null ? dto.getDispenseDate() : currentDate)
                .priorRequestId(dto.getDispenseId())  // Using dispenseId as priorRequestId
                .contactNumber(dto.getContactNumber())
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .activity(activity)
                .build();

        // ==================== BUILD SUBMISSION ====================
        return DispenseSubmission.builder()
                .header(header)
                .dispense(dispense)
                .build();
    }
}