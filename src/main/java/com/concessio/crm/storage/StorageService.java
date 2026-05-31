package com.concessio.crm.storage;

public interface StorageService {
    void uploadFile(String key, byte[] content, String contentType);
    byte[] downloadFile(String key);
    void deleteFile(String key);
    String getFileUrl(String key);
}
