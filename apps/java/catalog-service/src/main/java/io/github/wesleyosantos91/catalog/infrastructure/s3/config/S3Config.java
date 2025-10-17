package io.github.wesleyosantos91.catalog.infrastructure.s3.config;

import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    private final S3PropertiesConfig props;

    public S3Config(S3PropertiesConfig props) {
        this.props = props;
    }

    @Bean
    public S3Client s3Client() {
        return S3Client
                .builder()
                .endpointOverride(URI.create(props.getEndpointUrl()))
                .region(Region.of(props.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())))
                .forcePathStyle(true)
                .build();
    }
}
