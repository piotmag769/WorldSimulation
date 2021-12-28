package agh.ics.oop.gui;

import agh.ics.oop.api.Animal;
import agh.ics.oop.api.IEngine;
import agh.ics.oop.api.IMapElement;
import agh.ics.oop.api.MapDirection;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;



import java.util.Arrays;
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

    public static Button createElement(IMapElement element, double width, double height, int startEnergy, IEngine engine)
    {
        Image image = views_map.get(element.getOrientation());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width/2);
        imageView.setFitHeight(height/2);
        imageView.setPreserveRatio(true);

        // in case of grass clicking on button has no effect
        Button button = new Button();

        setButtonColor(button, startEnergy, element, engine);

        button.setGraphic(imageView);

        button.setMaxSize(width, height);
        button.setMinSize(width, height);
        button.setCenterShape(true);

        return button;
    }


    private static void setButtonColor(Button button, int startEnergy, IMapElement element, IEngine engine)
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

            setButtonOnAction(button, animal, engine);
        }
        else {
            greenComponent = "ff";
        }

        button.setStyle("-fx-background-color: #ff" + greenComponent + "ff");
    }

    private static void setButtonOnAction(Button button, Animal animal, IEngine engine)
    {
        // to track or display genotype
        button.setOnAction(event -> {
            // only can do that if simulation is stopped
            if (engine.isStopped())
            {
                Button genotypeButton = new Button("Show genotype");
                Button trackButton = new Button("Track");

                HBox.setMargin(genotypeButton, new Insets(20));

                HBox.setMargin(trackButton, new Insets(20));

                HBox hBox = new HBox(genotypeButton, trackButton);

                Scene scene = new Scene(hBox, 230, 100);

                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();

                genotypeButton.setOnAction(e -> {
                    stage.hide();
                    Stage genotypeStage = new Stage();
                    genotypeStage.setScene(new Scene(new Label(Arrays.toString(animal.getGenotype())), 400, 100));
                    genotypeStage.show();
                });

                trackButton.setOnAction(e -> {
                    engine.startTrackingAnimal(animal);
                    stage.hide();
                });
            }
        });
    }
}
