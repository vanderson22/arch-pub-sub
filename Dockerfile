# Use the official RabbitMQ image with management plugin
FROM rabbitmq:3-management

# Expose the default RabbitMQ port and management UI port
EXPOSE 5672
EXPOSE 15672

# Set default username and password (for development purposes)
ENV RABBITMQ_DEFAULT_USER=guest
ENV RABBITMQ_DEFAULT_PASS=guest
