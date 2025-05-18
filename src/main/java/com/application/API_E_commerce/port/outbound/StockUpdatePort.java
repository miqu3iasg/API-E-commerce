package com.application.API_E_commerce.port.outbound;

import com.application.API_E_commerce.domain.product.dtos.StockUpdateEvent;

public interface StockUpdatePort {

	void publishStockUpdate (StockUpdateEvent stockUpdateEvent);

}
