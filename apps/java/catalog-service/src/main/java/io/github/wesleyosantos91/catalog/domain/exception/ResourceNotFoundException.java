package io.github.wesleyosantos91.catalog.domain.exception;

public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String identifier;

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(String.format("Resource %s with identifier %s not found", resourceName, identifier));
        this.resourceName = resourceName;
        this.identifier = identifier;
    }

    public ResourceNotFoundException(String resourceName, Long identifier) {
        this(resourceName, String.valueOf(identifier));
    }

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = "Unknown";
        this.identifier = "Unknown";
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getIdentifier() {
        return identifier;
    }
}
