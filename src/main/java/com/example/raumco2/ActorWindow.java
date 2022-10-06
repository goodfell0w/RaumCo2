package com.example.raumco2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ActorWindow {

    private String roomToHandle;
    private boolean windowIsOpen;

    private Channel channel;

    public ActorWindow(String roomToHandle, Channel channel) {
        this.roomToHandle = roomToHandle;
        this.channel = channel;
    }

    public void consumeMessage(){
        try {
            channel.queueDeclare(this.roomToHandle+"actorWindow", false, false, false, null);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String instructionToOpen = new String(delivery.getBody(), StandardCharsets.UTF_8);
                this.windowIsOpen = Boolean.parseBoolean(instructionToOpen);
                System.out.println("Instructions is: " + instructionToOpen);
                System.out.println("Window opened status is: " + this.windowIsOpen);
            };
            channel.basicConsume(this.roomToHandle+"actorWindow", true, deliverCallback, consumerTag -> {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
