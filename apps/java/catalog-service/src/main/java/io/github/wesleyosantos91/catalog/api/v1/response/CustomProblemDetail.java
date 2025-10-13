package io.github.wesleyosantos91.catalog.api.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class CustomProblemDetail extends ProblemDetail {

    @Schema(description = "Timestamp when the error occurred")
    private static final String TIMESTAMP_PROPERTY = "timestamp";

    @Schema(description = "List of validation errors")
    private static final String ERRORS_PROPERTY = "errors";

    @Schema(description = "Error reference for support tracking")
    private static final String REFERENCE_PROPERTY = "reference";

    @Schema(description = "Path where the error occurred")
    private static final String PATH_PROPERTY = "path";

    public CustomProblemDetail(HttpStatus status, String title, String detail, List<ErrorResponse> errors) {
        this.setStatus(status.value());
        this.setTitle(title != null ? title : status.getReasonPhrase());
        this.setDetail(detail);
        this.setProperty(TIMESTAMP_PROPERTY, Instant.now());
        this.setProperty(ERRORS_PROPERTY, errors != null ? errors : List.of());
    }

    public CustomProblemDetail(HttpStatus status, String detail, List<ErrorResponse> errors) {
        this(status, status.getReasonPhrase(), detail, errors);
    }

    public Instant getTimestamp() {
        return (Instant) this.getProperties().get(TIMESTAMP_PROPERTY);
    }

    @SuppressWarnings("unchecked")
    public List<ErrorResponse> getErrors() {
        return (List<ErrorResponse>) this.getProperties().get(ERRORS_PROPERTY);
    }

    public String getReference() {
        return (String) this.getProperties().get(REFERENCE_PROPERTY);
    }

    public String getPath() {
        return (String) this.getProperties().get(PATH_PROPERTY);
    }
}