package agh.ics.oop.gui;

import agh.ics.oop.api.Animal;
import agh.ics.oop.api.Grass;
import agh.ics.oop.api.IMapElement;
import agh.ics.oop.api.MapDirection;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.util.HashMap;
import java.util.Map;

public class GuiElementButton {
    private static final Map<MapDirection, Image> views_map = new HashMap<>() {{
        put(MapDirection.NORTH, new Image(MapDirection.NORTH.getImagePath()));
        put(MapDirection.NORTH_EAST, new Image(MapDirection.NORTH_EAST.getImagePath()));
        put(MapDirection.EAST, new Image(MapDirection.EAST.getImagePath()));
        put(MapDirection.SOUTH_EAST, new Image(MapDirection.SOUTH_EAST.getImagePath()));
        put(MapDirection.SOUTH, new Image(MapDirection.SOUTH.getImagePath()));
        put(MapDirection.SOUTH_WEST, new Image(MapDirection.SOUTH_WEST.getImagePath()));
        put(MapDirection.WEST, new Image(MapDirection.WEST.getImagePath()));
        put(MapDirection.NORTH_WEST, new Image(MapDirection.NORTH_WEST.getImagePath()));

        //for grass
        put(MapDirection.GRASS, new Image(MapDirection.GRASS.getImagePath()));
    }};

    public static Button createElement(IMapElement element, double width, double height, int startEnergy)
    {
        Image image = views_map.get(element.getOrientation());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width/2);
        imageView.setFitHeight(height/2);
        imageView.setPreserveRatio(true);

        // in case of grass clicking on button has no effect
        Button button = new Button();

        setButtonColor(button, startEnergy, element);

        button.setGraphic(imageView);

        button.setMaxSize(width, height);
        button.setMinSize(width, height);
        button.setCenterShape(true);

        return button;
    }


    private static void setButtonColor(Button button, int startEnergy, IMapElement element)
    {
        String greenComponent;

        if (element instanceof Animal animal)
        {
            // energy of animal is defined by green component in its background color
            // from magenta = #ff00ff (energy >= startEnergy)  to white = #ffffff (energy == 0)

            int energy = animal.getEnergy();

            if (startEnergy == 0)
                greenComponent = "ff";
            else {
                greenComponent = (energy > startEnergy) ? "00" : Integer.toHexString((int) ((1.0 - (double) energy / (double) startEnergy) * 255.0));
            }

            // to match css format
            if (greenComponent.length() == 1)
                greenComponent = "0" + greenComponent;
        }
        else {
            greenComponent = "ff";
        }

        button.setStyle("-fx-background-color: #ff" + greenComponent + "ff");
    }

    private static void setButtonOnClick()
    {

    }
}
