package com.example.kincir.service.impl;

import com.example.kincir.model.meta.Round;
import com.example.kincir.service.DataSyncService;
import com.example.kincir.service.LitatomService;
import com.example.kincir.service.RoundService;
import com.example.kincir.utils.dto.response.RoundInfoResponseDTO;
import com.example.kincir.utils.dto.response.RoundResultResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSyncServiceImpl implements DataSyncService {

    private final LitatomService litatomService;
    private final RoundService roundService;

    private final ScheduledExecutorService roundInfoScheduler = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService resultScheduler = Executors.newSingleThreadScheduledExecutor();
    private final ConcurrentHashMap<Integer, ScheduledFuture<?>> pendingResults = new ConcurrentHashMap<>();

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int RETRY_DELAY_SECONDS = 2;

    @Override
    public void startDataSync() {
        try {
            // Get initial round info to calculate proper delay
            RoundInfoResponseDTO initialRoundInfo = litatomService.getRoundInfo();
            if (initialRoundInfo != null && initialRoundInfo.getData() != null) {
                scheduleNextRoundInfo(initialRoundInfo);
            }
        } catch (Exception e) {
            log.error("Error starting data sync: ", e);
            // Retry starting sync after 5 seconds
            roundInfoScheduler.schedule(this::startDataSync, 5, TimeUnit.SECONDS);
        }
    }

    private void scheduleNextRoundInfo(RoundInfoResponseDTO currentRoundInfo) {
        try {
            RoundInfoResponseDTO.Data data = currentRoundInfo.getData();
            long currentTimeSeconds = System.currentTimeMillis() / 1000;

            // Calculate when this round ends
            long endTimeSeconds = data.getEnd();
            // Calculate start time of next round
            long nextRoundStartSeconds = endTimeSeconds;

            // Schedule result fetching for current round
            long delayUntilEnd = Math.max(0, endTimeSeconds - currentTimeSeconds);
            if (delayUntilEnd > 0) {
                scheduleResultFetching(data.getRoundTimes(), delayUntilEnd);
            } else {
                // If we're past the end time, try to get result with retries
                fetchAndSaveResultWithRetry(currentRoundInfo, 0);
            }

            // Calculate delay for next round info fetch
            long delayUntilNextRound = Math.max(0, nextRoundStartSeconds - currentTimeSeconds + 1);
            log.info("Scheduling next round info fetch in {} seconds for round {}",
                    delayUntilNextRound, data.getRoundTimes());

            roundInfoScheduler.schedule(this::fetchRoundInfo, delayUntilNextRound, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error scheduling next round: ", e);
            roundInfoScheduler.schedule(this::fetchRoundInfo, 5, TimeUnit.SECONDS);
        }
    }

    private void scheduleResultFetching(int roundTimes, long delaySeconds) {
        // Cancel existing task if any
        ScheduledFuture<?> existingTask = pendingResults.get(roundTimes);
        if (existingTask != null) {
            existingTask.cancel(false);
        }

        // Schedule new task with buffer time (0.5 seconds before end)
        long adjustedDelay = Math.max(0, delaySeconds - 1);
        ScheduledFuture<?> future = resultScheduler.schedule(() -> {
            try {
                RoundInfoResponseDTO roundInfo = litatomService.getRoundInfo();
                fetchAndSaveResultWithRetry(roundInfo, 0);
                pendingResults.remove(roundTimes);
            } catch (Exception e) {
                log.error("Error fetching result for round {}: ", roundTimes, e);
                // Schedule retry for result fetching
                retryResultFetching(roundTimes);
            }
        }, adjustedDelay, TimeUnit.SECONDS);

        pendingResults.put(roundTimes, future);
        log.info("Scheduled result fetching for round {} in {} seconds", roundTimes, adjustedDelay);
    }

    private void retryResultFetching(int roundTimes) {
        resultScheduler.schedule(() -> {
            try {
                RoundInfoResponseDTO roundInfo = litatomService.getRoundInfo();
                fetchAndSaveResultWithRetry(roundInfo, 0);
            } catch (Exception e) {
                log.error("Error in retry fetching result for round {}: ", roundTimes, e);
            }
        }, RETRY_DELAY_SECONDS, TimeUnit.SECONDS);
    }

    private void fetchAndSaveResultWithRetry(RoundInfoResponseDTO roundInfo, int retryCount) {
        try {
            RoundResultResponseDTO result = litatomService.getRoundResultByRoundTime(roundInfo.getData().getRoundTimes());
            if (result != null && result.getData() != null) {
                Round round = Round.builder()
                        .roundTimes(roundInfo.getData().getRoundTimes())
                        .start(roundInfo.getData().getStart())
                        .now(roundInfo.getData().getNow())
                        .end(roundInfo.getData().getEnd())
                        .fileId(result.getData().getFileId())
                        .build();

                roundService.create(round);
                log.info("Successfully saved round {} with fileId: {}",
                        round.getRoundTimes(), round.getFileId());
            } else if (retryCount < MAX_RETRY_ATTEMPTS) {
                // Schedule retry with exponential backoff
                long delay = (long) RETRY_DELAY_SECONDS * (retryCount + 1);
                resultScheduler.schedule(
                        () -> fetchAndSaveResultWithRetry(roundInfo, retryCount + 1),
                        delay,
                        TimeUnit.SECONDS
                );
                log.info("Scheduling retry #{} for round {} in {} seconds",
                        retryCount + 1, roundInfo.getData().getRoundTimes(), delay);
            } else {
                log.error("Failed to fetch result for round {} after {} attempts",
                        roundInfo.getData().getRoundTimes(), MAX_RETRY_ATTEMPTS);
            }
        } catch (Exception e) {
            log.error("Error fetching and saving round result: {}", e.getMessage());
            if (retryCount < MAX_RETRY_ATTEMPTS) {
                long delay = (long) RETRY_DELAY_SECONDS * (retryCount + 1);
                resultScheduler.schedule(
                        () -> fetchAndSaveResultWithRetry(roundInfo, retryCount + 1),
                        delay,
                        TimeUnit.SECONDS
                );
            }
        }
    }

    private void fetchRoundInfo() {
        try {
            RoundInfoResponseDTO roundInfo = litatomService.getRoundInfo();
            if (roundInfo != null && roundInfo.getData() != null) {
                log.info("Fetched round info for round: {}", roundInfo.getData().getRoundTimes());
                scheduleNextRoundInfo(roundInfo);
            }
        } catch (Exception e) {
            log.error("Error fetching round info: ", e);
            roundInfoScheduler.schedule(this::fetchRoundInfo, 5, TimeUnit.SECONDS);
        }
    }

    @Override
    public void stopDataSync() {
        try {
            pendingResults.values().forEach(future -> future.cancel(false));
            pendingResults.clear();
            shutdownScheduler(roundInfoScheduler);
            shutdownScheduler(resultScheduler);
        } catch (Exception e) {
            log.error("Error stopping data sync: ", e);
        }
    }

    private void shutdownScheduler(ExecutorService scheduler) {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}