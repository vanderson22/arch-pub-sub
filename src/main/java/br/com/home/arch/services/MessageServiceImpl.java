package br.com.home.arch.services;

import br.com.home.arch.publishers.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    private final Publisher publisher;
    private final String exchangeName;

    public MessageServiceImpl(Publisher publisher, @Value("${spring.rabbitmq.exchange.name}") String exchangeName) {
        this.publisher = publisher;
        this.exchangeName = exchangeName;
    }

    @Override
    public void publishMessage(String message, String routingKey) {
        logger.info("MessageService publishing message: {} with routing key: {}", message, routingKey);
        publisher.sendMessage(message, exchangeName, routingKey);
    }
}