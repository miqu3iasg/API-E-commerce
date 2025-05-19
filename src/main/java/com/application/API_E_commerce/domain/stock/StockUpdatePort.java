package com.application.API_E_commerce.domain.stock;

import com.application.API_E_commerce.adapters.inbound.dtos.StockUpdateEvent;

public interface StockUpdatePort {

	void publishStockUpdate (StockUpdateEvent stockUpdateEvent);

}
