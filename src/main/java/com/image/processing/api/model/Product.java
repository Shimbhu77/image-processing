package com.image.processing.api.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "d_product_tbl",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_product_per_request",
                        columnNames = {"request_id", "serial_number"})
        })
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private ProcessingRequest processingRequest;

    @Column(nullable = false, name = "serial_number")
    private String serialNumber;

    @Column(nullable = false,name = "product_name")
    private String productName;

    @Column(updatable = false,name = "created_ts")
    private LocalDateTime createdTs;

    @Column(name = "deleted_ts")
    private LocalDateTime deletedTs = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    @PrePersist
    protected void onCreate() {
        this.createdTs = LocalDateTime.now();
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public ProcessingRequest getProcessingRequest() {
        return processingRequest;
    }

    public void setProcessingRequest(ProcessingRequest processingRequest) {
        this.processingRequest = processingRequest;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public LocalDateTime getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(LocalDateTime createdTs) {
        this.createdTs = createdTs;
    }

    public LocalDateTime getDeletedTs() {
        return deletedTs;
    }

    public void setDeletedTs(LocalDateTime deletedTs) {
        this.deletedTs = deletedTs;
    }
}

