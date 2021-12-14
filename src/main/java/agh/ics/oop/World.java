package agh.ics.oop;

import agh.ics.oop.gui.*;
import javafx.application.Application;
import javafx.scene.image.Image;


public class World {

    public static void main(String[] args)
    {
        System.out.println("system wystartował");

        Application.launch(App.class, args);

        System.out.println("system zakończył działanie");
    }


    static Direction[] convert(String[] args) throws IllegalArgumentException
    {
        Direction[] animal_move = new Direction[args.length];  // VLA - is that okay?

        for (int i = 0; i < args.length; i++)
        {
            Direction move = switch (args[i]) {
                case "f" -> Direction.FORWARD;
                case "b" -> Direction.BACKWARD;
                case "r" -> Direction.RIGHT;
                case "l" -> Direction.LEFT;
                default -> throw new IllegalArgumentException(args[i] + " is not legal move specification");
            };

            animal_move[i] = move;
        }

        return animal_move;
    }

    static void run(Direction[] animal_move)
    {

        for (Direction move : animal_move)
        {
            String message = switch (move) {
                case FORWARD -> "idzie do przodu";
                case BACKWARD -> "idzie do tyłu";
                case RIGHT -> "idzie w prawo";
                case LEFT -> "idzie w lewo";
                case ERROR -> "";
            };

            if (!message.equals(""))
                System.out.println("Zwierzak " + message);
        }
    }
}
