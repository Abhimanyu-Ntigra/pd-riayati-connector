package com.ntigra.dha_middleware.dto.request;

import jakarta.xml.bind.annotation.*;
@XmlAccessorType(XmlAccessType.FIELD)
public class Diagnosis {
    @XmlElement(name="Type") public String type;
    @XmlElement(name="Code") public String code;
}