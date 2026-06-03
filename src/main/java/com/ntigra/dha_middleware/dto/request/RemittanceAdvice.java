package com.ntigra.dha_middleware.dto.request;

import jakarta.xml.bind.annotation.*;
@XmlRootElement(name="Remittance.Advice")
@XmlAccessorType(XmlAccessType.FIELD)
public class RemittanceAdvice {
    @XmlElement(name="Header") public Header header;
    @XmlElement(name="Claim") public RemittanceClaim claim;
}