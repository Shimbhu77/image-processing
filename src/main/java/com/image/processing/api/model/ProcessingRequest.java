package com.image.processing.api.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "d_processing_request_tbl")
public class ProcessingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer requestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(name = "webhook_triggered")
    private Boolean webhookTriggered = false;

    @Column(name = "webhook_triggered_is_successful")
    private Boolean webhookTriggeredIsSuccessful;

    public Boolean getWebhookTriggeredIsSuccessful() {
        return webhookTriggeredIsSuccessful;
    }

    public void setWebhookTriggeredIsSuccessful(Boolean webhookTriggeredIsSuccessful) {
        this.webhookTriggeredIsSuccessful = webhookTriggeredIsSuccessful;
    }

    @Column(name = "original_csv_file_name")
    private String originalCSVFileName;

    @Column(name = "original_csv_file_s3_url")
    private String originalCSVFileS3Url;

    @Column(name = "s3_csv_file_name")
    private String s3CSVFileName;

    @Column(updatable = false, name = "created_ts")
    private LocalDateTime createdTs;

    @Column(name = "updated_ts")
    private LocalDateTime updatedTs;

    @Column(name = "completion_time")
    private LocalDateTime completionTime;

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

    public enum RequestStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Boolean getWebhookTriggered() {
        return webhookTriggered;
    }

    public void setWebhookTriggered(Boolean webhookTriggered) {
        this.webhookTriggered = webhookTriggered;
    }

    public String getOriginalCSVFileName() {
        return originalCSVFileName;
    }

    public void setOriginalCSVFileName(String originalCSVFileName) {
        this.originalCSVFileName = originalCSVFileName;
    }

    public String getOriginalCSVFileS3Url() {
        return originalCSVFileS3Url;
    }

    public void setOriginalCSVFileS3Url(String originalCSVFileS3Url) {
        this.originalCSVFileS3Url = originalCSVFileS3Url;
    }

    public String getS3CSVFileName() {
        return s3CSVFileName;
    }

    public void setS3CSVFileName(String s3CSVFileName) {
        this.s3CSVFileName = s3CSVFileName;
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

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(LocalDateTime completionTime) {
        this.completionTime = completionTime;
    }

    public LocalDateTime getDeletedTs() {
        return deletedTs;
    }

    public void setDeletedTs(LocalDateTime deletedTs) {
        this.deletedTs = deletedTs;
    }
}

