package com.example.aslapp_backend.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


//for local test only, remove in cloud
// this stoge the image in folder externe
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.storage.local.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Handle both absolute and relative paths
        String uploadPath;
        if (uploadDir.startsWith("/")) {
            // Absolute path (Docker)
            uploadPath = "file:" + uploadDir + "/";
        } else {
            // Relative path (local development)
            uploadPath = Paths.get(uploadDir).toAbsolutePath().toUri().toString();
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }

    @Bean
    CommandLineRunner initStorage() {
        return (args) -> {
            try {
                Files.createDirectories(Paths.get(uploadDir));
                System.out.println(">> Upload directory verified at: " + Paths.get(uploadDir).toAbsolutePath());
            } catch (IOException e) {
                System.err.println(">> Could not initialize storage: " + e.getMessage());
            }
        };
    }
}