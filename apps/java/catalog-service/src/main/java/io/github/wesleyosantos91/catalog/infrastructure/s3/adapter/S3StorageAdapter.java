package io.github.wesleyosantos91.catalog.infrastructure.s3.adapter;

import io.awspring.cloud.s3.S3Template;
import io.github.wesleyosantos91.catalog.core.annotation.Adapter;
import io.github.wesleyosantos91.catalog.core.port.out.storage.StoragePort;
import io.github.wesleyosantos91.catalog.domain.exception.InfrastructureException;
import io.github.wesleyosantos91.catalog.infrastructure.s3.config.S3PropertiesConfig;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Adapter(type = Adapter.AdapterType.OUTBOUND, description = "S3 Storage Adapter")
public record S3StorageAdapter(S3Template s3Template, S3Client s3Client, S3PropertiesConfig props) implements StoragePort {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3StorageAdapter.class);

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        LOGGER.info("Uploading file to S3: {}", file.getOriginalFilename());
        validateFile(file);
        final String fileKey = generateFileKey(file.getOriginalFilename());
        s3Template.upload(props.getBucketName(), fileKey, file.getInputStream());
        LOGGER.info("File uploaded to S3: {}", fileKey);
        return fileKey;
    }

    @Override
    public byte[] downloadFile(String fileKey) {
        try {
            LOGGER.debug("Downloading file from S3. Key: {}, Bucket: {}", fileKey, props.getBucketName());
            final byte[] content = s3Template.download(props.getBucketName(), fileKey).getContentAsByteArray();
            LOGGER.debug("File downloaded successfully. Key: {}, Size: {} bytes", fileKey, content.length);
            return content;
        } catch (IOException e) {
            throw new InfrastructureException("Error downloading file from S3", e);
        }
    }

    @Override
    public void deleteFile(String fileKey) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(props.getBucketName())
                .key(fileKey)
                .build());
        LOGGER.info("File deleted successfully from S3. Key: {}", fileKey);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > props.getMaxSizeBytes()) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size: " + props.getMaxSizeBytes());
        }

        final String contentType = file.getContentType();
        if (!props.getAllowedContentTypes().contains(contentType)) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: " + props.getAllowedContentTypes());
        }
    }

    private String generateFileKey(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID() + extension;
    }
}
