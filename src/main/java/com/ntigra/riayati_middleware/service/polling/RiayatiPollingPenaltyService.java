package com.ntigra.riayati_middleware.service.polling;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.polling.GetNewEntity;
import com.ntigra.riayati_middleware.dto.polling.GetNewResponse;
import com.ntigra.riayati_middleware.dto.request.penality.response.PenaltyResponse;
import com.ntigra.riayati_middleware.dto.request.penality.response.PenaltyResultDto;
import com.ntigra.riayati_middleware.mapper.penality.PenaltyResponseMapper;
import com.ntigra.riayati_middleware.service.Eligibility.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiayatiPollingPenaltyService {

    private final RiayatiRestClient client;
    private final PenaltyResponseMapper mapper;
    private final TransactionHistoryService historyService;

    public void pollPenaltyResponses() {
        GetNewResponse response = client.getNewPenalty();

        if (response == null || response.getEntities() == null) {
            return;
        }

        for (GetNewEntity entity : response.getEntities()) {
            process(entity);
        }
    }

    private void process(GetNewEntity entity) {
        try {
            PenaltyResponse response = client.viewPenalty(entity.getId());
            PenaltyResultDto result = mapper.map(response);

            historyService.log(
                    "PENALTY_RESPONSE",
                    entity,
                    result
            );

            client.setPenaltyDownloaded(entity.getId());

        } catch (Exception ex) {
            log.error("Error processing penalty transaction {}", entity.getId(), ex);
        }
    }
}