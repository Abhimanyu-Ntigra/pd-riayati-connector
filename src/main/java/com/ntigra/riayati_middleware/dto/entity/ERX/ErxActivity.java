package com.ntigra.riayati_middleware.dto.entity.ERX;

import lombok.Data;

@Data
public class ErxActivity {
    private Long id;
    private String prescriptionId;
    private String activityId;
    private String type;                  // "5" for Drug, "10" for scientific code
    private String code;                  // Medication code
    private Double quantity;
    private Double duration;              // Duration in days
    private Integer unitId;               // Medication unit type code
    private Integer refills;
    private String routeOfAdmin;
    private String instructions;
    private String startDate;
    private Integer unitPerFrequency;
    private Integer frequencyValue;
    private String frequencyType;         // Day, Week, Month
    private String visitOrderId;
}
