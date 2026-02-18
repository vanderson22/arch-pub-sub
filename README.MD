# Publish/Subscribe (Pub/Sub) Application

This project demonstrates the Publish/Subscribe messaging pattern using Spring Boot and RabbitMQ.

## Project Setup

1.  **Generate Spring Boot Project**:
    The project was generated using Spring Initializr with `web` and `amqp` dependencies.
    You can recreate it using the following `curl` command:
    ```bash
    curl "https://start.spring.io/starter.zip?dependencies=web,amqp&packaging=jar&javaVersion=17&type=maven-project&groupId=br.com.home&artifactId=pub-sub-app&name=pub-sub-app" -o pub-sub-app.zip
    ```
    Then unzip the file:
    ```bash
    Expand-Archive -Path pub-sub-app.zip -DestinationPath .
    ```
2.  **Navigate to the project directory**:
    ```bash
    cd .
    ```

## Publisher Implementation

The publisher component is responsible for sending messages to RabbitMQ.

1.  **RabbitMQ Configuration (`src/main/java/br/com/home/arch/config/RabbitMQConfig.java`)**:
    This class sets up the RabbitMQ queue, exchange, and binds them together.

    ```java
    package br.com.home.arch.config;

    import org.springframework.amqp.core.Binding;
    import org.springframework.amqp.core.BindingBuilder;
    import org.springframework.amqp.core.Queue;
    import org.springframework.amqp.core.TopicExchange;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;

    @Configuration
    public class RabbitMQConfig {

        public static final String EXCHANGE_NAME = "my-exchange";
        public static final String QUEUE_NAME = "my-queue";
        public static final String ROUTING_KEY = "my-routing-key";

        @Bean
        public Queue queue() {
            return new Queue(QUEUE_NAME, false);
        }

        @Bean
        public TopicExchange exchange() {
            return new TopicExchange(EXCHANGE_NAME);
        }

        @Bean
        public Binding binding(Queue queue, TopicExchange exchange) {
            return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
        }
    }
    ```

2.  **Publisher Service (`src/main/java/br/com/home/arch/publishers/Publisher.java`)**:
    This service uses `RabbitTemplate` to send messages.

    ```java
    package br.com.home.arch.publishers;

    import org.springframework.amqp.rabbit.core.RabbitTemplate;
    import org.springframework.stereotype.Service;
    import br.com.home.arch.config.RabbitMQConfig;
    
    import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.Logger;

    @Service
    public class Publisher {

        private static final Logger logger = LogManager.getLogger(Publisher.class);

        private final RabbitTemplate rabbitTemplate;

        public Publisher(RabbitTemplate rabbitTemplate) {
            this.rabbitTemplate = rabbitTemplate;
        }

        public void sendMessage(String message) {
            logger.info("Sending message: {}", message);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, message);
        }
    }
    ```

3.  **Message Controller (`src/main/java/br/com/home/arch/controllers/MessageController.java`)**:
    A REST controller to expose an endpoint for sending messages.

    ```java
    package br.com.home.arch.controllers;

    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.bind.annotation.RestController;
    import br.com.home.arch.publishers.Publisher;

    @RestController
    public class MessageController {

        private final Publisher publisher;

        public MessageController(Publisher publisher) {
            this.publisher = publisher;
        }

        @PostMapping("/publish")
        public ResponseEntity<String> publishMessage(@RequestParam String message) {
        logger.info("Received message to publish: {}", message);
        publisher.sendMessage(message);
        return new ResponseEntity<>(String.format("Message sent: %s", message), HttpStatus.OK);
    }
}
    ```

### How to use the Publisher

1.  Ensure RabbitMQ is running (see Dockerfile section below).
2.  Start the Spring Boot application.
3.  Send a POST request to `/publish` with a `message` parameter. For example, using `curl`:
    ```bash
    curl -X POST "http://localhost:8080/publish?message=Hello%20RabbitMQ"
    ```

## Logging with Log4j2

This project uses Log4j2 for logging. The configuration is defined in `src/main/resources/log4j2.xml`.

### `pom.xml` additions

To use Log4j2 with Spring Boot, the default `spring-boot-starter-logging` dependency is excluded, and `spring-boot-starter-log4j2` is included:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
```

### `log4j2.xml` configuration

This file configures console and rolling file appenders:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
        <RollingFile name="File" fileName="logs/app.log"
                     filePattern="logs/app-%d{yyyy-MM-dd}-%i.log.gz">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
            <Policies>
                <TimeBasedTriggeringPolicy/>
                <SizeBasedTriggeringPolicy size="10 MB"/>
            </Policies>
            <DefaultRolloverStrategy max="10"/>
        </RollingFile>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="File"/>
        </Root>
    </Loggers>
</Configuration>
```


1.  Ensure RabbitMQ is running (see Dockerfile section below).
2.  Start the Spring Boot application.
3.  Send a POST request to `/publish` with a `message` parameter. For example, using `curl`:
    ```bash
    curl -X POST "http://localhost:8080/publish?message=Hello%20RabbitMQ"
    ```

## Subscriber Implementation

The subscriber component is responsible for receiving messages from RabbitMQ.

1.  **Subscriber Listener (`src/main/java/br/com/home/arch/subscribers/Subscriber.java`)**:
    This class contains a method that listens to the `my-queue` and processes incoming messages.

    ```java
    package br.com.home.arch.subscribers;

    import org.springframework.amqp.rabbit.annotation.RabbitListener;
    import org.springframework.stereotype.Component;
    import br.com.home.arch.config.RabbitMQConfig;

    import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.Logger;

    @Component
    public class Subscriber {

        private static final Logger logger = LogManager.getLogger(Subscriber.class);

        @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
        public void receiveMessage(String message) {
            logger.info("Received message: {}", message);
        }
    }
    ```

### How to observe the Subscriber

1.  Ensure RabbitMQ is running (see Dockerfile section below).
2.  Start the Spring Boot application.
3.  When a message is published (using the publisher endpoint), the `receiveMessage` method in `Subscriber.java` will be invoked, and the message will be printed to the console.

## RabbitMQ with Docker

A `Dockerfile` is provided to easily set up a RabbitMQ instance.

1.  **Build the Docker image** (from the root of `pub-sub-app` directory):
    ```bash
    docker build -t my-rabbitmq .
    ```
2.  **Run the Docker container**:
    ```bash
    docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 my-rabbitmq
    ```
    -   `5672`: Default RabbitMQ port for AMQP.
    -   `15672`: Port for the RabbitMQ Management UI (access at `http://localhost:15672` with username `guest` and password `guest`).
3.  **Stop the Docker container**:
    ```bash
    docker stop rabbitmq
    ```
4.  **Remove the Docker container**:
    ```bash
    docker rm rabbitmq
    ```

