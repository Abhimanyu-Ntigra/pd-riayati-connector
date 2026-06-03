package com.ntigra.dha_middleware.dto.request;

import jakarta.xml.bind.annotation.*;
import java.util.*;
@XmlAccessorType(XmlAccessType.FIELD)
public class PriorAuthorizationBody {
    @XmlElement(name="Result") public String result;
    @XmlElement(name="ID") public String id;
    @XmlElement(name="IDPayer") public String idPayer;
    @XmlElement(name="Start") public String start;
    @XmlElement(name="End") public String end;
    @XmlElement(name="Comments") public String comments;
    @XmlElement(name="Activity") public List<PriorAuthorizationActivity> activity;
}