package com.image.processing.api.controller;

import com.image.processing.api.exceptions.WebhookException;
import com.image.processing.api.model.Webhook;
import com.image.processing.api.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/webhooks")
@CrossOrigin
public class WebhookController {

    private final WebhookService webhookConfigurationService;
    private final Logger logger = Logger.getLogger(WebhookController.class.getName());

    public WebhookController(WebhookService webhookConfigurationService) {
        this.webhookConfigurationService = webhookConfigurationService;
    }

    @Operation(summary = "Add Webhook which called after the image processing is completed")
    @PostMapping("/public/add-webhook")
    public ResponseEntity<Webhook> addWebhook(@RequestBody Webhook webhook) throws WebhookException {
        Webhook configuration = webhookConfigurationService.addWebhookConfiguration(webhook);
        return new ResponseEntity<>(configuration, HttpStatus.CREATED);
    }

    @Operation(summary = "Receive Webhook Notification which called after image compression is completed")
    @PostMapping("/public/webhook-notification")
    public void receiveWebhookNotification(@RequestBody String message) throws WebhookException {
       logger.info("Webhook Notification Received: " + message);
    }
}