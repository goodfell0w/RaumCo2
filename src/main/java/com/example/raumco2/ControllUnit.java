package com.example.raumco2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/*
Möglichkeit über eine Map alle Räume an die Zentrale Steuereinheit weiter zu geben
 */
public class ControllUnit {

    ArrayList<Co2Sensor> co2Sensors;
    private final Channel channel;

    public ControllUnit(ArrayList<Co2Sensor> co2Sensors, Channel channel) {
        this.co2Sensors = co2Sensors;
        this.channel = channel;
    }

    private final double treshHold = 0.40;

    private void init() {
        this.co2Sensors.forEach((this::consumeMessage));
    }


    public void consumeMessage(Co2Sensor co2Sensor) {

        try {
            channel.queueDeclare(co2Sensor.getRoomName(), false, false, false, null);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String co2Value = new String(delivery.getBody(), StandardCharsets.UTF_8);
                String roomName = delivery.getEnvelope().getRoutingKey();
                System.out.println(" [x] Received " + co2Value + " von Raum " + roomName);
                handleProcess(co2Value, roomName);
            };
        try {
            channel.basicConsume(co2Sensor.getRoomName(), true, deliverCallback, consumerTag -> {
            });
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
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
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

}
