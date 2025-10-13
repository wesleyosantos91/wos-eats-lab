package io.github.wesleyosantos91.catalog.domain.exception;

public class ResourceAlreadyExistsException extends RuntimeException {

    private final String resourceName;
    private final String identifier;

    public ResourceAlreadyExistsException(String resourceName, String identifier) {
        super(String.format("Resource %s with identifier %s already exists", resourceName, identifier));
        this.resourceName = resourceName;
        this.identifier = identifier;
    }

    public ResourceAlreadyExistsException(String resourceName, String identifier, String message) {
        super(message);
        this.resourceName = resourceName;
        this.identifier = identifier;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getIdentifier() {
        return identifier;
    }
}
