package com.image.processing.api.repository;

import com.image.processing.api.model.ProcessingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessingRequestRepository extends JpaRepository<ProcessingRequest, Integer> {
    ProcessingRequest findByS3CSVFileName(String s3FileName);
    List<ProcessingRequest> findByStatus(ProcessingRequest.RequestStatus status);
 }

