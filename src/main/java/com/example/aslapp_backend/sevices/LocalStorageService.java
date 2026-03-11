package com.example.aslapp_backend.sevices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Profile("local") // Active when you run with the 'local development' profile
public class LocalStorageService implements StorageService {

    private final String uploadDir;
    private final String baseUrl;

    public LocalStorageService(
            @Value("${app.storage.local.upload-dir}") String uploadDir,
            @Value("${app.storage.local.base-url:http://localhost:8081/uploads}") String baseUrl) {
        this.uploadDir = uploadDir;
        this.baseUrl = baseUrl;
        initializeStorage();
    }

    private void initializeStorage() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created upload directory: " + uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Cannot upload empty file");
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        // Save file to local directory
        Path targetLocation = Paths.get(uploadDir).resolve(filename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Return public URL
        return baseUrl + "/" + filename;
    }
}