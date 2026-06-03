package com.ntigra.dha_middleware.dto.request;

import jakarta.xml.bind.annotation.*;
@XmlAccessorType(XmlAccessType.FIELD)
public class PriorAuthorizationActivity {
    @XmlElement(name="ID") public String id;
    @XmlElement(name="Type") public String type;
    @XmlElement(name="Code") public String code;
    @XmlElement(name="Quantity") public String quantity;
    @XmlElement(name="Net") public String net;
    @XmlElement(name="PatientShare") public String patientShare;
    @XmlElement(name="PaymentAmount") public String paymentAmount;
    @XmlElement(name="DenialCode") public String denialCode;
}