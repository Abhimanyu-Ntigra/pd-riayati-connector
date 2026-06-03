package com.ntigra.dha_middleware.dto.request;

import jakarta.xml.bind.annotation.*;
@XmlAccessorType(XmlAccessType.FIELD)
public class Encounter {
    @XmlElement(name="FacilityID") public String facilityID;
    @XmlElement(name="Type") public String type;
    @XmlElement(name="PatientID") public String patientID;
    @XmlElement(name="Start") public String start;
    @XmlElement(name="End") public String end;
    @XmlElement(name="StartType") public String startType;
    @XmlElement(name="EndType") public String endType;
}