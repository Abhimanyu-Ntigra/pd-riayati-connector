package com.ntigra.riayati_middleware.scheduler.Penalty;

import com.ntigra.riayati_middleware.dto.request.PenaltyRequestDto;
import com.ntigra.riayati_middleware.respository.PenaltyRepository;
import com.ntigra.riayati_middleware.service.Penalty.PenaltyServiceOld;
import com.ntigra.riayati_middleware.util.Penalty.PenaltyUploadThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class PenaltyUploadScheduler {

    private final ThreadPoolTaskExecutor penaltyUploadTaskExecutor;
    private final PenaltyRepository penaltyRepository;
    private final PenaltyServiceOld penaltyService;

    public PenaltyUploadScheduler(
            @Qualifier("penaltyUploadTaskExecutor") ThreadPoolTaskExecutor penaltyUploadTaskExecutor,
            PenaltyRepository penaltyRepository,
            PenaltyServiceOld penaltyService) {
        this.penaltyUploadTaskExecutor = penaltyUploadTaskExecutor;
        this.penaltyRepository = penaltyRepository;
        this.penaltyService = penaltyService;
    }

    @Scheduled(fixedDelay = 8000, initialDelay = 6000)
    public void processPendingPenalties() {
        log.info("===== PENALTY UPLOAD SCHEDULER STARTED =====");

        try {
            List<PenaltyRequestDto> pendingPenalties = penaltyRepository.fetchPendingPenalties();

            if (pendingPenalties != null && !pendingPenalties.isEmpty()) {
                log.info("Found {} pending penalties to upload", pendingPenalties.size());

                for (PenaltyRequestDto request : pendingPenalties) {
                    penaltyUploadTaskExecutor.submit(
                            new PenaltyUploadThread(request, penaltyService)
                    );
                }
            } else {
                log.debug("No pending penalties found");
            }

        } catch (Exception e) {
            log.error("Penalty upload scheduler failed: ", e);
        }

        log.info("===== PENALTY UPLOAD SCHEDULER COMPLETED =====");
    }
}
