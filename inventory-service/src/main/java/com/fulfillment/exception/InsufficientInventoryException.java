package com.fulfillment.exception;

public class InsufficientInventoryException extends RuntimeException{

	public InsufficientInventoryException(String msg) {
		super(msg);
	}

}
