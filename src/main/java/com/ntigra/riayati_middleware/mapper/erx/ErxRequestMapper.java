package com.ntigra.riayati_middleware.mapper.erx;

import com.ntigra.riayati_middleware.config.RiayatiProperties;
import com.ntigra.riayati_middleware.dto.request.Authorization.request.DiagnosisDto;
import com.ntigra.riayati_middleware.dto.request.Authorization.request.ObservationDto;
import com.ntigra.riayati_middleware.dto.request.erx.request.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ErxRequestMapper {

    private final RiayatiProperties properties;
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ErxRequest map(ErxRequestDto dto) {
        ErxPrescription prescription = buildPrescription(dto);

        ErxRequest request = new ErxRequest();
        request.setPrescription(prescription);

        return request;
    }

    private ErxPrescription buildPrescription(ErxRequestDto dto) {
        ErxPrescription prescription = new ErxPrescription();
        prescription.setId(dto.getPrescriptionId());
        prescription.setType(dto.getTransactionType() != null ? dto.getTransactionType() : "eRxRequest");
        prescription.setClinician(dto.getClinician() != null ? dto.getClinician() : "CONSTANT_CLINICIAN");

        // Patient
        ErxPatient patient = new ErxPatient();
        patient.setMemberId(dto.getMemberId());
        patient.setNationalIdNumber(dto.getEmiratesId());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setFullName(dto.getFullName());
        patient.setGender(dto.getGender());
        patient.setContactNumber(dto.getContactNumber());
        patient.setWeight(dto.getWeight() != null ? dto.getWeight() : 50.0);
        patient.setEmail(dto.getEmail());
        prescription.setPatient(patient);

        // Encounter
        ErxEncounter encounter = new ErxEncounter();
        encounter.setFacilityId(dto.getFacilityId());
        encounter.setType(dto.getEncounterType() != null ? dto.getEncounterType() : 1);
        prescription.setEncounter(encounter);

        // Diagnoses
        if (dto.getDiagnoses() != null && !dto.getDiagnoses().isEmpty()) {
            prescription.setDiagnosis(dto.getDiagnoses().stream()
                    .map(this::mapDiagnosis)
                    .collect(Collectors.toList()));
        } else {
            prescription.setDiagnosis(Collections.emptyList());
        }

        // Activities
        if (dto.getActivities() != null && !dto.getActivities().isEmpty()) {
            prescription.setActivity(dto.getActivities().stream()
                    .map(this::mapActivity)
                    .collect(Collectors.toList()));
        } else {
            prescription.setActivity(Collections.emptyList());
        }

        return prescription;
    }

    private ErxDiagnosis mapDiagnosis(DiagnosisDto dto) {
        ErxDiagnosis diagnosis = new ErxDiagnosis();
        diagnosis.setType(dto.getType() != null ? dto.getType() : "Principal");
        diagnosis.setCode(dto.getCode());
        return diagnosis;
    }

    private ErxActivity mapActivity(ErxActivityDto dto) {
        ErxActivity activity = new ErxActivity();
        activity.setId(dto.getId());
        activity.setType(dto.getType() != null ? dto.getType() : "5");
        activity.setCode(dto.getCode());
        activity.setQuantity(dto.getQuantity());
        activity.setDuration(dto.getDuration() != null ? dto.getDuration() : 0.0);
        activity.setUnitId(dto.getUnitId() != null ? dto.getUnitId() : 1);
        activity.setRefills(dto.getRefills() != null ? dto.getRefills() : 0);
        activity.setRouteOfAdmin(dto.getRouteOfAdmin() != null ? dto.getRouteOfAdmin() : "001");
        activity.setInstructions(dto.getInstructions() != null ? dto.getInstructions() : "Take as prescribed");
        activity.setStart(dto.getStart() != null ? dto.getStart() : now());

        // Frequency
        if (dto.getFrequency() != null) {
            ErxFrequency frequency = new ErxFrequency();
            frequency.setUnitPerFrequency(dto.getFrequency().getUnitPerFrequency());
            frequency.setFrequencyValue(dto.getFrequency().getFrequencyValue());
            frequency.setFrequencyType(dto.getFrequency().getFrequencyType() != null ? dto.getFrequency().getFrequencyType() : "Day");
            activity.setFrequency(frequency);
        }

        // Observations
        if (dto.getObservations() != null && !dto.getObservations().isEmpty()) {
            activity.setObservation(dto.getObservations().stream()
                    .map(this::mapObservation)
                    .collect(Collectors.toList()));
        } else {
            activity.setObservation(Collections.emptyList());
        }

        return activity;
    }

    private Observation mapObservation(ObservationDto dto) {
        Observation observation = new Observation();
        observation.setType(dto.getType());
        observation.setCode(dto.getCode());
        observation.setValue(dto.getValue());
        observation.setValueType(dto.getValueType());
        return observation;
    }

    private String now() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }
}