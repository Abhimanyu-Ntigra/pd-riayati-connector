package com.ntigra.dha_middleware.dto.request;

import jakarta.xml.bind.annotation.*;
import java.util.*;
@XmlAccessorType(XmlAccessType.FIELD)
public class Activity {
    @XmlElement(name="ID") public String id;
    @XmlElement(name="Start") public String start;
    @XmlElement(name="Type") public String type;
    @XmlElement(name="Code") public String code;
    @XmlElement(name="Quantity") public String quantity;
    @XmlElement(name="Net") public String net;
    @XmlElement(name="Clinician") public String clinician;
    @XmlElement(name="Observation") public List<Observation> observations;
    @XmlElement(name="List") public String list;
    @XmlElement(name="PriorAuthorizationID") public String priorAuthorizationID;
    @XmlElement(name="Gross") public String gross;
    @XmlElement(name="PatientShare") public String patientShare;
    @XmlElement(name="PaymentAmount") public String paymentAmount;
    @XmlElement(name="DenialCode") public String denialCode;
}