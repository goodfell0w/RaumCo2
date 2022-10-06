package com.example.raumco2;

import com.rabbitmq.client.Channel;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.example.raumco2.ConnectionConfigurator.setQueueNameSensor;
import static com.example.raumco2.ConnectionConfigurator.setupConnection;
import static java.lang.Thread.sleep;
import static java.time.LocalTime.now;


/**
 * The type Raum co 2 application.
 */
@SpringBootApplication
public class RaumCo2Application {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws InterruptedException the interrupted exception
     */
    public static void main(String[] args) throws InterruptedException {

        // Erzeugung eines Channels Objekt mit einer Verbindung welcher für die Übertragung genutzt wird
        // Einmal für Co2 Werte übertragung und einmal für Consuming der Co2 Werte
        Channel connectionCo2Values = setupConnection("Co2Values");
        Channel connectionControlUnit = setupConnection("ControllUnit");
        Channel connectionActors = setupConnection("ActorsWindow");

        // Erzeugung einer Liste und Instanzierung der Räume mit Verknüfung des Channels
        ArrayList<Co2Sensor> co2Sensors = new ArrayList<>();
        Co2Sensor e004 = new Co2Sensor("e004", connectionCo2Values);
        // Erzeugung des Channelqueue mit Namen des Raumes
        // Kann auch in die Publish Function gesetzt werden
        setQueueNameSensor(connectionCo2Values,e004);
        Co2Sensor e003 = new Co2Sensor("e003", connectionCo2Values);
        setQueueNameSensor(connectionCo2Values,e004);
        co2Sensors.add(e003);
        co2Sensors.add(e004);
        ControllUnit controllUnit = new ControllUnit(co2Sensors, connectionControlUnit);
        ActorWindow windowsE003 = new ActorWindow("e003", connectionActors);
        ActorWindow windowsE004 = new ActorWindow("e004", connectionActors);

        while(true) {
            sleep(10000);
            co2Sensors.forEach(Co2Sensor::publishCo2Value);
            System.out.println(LocalDateTime.now());
            co2Sensors.forEach(controllUnit::consumeMessage);
            sleep(2000);
            windowsE003.consumeMessage();
            windowsE004.consumeMessage();
            sleep(1000);
            System.out.println("___________________________________________________");
            System.out.println();
            System.out.println();
        }

    }
}
