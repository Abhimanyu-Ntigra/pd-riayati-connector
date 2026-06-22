package com.ntigra.riayati_middleware.service.polling;


import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.polling.GetNewEntity;
import com.ntigra.riayati_middleware.dto.polling.GetNewResponse;
import com.ntigra.riayati_middleware.dto.request.eligibility.response.EligibilityResponse;
import com.ntigra.riayati_middleware.dto.request.eligibility.response.EligibilityResultDto;
import com.ntigra.riayati_middleware.mapper.eligibility.EligibilityResponseMapper;
import com.ntigra.riayati_middleware.service.Eligibility.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiayatiPollingEligibilityService {
    private final RiayatiRestClient client;

    private final EligibilityResponseMapper mapper;

    private final TransactionHistoryService historyService;

    public void pollEligibilityResponses() {

        GetNewResponse response =
                client.getNewEligibility();

        if (response == null ||
                response.getEntities() == null) {

            return;
        }

        for (GetNewEntity entity :
                response.getEntities()) {

            process(entity);
        }
    }

    private void process(
            GetNewEntity entity) {

        try {

            EligibilityResponse response =
                    client.viewEligibility(
                            entity.getId());

            EligibilityResultDto result =
                    mapper.map(response);

            historyService.log(
                    "ELIGIBILITY_RESPONSE",
                    entity,
                    result);

            client.setDownloaded(
                    entity.getId());

        } catch (Exception ex) {

            log.error(
                    "Error processing transaction {}",
                    entity.getId(),
                    ex);
        }
    }
}
