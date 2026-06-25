package com.ntigra.riayati_middleware.mapper.dispense;

import com.ntigra.riayati_middleware.config.RiayatiProperties;
import com.ntigra.riayati_middleware.dto.request.dispense.request.DispenseActivity;
import com.ntigra.riayati_middleware.dto.request.dispense.request.DispenseData;
import com.ntigra.riayati_middleware.dto.request.dispense.request.DispenseRequest;
import com.ntigra.riayati_middleware.dto.request.dispense.request.DispenseRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class DispenseRequestMapper {

    private final RiayatiProperties properties;
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public DispenseRequest map(DispenseRequestDto dto) {
        DispenseData dispense = buildDispense(dto);

        DispenseRequest request = new DispenseRequest();
        request.setDispense(dispense);

        return request;
    }

    private DispenseData buildDispense(DispenseRequestDto dto) {
        DispenseData dispense = new DispenseData();
        dispense.setId(dto.getDispenseId());
        dispense.setType("Dispense");
        dispense.setReferenceNumber(dto.getReferenceNumber());
        dispense.setDispenseDate(dto.getDispenseDate() != null ? dto.getDispenseDate() : now());
        dispense.setPriorRequestId(dto.getPriorRequestId());
        dispense.setContactNumber(dto.getContactNumber());
        dispense.setEmail(dto.getEmail());
        dispense.setFullName(dto.getFullName());

        // Activity
        if (dto.getActivity() != null) {
            DispenseActivity activity = new DispenseActivity();
            activity.setId(dto.getActivity().getId());
            activity.setType(dto.getActivity().getType() != null ? dto.getActivity().getType() : "5");
            activity.setCode(dto.getActivity().getCode());
            activity.setActivityReference(dto.getActivity().getActivityReference());
            activity.setQuantity(dto.getActivity().getQuantity());
            activity.setDispensedQuantity(dto.getActivity().getDispensedQuantity());
            activity.setLocation(dto.getActivity().getLocation() != null ? dto.getActivity().getLocation() : "2");
            activity.setPerformerName(dto.getActivity().getPerformerName() != null ? dto.getActivity().getPerformerName() : "Pharmacy Name");
            activity.setAuthorizationId(dto.getActivity().getAuthorizationId());
            activity.setComments(dto.getActivity().getComments() != null ? dto.getActivity().getComments() : "Dispensed");
            dispense.setActivity(activity);
        }

        return dispense;
    }

    private String now() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }
}
