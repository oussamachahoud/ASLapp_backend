package com.example.aslapp_backend.sevices;

import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
@Profile("aws") // This service is only active when the 'aws' profile is enabled
public class AwsS3StorageService implements StorageService {

    private final S3Template s3Template;
    private final String bucketName;

    public AwsS3StorageService(S3Template s3Template, @Value("${app.storage.bucket-name}") String bucketName) {
        this.s3Template = s3Template;
        this.bucketName = bucketName;
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String key = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        s3Template.upload(bucketName, key, file.getInputStream());
        // You might need to adjust the region logic depending on your S3 setup
       // return "https://" + bucketName + ".s3." + s3Template.getRegion().id() + ".amazonaws.com/" + key;

        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }
}
