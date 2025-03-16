package com.image.processing.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "d_webhook_tbl")
public class Webhook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "webhook_id")
    private Integer webhookId;

    @Column(nullable = false,name = "endpoint_url")
    private String endpointUrl;

    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String headers;

    @Column(name = "retry_count")
    private Integer retryCount = 3;

    @Column(updatable = false,name = "created_ts")
    private LocalDateTime createdTs;

    @Column(name = "updated_ts")
    private LocalDateTime updatedTs;

    @Column(name = "deleted_ts")
    private LocalDateTime deletedTs = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    @PrePersist
    protected void onCreate() {
        this.createdTs = LocalDateTime.now();
        this.updatedTs = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedTs = LocalDateTime.now();
    }

    public Integer getWebhookId() {
        return webhookId;
    }

    public void setWebhookId(Integer webhookId) {
        this.webhookId = webhookId;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public LocalDateTime getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(LocalDateTime createdTs) {
        this.createdTs = createdTs;
    }

    public LocalDateTime getUpdatedTs() {
        return updatedTs;
    }

    public void setUpdatedTs(LocalDateTime updatedTs) {
        this.updatedTs = updatedTs;
    }

    public LocalDateTime getDeletedTs() {
        return deletedTs;
    }

    public void setDeletedTs(LocalDateTime deletedTs) {
        this.deletedTs = deletedTs;
    }
}

