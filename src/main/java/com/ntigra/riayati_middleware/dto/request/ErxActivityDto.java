package com.ntigra.riayati_middleware.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class ErxActivityDto {
    private String id;                  // ItemSequenceNo from PreAuthOrders
    private String activityReference;
    private String type;                // Constant: "5"
    private String code;                // ServiceCode from PreAuthOrders
    private Double quantity;            // Quantity from PreAuthOrders
    private String start;
    private String dispensedQuantity;
    private String location;
    private String performerName;
    private String authorizationId;
    private String comments;
    private List<ObservationDto> observations;
}
