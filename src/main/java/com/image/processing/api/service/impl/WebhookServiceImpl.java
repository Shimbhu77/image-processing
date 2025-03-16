package com.image.processing.api.service.impl;

import com.image.processing.api.exceptions.WebhookException;
import com.image.processing.api.model.ProcessingRequest;
import com.image.processing.api.model.Webhook;
import com.image.processing.api.repository.ProcessingRequestRepository;
import com.image.processing.api.repository.WebhookRepository;
import com.image.processing.api.service.WebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class WebhookServiceImpl implements WebhookService {

    private final WebhookRepository webhookRepository;
    private final ProcessingRequestRepository processingRequestRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Logger logger = Logger.getLogger(WebhookServiceImpl.class.getName());

    public WebhookServiceImpl(WebhookRepository webhookRepository, ProcessingRequestRepository processingRequestRepository) {
        this.webhookRepository = webhookRepository;
        this.processingRequestRepository = processingRequestRepository;
    }

    @Override
    public Webhook addWebhookConfiguration(Webhook webhook) throws WebhookException {

        if(Objects.isNull(webhook.getEndpointUrl())) {
            throw new WebhookException("Endpoint URL cannot be null");
        }

        if(Objects.isNull(webhook.getRetryCount())) {
            webhook.setRetryCount(3);
        }

        LocalDateTime maxDate = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
        List<Webhook> activeWebhooks = webhookRepository.findByDeletedTs(maxDate);

        for (Webhook activeWebhook : activeWebhooks) {
            activeWebhook.setDeletedTs(LocalDateTime.now());
            webhookRepository.save(activeWebhook);
        }

        return webhookRepository.save(webhook);
    }

    @Override
    @Async
    public void triggerWebhooks(ProcessingRequest processingRequest) {

        LocalDateTime maxDate = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
        List<Webhook> activeWebhooks = webhookRepository.findByDeletedTs(maxDate);

        for (Webhook webhook : activeWebhooks) {

            int retryCount = webhook.getRetryCount();
            boolean isSuccessful = false;
            while (retryCount-- > 0) {
                String request = "We have processed all images and compressed them and uploaded then to S3 with this request id " + processingRequest.getRequestId() + " , the process has been completed and you can download the new output csv file";
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(webhook.getEndpointUrl(), request, String.class);
                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    logger.info("Webhook call successful");
                    isSuccessful = true;
                    processingRequest.setWebhookTriggered(true);
                    processingRequest.setWebhookTriggeredIsSuccessful(true);
                    this.processingRequestRepository.save(processingRequest);
                    break;
                }
            }

            if (!isSuccessful) {
                processingRequest.setWebhookTriggered(true);
                processingRequest.setWebhookTriggeredIsSuccessful(false);
                this.processingRequestRepository.save(processingRequest);
            }

        }

        logger.info("Webhooks triggered successfully");
    }
}
