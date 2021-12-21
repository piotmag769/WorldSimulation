package agh.ics.oop;

import agh.ics.oop.gui.*;
import javafx.application.Application;


public class World {

    public static void main(String[] args)
    {
        System.out.println("system wystartował");

        try {
//        Application.launch(App.class, args);
            GrassFieldUnbounded map = new GrassFieldUnbounded(5, 5, 0.5, 1, 2, 20);
            System.out.println(map.jungleLowerLeft + " " + map.jungleUpperRight);

            Animal a1 = new Animal(map, new Vector2d(4, 4), map.startEnergy);
            map.place(a1);

            Animal a2 = new Animal(map, new Vector2d(4, 4), map.startEnergy);
            map.place(a2);

            Animal a3 = new Animal(map, new Vector2d(4, 4), map.startEnergy);
            a3.orientation = MapDirection.NORTH_EAST;
            a3.position = new Vector2d(0, 3);
            map.place(a3);
            a3.move(MoveDirection.BACKWARD);

            System.out.println(map + " " + map.elementMultimap);
//
//            for (int i = 0; i < 10; i++) {
//                map.let_day_pass();
//                System.out.println(map + " " + map.elementMultimap);
//            }
        }
        catch (RuntimeException e)
        {
            System.out.println(e.getMessage());
        }

        System.out.println("system zakończył działanie");
    }
}
