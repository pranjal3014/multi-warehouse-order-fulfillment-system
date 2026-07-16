package com.fulfillment.dto.request;

import lombok.Data;

@Data
public class WarehouseRequest {
	
	private String wName;
	private String wCode;
	private String wCity;
	private String wState;
	private String wAddress;
}
