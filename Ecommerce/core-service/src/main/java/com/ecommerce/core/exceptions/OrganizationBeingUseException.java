package com.ecommerce.core.exceptions;

public class OrganizationBeingUseException extends Exception{
    public OrganizationBeingUseException(String messageError){
        super(messageError);
    }
}
