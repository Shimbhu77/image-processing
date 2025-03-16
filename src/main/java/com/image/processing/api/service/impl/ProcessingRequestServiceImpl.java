package com.image.processing.api.service.impl;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.image.processing.api.exceptions.RequestException;
import com.image.processing.api.model.Image;
import com.image.processing.api.model.ProcessingRequest;
import com.image.processing.api.model.Product;
import com.image.processing.api.repository.ImageRepository;
import com.image.processing.api.repository.ProcessingRequestRepository;
import com.image.processing.api.repository.ProductRepository;
import com.image.processing.api.service.ImageService;
import com.image.processing.api.service.ProcessingRequestService;
import com.image.processing.api.service.S3Service;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ProcessingRequestServiceImpl implements ProcessingRequestService {

    private static final List<String> REQUIRED_HEADERS = Arrays.asList("S. No.", "Product Name", "Input Image Urls");

    private final S3Service s3Service;
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final ProcessingRequestRepository processingRequestRepository;
    private final ProductRepository productRepository;

    public ProcessingRequestServiceImpl(S3Service s3Service, ImageService imageService, ImageRepository imageRepository, ProcessingRequestRepository processingRequestRepository, ProductRepository productRepository) {
        this.s3Service = s3Service;
        this.imageService = imageService;
        this.imageRepository = imageRepository;
        this.processingRequestRepository = processingRequestRepository;
        this.productRepository = productRepository;
    }

    @Override
    public ProcessingRequest uploadCsvFile(MultipartFile file) throws IOException {

        // 1. Validate file is not empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // 2. Validate file is CSV
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        if (Objects.isNull(contentType) || !contentType.contains("csv") &&
                (Objects.isNull(fileName) || !fileName.toLowerCase().endsWith(".csv"))) {
            throw new IllegalArgumentException("File must be in CSV format");
        }

        // 3. Validate CSV structure
        validateCsvStructure(file);

        // 4. Upload to S3
        String s3FileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType("text/csv");

        String csvS3Url = s3Service.uploadCSVFile(fileName, s3FileName, metadata, file);

        ProcessingRequest processingRequest = new ProcessingRequest();
        processingRequest.setOriginalCSVFileName(fileName);
        processingRequest.setOriginalCSVFileS3Url(csvS3Url);
        processingRequest.setS3CSVFileName(s3FileName);
        processingRequest.setStatus(ProcessingRequest.RequestStatus.PENDING);

        ProcessingRequest request = processingRequestRepository.save(processingRequest);

        // Async call
       // imageService.processCsvFile(s3FileName);
        return request;

    }

    @Override
    public String downloadCsvFile(Integer requestId) throws RequestException {

        ProcessingRequest processingRequest = processingRequestRepository.findById(requestId).orElseThrow(() -> new RequestException("Please provide a valid request id"));

        if(processingRequest.getStatus() != ProcessingRequest.RequestStatus.COMPLETED) {
            throw new RequestException("Processing request is not completed yet");
        }

        List<Product> productList = productRepository.findByProcessingRequest_RequestId(processingRequest.getRequestId());

        List<List<String>> data = new ArrayList<>();
        for (Product product : productList) {
            List<String> row = new ArrayList<>();
            row.add(product.getSerialNumber());
            row.add(product.getProductName());

            List<Image> imageList = imageRepository.findByProduct_ProductId(product.getProductId());

            String inputImageUrls = "";
            String outputImageUrls = "";

            int count = 0;
            for (Image image : imageList) {
                inputImageUrls += image.getInputUrl();
                outputImageUrls += image.getOutputUrl();

                if(count < imageList.size() - 1) {
                    inputImageUrls += ",";
                    outputImageUrls += ",";
                }
                count++;
            }

            row.add(inputImageUrls);
            row.add(outputImageUrls);

            data.add(row);
        }

        StringWriter stringWriter = new StringWriter();
        try (CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT.withHeader("S. No.", "Product Name", "Input Image Urls", "Output Image Urls"))) {
            for (List<String> row : data) {
                csvPrinter.printRecord(row);
            }
        } catch (Exception e) {
            throw new RequestException(e.getMessage());
        }

        return stringWriter.toString();
    }

    @Override
    public ProcessingRequest processingStatusCheckByRequestId(Integer requestId) throws RequestException {
        return processingRequestRepository.findById(requestId).orElseThrow(() -> new RequestException("Please a valid request id"));
    }

    @Override
    public List<ProcessingRequest> getAllProcessingRequests() {
        return processingRequestRepository.findAll();
    }

    /**
     *  Explicitly validate column headers
     */
    private void validateCsvStructure(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

            // Check if all required headers are present
            List<String> headerNames = csvParser.getHeaderNames();
            if (!headerNames.containsAll(REQUIRED_HEADERS)) {
                throw new IllegalArgumentException(
                        "CSV file must contain the following columns: " +
                                String.join(", ", REQUIRED_HEADERS));
            }

            // Optional: Check if there are at least some records
            if (!csvParser.iterator().hasNext()) {
                throw new IllegalArgumentException("CSV file doesn't contain any data records");
            }
        }
    }
}
