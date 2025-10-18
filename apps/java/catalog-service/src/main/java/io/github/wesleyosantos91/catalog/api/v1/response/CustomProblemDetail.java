package io.github.wesleyosantos91.catalog.api.v1.response;

import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class CustomProblemDetail extends ProblemDetail {

    public CustomProblemDetail(HttpStatus status, String title, String detail, List<ErrorResponse> errors) {
        this.setStatus(status.value());
        this.setTitle(title != null ? title : status.getReasonPhrase());
        this.setDetail(detail);
        this.setProperty("timestamp", Instant.now());
        this.setProperty("errors", errors != null ? errors : List.of());
    }

    public CustomProblemDetail(HttpStatus status, String detail, List<ErrorResponse> errors) {
        this(status, status.getReasonPhrase(), detail, errors);
    }
}
