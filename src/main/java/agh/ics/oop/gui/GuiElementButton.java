package agh.ics.oop.gui;

import agh.ics.oop.*;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
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

    public static Labeled createElement(IMapElement element)
    {
        Image image = views_map.get(element.getOrientation());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        Labeled node;

        node = (element instanceof Grass) ? new Label(): new Button();

        node.setGraphic(imageView);
        node.setText(element.getPosition().toString());
        node.setContentDisplay(ContentDisplay.TOP);

        return node;
    }
}
