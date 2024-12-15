package com.example.kincir.config;

import com.example.kincir.service.DataSyncService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSyncConfig {

    private final DataSyncService dataSyncService;

    @PostConstruct
    public void startSync() {
        log.info("Starting data synchronization...");
        dataSyncService.startDataSync();
    }

    @PreDestroy
    public void stopSync() {
        log.info("Stopping data synchronization...");
        dataSyncService.stopDataSync();
    }
}