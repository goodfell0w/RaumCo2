package com.example.raumco2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalTime;
import java.util.Timer;

import static java.lang.Thread.sleep;
import static java.time.LocalTime.now;


@SpringBootApplication
public class RaumCo2Application {

    public static void main(String[] args) throws InterruptedException {

        ControllUnit controllUnit = new ControllUnit();
        Room e004 = new Room("e004", -1.0);
        Room e003 = new Room("e003", -1.0);

        System.out.println("Start");
        LocalTime startTime = now().plusMinutes(5);
        System.out.println("Time started");
        Double temp = 0.0;
        while(now().compareTo(startTime) < 0) {
            System.out.println("While entered");
            e004.setCo2Value(500.2 + temp );
            e004.publishCo2Value();
            controllUnit.consumeMessage(e004);

            sleep(5000);
            temp += 5.0;



        }
    }
}
