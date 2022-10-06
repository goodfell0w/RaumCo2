package com.example.raumco2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Klasse mit Funktionalität zum aufsetzten der Verbindungen zu MQTT
 */
@Service
public class ConnectionConfigurator {

    /**
     * Instantiates a new Connection configurator.
     */
    public ConnectionConfigurator() {}

    /**
     * Sets connection.
     *
     * @param connectionName the connection name
     * @return Channel Objekt mit der Verbindung
     * // TODO: 06.10.2022 Verbindungsdetails aufsetzen mit Benutzerkennung und Passwort
     */
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

    /**
     * Setzt einmalig den Queue Bezeichner. -> aktuell unnötig
     *
     * @param channel the channel
     * @param sensor  the sensor
     */

    public static void setQueueNameSensor(Channel channel, Co2Sensor sensor){

        try {
            channel.queueDeclare(sensor.getRoomName(), false, false, false, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}