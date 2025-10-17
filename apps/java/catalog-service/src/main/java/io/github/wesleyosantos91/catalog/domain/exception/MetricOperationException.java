package io.github.wesleyosantos91.catalog.domain.exception;

public class MetricOperationException extends RuntimeException {

    public MetricOperationException(String message) {
        super(message);
    }

    public MetricOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
