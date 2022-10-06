package com.example.raumco2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 * Klasse für die Kontrolleinheit über alle RaumCo2Sensoren
 * Schwellenwert treshHold für die Weiterleitungslogik an die Fenster
 */

public class ControllUnit {

    /**
     * Liste für die Co2Sensoren
     */
    ArrayList<Co2Sensor> co2Sensors;
    private final Channel channel;
    private final double treshHold = 0.40;

    /**
     * Instantiates a new Controll unit.
     * Benötigt die Liste aller RaumSensoren
     *
     * @param co2Sensors the co 2 sensors
     * @param channel    the channel
     */
    public ControllUnit(ArrayList<Co2Sensor> co2Sensors, Channel channel) {
        this.co2Sensors = co2Sensors;
        this.channel = channel;
    }


    private void init() {
        this.co2Sensors.forEach((this::consumeMessage));
    }


    /**
     * Consume message.
     *
     * @param co2Sensor the co 2 sensor
     */
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

    /**
     * Funktion mit Weiterleitungslogik
     * @param co2Value ist der umgewandelte Wert von der konsumierten Nachricht
     * @param roomName ist der Raumname ausgelesen aus dem Routing Key der konsumierten Nachricht
     */
    private void handleProcess(String co2Value, String roomName) {
        boolean toOpen = false;
        double co2 = Double.parseDouble(co2Value);
        if (co2 > this.treshHold) {
            toOpen = true;
        }
        publishToActors(toOpen, roomName);

    }

    /**
     * Funktion um das Ergebnis der Berechnung per MQTT weiterzuleiten.
     * @param toOpen Bool Wert -> true = Fenster wird geöffnet.
     * @param roomName Wird genutzt um die Queue und den Routing Key zu setzen.
     */
    private void publishToActors(boolean toOpen, String roomName) {

        try {
            this.channel.queueDeclare(roomName+"actorWindow", false, false, false, null);
            this.channel.basicPublish("", roomName+"actorWindow", null, String.valueOf(toOpen).getBytes(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
