package com.ntigra.riayati_middleware.dto.request;

import com.ntigra.riayati_middleware.dto.request.Authorization.request.ObservationDto;
import lombok.Data;
import java.util.List;

@Data
public class ErxActivityDto {
    private String id;                  // ItemSequenceNo from PreAuthOrders
    private String activityReference;
    private String type;                // Constant: "5"
    private String code;                // ServiceCode from PreAuthOrders
    private Double quantity;            // Quantity from PreAuthOrders
    private Double duration;
    private Integer unitId;
    private Integer refills;
    private String routeOfAdmin;
    private String instructions;
    private String start;
    private String dispensedQuantity;
    private String location;
    private String performerName;
    private String authorizationId;
    private String comments;
    private List<ObservationDto> observations;
    private ErxFrequencyDto frequency;
}
