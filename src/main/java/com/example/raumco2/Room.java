package com.example.raumco2;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

public final class Room {
    private final String name;
    public Double co2Value;

    public Room(String name, Double co2Value) {
        this.name = name;
        this.co2Value = co2Value;
    }

    public Double getCo2Value() {
        return this.co2Value;
    }

    /**
     *
     * @param co2Value
     * @return Returns a new Room Object with the updated co2Value
     */
    public void setCo2Value(Double co2Value) {
        this.co2Value = co2Value;
    }

    public void publishCo2Value() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(this.name, false, false, false, null);
            channel.basicPublish("", this.name, null, this.getCo2Value().toString().getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + this.getCo2Value() + "'");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
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
