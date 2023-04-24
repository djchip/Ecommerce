package com.ecommerce.core.exceptions;

public class CriterionBeingUsedException extends Exception {
    public CriterionBeingUsedException(String errorMessage){
        super(errorMessage);
    }
}
