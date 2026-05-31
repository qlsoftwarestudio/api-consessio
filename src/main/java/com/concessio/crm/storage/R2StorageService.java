package com.concessio.crm.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Service
@ConditionalOnProperty(prefix = "app.storage", name = "provider", havingValue = "r2")
public class R2StorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(R2StorageService.class);

    private final S3Client s3Client;

    @Value("${app.storage.r2.bucket}")
    private String bucketName;

    @Value("${app.storage.r2.public-url:}")
    private String publicUrl;

    public R2StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public void uploadFile(String key, byte[] content, String contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content));
            logger.info("Archivo subido a R2: {}/{}", bucketName, key);
        } catch (S3Exception e) {
            logger.error("Error al subir archivo a R2: {}", e.getMessage());
            throw new RuntimeException("No se pudo subir el archivo a R2", e);
        }
    }

    @Override
    public byte[] downloadFile(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            return s3Client.getObject(getObjectRequest).readAllBytes();
        } catch (S3Exception | IOException e) {
            logger.error("Error al descargar archivo de R2: {}", e.getMessage());
            throw new RuntimeException("No se pudo descargar el archivo de R2", e);
        }
    }

    @Override
    public void deleteFile(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            logger.info("Archivo eliminado de R2: {}/{}", bucketName, key);
        } catch (S3Exception e) {
            logger.error("Error al eliminar archivo de R2: {}", e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String key) {
        if (publicUrl != null && !publicUrl.isBlank()) {
            return publicUrl + "/" + key;
        }
        return key;
    }
}
