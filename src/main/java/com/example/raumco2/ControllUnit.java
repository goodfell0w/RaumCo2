package com.example.raumco2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/*
Möglichkeit über eine Map alle Räume an die Zentrale Steuereinheit weiter zu geben
 */
public class ControllUnit {

    private Map mapOfRooms;

    public void subscribeToRoom(){
        //
    }

    private double treshHold = 500;

    public void consumeMessage(Room room){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = null;
        try {
            connection = factory.newConnection("e004");
            Channel channel = connection.createChannel(1);

            channel.queueDeclare(room.getRoomName(), false, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                String roomName = delivery.getEnvelope().getRoutingKey();
                System.out.println(" [x] Received " + message + " von Raum " + roomName);
            };
            channel.basicConsume(room.getRoomName(), true, deliverCallback, consumerTag -> {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        // Here you could add the Message to a List
    }

    public void publishToActors(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection("actors");
            Channel channel = connection.createChannel(2);
            channel.queueDeclare("actors", false, false, false, null);
            // rest fehlt
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

}
