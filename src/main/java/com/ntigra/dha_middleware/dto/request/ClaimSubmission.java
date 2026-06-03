package com.ntigra.dha_middleware.dto.request;

import jakarta.xml.bind.annotation.*;
@XmlRootElement(name="Claim.Submission")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClaimSubmission {
    @XmlElement(name="Header") public Header header;
    @XmlElement(name="Claim") public SubmissionClaim claim;
}