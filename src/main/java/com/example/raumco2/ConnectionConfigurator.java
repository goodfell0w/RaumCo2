package com.example.raumco2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
public class ConnectionConfigurator {

    public ConnectionConfigurator() {}

    public static Channel setupConnection(String connectionName) {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection;

        try {
            connection = factory.newConnection(connectionName);
            Channel channel = connection.createChannel();
            System.out.println("Connection -> " + channel.getConnection() + " | " + connectionName + " | Channel -> " + channel.getChannelNumber() + " initialisiert");
            return channel;
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    // Unnötig
    public static void setQueueNameSensor(Channel channel, Co2Sensor sensor){

        try {
            channel.queueDeclare(sensor.getRoomName(), false, false, false, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}