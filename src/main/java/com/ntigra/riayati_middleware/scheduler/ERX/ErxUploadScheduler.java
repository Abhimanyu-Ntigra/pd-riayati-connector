package com.ntigra.riayati_middleware.scheduler.ERX;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.polling.GetNewEntity;
import com.ntigra.riayati_middleware.dto.polling.GetNewResponse;
import com.ntigra.riayati_middleware.dto.request.ErxRequestDto;
import com.ntigra.riayati_middleware.respository.ErxRepository;
import com.ntigra.riayati_middleware.service.ERX.ErxServiceOld;
import com.ntigra.riayati_middleware.util.ERX.ErxUploadThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ErxUploadScheduler {

    private final ThreadPoolTaskExecutor erxUploadTaskExecutor;
    private final RiayatiRestClient riayatiClient;
    private final ErxRepository erxRepository;
    private final ErxServiceOld erxService;

    public ErxUploadScheduler(
            @Qualifier("erxUploadTaskExecutor") ThreadPoolTaskExecutor erxUploadTaskExecutor,
            RiayatiRestClient riayatiClient,
            ErxRepository erxRepository,
            ErxServiceOld erxService) {
        this.erxUploadTaskExecutor = erxUploadTaskExecutor;
        this.riayatiClient = riayatiClient;
        this.erxRepository = erxRepository;
        this.erxService = erxService;
    }

    @Scheduled(fixedDelay = 8000, initialDelay = 4000)
    public void processPendingErx() {
        log.info("===== ERX UPLOAD SCHEDULER STARTED =====");

        try {

            GetNewResponse getNewResponse = riayatiClient.getNewErx();

            if (getNewResponse != null && getNewResponse.getEntities() != null) {
                log.info("Found {} transactions in GetNew response", getNewResponse.getEntities().size());
                // Log the transaction IDs for reference
                for (GetNewEntity entity : getNewResponse.getEntities()) {
                    log.debug("GetNew transaction: ID={}, SenderID={}, TransactionDate={}",
                            entity.getId(), entity.getSenderId(), entity.getTransactionDate());
                }
            }

            List<ErxRequestDto> pendingRequests = erxRepository.fetchPendingErxRequests();

            if (pendingRequests != null && !pendingRequests.isEmpty()) {
                log.info("Found {} pending ERX requests to upload", pendingRequests.size());

                for (ErxRequestDto request : pendingRequests) {
                    erxUploadTaskExecutor.submit(
                            new ErxUploadThread(request, erxService)
                    );
                }
            } else {
                log.debug("No pending ERX requests found");
            }

        } catch (Exception e) {
            log.error("ERX upload scheduler failed: ", e);
        }

        log.info("===== ERX UPLOAD SCHEDULER COMPLETED =====");
    }
}
