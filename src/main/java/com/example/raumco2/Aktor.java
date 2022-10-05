package com.example.raumco2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Aktor {

    private String roomToHandle;
    private boolean windowIsOpen;

    public Aktor(String roomToHandle) {
        this.roomToHandle = roomToHandle;
        this.init();
    }

    private void init(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        //Connection connection = null;
        try {
            Connection connection = factory.newConnection(this.roomToHandle+"actors");
            Channel channel = connection.createChannel(2);

            channel.queueDeclare(this.roomToHandle+"actors", false, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String instructionToOpen = new String(delivery.getBody(), StandardCharsets.UTF_8);
                this.windowIsOpen = Boolean.parseBoolean(instructionToOpen);
                System.out.println("Instructions is: " + instructionToOpen);
                System.out.println("Window opened status is: " + this.windowIsOpen);
            };
            channel.basicConsume(this.roomToHandle+"actors", true, deliverCallback, consumerTag -> {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

}
