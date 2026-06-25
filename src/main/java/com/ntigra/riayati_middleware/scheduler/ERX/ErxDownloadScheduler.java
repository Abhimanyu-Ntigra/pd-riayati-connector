package com.ntigra.riayati_middleware.scheduler.ERX;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.ApiResponseDto;
import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.service.ERX.ErxServiceOld;
import com.ntigra.riayati_middleware.util.ERX.ErxDownloadThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ErxDownloadScheduler {

    private final ThreadPoolTaskExecutor erxDownloadTaskExecutor;
    private final RiayatiRestClient riayatiClient;
    private final ErxServiceOld erxService;

    public ErxDownloadScheduler(
            @Qualifier("erxDownloadTaskExecutor") ThreadPoolTaskExecutor erxDownloadTaskExecutor,
            RiayatiRestClient riayatiClient,
            ErxServiceOld erxService) {
        this.erxDownloadTaskExecutor = erxDownloadTaskExecutor;
        this.riayatiClient = riayatiClient;
        this.erxService = erxService;
    }

//    @Scheduled(fixedDelay = 10000, initialDelay = 8000)
//    public void processPendingResponses() {
//        log.info("===== ERX DOWNLOAD SCHEDULER STARTED =====");
//
//        try {
//            ApiResponseDto response = riayatiClient.getNewErx();
//
//            if (response == null) {
//                log.warn("Received null response from getNewErx");
//                return;
//            }
//
//            List<TransactionEntityDto> transactions = response.getEntities();
//
//            if (transactions != null && !transactions.isEmpty()) {
//                log.info("Found {} pending ERX responses to download", transactions.size());
//
//                for (TransactionEntityDto transaction : transactions) {
//                    erxDownloadTaskExecutor.submit(
//                            new ErxDownloadThread(transaction, erxService)
//                    );
//                }
//            } else {
//                log.debug("No pending ERX responses found");
//            }
//
//        } catch (Exception e) {
//            log.error("ERX download scheduler failed: ", e);
//        }
//
//        log.info("===== ERX DOWNLOAD SCHEDULER COMPLETED =====");
//    }
}
