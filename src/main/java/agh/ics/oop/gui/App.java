package agh.ics.oop.gui;

import agh.ics.oop.api.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application{
    // temporary fields to simplify the code
    private Vector2d lower_corner;
    private Vector2d upper_corner;

    private int x_len;
    private int y_len;

    //TODO get moveDelay
    private final int moveDelay = 1000;

    private IWorldMap boundedMap, unboundedMap;
    private IEngine boundedEngine, unboundedEngine;

    HBox boundedMapHBox = new HBox();
    HBox unboundedMapHBox = new HBox();

    private int width;
    private int height;
    private double jungleRatio;
    private int startEnergy;
    private int energyLoss;
    private int plantEnergy;
    private int animalsAtStart; // (>= 10)
    // decided to make both maps either magical or not, "dla każdej mapy" - specification not clear
    private boolean isMagical;

    private boolean parametersAccepted = false;

    @Override
    public void start(Stage primaryStage) {
        Thread parametersThread = new Thread(() -> getParametersFromUser(primaryStage));
        Thread mapCreationThread = new Thread(() -> {
            try {
                createMapsAndEngines(primaryStage);
            } catch (InterruptedException | IllegalStateException | IllegalArgumentException e ) {
                System.out.println(e.getMessage());
                System.exit(0);
            }
        });

        parametersThread.start();
        mapCreationThread.start();
    }

    public void getParametersFromUser(Stage primaryStage)
    {
        Platform.runLater(() -> {
            Scene scene = new Scene(createArgumentGetter(), 500, 600);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setTitle("Map project");
            primaryStage.show();
        });
    }

    public VBox createArgumentGetter()
    {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.BASELINE_CENTER);

        // to simplify the code
        Slider[] sliders = new Slider[7];

        int[][] numericParameters = {
                {5, 25, 15, 5}, // width
                {5, 25, 15, 5}, // height
                {0, 100, 71, 10}, // jungleRatio
                {0, 100, 100, 10}, // startEnergy
                {0, 100, 3, 10}, // energyLoss
                {0, 100, 100, 10}, // plantEnergy
                {10, 100, 30, 10} // animalsAtStart
        };

        String[] stringParameters = {
                "Width",
                "Height",
                "Jungle ratio (percentage)",
                "Start energy",
                "Energy loss",
                "Plant energy",
                "Animals at start"
        };

        for(int i = 0; i < numericParameters.length; i++)
            addLabelAndSlider(sliders, i, vbox, numericParameters[i][0], numericParameters[i][1], numericParameters[i][2], numericParameters[i][3], stringParameters[i]);

        // adding checkbox for isMagical attribute
        CheckBox checkBox = new CheckBox("Magical events");
        checkBox.setIndeterminate(false);
        vbox.getChildren().add(checkBox);

        addAcceptButton(vbox, sliders, checkBox);

        return vbox;
    }

    // adding label and slider to the vbox and the sliders table
    private void addLabelAndSlider(Slider[] sliders, int index, VBox vbox, int min, int max, int value, int majorTickUnit, String title)
    {
        Label label = new Label(title);

        Slider slider = new Slider(min, max, value);

        slider.setAccessibleText(title);
        slider.setMajorTickUnit(majorTickUnit);
        slider.setMinorTickCount(majorTickUnit - 1);
        slider.setShowTickMarks(true);
        slider.setSnapToTicks(true);
        slider.setShowTickLabels(true);

        sliders[index] = slider;

        VBox.setMargin(slider, new Insets(0, 10, 0, 10));

        vbox.getChildren().add(label);
        vbox.getChildren().add(slider);
    }

    // adding button (getting values from sliders on action) to the vbox
    private void addAcceptButton(VBox vbox, Slider[] sliders, CheckBox checkBox)
    {
        Button button = new Button("Accept parameters");
        button.setOnAction(e -> {
            this.width = (int) sliders[0].getValue();
            this.height = (int) sliders[1].getValue();
            this.jungleRatio = sliders[2].getValue() / 100;
            this.startEnergy = (int) sliders[3].getValue();
            this.energyLoss = (int) sliders[4].getValue();
            this.plantEnergy = (int) sliders[5].getValue();
            this.animalsAtStart = (int) sliders[6].getValue();
            this.isMagical = checkBox.isSelected();

            System.out.println(width + " " + height + " " + jungleRatio + " " + startEnergy + " " + energyLoss + " " + " " + plantEnergy + " " + plantEnergy + " " + isMagical);

            synchronized(this)
            {
                parametersAccepted = true;
                notifyAll();
            }
        });

        vbox.getChildren().add(button);
    }

    public void createMapsAndEngines(Stage primaryStage) throws InterruptedException
    {
        synchronized (this) {
            while (!parametersAccepted)
                wait();
        }

        this.boundedMap = new GrassField(width, height, jungleRatio, energyLoss, plantEnergy, startEnergy, true);
        this.unboundedMap = new GrassField(width, height, jungleRatio, energyLoss, plantEnergy, startEnergy, false);

        this.boundedEngine = new SimulationEngine(boundedMap, this.animalsAtStart, this.isMagical);
        this.unboundedEngine = new SimulationEngine(unboundedMap, this.animalsAtStart, this.isMagical);

        // temporary fields to simplify the code
        this.lower_corner = boundedMap.getLowerCorner();
        this.upper_corner = boundedMap.getUpperCorner();

        this.x_len = upper_corner.x - lower_corner.x + 1;
        this.y_len = upper_corner.y - lower_corner.y + 1;

        // creating gui for maps
        Platform.runLater(() -> {


            VBox.setMargin(boundedMapHBox, new Insets(20));
            VBox.setMargin(unboundedMapHBox, new Insets(20));


            GridPane boundedGridPane = new GridPane();
            GridPane unboundedGridPane = new GridPane();

            boundedGridPane.setGridLinesVisible(true);
            unboundedGridPane.setGridLinesVisible(true);

            boundedMapHBox.getChildren().add(boundedGridPane);
            unboundedMapHBox.getChildren().add(unboundedGridPane);

            createAndAddAxisLabels(boundedGridPane);
            createAndAddAxisLabels(unboundedGridPane);

            setColRowSizes(boundedGridPane);
            setColRowSizes(unboundedGridPane);



            // packing HBoxes into VBox and assigning it to stage
            VBox root = new VBox();
            root.setPadding(new Insets(10));
            root.setFillWidth(true);
            root.getChildren().addAll(boundedMapHBox, unboundedMapHBox);
            root.setAlignment(Pos.CENTER);

            Scene scene = new Scene(root, 1000, 800);

            Stage mapStage = new Stage();

            mapStage.setScene(scene);

            // hide previous window, show the new one
            primaryStage.hide();
            mapStage.show();

            // center window on screen
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            mapStage.setX((primScreenBounds.getWidth() - mapStage.getWidth()) / 2);
            mapStage.setY((primScreenBounds.getHeight() - mapStage.getHeight()) / 2);

        });
    }

    public void createAndAddAxisLabels(GridPane grid)
    {
        // y/x label
        Label label1 = new Label("y\\x");
        GridPane.setHalignment(label1, HPos.CENTER);
        grid.add(label1, 0, 0);

        // labeling y axis
        for(int i = 0; i < y_len; i++)
        {
            Label label = new Label(Integer.toString(upper_corner.y - i));
            GridPane.setHalignment(label, HPos.CENTER);
            grid.add(label, 0, i + 1);
        }

        // labeling x axis
        for(int i = 0; i < x_len; i++)
        {
            Label label = new Label(Integer.toString(i + lower_corner.x));
            GridPane.setHalignment(label, HPos.CENTER);
            grid.add(label, i + 1, 0);
        }
    }

    public void createAndAddElements(GridPane grid)
    {
        // filling map
        for(int i = 0; i < x_len; i++)
            for(int j = 0; j < y_len; j++)
            {
                // buttons[i][j] refers to position (i + lower_corner.x, j + lower_corner.y) on the map
                Object obj = unboundedMap.objectAt(new Vector2d(lower_corner.x + i, lower_corner.y + j));

                if (obj != null)
                {
                    Labeled node = GuiElementButton.createElement((IMapElement) obj);
                    GridPane.setHalignment(node, HPos.CENTER);
                    grid.add(node, i + 1, y_len - j);
                }
            }
    }

    public void setColRowSizes(GridPane grid)
    {
        //setting columns' sizes
        for(int i = 0; i < x_len + 1; i++)
        {
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPercentWidth(100);
            col1.setFillWidth(Boolean.TRUE);
            grid.getColumnConstraints().add(col1);
        }

        // setting rows' sizes
        for(int i = 0; i < y_len + 1; i++)
        {
            RowConstraints row1 = new RowConstraints();
            row1.setPercentHeight(100);
//            row1.setFillHeight(Boolean.TRUE);
            grid.getRowConstraints().add(row1);

        }
    }


}
