package com.ecommerce.core.exceptions;

public class AppParamBeingUsedException extends Exception {
    public AppParamBeingUsedException(String errorMessage) {
        super(errorMessage);
    }
}
