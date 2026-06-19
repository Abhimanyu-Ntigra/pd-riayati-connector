package com.ntigra.riayati_middleware.service;

import com.ntigra.riayati_middleware.dto.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RiayatiResponseProcessor {

    /**
     * Validate upload response
     */
    public void validateUploadResponse(ApiResponseDto response) {
        if (response == null) {
            throw new RuntimeException("Null response from Riayati");
        }

        if (response.getSuccess() == null || !response.getSuccess()) {
            String errorMsg = String.format(
                    "Riayati upload failed. StatusCode: %s, Message: %s",
                    response.getStatusCode(),
                    response.getMessage()
            );
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        log.info("Riayati upload successful. EntityID: {}", response.getEntityId());
    }

    public void validateDownloadResponse(ApiResponseDto response) {
        if (response == null) {
            throw new RuntimeException("Null response from Riayati");
        }

        if (response.getSuccess() == null || !response.getSuccess()) {
            String errorMsg = String.format(
                    "Riayati download failed. StatusCode: %s, Message: %s",
                    response.getStatusCode(),
                    response.getMessage()
            );
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }
}
