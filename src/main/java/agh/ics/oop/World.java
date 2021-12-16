package agh.ics.oop;

import agh.ics.oop.gui.*;
import javafx.application.Application;


public class World {

    public static void main(String[] args)
    {
        System.out.println("system wystartował");

//        Application.launch(App.class, args);
        GrassFieldBounded map = new GrassFieldBounded(5, 5, 0.5);

        Animal a1 = new Animal(map, new Vector2d(4, 4), 5);
        System.out.println(a1);
        map.place(a1);

        Animal a2 = new Animal(map, new Vector2d(4, 4), 5);
        System.out.println(a2);
        map.place(a2);

        a1.energy = 0;
        a2.energy = 0;
        map.let_day_pass();

        System.out.println(map);

        System.out.println("system zakończył działanie");
    }
}
