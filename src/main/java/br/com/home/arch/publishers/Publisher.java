package br.com.home.arch.publishers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class Publisher {

    private static final Logger logger = LoggerFactory.getLogger(Publisher.class);

    private final RabbitTemplate rabbitTemplate;

    public Publisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message, String exchangeName, String routingKey) {
        logger.info("Sending message: {} to exchange: {} with routing key: {}", message, exchangeName, routingKey);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
    }
}