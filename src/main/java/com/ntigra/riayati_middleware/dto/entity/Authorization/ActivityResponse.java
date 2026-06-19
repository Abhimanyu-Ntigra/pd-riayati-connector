package com.ntigra.riayati_middleware.dto.entity.Authorization;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ActivityResponse {
    private String id;
    private String type;
    private String code;
    private Double quantity;
    private Double net;
    private Double paymentAmount;
    private String denialCode;
}