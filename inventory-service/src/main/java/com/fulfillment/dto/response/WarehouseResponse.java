package com.fulfillment.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WarehouseResponse {

	private Long wId;
	private String wName;
	private String wCode;
	private String wCity;
	private String wState;
	private String wAddress;
	private Boolean wActive;
}
