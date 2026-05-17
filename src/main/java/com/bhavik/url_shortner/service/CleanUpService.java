package com.bhavik.url_shortner.service;

import com.bhavik.url_shortner.repository.UrlMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class CleanUpService {

    private static final Logger logger = LoggerFactory.getLogger(CleanUpService.class);

    private final UrlMappingRepository urlMappingRepository;

    public CleanUpService(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanupExpiredUrls() {

        logger.info("Starting scheduled job: Cleaning up expired URL mappings...");
        LocalDateTime now = LocalDateTime.now();

        long deletedCount = urlMappingRepository.deleteByExpirationDateBefore(now);

        if (deletedCount > 0) {
            logger.info("Finished scheduled job: Successfully deleted {} expired URL mappings.", deletedCount);
        } else {
            logger.info("Finished scheduled job: No expired URL mappings found to delete.");
        }
    }
}
