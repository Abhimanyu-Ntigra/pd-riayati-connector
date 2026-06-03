package com.ntigra.dha_middleware.dto.request;

import jakarta.xml.bind.annotation.*;
import java.util.*;
@XmlAccessorType(XmlAccessType.FIELD)
public class SubmissionClaim {
    @XmlElement(name="ID") public String id;
    @XmlElement(name="IDPayer") public String idPayer;
    @XmlElement(name="MemberID") public String memberID;
    @XmlElement(name="PayerID") public String payerID;
    @XmlElement(name="ProviderID") public String providerID;
    @XmlElement(name="EmiratesIDNumber") public String emiratesIDNumber;
    @XmlElement(name="Gross") public String gross;
    @XmlElement(name="PatientShare") public String patientShare;
    @XmlElement(name="Net") public String net;
    @XmlElement(name="Encounter") public Encounter encounter;
    @XmlElement(name="Diagnosis") public List<Diagnosis> diagnosis;
    @XmlElement(name="Activity") public List<Activity> activity;
}