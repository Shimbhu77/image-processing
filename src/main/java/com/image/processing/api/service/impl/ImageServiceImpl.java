package com.image.processing.api.service.impl;

import com.image.processing.api.exceptions.ImageException;
import com.image.processing.api.model.Image;
import com.image.processing.api.model.ProcessingRequest;
import com.image.processing.api.model.Product;
import com.image.processing.api.repository.ImageRepository;
import com.image.processing.api.repository.ProcessingRequestRepository;
import com.image.processing.api.repository.ProductRepository;
import com.image.processing.api.repository.WebhookRepository;
import com.image.processing.api.service.ImageService;
import com.image.processing.api.service.S3Service;
import com.image.processing.api.service.WebhookService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ImageServiceImpl implements ImageService {

    private final S3Service s3Service;
    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final ProcessingRequestRepository processingRequestRepository;
    private final WebhookRepository webhookRepository;
    private final WebhookService webhookService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Logger logger = Logger.getLogger(ImageServiceImpl.class.getName());

    public ImageServiceImpl(S3Service s3Service, ImageRepository imageRepository, ProductRepository productRepository, ProcessingRequestRepository processingRequestRepository, WebhookRepository webhookRepository, WebhookService webhookService) {
        this.s3Service = s3Service;
        this.imageRepository = imageRepository;
        this.productRepository = productRepository;
        this.processingRequestRepository = processingRequestRepository;
        this.webhookRepository = webhookRepository;
        this.webhookService = webhookService;
    }

    @Async
    public void processCsvFile(String s3FileName) {

        ProcessingRequest processingRequest = this.processingRequestRepository.findByS3CSVFileName(s3FileName);

//        // Mark the processing request as in progress
//        processingRequest.setStatus(ProcessingRequest.RequestStatus.IN_PROGRESS);
//        processingRequestRepository.save(processingRequest);

        // Simulate processing time
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        byte[] csvFile = null;

        try {
            csvFile = s3Service.getCSVFileFromS3(s3FileName);
        } catch (IOException e) {
            // Mark the processing request as failed
            processingRequest.setStatus(ProcessingRequest.RequestStatus.FAILED);
            processingRequestRepository.save(processingRequest);
        }

        List<List<String>> productsData = parseCsvFromByteArray(csvFile);

        for (List<String> row : productsData) {
            String serialNumber = row.get(0);
            String productName = row.get(1);
            String imageUrl = row.get(2);

            String[] urls = imageUrl.split(",");

            // Trim each URL and remove additional spaces
            for (int i = 0; i < urls.length; i++) {
                urls[i] = urls[i].trim();
            }

            Product product = new Product();
            product.setSerialNumber(serialNumber);
            product.setProductName(productName);
            product.setProcessingRequest(processingRequest);
            Product savedProduct = this.productRepository.save(product);

            for (String url : urls) {
                Image image = new Image();
                image.setProduct(savedProduct);
                image.setInputUrl(url);

                try {
                    String compressImageS3Url = processImages(url, 0.5f, "jpg");
                    image.setOutputUrl(compressImageS3Url);
                    image.setProcessingStatus(Image.ProcessingStatus.COMPLETED);
                } catch (ImageException e) {
                    image.setErrorMessage(e.getMessage());
                    image.setProcessingStatus(Image.ProcessingStatus.FAILED);
                }

                this.imageRepository.save(image);
            }
        }

        logger.info("Processing of the CSV file have been completed");

        // we will call the webhook url is here

       /* LocalDateTime maxDate = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
        List<Webhook> activeWebhooks = webhookRepository.findByDeletedTs(maxDate);

        for (Webhook webhookConfiguration : activeWebhooks) {

            int retryCount = webhookConfiguration.getRetryCount();
            boolean isSuccessful = false;
            while (retryCount-- > 0) {
                String request = "We have processed all images and compressed them and uploaded then to S3 with this request id " + processingRequest.getRequestId() + " , the process has been completed and you can download the new output csv file";
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(webhookConfiguration.getEndpointUrl(), request, String.class);
                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    logger.info("Webhook call successful");
                    isSuccessful = true;
                    processingRequest.setWebhookTriggered(true);
                    processingRequest.setWebhookTriggeredIsSuccessful(true);
                    this.processingRequestRepository.save(processingRequest);
                    break;
                }
            }

            if (!isSuccessful) {
                processingRequest.setWebhookTriggered(true);
                processingRequest.setWebhookTriggeredIsSuccessful(false);
                this.processingRequestRepository.save(processingRequest);
            }

        }*/

        // Async call
        webhookService.triggerWebhooks(processingRequest);


        // Mark the processing request as completed
        processingRequest.setStatus(ProcessingRequest.RequestStatus.COMPLETED);
        processingRequestRepository.save(processingRequest);

    }

    public List<List<String>> parseCsvFromByteArray(byte[] csvData) {
        List<List<String>> result = new ArrayList<>();

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(csvData);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : csvParser) {
                List<String> row = new ArrayList<>();
                for (String value : record) {
                    row.add(value);
                }
                result.add(row);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV file", e);
        }

        return result;
    }


    /**
     * Downloads an image from a URL, compresses it and returns the byte array
     */
    public byte[] downloadAndCompressImage(String imageUrl, float compressionQuality, String outputFormat)
            throws IOException {
        // Download the image from URL
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        long fileSizeInBytes = connection.getContentLength();
        logger.info("Initial images size is this : " + fileSizeInBytes + " Bytes");
        BufferedImage originalImage = ImageIO.read(url);
/*
        // this we can use for fetching images bytes from object urls of s3
        String bucketName = extractBucketName(imageUrl);
        String key = extractKey(imageUrl);

        byte[] imageBytes = s3Service.downloadFile(bucketName, key);
        logger.info("able to fetch images from object urls : ");*/

        if (originalImage == null) {
            throw new IOException("Failed to read image from URL: " + imageUrl);
        }

        // Compress the image
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // For formats that support compression like JPEG
        if ("jpg".equalsIgnoreCase(outputFormat) || "jpeg".equalsIgnoreCase(outputFormat)) {
            ImageWriter writer = ImageIO.getImageWritersByFormatName(outputFormat).next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionQuality(compressionQuality);

            ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);
            writer.setOutput(imageOutputStream);
            writer.write(null, new IIOImage(originalImage, null, null), writeParam);
            writer.dispose();
            imageOutputStream.close();
        } else {
            // For formats that don't support compression like PNG
            ImageIO.write(originalImage, outputFormat, outputStream);
        }

        return outputStream.toByteArray();
    }

    private String extractBucketName(String objectUrl) {
        // Example URL: https://input-images-bucket-001.s3.ap-south-1.amazonaws.com/carlos-sabillon-34I7lElvdDw-unsplash.jpg
        String[] parts = objectUrl.split("\\.");
        return parts[0].replace("https://", "");
    }

    private String extractKey(String objectUrl) {
        // Get everything after the .com/ or .amazonaws.com/
        return objectUrl.substring(objectUrl.indexOf(".com/") + 5);
    }

    /**
     * Process multiple image URLs: download, compress, and upload to S3
     */
    public String processImages(String imageUrl, float compressionQuality, String outputFormat) throws ImageException {
        try {

            // Extract filename from URL or create a new one
            String fileName = "compressed_" + System.currentTimeMillis() + "." + outputFormat;

            // Download and compress
            byte[] compressedImage = downloadAndCompressImage(imageUrl, compressionQuality, outputFormat);

            // Upload to S3
            String contentType = "image/" + (outputFormat.equals("jpg") ? "jpeg" : outputFormat);
            String s3Url = s3Service.uploadImagesToS3(compressedImage, fileName, contentType);

            logger.info("Processed image " + imageUrl);
            logger.info("Uploaded to S3: " + s3Url);
            logger.info("Compressed size: " + compressedImage.length + " bytes");
            logger.info("---------------------------------");

            return s3Url;

        } catch (IOException e) {
            System.err.println("Error processing image: " + imageUrl);
            e.printStackTrace();
            throw new ImageException(e.getMessage());
        }
    }
}
