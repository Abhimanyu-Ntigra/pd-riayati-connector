package com.ntigra.riayati_middleware.dto.entity.ERX;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErxResponseData {

    private String prescriptionId;
    private String result;
    private String idPayer;
    private String denialCode;
    private String startDate;
    private String endDate;
    private Double limit;
}
