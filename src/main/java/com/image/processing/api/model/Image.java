package com.image.processing.api.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "d_image_tbl")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 1024,name = "input_url")
    private String inputUrl;

    @Column(length = 1024,name = "output_url")
    private String outputUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,name = "processing_status")
    private ProcessingStatus processingStatus = ProcessingStatus.PENDING;

    @Column(columnDefinition = "TEXT",name = "error_message")
    private String errorMessage;

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

    public enum ProcessingStatus {
        PENDING, DOWNLOADING, PROCESSING, COMPLETED, FAILED
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getInputUrl() {
        return inputUrl;
    }

    public void setInputUrl(String inputUrl) {
        this.inputUrl = inputUrl;
    }

    public String getOutputUrl() {
        return outputUrl;
    }

    public void setOutputUrl(String outputUrl) {
        this.outputUrl = outputUrl;
    }

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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