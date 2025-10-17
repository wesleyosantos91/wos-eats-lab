package io.github.wesleyosantos91.catalog.infrastructure.s3.adapter;

import io.github.wesleyosantos91.catalog.core.annotation.Adapter;
import io.github.wesleyosantos91.catalog.core.port.out.storage.StoragePort;
import io.github.wesleyosantos91.catalog.infrastructure.s3.config.S3PropertiesConfig;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Adapter(type = Adapter.AdapterType.OUTBOUND, description = "S3 Storage Adapter")
public record S3StorageAdapter(S3Client s3Client, S3PropertiesConfig props) implements StoragePort {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3StorageAdapter.class);

    @Override
    public void upload(String key, InputStream in, long size, String contentType) {
        final String resolvedType = contentType != null ? contentType : "application/octet-stream";
        final PutObjectRequest put = PutObjectRequest.builder()
                .bucket(props.getBucketName())
                .key(key)
                .contentLength(size)
                .contentType(resolvedType)
                .build();
        s3Client.putObject(put, RequestBody.fromInputStream(in, size));
    }

    @Override
    public StorageObjectMetadata head(String key) {
        final HeadObjectRequest req = HeadObjectRequest.builder()
                .bucket(props.getBucketName())
                .key(key)
                .build();
        final HeadObjectResponse resp = s3Client.headObject(req);
        return new StorageObjectMetadata(resp.contentType(), resp.eTag(), resp.contentLength());
    }

    @Override
    public DownloadedObject download(String key) {
        final GetObjectRequest req = GetObjectRequest.builder()
                .bucket(props.getBucketName())
                .key(key)
                .build();
        final ResponseBytes<GetObjectResponse> bytes = s3Client.getObjectAsBytes(req);
        final GetObjectResponse resp = bytes.response();
        final Resource body = new InputStreamResource(bytes.asInputStream());
        final String contentType = resp.contentType();
        final Long contentLength = resp.contentLength();
        final String eTag = resp.eTag();
        return new DownloadedObject(key, contentType, eTag, contentLength, body);
    }

    @Override
    public void delete(String key) {
        try {
            final DeleteObjectRequest req = DeleteObjectRequest.builder()
                    .bucket(props.getBucketName())
                    .key(key)
                    .build();
            s3Client.deleteObject(req);
        } catch (S3Exception e) {

            LOGGER.warn("Failed to delete object from S3. key={}", key, e);
        }
    }
}
