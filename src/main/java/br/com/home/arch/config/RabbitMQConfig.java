package br.com.home.arch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RabbitMQConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${spring.rabbitmq.queue.name}")
    private String queueName;

    @Value("${spring.rabbitmq.routing.key}")
    private String routingKey;

    @Bean
    public Queue queue() {
        logger.info("Creating queue: {}", queueName);
        return new Queue(queueName, false);
    }

    @Bean
    public TopicExchange exchange() {
        logger.info("Creating exchange: {}", exchangeName);
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        logger.info("Creating binding for queue: {}, exchange: {}, routing key: {}", queueName, exchangeName, routingKey);
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

}