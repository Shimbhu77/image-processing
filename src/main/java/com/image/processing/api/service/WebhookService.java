package com.image.processing.api.service;

import com.image.processing.api.exceptions.WebhookException;
import com.image.processing.api.model.ProcessingRequest;
import com.image.processing.api.model.Webhook;

public interface WebhookService {
    Webhook addWebhookConfiguration(Webhook webhook) throws WebhookException;
    void triggerWebhooks(ProcessingRequest processingRequest);
}
