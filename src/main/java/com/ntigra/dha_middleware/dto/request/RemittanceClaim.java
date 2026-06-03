package com.ntigra.dha_middleware.dto.request;

import jakarta.xml.bind.annotation.*;
import java.util.*;
@XmlAccessorType(XmlAccessType.FIELD)
public class RemittanceClaim {
    @XmlElement(name="ID") public String id;
    @XmlElement(name="IDPayer") public String idPayer;
    @XmlElement(name="ProviderID") public String providerID;
    @XmlElement(name="PaymentReference") public String paymentReference;
    @XmlElement(name="DateSettlement") public String dateSettlement;
    @XmlElement(name="Comments") public String comments;
    @XmlElement(name="Encounter") public Encounter encounter;
    @XmlElement(name="Activity") public List<Activity> activity;
}