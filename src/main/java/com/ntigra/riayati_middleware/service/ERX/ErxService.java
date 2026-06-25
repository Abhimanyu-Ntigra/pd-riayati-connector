package com.ntigra.riayati_middleware.service.ERX;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.request.erx.request.ErxRequest;
import com.ntigra.riayati_middleware.dto.request.erx.request.ErxRequestDto;
import com.ntigra.riayati_middleware.mapper.erx.ErxRequestMapper;
import com.ntigra.riayati_middleware.service.Eligibility.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErxService {

    private final ErxRequestMapper mapper;
    private final RiayatiRestClient client;
    private final TransactionHistoryService historyService;

    public String submit(ErxRequestDto dto) {
        log.info("Submitting ERX request for prescription: {}", dto.getPrescriptionId());

        try {
            // Upload attachment if present
            String attachmentId = null;
            if (dto.getFileContent() != null && dto.getFileContent().length > 0) {
                attachmentId = client.uploadAttachment(
                        dto.getFileContent(),
                        dto.getFileName() != null ? dto.getFileName() : "prescription.pdf"
                );
                log.info("Attachment uploaded with ID: {}", attachmentId);
            }

            ErxRequest request = mapper.map(dto);

            // Add attachment to first activity if present
            if (attachmentId != null && request.getPrescription() != null
                    && request.getPrescription().getActivity() != null
                    && !request.getPrescription().getActivity().isEmpty()) {
                // Add attachment observation to first activity
                // Implementation depends on your observation structure
            }

            String response = client.submitErx(request);

            historyService.log("ERX_REQUEST", request, response);

            return response;

        } catch (Exception e) {
            log.error("ERX submission failed", e);
            return "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    public String getStatus(String prescriptionId) {
        log.info("Getting ERX status for: {}", prescriptionId);

        try {
            return client.getErxStatus(prescriptionId);
        } catch (Exception e) {
            log.error("Failed to get ERX status", e);
            return "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}";
        }
    }
}
