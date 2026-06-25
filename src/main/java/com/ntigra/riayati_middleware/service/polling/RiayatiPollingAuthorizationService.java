package com.ntigra.riayati_middleware.service.polling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.polling.GetNewEntity;
import com.ntigra.riayati_middleware.dto.polling.GetNewResponse;
import com.ntigra.riayati_middleware.dto.request.Authorization.response.*;
import com.ntigra.riayati_middleware.mapper.authorization.AuthorizationResponseMapper;
import com.ntigra.riayati_middleware.respository.AuthorizationRepository;
import com.ntigra.riayati_middleware.service.Eligibility.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiayatiPollingAuthorizationService {

    private final RiayatiRestClient client;
    private final AuthorizationResponseMapper mapper;
    private final TransactionHistoryService historyService;
    private final AuthorizationRepository authorizationRepository;
    private final ObjectMapper objectMapper;

    public void pollAuthorizationResponses() {
        GetNewResponse response = client.getNewAuthorization();

        if (response == null || response.getEntities() == null) {
            return;
        }

        for (GetNewEntity entity : response.getEntities()) {
            process(entity);
        }
    }

    private void process(GetNewEntity entity) {
        try {
            AuthorizationResponse response = client.viewAuthorization(entity.getId());

            if (response == null || response.getPriorAuthorization() == null) {
                log.warn("No authorization data found for entity: {}", entity.getId());
                client.setAuthorizationDownloaded(entity.getId());
                return;
            }

            PriorAuthorization priorAuth = response.getPriorAuthorization();
            Authorization auth = priorAuth.getAuthorization();

            if (auth == null) {
                log.warn("No authorization details found for entity: {}", entity.getId());
                client.setAuthorizationDownloaded(entity.getId());
                return;
            }

            String preAuthRef = auth.getId();  // Authorization ID from response

            AuthorizationResultDto result = mapper.map(response);

            historyService.log(
                    "AUTHORIZATION_RESPONSE",
                    entity,
                    result
            );

            String resultValue = auth.getResult();  // "Yes" or "No"
            List<Activity> activities = auth.getActivity();

            Double totalNetAmount = 0.0;
            Double totalPaymentAmount = 0.0;
            Integer authStatus = 1;

            if (resultValue != null && resultValue.equalsIgnoreCase("Yes")) {

                if (activities != null && !activities.isEmpty()) {

                    for (Activity act : activities) {

                        if (act.getNet() != null) {
                            totalNetAmount += act.getNet();
                        }
                        if (act.getPaymentAmount() != null) {
                            totalPaymentAmount += act.getPaymentAmount();
                        }

                        authorizationRepository.updatePreAuthOrder(preAuthRef, act);
                    }

                    if (totalNetAmount.equals(totalPaymentAmount)) {
                        authStatus = 3;
                    } else if (totalNetAmount > totalPaymentAmount && totalPaymentAmount > 0) {
                        authStatus = 2;
                    } else if (totalPaymentAmount == 0) {
                        authStatus = 4;
                    }

                    authorizationRepository.updatePreAuthStatus(preAuthRef, authStatus, 2);

                } else {
                    authorizationRepository.updatePreAuthStatus(preAuthRef, 4, 2);
                }

            }

            else if (resultValue != null && resultValue.equalsIgnoreCase("No")) {

                authorizationRepository.updatePreAuthStatus(preAuthRef, 4, 2);

                if (activities != null && !activities.isEmpty()) {
                    for (Activity act : activities) {
                        authorizationRepository.updatePreAuthOrder(preAuthRef, act);
                    }
                }
            }

            String preAuthId = authorizationRepository.findPreAuthHeadIdByPreAuthRef(preAuthRef);

            if (preAuthId != null) {
                updateBillStatus(preAuthId);
            }

            // save in the DB
            client.setAuthorizationDownloaded(entity.getId());

            log.info("Successfully processed authorization response for: {}, Status: {}", preAuthRef, authStatus);

        } catch (Exception ex) {
            log.error("Error processing authorization transaction {}", entity.getId(), ex);
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
