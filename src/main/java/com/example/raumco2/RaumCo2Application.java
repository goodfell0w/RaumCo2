package com.example.raumco2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Timer;

import static java.lang.Thread.sleep;
import static java.time.LocalTime.now;


@SpringBootApplication
public class RaumCo2Application {

    public static void main(String[] args) throws InterruptedException {

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(new Room("e004"));
        rooms.add(new Room("e003"));
        ControllUnit controllUnit = new ControllUnit(rooms);



        Aktor aktore004 = new Aktor("e004");
        Aktor aktore003 = new Aktor("e003");
    }
}
