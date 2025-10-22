package org.technoready.meliecommerce.exception;

/**
 * Exception thrown when an operation is attempted on an inactive resource.
 * DATE: 18 - October - 2025
 *
 * @author Jorge Armando Avila Carrillo | NAOID: 3310
 * @version 1.0
 */

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
