package io.github.wesleyosantos91.catalog.api.exception;

import io.github.wesleyosantos91.catalog.api.v1.response.CustomProblemDetail;
import io.github.wesleyosantos91.catalog.api.v1.response.ErrorResponse;
import io.github.wesleyosantos91.catalog.domain.exception.BusinessException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceAlreadyExistsException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.filter.ServerHttpObservationFilter;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR_LOG_MESSAGE = "Handling exception: {} - {}";
    public static final String RESOURCE_NOT_FOUND = "Resource Not Found";
    public static final String TIMESTAMP = "timestamp";

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        LOG.warn(ERROR_LOG_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());

        final List<ErrorResponse> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::buildErrorResponse)
                .collect(Collectors.toList());

        ex.getBindingResult()
                .getGlobalErrors()
                .forEach(error -> errors.add(new ErrorResponse(
                        "global",
                        messageSource.getMessage(error, LocaleContextHolder.getLocale()),
                        null,
                        "GLOBAL_VALIDATION_ERROR"
                )));

        final CustomProblemDetail problemDetail = new CustomProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "One or more validation errors occurred",
                errors
        );

        setObservationError(ex, request);

        logRequestDetails(request, "Validation Error");
        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        LOG.warn(ERROR_LOG_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());

        final List<ErrorResponse> errors = ex.getConstraintViolations()
                .stream()
                .map(this::buildErrorResponse)
                .toList();

        final CustomProblemDetail problemDetail = new CustomProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Constraint Violation",
                "One or more constraint violations occurred",
                errors
        );

        setObservationError(ex, request);

        logRequestDetails(request, "Constraint Violation");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        LOG.info(ERROR_LOG_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle(RESOURCE_NOT_FOUND);
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty("resource", ex.getResourceName());
        problemDetail.setProperty("identifier", ex.getIdentifier());

        setObservationError(ex, request);

        logRequestDetails(request, RESOURCE_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleResourceAlreadyExists(
            ResourceAlreadyExistsException ex, HttpServletRequest request) {

        LOG.info(ERROR_LOG_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Resource Already Exists");
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty("resource", ex.getResourceName());
        problemDetail.setProperty("identifier", ex.getIdentifier());

        setObservationError(ex, request);

        logRequestDetails(request, "Resource Already Exists");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {

        LOG.warn(ERROR_LOG_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setTitle("Business Rule Violation");
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty("errorCode", ex.getErrorCode());

        setObservationError(ex, request);

        logRequestDetails(request, "Business Rule Violation");
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problemDetail);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        LOG.error(ERROR_LOG_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage(), ex);

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, "Data integrity violation occurred");
        problemDetail.setTitle("Data Integrity Violation");
        problemDetail.setProperty(TIMESTAMP, Instant.now());

        setObservationError(ex, request);

        logRequestDetails(request, "Data Integrity Violation");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ProblemDetail> handleDataAccessException(
            DataAccessException ex, HttpServletRequest request) {

        LOG.error(ERROR_LOG_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage(), ex);

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE, "Database access error occurred");
        problemDetail.setTitle("Database Error");
        problemDetail.setProperty(TIMESTAMP, Instant.now());

        setObservationError(ex, request);

        logRequestDetails(request, "Database Access Error");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        LOG.warn(ERROR_LOG_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());

        final String error = String.format("Parameter '%s' should be of type '%s'",
                ex.getName(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName());

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, error);
        problemDetail.setTitle("Type Mismatch");
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty("parameter", ex.getName());
        problemDetail.setProperty("expectedType",
                Objects.requireNonNull(ex.getRequiredType()).getSimpleName());

        setObservationError(ex, request);

        logRequestDetails(request, "Method Argument Type Mismatch");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        LOG.warn(ERROR_LOG_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());

        final String error = ex.getParameterName() + " parameter is missing";

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, error);
        problemDetail.setTitle("Missing Parameter");
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty("parameter", ex.getParameterName());

        setObservationError(ex, request);

        logRequestDetails(request, "Missing Servlet Request Parameter");
        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        LOG.warn(ERROR_LOG_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Malformed JSON request");
        problemDetail.setTitle("Message Not Readable");
        problemDetail.setProperty(TIMESTAMP, Instant.now());

        setObservationError(ex, request);

        logRequestDetails(request, "HTTP Message Not Readable");
        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAllUncaughtException(
            Exception ex, HttpServletRequest request) {

        LOG.error(ERROR_LOG_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage(), ex);

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty("reference", generateErrorReference());

        setObservationError(ex, request);

        logRequestDetails(request, "Uncaught Exception");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    private void setObservationError(Exception ex, HttpServletRequest request) {
        ServerHttpObservationFilter.findObservationContext(request)
                .ifPresent(context -> context.setError(ex));
    }

    private void setObservationError(Exception ex, WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            final HttpServletRequest httpServletRequest = servletWebRequest.getRequest();
            ServerHttpObservationFilter.findObservationContext(httpServletRequest)
                    .ifPresent(context -> context.setError(ex));
        }
    }

    private ErrorResponse buildErrorResponse(FieldError fieldError) {
        return new ErrorResponse(
                fieldError.getField(),
                messageSource.getMessage(fieldError, LocaleContextHolder.getLocale()),
                fieldError.getRejectedValue(),
                fieldError.getCode()
        );
    }

    private ErrorResponse buildErrorResponse(ConstraintViolation<?> violation) {
        return new ErrorResponse(
                getPropertyName(violation.getPropertyPath()),
                violation.getMessage(),
                violation.getInvalidValue(),
                violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName()
        );
    }

    private String getPropertyName(Path path) {
        final String[] pathElements = path.toString().split("\\.");
        return pathElements[pathElements.length - 1];
    }

    private void logRequestDetails(HttpServletRequest request, String errorType) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} - Method: {}, Path: {}, Client: {}",
                    errorType,
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr());
        }
    }

    private void logRequestDetails(WebRequest request, String errorType) {
        if (LOG.isDebugEnabled() && request instanceof ServletWebRequest servletWebRequest) {
            final HttpServletRequest httpRequest = servletWebRequest.getRequest();
            logRequestDetails(httpRequest, errorType);
        }
    }

    private String generateErrorReference() {
        return "ERR-" + Instant.now().toEpochMilli();
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {


        if (body instanceof ProblemDetail problemDetail) {
            final Map<String, Object> props = problemDetail.getProperties();
            final boolean shouldAddTimestamp = props == null || !props.containsKey(TIMESTAMP);
            if (shouldAddTimestamp) {
                problemDetail.setProperty(TIMESTAMP, Instant.now());
            }

            if (request instanceof ServletWebRequest servletWebRequest) {
                problemDetail.setProperty("path", servletWebRequest.getRequest().getRequestURI());
            }
        }

        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }
}
