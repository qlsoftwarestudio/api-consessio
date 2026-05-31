package com.concessio.crm.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@ConditionalOnProperty(prefix = "app.storage", name = "provider", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(LocalStorageService.class);

    @Value("${app.storage.path:./uploads}")
    private String storagePath;

    @Override
    public void uploadFile(String key, byte[] content, String contentType) {
        try {
            Path filePath = Paths.get(storagePath, key);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, content);
            logger.info("Archivo guardado localmente: {}", key);
        } catch (IOException e) {
            logger.error("Error al guardar archivo local: {}", e.getMessage());
            throw new RuntimeException("No se pudo guardar el archivo", e);
        }
    }

    @Override
    public byte[] downloadFile(String key) {
        try {
            Path filePath = Paths.get(storagePath, key);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            logger.error("Error al leer archivo local: {}", e.getMessage());
            throw new RuntimeException("No se pudo leer el archivo", e);
        }
    }

    @Override
    public void deleteFile(String key) {
        try {
            Path filePath = Paths.get(storagePath, key);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                logger.info("Archivo eliminado localmente: {}", key);
            }
        } catch (IOException e) {
            logger.error("Error al eliminar archivo local: {}", e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String key) {
        return Paths.get(storagePath, key).toString();
    }
}
