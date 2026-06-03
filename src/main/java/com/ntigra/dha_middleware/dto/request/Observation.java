package com.ntigra.dha_middleware.dto.request;

import jakarta.xml.bind.annotation.*;
@XmlAccessorType(XmlAccessType.FIELD)
public class Observation {
    @XmlElement(name="Type") public String type;
    @XmlElement(name="Code") public String code;
    @XmlElement(name="Value") public String value;
    @XmlElement(name="ValueType") public String valueType;
}