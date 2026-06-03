package com.ntigra.dha_middleware.dto.request;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Header {
    @XmlElement(name="SenderID") public String senderID;
    @XmlElement(name="ReceiverID") public String receiverID;
    @XmlElement(name="TransactionDate") public String transactionDate;
    @XmlElement(name="RecordCount") public String recordCount;
    @XmlElement(name="DispositionFlag") public String dispositionFlag;
}