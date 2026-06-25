package com.ntigra.riayati_middleware.service.polling;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.polling.GetNewEntity;
import com.ntigra.riayati_middleware.dto.polling.GetNewResponse;
import com.ntigra.riayati_middleware.dto.request.dispense.response.DispenseResponse;
import com.ntigra.riayati_middleware.dto.request.dispense.response.DispenseResultDto;
import com.ntigra.riayati_middleware.mapper.dispense.DispenseResponseMapper;
import com.ntigra.riayati_middleware.service.Eligibility.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiayatiPollingDispenseService {

    private final RiayatiRestClient client;
    private final DispenseResponseMapper mapper;
    private final TransactionHistoryService historyService;

    public void pollDispenseResponses() {
        GetNewResponse response = client.getNewDispense();

        if (response == null || response.getEntities() == null) {
            return;
        }

        for (GetNewEntity entity : response.getEntities()) {
            process(entity);
        }
    }

    private void process(GetNewEntity entity) {
        try {
            DispenseResponse response = client.viewDispense(entity.getId());
            DispenseResultDto result = mapper.map(response);

            historyService.log(
                    "DISPENSE_RESPONSE",
                    entity,
                    result
            );

            client.setDispenseDownloaded(entity.getId());

        } catch (Exception ex) {
            log.error("Error processing dispense transaction {}", entity.getId(), ex);
        }
    }
}
