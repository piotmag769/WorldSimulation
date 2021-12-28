package agh.ics.oop.api;

import agh.ics.oop.gui.App;
import javafx.application.Application;

public class World {

    public static void main(String[] args)
    {
        System.out.println("system wystartował");

        Application.launch(App.class, args);

        System.out.println("system zakończył działanie");
    }
}
