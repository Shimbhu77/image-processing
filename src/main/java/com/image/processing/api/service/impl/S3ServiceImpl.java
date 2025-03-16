package com.image.processing.api.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.image.processing.api.service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 s3Client;

    @Value("${aws.s3.csv.bucket}")
    private String csvBucketName;

    @Value("${aws.s3.image.bucket}")
    private String imageBucketName;

    public S3ServiceImpl(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadCSVFile(String originalFileName, String s3FileName, ObjectMetadata metadata, MultipartFile file) throws IOException {

        s3Client.putObject(new PutObjectRequest(
                csvBucketName,
                s3FileName,
                file.getInputStream(),
                metadata));

        return s3Client.getUrl(csvBucketName, s3FileName).toString();
    }

    public byte[] getCSVFileFromS3(String s3FileName) throws IOException {
        // Check if file exists
        if (!s3Client.doesObjectExist(csvBucketName, s3FileName)) {
            throw new IOException("File not found in S3: " + s3FileName);
        }

        // Get the object from S3
        S3Object s3Object = s3Client.getObject(csvBucketName, s3FileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        // Read the file content
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        byte[] fileBytes = outputStream.toByteArray();

        // Close streams
        outputStream.close();
        inputStream.close();

        return fileBytes;
    }

    /**
     * Uploads a byte array to S3
     * @param imageData The byte array of image data to upload
     * @param s3FileName The target filename in S3
     * @param contentType The MIME type of the image (e.g., "image/jpeg")
     * @return The URL of the uploaded file in S3
     */
    public String uploadImagesToS3(byte[] imageData, String s3FileName, String contentType) {
        // Create metadata object for content type
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(imageData.length);

        // Create input stream from byte array
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);

        // Upload to S3
        s3Client.putObject(new PutObjectRequest(
                imageBucketName,
                s3FileName,
                inputStream,
                metadata));

        // Return the S3 URL of the uploaded file
        return s3Client.getUrl(imageBucketName, s3FileName).toString();
    }

    @Override
    public byte[] downloadFile(String bucketName, String key) throws IOException {
        S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, key));
        try (InputStream is = s3Object.getObjectContent()) {
            return is.readAllBytes();
        }
    }
}

