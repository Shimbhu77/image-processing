package com.image.processing.api.controller;

import com.image.processing.api.exceptions.RequestException;
import com.image.processing.api.model.ProcessingRequest;
import com.image.processing.api.service.ImageService;
import com.image.processing.api.service.ProcessingRequestService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@CrossOrigin
public class RequestController {

    private final ProcessingRequestService processingRequestService;
    private final ImageService imageService;

    public RequestController(ProcessingRequestService processingRequestService, ImageService imageService) {
        this.processingRequestService = processingRequestService;
        this.imageService = imageService;
    }

    @Operation(summary = "Upload CSV file")
    @PostMapping("/public/upload/csv-file")
    public ResponseEntity<ProcessingRequest> uploadCSVFile(@RequestParam("file") MultipartFile file) throws IOException {
        ProcessingRequest processingRequest = processingRequestService.uploadCsvFile(file);
        return new ResponseEntity<>(processingRequest, HttpStatus.CREATED);
    }

    @Operation(summary = "Check processing status using request ID")
    @GetMapping("/public/processing-status/{requestId}")
    public ResponseEntity<ProcessingRequest> processingStatusCheckByRequestId(@PathVariable("requestId") Integer requestId) throws IOException, RequestException {
        ProcessingRequest processingRequest = processingRequestService.processingStatusCheckByRequestId(requestId);
        return new ResponseEntity<>(processingRequest, HttpStatus.OK);
    }

    @Operation(summary = "Get All Processing Requests Status")
    @GetMapping("/public/processing-requests")
    public ResponseEntity<List<ProcessingRequest>> getAllProcessingRequests() {
        List<ProcessingRequest> processingRequests = processingRequestService.getAllProcessingRequests();
        return new ResponseEntity<>(processingRequests, HttpStatus.OK);
    }

    @Operation(summary = "Download CSV file using request ID")
    @GetMapping("/public/download-csv-file/{requestId}")
    public ResponseEntity<String> downloadCsvFileByRequestId(@PathVariable("requestId") Integer requestId) throws RequestException {
        String bytes = processingRequestService.downloadCsvFile(requestId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=output.csv");
        headers.add("Content-Type", "text/csv");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    /*@PostMapping("/public/process/csv-file/{s3FileName}")
    public ResponseEntity<String>  processCSVFile(@PathVariable("s3FileName") String s3FileName) throws IOException {
        imageService.processCsvFile(s3FileName);
        return new ResponseEntity<>("Processing has been started", HttpStatus.CREATED);
    }*/


}