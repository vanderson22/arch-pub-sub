package br.com.home.arch.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import br.com.home.arch.services.MessageService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
public class MessageController {

    private static final Logger logger = LogManager.getLogger(MessageController.class);

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/publish")
    public ResponseEntity<String> publishMessage(@RequestParam String message, @RequestParam String routingKey) {
        logger.info("Received message to publish: {} with routing key: {}", message, routingKey);
        messageService.publishMessage(message, routingKey);
        return new ResponseEntity<>(String.format("Message sent: %s to routing key: %s", message, routingKey), HttpStatus.OK);
    }
}