package agh.ics.oop;


import agh.ics.oop.gui.App;
import javafx.application.Application;

public class World {

    public static void main(String[] args)
    {
        System.out.println("system wystartował");

        try
        {
            Application.launch(App.class, args);
        }
        catch(IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
        }

        System.out.println("system zakończył działanie");
    }
}
