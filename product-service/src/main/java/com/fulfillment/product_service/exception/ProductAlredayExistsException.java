package com.fulfillment.product_service.exception;

public class ProductAlredayExistsException extends RuntimeException{

	public ProductAlredayExistsException(String msg) {
		super(msg);
	}

}
