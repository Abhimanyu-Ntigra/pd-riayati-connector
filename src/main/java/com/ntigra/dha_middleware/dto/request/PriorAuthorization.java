package com.ntigra.dha_middleware.dto.request;

import jakarta.xml.bind.annotation.*;
@XmlRootElement(name="Prior.Authorization")
@XmlAccessorType(XmlAccessType.FIELD)
public class PriorAuthorization {
    @XmlElement(name="Header") public Header header;
    @XmlElement(name="Authorization") public PriorAuthorizationBody authorization;
}