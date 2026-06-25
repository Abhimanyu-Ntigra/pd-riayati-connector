package com.ntigra.riayati_middleware.dto.request.erx.request;

import lombok.Data;

@Data
public class ErxPatient {
    private String memberId;
    private String nationalIdNumber;
    private String dateOfBirth;
    private String fullName;
    private String gender;
    private String contactNumber;
    private Double weight;
    private String email;
}
