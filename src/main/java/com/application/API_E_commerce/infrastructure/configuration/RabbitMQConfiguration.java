package com.application.API_E_commerce.infrastructure.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

	public static final String EXCHANGE_NAME = "stock.exchange";
	public static final String QUEUE_NAME = "stock.update.queue";
	public static final String ROUTING_KEY = "stock.update";
	public static final String DLQ_NAME = "stock.update.dlq";
	public static final String DLX_NAME = "stock.dlx";
	public static final String DLQ_ROUTING_KEY = "stock.update.dlq";

	@Bean
	public TopicExchange stockExchange () {
		return new TopicExchange(EXCHANGE_NAME);
	}

	@Bean
	public Queue stockUpdateQueue () {
		return QueueBuilder.durable(QUEUE_NAME)
				.withArgument("x-dead-letter-exchange", DLX_NAME)
				.withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
				.build();
	}

	@Bean
	public Queue deadLetterQueue () {
		return new Queue(DLQ_NAME, true);
	}

	@Bean
	public TopicExchange deadLetterExchange () {
		return new TopicExchange(DLX_NAME);
	}

	@Bean
	public Binding stockUpdateBinding (@Qualifier("stockUpdateQueue") Queue stockUdateQueue, TopicExchange stockExchange) {
		return BindingBuilder.bind(stockUdateQueue)
				.to(stockExchange)
				.with(ROUTING_KEY);
	}

	@Bean
	public Binding deadLetterBinding (
			@Qualifier("deadLetterQueue") Queue deadLetterQueue,
			TopicExchange deadLetterExchange
	) {
		return BindingBuilder.bind(deadLetterQueue)
				.to(deadLetterExchange)
				.with(DLQ_ROUTING_KEY);
	}

	@Bean
	public Jackson2JsonMessageConverter messageConverter () {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory (
			ConnectionFactory connectionFactory,
			Jackson2JsonMessageConverter messageConverter
	) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(messageConverter);
		factory.setMaxConcurrentConsumers(5);
		factory.setDefaultRequeueRejected(false);
		return factory;
	}

}
