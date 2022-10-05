package com.example.raumco2;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import static java.lang.Thread.sleep;

public final class Room {
    private final String name;
    public Double co2Value;

    public Room(String name) {
        this.name = name;
        Thread newThread = new Thread(() -> {
            this.initSensor();
        });
        newThread.start();
    }

    public Double getCo2Value() {
        return this.co2Value;
    }

    private  void initSensor() {
        while(true){
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.readCO2();
            publishCo2Value();
        }
    }


    private void readCO2(){
        this.co2Value =  Math.random();
    }

    public String getRoomName(){
        return this.name;
    }

    public void publishCo2Value() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection(this.name);
            Channel channel = connection.createChannel(1)) {
            channel.queueDeclare(this.name, false, false, false, null);
            channel.basicPublish("", this.name, null, this.parseValues());
            System.out.println(" [x] Sent '" + this.getCo2Value() + " from room " + this.name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] parseValues(){
        return this.getCo2Value().toString().getBytes(StandardCharsets.UTF_8);
    }


    public String name() {
        return name;
    }

    public Double co2Value() {
        return co2Value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Room) obj;
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
