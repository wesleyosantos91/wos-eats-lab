package io.github.wesleyosantos91.catalog.infrastructure.s3.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.s3.DiskBufferingS3OutputStreamProvider;
import io.awspring.cloud.s3.Jackson2JsonS3ObjectConverter;
import io.awspring.cloud.s3.PropertiesS3ObjectContentTypeResolver;
import io.awspring.cloud.s3.S3ObjectContentTypeResolver;
import io.awspring.cloud.s3.S3ObjectConverter;
import io.awspring.cloud.s3.S3OutputStreamProvider;
import io.awspring.cloud.s3.S3Template;
import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

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
                .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())))
                .forcePathStyle(true)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner
                .builder()
                .endpointOverride(URI.create(props.getEndpointUrl()))
                .region(Region.of(props.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())))
                .build();
    }

    @Bean
    public S3ObjectConverter s3ObjectConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonS3ObjectConverter(objectMapper);
    }

    @Bean
    public S3ObjectContentTypeResolver s3ObjectContentTypeResolver() {
        return new PropertiesS3ObjectContentTypeResolver();
    }

    @Bean
    public S3OutputStreamProvider s3OutputStreamProvider(S3Client s3Client, S3ObjectContentTypeResolver s3ObjectContentTypeResolver) {
        return new DiskBufferingS3OutputStreamProvider(s3Client, s3ObjectContentTypeResolver);
    }

    @Bean
    public S3Template s3Template(S3Client s3Client,
                                 S3OutputStreamProvider s3OutputStreamProvider,
                                 S3ObjectConverter s3ObjectConverter,
                                 S3Presigner s3Presigner) {
        return new S3Template(s3Client, s3OutputStreamProvider, s3ObjectConverter, s3Presigner);
    }

}
