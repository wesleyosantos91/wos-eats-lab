package io.github.wesleyosantos91.catalog.infrastructure.s3.adapter;

import io.awspring.cloud.s3.S3Template;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.wesleyosantos91.catalog.core.annotation.Adapter;
import io.github.wesleyosantos91.catalog.domain.port.out.storage.StoragePort;
import io.github.wesleyosantos91.catalog.domain.exception.InfrastructureException;
import io.github.wesleyosantos91.catalog.infrastructure.properties.config.AppProperties;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Adapter(type = Adapter.AdapterType.OUTBOUND, description = "S3 Storage Adapter")
public record S3StorageAdapter(S3Template s3Template, S3Client s3Client, AppProperties props) implements StoragePort {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3StorageAdapter.class);

    @Override
    @Retry(name = "s3Storage")
    @CircuitBreaker(name = "s3Storage", fallbackMethod = "uploadFileFallback")
    @Bulkhead(name = "s3StorageBulkhead", type = Bulkhead.Type.SEMAPHORE, fallbackMethod = "uploadFileFallback")
    public String uploadFile(MultipartFile file) throws IOException {
        LOGGER.info("Uploading file to S3: {}", file.getOriginalFilename());
        validateFile(file);
        final String fileKey = generateFileKey(file.getOriginalFilename());
        s3Template.upload(props.getS3BucketName(), fileKey, file.getInputStream());
        LOGGER.info("File uploaded to S3: {}", fileKey);
        return fileKey;
    }

    @Override
    @Retry(name = "s3Storage")
    @CircuitBreaker(name = "s3Storage", fallbackMethod = "downloadFileFallback")
    @Bulkhead(name = "s3StorageBulkhead", type = Bulkhead.Type.SEMAPHORE, fallbackMethod = "downloadFileFallback")
    public byte[] downloadFile(String fileKey) {
        try {
            LOGGER.debug("Downloading file from S3. Key: {}, Bucket: {}", fileKey, props.getS3BucketName());
            final byte[] content = s3Template.download(props.getS3BucketName(), fileKey).getContentAsByteArray();
            LOGGER.debug("File downloaded successfully. Key: {}, Size: {} bytes", fileKey, content.length);
            return content;
        } catch (IOException e) {
            throw new InfrastructureException("Error downloading file from S3", e);
        }
    }

    @Override
    @Retry(name = "s3Storage")
    @CircuitBreaker(name = "s3Storage", fallbackMethod = "deleteFileFallback")
    @Bulkhead(name = "s3StorageBulkhead", type = Bulkhead.Type.SEMAPHORE, fallbackMethod = "deleteFileFallback")
    public void deleteFile(String fileKey) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(props.getS3BucketName())
                .key(fileKey)
                .build());
        LOGGER.info("File deleted successfully from S3. Key: {}", fileKey);
    }

    private String uploadFileFallback(MultipartFile file, Throwable throwable) throws Throwable {
        handleResilienceFailure("upload", file.getOriginalFilename(), throwable);
        throw propagateThrowable(throwable, () -> new InfrastructureException("Error uploading file to S3", throwable));
    }

    private byte[] downloadFileFallback(String fileKey, Throwable throwable) throws Throwable {
        handleResilienceFailure("download", fileKey, throwable);
        throw propagateThrowable(throwable, () -> new InfrastructureException("Error downloading file from S3", throwable));
    }

    private void deleteFileFallback(String fileKey, Throwable throwable) throws Throwable {
        handleResilienceFailure("delete", fileKey, throwable);
        throw propagateThrowable(throwable, () -> new InfrastructureException("Error deleting file from S3", throwable));
    }

    private void handleResilienceFailure(String operation, String target, Throwable throwable) {
        if (throwable instanceof BulkheadFullException) {
            LOGGER.error("S3 {} operation blocked by bulkhead. target={} bucket={}",
                    operation, target, props.getS3BucketName(), throwable);
        } else if (throwable instanceof CallNotPermittedException) {
            LOGGER.error("S3 {} operation blocked by open circuit. target={} bucket={}",
                    operation, target, props.getS3BucketName(), throwable);
        } else {
            LOGGER.error("S3 {} operation failed after retries. target={} bucket={}",
                    operation, target, props.getS3BucketName(), throwable);
        }
    }

    private RuntimeException propagateThrowable(Throwable throwable, java.util.function.Supplier<RuntimeException> fallbackSupplier)
            throws Throwable {
        if (throwable instanceof IOException ioException) {
            throw ioException;
        }
        if (throwable instanceof RuntimeException runtimeException) {
            throw runtimeException;
        }
        return fallbackSupplier.get();
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
