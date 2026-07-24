package com.fulfillment.exception;

public class ShipmentAlreadyExistsException extends RuntimeException {

    public ShipmentAlreadyExistsException(String message) {
        super(message);
    }
}
