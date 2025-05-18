package com.application.API_E_commerce.adapters.outbound.messaging;

import com.application.API_E_commerce.domain.product.dtos.StockUpdateEvent;
import com.application.API_E_commerce.port.outbound.StockUpdatePort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQStockUpdateAdapter implements StockUpdatePort {

	private static final String EXCHANGE_NAME = "stock.exchange";
	private static final String ROUTING_KEY = "stock.update";

	private final RabbitTemplate rabbitTemplate;

	public RabbitMQStockUpdateAdapter (RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void publishStockUpdate (StockUpdateEvent stockUpdateEvent) {
		rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, stockUpdateEvent);
	}

}
