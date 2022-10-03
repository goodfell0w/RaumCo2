package com.example.raumco2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Send {
    private final static String QUEUE_NAME = "hello";

    public Send() throws IOException, TimeoutException {
    }

    public static void main(String[] argv) throws Exception {
    }
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    try {
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

    }
}
