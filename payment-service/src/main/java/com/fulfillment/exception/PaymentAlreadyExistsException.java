package com.fulfillment.exception;

public class PaymentAlreadyExistsException extends RuntimeException{

	public PaymentAlreadyExistsException(String msg) {
		super(msg);
	}

}
