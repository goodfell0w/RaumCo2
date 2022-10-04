package com.example.raumco2;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public record Room(String name, Double co2Value) {
    public Double getCo2Value() {
        return this.co2Value;
    }

    /**
     *
     * @param co2Value
     * @return Returns a new Room Object with the updated co2Value
     */
    public Room setCo2Value(Double co2Value){
        return new Room(this.name, co2Value);
    }

    public void publishCo2Value(){
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
}
