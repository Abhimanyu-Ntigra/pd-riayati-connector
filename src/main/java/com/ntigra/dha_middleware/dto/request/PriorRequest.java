package com.ntigra.dha_middleware.dto.request;

import jakarta.xml.bind.annotation.*;
@XmlRootElement(name="Prior.Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class PriorRequest {
    @XmlElement(name="Header") public Header header;
    @XmlElement(name="Authorization") public PriorRequestAuthorization authorization;
}