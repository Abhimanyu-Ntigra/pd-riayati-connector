package com.ntigra.riayati_middleware.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "riayati")
public class RiayatiProperties {

    // Environment
    private String env = "uat";

    // Credentials - Read from environment variables
    private String username;     // RIAYATI_USERNAME
    private String password;     // RIAYATI_PASSWORD
    private String senderId;   // RIAYATI_SENDER_ID
    private String receiverId; // RIAYATI_RECEIVER_ID
    private String payerId;    // RIAYATI_PAYER_ID

    // URLs - Hardcoded here, not in properties file
    private String uatBaseUrl = "https://o-tmbapi.riayati.ae:8083";
    private String prodBaseUrl = "https://tmbapi.riayati.ae:8083";
    private String uatAttachmentUrl = "https://o-tmbapi.riayati.ae:8084";
    private String prodAttachmentUrl = "https://tmbapi.riayati.ae:8084";

    public String getBaseUrl() {
        return "prod".equalsIgnoreCase(env) ? prodBaseUrl : uatBaseUrl;
    }

    public String getAttachmentUrl() {
        return "prod".equalsIgnoreCase(env) ? prodAttachmentUrl : uatAttachmentUrl;
    }
}



// in IntelliJ: Run → Edit Configurations → Environment Variables
//RIAYATI_USERNAME=xxx;RIAYATI_PASSWORD=xxx;RIAYATI_SENDER_ID=xxx;RIAYATI_RECEIVER_ID=xxx;RIAYATI_PAYER_ID=xxx