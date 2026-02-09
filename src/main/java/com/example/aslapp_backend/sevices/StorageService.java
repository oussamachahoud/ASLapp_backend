package com.example.aslapp_backend.sevices;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface StorageService {
    /**
     * Uploads a file to the cloud storage.
     * @param file The file to upload.
     * @return The public URL of the uploaded file.
     * @throws IOException if the upload fails.
     */
    String uploadFile(MultipartFile file) throws IOException;
}

