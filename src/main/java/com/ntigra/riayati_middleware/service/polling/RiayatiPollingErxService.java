package com.ntigra.riayati_middleware.service.polling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.polling.GetNewEntity;
import com.ntigra.riayati_middleware.dto.polling.GetNewResponse;
import com.ntigra.riayati_middleware.dto.request.erx.response.ErxActivityResponse;
import com.ntigra.riayati_middleware.dto.request.erx.response.ErxAuthorization;
import com.ntigra.riayati_middleware.dto.request.erx.response.ErxResponse;
import com.ntigra.riayati_middleware.dto.request.erx.response.ErxResultDto;
import com.ntigra.riayati_middleware.mapper.erx.ErxResponseMapper;
import com.ntigra.riayati_middleware.respository.ErxRepository;
import com.ntigra.riayati_middleware.service.Eligibility.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiayatiPollingErxService {

    private final RiayatiRestClient client;
    private final ErxResponseMapper mapper;
    private final TransactionHistoryService historyService;
    private final ErxRepository erxRepository;
    private final ObjectMapper objectMapper;

    public void pollErxResponses() {
        GetNewResponse response = client.getNewErx();

        if (response == null || response.getEntities() == null) {
            return;
        }

        for (GetNewEntity entity : response.getEntities()) {
            process(entity);
        }
    }

    private void process(GetNewEntity entity) {
        try {
            ErxResponse response = client.viewErx(entity.getId());

            if (response == null || response.getAuthorization() == null) {
                log.warn("No ERX data found for entity: {}", entity.getId());
                client.setErxDownloaded(entity.getId());
                return;
            }

            ErxAuthorization auth = response.getAuthorization();

            // Get PreAuthRef
            String preAuthRef = auth.getId();
            ErxResultDto result = mapper.map(response);

            historyService.log("ERX_RESPONSE", entity, result);

            String resultValue = auth.getResult();  // "Yes" or "No"
            List<ErxActivityResponse> activities = auth.getActivity();

            Double totalNetAmount = 0.0;
            Double totalPaymentAmount = 0.0;
            Integer erxStatus = 1;

            // Result = "Yes" (APPROVED)
            if (resultValue != null && resultValue.equalsIgnoreCase("Yes")) {

                if (activities != null && !activities.isEmpty()) {

                    for (ErxActivityResponse act : activities) {

                        if (act.getNet() != null) {
                            totalNetAmount += act.getNet();
                        }
                        if (act.getPaymentAmount() != null) {
                            totalPaymentAmount += act.getPaymentAmount();
                        }

                        erxRepository.updateErxOrders(preAuthRef, act);
                    }

                    if (totalNetAmount.equals(totalPaymentAmount)) {
                        erxStatus = 3;
                    } else if (totalNetAmount > totalPaymentAmount && totalPaymentAmount > 0) {
                        erxStatus = 2;
                    } else if (totalPaymentAmount == 0) {
                        erxStatus = 4;
                    }

                    erxRepository.updateErxStatus(preAuthRef, erxStatus, 2);

                } else {
                    erxRepository.updateErxStatus(preAuthRef, 4, 2);
                }
            }

            //Result = "No" (REJECTED)
            else if (resultValue != null && resultValue.equalsIgnoreCase("No")) {

                erxRepository.updateErxStatus(preAuthRef, 4, 2);

                if (activities != null && !activities.isEmpty()) {
                    for (ErxActivityResponse act : activities) {
                        erxRepository.updateErxOrders(preAuthRef, act);
                    }
                }
            }

            //Find PreAuthHead ID and update bill status
            String preAuthId = erxRepository.findPreAuthHeadIdByPreAuthRef(preAuthRef);
            if (preAuthId != null) {
                updateBillStatus(preAuthId);
            }

            client.setErxDownloaded(entity.getId());

            log.info("Successfully processed ERX response for: {}, Status: {}", preAuthRef, erxStatus);

        } catch (Exception ex) {
            log.error("Error processing ERX transaction {}", entity.getId(), ex);
        }
    }

    public void updateBillStatus(String preAuthId) {
        String url = "https://dev-powerdocservice.ntigra.com/api/v1/PreAuth/FinalizeApproval/" + preAuthId;

        RestTemplate restTemplate = new RestTemplate();

        org.springframework.http.HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>("{}", headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );

        System.out.println("Response: " + response.getBody());
    }
}