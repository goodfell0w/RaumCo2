package com.example.raumco2;

import com.rabbitmq.client.Channel;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;

import static com.example.raumco2.ConnectionConfigurator.setQueueNameSensor;
import static com.example.raumco2.ConnectionConfigurator.setupConnection;
import static java.lang.Thread.sleep;
import static java.time.LocalTime.now;


@SpringBootApplication
public class RaumCo2Application {

    public static void main(String[] args) throws InterruptedException {

        Channel channelCo2Values = setupConnection("Co2Values");
        Channel channelControlUnit = setupConnection("ControllUnit");
        ArrayList<Co2Sensor> co2Sensors = new ArrayList<>();
        Co2Sensor e004 = new Co2Sensor("e004", channelCo2Values);
        setQueueNameSensor(channelCo2Values,e004);
        Co2Sensor e003 = new Co2Sensor("e003", channelCo2Values);
        setQueueNameSensor(channelCo2Values,e004);
        co2Sensors.add(e003);
        co2Sensors.add(e004);
        ControllUnit controllUnit = new ControllUnit(co2Sensors, channelControlUnit);

        while(true){
            co2Sensors.forEach(Co2Sensor::publishCo2Value);
            co2Sensors.forEach(controllUnit::consumeMessage);
            sleep(5000);
        }

    }
}
