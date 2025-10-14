package io.github.wesleyosantos91.catalog.infrastructure.s3;

import java.net.URI;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {



    public S3Client s3Client() {
        return S3Client
                .builder()
                .endpointOverride(URI.create("https://localhost.localstack.cloud:4566"))
                .region(Region.US_EAST_1)
                .build();
    }

}
