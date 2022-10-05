package com.example.raumco2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/*
Möglichkeit über eine Map alle Räume an die Zentrale Steuereinheit weiter zu geben
 */
public class ControllUnit {

    ArrayList<Room> rooms;

    public ControllUnit(ArrayList<Room> rooms) {
        this.rooms = rooms;
        this.init();
    }

    private double treshHold = 0.40;

    private void init() {
        this.rooms.forEach(((room) -> consumeMessage(room)));
    }


    private void consumeMessage(Room room) {
        System.out.println("Connection was established for room: " + room.getRoomName());
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        //Connection connection = null;
        try {
            Connection connection = factory.newConnection(room.getRoomName());
            Channel channel = connection.createChannel(1);

            channel.queueDeclare(room.getRoomName(), false, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String co2Value = new String(delivery.getBody(), StandardCharsets.UTF_8);
                String roomName = delivery.getEnvelope().getRoutingKey();
                System.out.println(" [x] Received " + co2Value + " von Raum " + roomName);
                handleProcess(co2Value, roomName);
            };
            channel.basicConsume(room.getRoomName(), true, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        // Here you could add the Message to a List
    }

    private void handleProcess(String co2Value, String roomName) {
        boolean toOpen = false;
        double co2 = Double.parseDouble(co2Value);
        if (co2 > this.treshHold) {
            toOpen = true;
        }
        publishToActors(toOpen, roomName);

    }


    private void publishToActors(boolean toOpen, String roomName) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection(roomName+"actors");
             Channel channel = connection.createChannel(2)){
            channel.queueDeclare(roomName+"actors", false, false, false, null);
            channel.basicPublish("", roomName+"actors", null, String.valueOf(toOpen).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

}
