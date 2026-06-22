package com.ntigra.riayati_middleware.dto.entity.Dispense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispenseResponseData {

    private String dispenseId;
    private String result;
    private String idPayer;
    private String denialCode;
    private String startDate;
    private String endDate;
    private Double limit;
}
