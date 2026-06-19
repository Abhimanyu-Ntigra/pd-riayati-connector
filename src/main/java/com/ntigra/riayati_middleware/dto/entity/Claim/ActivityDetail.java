package com.ntigra.riayati_middleware.dto.entity.Claim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDetail {

    private String activityId;
    private String code;
    private Double paymentAmount;
    private String denialCode;

}
