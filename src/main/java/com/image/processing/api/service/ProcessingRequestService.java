package com.image.processing.api.service;

import com.image.processing.api.exceptions.RequestException;
import com.image.processing.api.model.ProcessingRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProcessingRequestService {
    ProcessingRequest uploadCsvFile(MultipartFile csvFile) throws IOException;
    String downloadCsvFile(Integer requestId) throws RequestException;
    ProcessingRequest processingStatusCheckByRequestId(Integer requestId) throws RequestException;
    List<ProcessingRequest> getAllProcessingRequests();
}
