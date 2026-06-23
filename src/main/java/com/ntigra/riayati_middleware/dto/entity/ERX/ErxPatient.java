package com.ntigra.riayati_middleware.dto.entity.ERX;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErxPatient {
    private String memberId;
    private String nationalIdNumber;
    private String dateOfBirth;
    private String fullName;
    private String gender;
    private String contactNumber;
    private Double weight;          // Constant: 50.0
    private String email;
}
