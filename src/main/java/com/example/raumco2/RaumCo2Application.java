package com.example.raumco2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalTime;
import java.util.Timer;

import static java.time.LocalTime.now;


@SpringBootApplication
public class RaumCo2Application {

    public static void main(String[] args) {
        SpringApplication.run(RaumCo2Application.class, args);
    }{
        Room e004 = new Room("e004", -1.0);
        Room e003 = new Room("e003", -1.0);

        LocalTime startTime = now().plusMinutes(5);

        while(now().compareTo(startTime) > 0){
        e004.setCo2Value(500.2);
        e004.publishCo2Value();
        e004.setCo2Value(300.3);
        e004.publishCo2Value();
        }
    }
}
