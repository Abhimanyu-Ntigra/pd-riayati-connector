package com.ntigra.dha_middleware.dto.request;

import jakarta.xml.bind.annotation.*;
import java.util.*;
@XmlAccessorType(XmlAccessType.FIELD)
public class PriorRequestAuthorization {
    @XmlElement(name="Type") public String type;
    @XmlElement(name="ID") public String id;
    @XmlElement(name="IDPayer") public String idPayer;
    @XmlElement(name="MemberID") public String memberID;
    @XmlElement(name="PayerID") public String payerID;
    @XmlElement(name="EmiratesIDNumber") public String emiratesIDNumber;
    @XmlElement(name="DateOrdered") public String dateOrdered;
    @XmlElement(name="Encounter") public Encounter encounter;
    @XmlElement(name="Diagnosis") public List<Diagnosis> diagnosis;
    @XmlElement(name="Activity") public List<Activity> activity;
}