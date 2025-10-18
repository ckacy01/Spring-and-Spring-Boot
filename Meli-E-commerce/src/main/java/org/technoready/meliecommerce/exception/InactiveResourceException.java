package org.technoready.meliecommerce.exception;

public class InactiveResourceException extends RuntimeException {

    private final String resourceName;
    private final Object resourceId;

    public InactiveResourceException(String resourceName, Object resourceId) {
        super(String.format("Cannot perform operation on inactive %s with id: '%s'", resourceName, resourceId));
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }
    public Object getResourceId() {
        return resourceId;
    }



}
