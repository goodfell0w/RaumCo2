package com.example.raumco2;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import static com.example.raumco2.ConnectionConfigurator.*;
import static java.lang.Thread.sleep;

/**
 * Co2Sensor des Raumes
 * name -> Raumname z.B. e004
 * co2Value -> Wert in Double
 * channel -> Zugewiesenes Channel Objekt der Verbindung für CO2Values
 */
public final class Co2Sensor {
    private final String name;
    private Double co2Value;

    private final Channel channel;

    /**
     * Instantiates a new Co 2 sensor.
     *
     * @param name    the name
     * @param channel the channel
     */
    public Co2Sensor(String name, Channel channel) {
        this.name = name;
        generateRandomCo2Value();
        this.channel = channel;
    }

    /**
     * Gets co 2 value.
     *
     * @return the co 2 value
     */
    public Double getCo2Value() {
        return this.co2Value;
    }

    public void start(){
        Thread newThread = new Thread(() -> {
            this.initSensor();
        });
        newThread.start();
    }


    private void initSensor() {
        while(true){
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.generateRandomCo2Value();
            publishCo2Value();
        }
    }


    private void generateRandomCo2Value(){
        this.co2Value =  Math.random();
    }

    /**
     * Get room name string.
     *
     * @return the string
     */
    public String getRoomName(){
        return this.name;
    }

    /**
     * Veröffentlicht den Co2 Wert des Raumes durch eine QueueDeclaration auf den Raumnamen
     * // TODO: 06.10.2022 Byteconvertierung auslagern in Funktion
     */
    public void publishCo2Value() {
        try {
            // Wird bereits durch setQueueNameSensor im ConnectionConfigurator gesetzt.
            // Ein erneutes Declare wird ignoriert wenn der Channel bereits steht.
            // Sinnvoll da Channels auch geschlossen werden können. -> Frage ist wann?
            this.channel.queueDeclare(this.name, false, false, false, null);
            this.channel.basicPublish("", this.name, null, this.getCo2Value().toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(" [x] Sent '" + this.getCo2Value() + " from room " + this.name);
    }


    /**
     * Name string.
     *
     * @return the string
     */
    public String name() {
        return name;
    }

    /**
     * Co 2 value double.
     *
     * @return the double
     */
    public Double co2Value() {
        return co2Value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Co2Sensor) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.co2Value, that.co2Value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, co2Value);
    }

    @Override
    public String toString() {
        return "Room[" +
                "name=" + name + ", " +
                "co2Value=" + co2Value + ']';
    }

}
