package com.image.processing.api.service.impl;

import com.image.processing.api.model.ProcessingRequest;
import com.image.processing.api.repository.ProcessingRequestRepository;
import com.image.processing.api.service.ImageService;
import com.image.processing.api.service.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    private final ProcessingRequestRepository processingRequestRepository;
    private final ImageService imageService;

    public SchedulerServiceImpl(ProcessingRequestRepository processingRequestRepository, ImageService imageService) {
        this.processingRequestRepository = processingRequestRepository;
        this.imageService = imageService;
    }

    // Run every 5 minutes
    @Override
    @Scheduled(cron = "0 */1 * * * *")
    public void startSchedulerTask() {
        logger.info("Scheduled task running at: {}", LocalDateTime.now());
        // Your task logic goes here

        List<ProcessingRequest> pendingRequests = processingRequestRepository.findByStatus(ProcessingRequest.RequestStatus.PENDING);

        for (ProcessingRequest request : pendingRequests) {
            // Start processing the request
            request.setStatus(ProcessingRequest.RequestStatus.IN_PROGRESS);
            processingRequestRepository.save(request);

            imageService.processCsvFile(request.getS3CSVFileName());
        }
    }
}
