package br.com.home.arch.subscribers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class Subscriber {

    private static final Logger logger = LoggerFactory.getLogger(Subscriber.class);

    @RabbitListener(queues = "queue.architecture")
    public void receiveMessageFromQueue(String message) {
        logger.info("Received message from queue 'queue.architecture': {}", message);
    }
}