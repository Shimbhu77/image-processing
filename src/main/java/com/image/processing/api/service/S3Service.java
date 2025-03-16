package com.image.processing.api.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.image.processing.api.model.ProcessingRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {
    String uploadCSVFile(String originalFileName, String s3FileName, ObjectMetadata metadata, MultipartFile file) throws IOException;
    String uploadImagesToS3(byte[] imageData, String s3FileName, String contentType);
    byte[] getCSVFileFromS3(String s3FileName) throws IOException;
    byte[] downloadFile(String bucketName, String key) throws IOException;
}
