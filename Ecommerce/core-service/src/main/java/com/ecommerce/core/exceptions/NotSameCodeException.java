package com.ecommerce.core.exceptions;

public class NotSameCodeException extends Exception{
    public NotSameCodeException(String errorMessage) {
        super(errorMessage);
    }
}
