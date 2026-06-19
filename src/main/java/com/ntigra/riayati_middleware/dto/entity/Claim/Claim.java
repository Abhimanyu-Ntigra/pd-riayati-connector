package com.ntigra.riayati_middleware.dto.entity.Claim;

import com.ntigra.riayati_middleware.dto.entity.Diagnosis;
import com.ntigra.riayati_middleware.dto.entity.Encounter;
import lombok.Builder;
import lombok.Data;

import java.util.*;

@Data
@Builder
public class Claim {
    private String id;
    private String type;
    private String memberId;
    private String providerId;
    private String nationalIdNumber;
    private String dateOfBirth;
    private String gender;
    private Double weight;
    private Double gross;
    private Double net;
    private Double patientShare;
    private Encounter encounter;
    private List<Diagnosis> diagnosis;
    private List<Activity> activity;
    private Resubmission resubmission;
}