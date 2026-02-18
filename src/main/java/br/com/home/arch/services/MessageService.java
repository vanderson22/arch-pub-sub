package br.com.home.arch.services;

public interface MessageService {
    void publishMessage(String message, String routingKey);
}