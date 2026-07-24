package com.fulfillment.exception;

public class PaymentNotFoundException extends RuntimeException{

	public PaymentNotFoundException(String msg) {
		super(msg);
	}

}
