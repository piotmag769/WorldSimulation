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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Objects;

public class App extends Application{
    // temporary fields to simplify the code
    private Vector2d lower_corner;
    private Vector2d upper_corner;
    private Vector2d jungleUpperCorner;
    private Vector2d jungleLowerCorner;

    private int x_len;
    private int y_len;

    private final double GRID_SIZE = 480.0;
    private double CELL_WIDTH;
    private double CELL_HEIGHT;

    private IWorldMap boundedMap, unboundedMap;
    private IEngine boundedEngine, unboundedEngine;

    private HBox boundedHBox;
    private HBox unboundedHBox;

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
    private boolean mapsAndEnginesCreated = false;


    @Override
    public void start(Stage welcomeStage) {
        getParametersFromUser(welcomeStage);
        Thread mapCreationThread = new Thread(() -> {
            try {
                makeMapStage(welcomeStage);
            } catch (InterruptedException | IllegalStateException | IllegalArgumentException e ) {
                System.out.println(e.getMessage());
            }
        });

        Thread mapThread = new Thread(this::startEngines);

        mapCreationThread.setDaemon(true);
        mapThread.setDaemon(true);

        mapCreationThread.start();
        mapThread.start();
    }

    public void getParametersFromUser(Stage primaryStage)
    {
        Scene scene = new Scene(createArgumentGetter(), 500, 600);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Map project");
        primaryStage.show();
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

            synchronized(this)
            {
                parametersAccepted = true;
                notifyAll();
            }
        });

        vbox.getChildren().add(button);
    }

    public void makeMapStage(Stage welcomeStage) throws InterruptedException
    {
        synchronized (this) {
            while (!parametersAccepted)
                wait();
        }

        createMapsAndEngines();

        // creating necessary gui elements for maps
        Platform.runLater(() -> {
            Stage mapStage = createGuiMaps(welcomeStage);

            // hide previous window, show the new one
            welcomeStage.hide();
            mapStage.show();

            // center window on screen
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            mapStage.setX((primScreenBounds.getWidth() - mapStage.getWidth()) / 2);
            mapStage.setY((primScreenBounds.getHeight() - mapStage.getHeight()) / 2);

            addPauseButton(boundedHBox, boundedEngine);
            addPauseButton(unboundedHBox, unboundedEngine);

            addGenotypeButton(boundedHBox, boundedEngine);
            addGenotypeButton(unboundedHBox, unboundedEngine);

            addPlots();
            addTexts();

            // to make average data save to file
            customizeExit(mapStage);
        });

        synchronized (this)
        {
            mapsAndEnginesCreated = true;
            notifyAll();
        }
    }

    public void createMapsAndEngines()
    {
        this.boundedMap = new GrassField(width, height, jungleRatio, energyLoss, plantEnergy, startEnergy, true);
        this.unboundedMap = new GrassField(width, height, jungleRatio, energyLoss, plantEnergy, startEnergy, false);

        this.boundedEngine = new SimulationEngine(boundedMap, this.animalsAtStart, this.isMagical, this, "src\\main\\resources\\statistics\\bounded_statistics.csv");
        this.unboundedEngine = new SimulationEngine(unboundedMap, this.animalsAtStart, this.isMagical, this, "src\\main\\resources\\statistics\\unbounded_statistics.csv");

        // temporary fields to simplify the code
        this.lower_corner = boundedMap.getLowerCorner();
        this.upper_corner = boundedMap.getUpperCorner();

        this.jungleLowerCorner = boundedMap.getJungleLowerCorner();
        this.jungleUpperCorner = boundedMap.getJungleUpperCorner();

        this.x_len = upper_corner.x - lower_corner.x + 1;
        this.y_len = upper_corner.y - lower_corner.y + 1;

        this.CELL_WIDTH = GRID_SIZE/(double) (x_len+1);
        this.CELL_HEIGHT = GRID_SIZE/(double) (y_len+1);
    }

    public Stage createGuiMaps(Stage welcomeStage)
    {
        // repeating code to have variables with different names
        GridPane boundedGridPane = new GridPane();
        GridPane unboundedGridPane = new GridPane();
        boundedGridPane.setMaxHeight(GRID_SIZE);
        boundedGridPane.setMinHeight(GRID_SIZE);
        boundedGridPane.setMaxWidth(GRID_SIZE);
        boundedGridPane.setMinWidth(GRID_SIZE);

        unboundedGridPane.setMaxHeight(GRID_SIZE);
        unboundedGridPane.setMinHeight(GRID_SIZE);
        unboundedGridPane.setMaxWidth(GRID_SIZE);
        unboundedGridPane.setMinWidth(GRID_SIZE);

        // adding references to GridPanes to engines (to let them modify them)
        this.boundedEngine.setGridPane(boundedGridPane);
        this.unboundedEngine.setGridPane(unboundedGridPane);

        this.boundedHBox = new HBox(boundedGridPane);
        this.unboundedHBox = new HBox(unboundedGridPane);

        VBox.setMargin(boundedHBox, new Insets(10));
        VBox.setMargin(unboundedHBox, new Insets(10));

        createAndAddAxisLabels(boundedGridPane);
        createAndAddAxisLabels(unboundedGridPane);

        setColRowSizes(boundedGridPane);
        setColRowSizes(unboundedGridPane);

        createAndAddElements(boundedGridPane, boundedMap, boundedEngine);
        createAndAddElements(unboundedGridPane, unboundedMap, unboundedEngine);

        boundedGridPane.setGridLinesVisible(true);
        unboundedGridPane.setGridLinesVisible(true);

        // packing HBoxes into VBox
        VBox root = new VBox();
        root.getChildren().addAll(boundedHBox, unboundedHBox);
        root.setAlignment(Pos.CENTER);

        // creating new scene and stage
        Scene scene = new Scene(root, 1400, 1000);
        Stage mapStage = new Stage();
        mapStage.setScene(scene);

        return mapStage;
    }

    private void addPauseButton(HBox hbox, IEngine engine)
    {
        Button button = new Button("\u23F8");

        button.setMaxSize(30,30);
        button.setMinSize(30,30);

        button.setOnAction(e -> {
            engine.changeEngineState();

            if (Objects.equals(button.getText(), "\u23F8"))
                button.setText("\u25B6");
            else
                button.setText("\u23F8");
        });

        HBox.setMargin(button, new Insets(20));

        hbox.getChildren().add(button);
    }

    private void addGenotypeButton(HBox hbox, IEngine engine)
    {
        Button button = new Button("genotype");
        button.setOnAction(event -> {
            if (engine.isStopped())
                engine.highlightDominantGenotypeAnimals();
        });

        hbox.getChildren().add(button);
    }

    public void createAndAddAxisLabels(GridPane grid)
    {

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

    private void setColRowSizes(GridPane grid)
    {
        //setting columns' sizes
        for(int i = 0; i < x_len + 1; i++)
        {
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setMinWidth(CELL_WIDTH);
            col1.setMaxWidth(CELL_WIDTH);
            grid.getColumnConstraints().add(col1);
        }

        // setting rows' sizes
        for(int i = 0; i < y_len + 1; i++)
        {
            RowConstraints row1 = new RowConstraints();
            row1.setMaxHeight(CELL_HEIGHT);
            row1.setMinHeight(CELL_HEIGHT);
            grid.getRowConstraints().add(row1);
        }
    }

    public void createAndAddElements(GridPane grid, IWorldMap map, IEngine engine)
    {
        // adding buttons to button lists in engines (to make highlighting animals with dominant genotype easier)
        engine.clearAnimalButtons();
        // filling map
        for(int i = 0; i < x_len; i++)
            for(int j = 0; j < y_len; j++)
            {
                // buttons[i][j] refers to position (i + lower_corner.x, j + lower_corner.y) on the map
                Vector2d position = new Vector2d(lower_corner.x + i, lower_corner.y + j);
                Object obj = map.objectAt(position);

                if (obj != null)
                {
                    Button button = GuiElementButton.createElement((IMapElement) obj, CELL_WIDTH, CELL_HEIGHT, startEnergy, engine);

                    GridPane.setHalignment(button, HPos.CENTER);
                    grid.add(button, i + 1, y_len - j);
                    if (obj instanceof Animal animal)
                        engine.addAnimalButton(button, animal);
                }
                // to symbolize jungle
                else if (position.follows(jungleLowerCorner) && position.precedes(jungleUpperCorner.subtract(new Vector2d(1, 1))))
                {
                    Shape jungleField = new Rectangle(CELL_WIDTH, CELL_HEIGHT);
                    jungleField.setFill(Color.color(0, 128.0/256.0, 0));

                    GridPane.setHalignment(jungleField, HPos.CENTER);
                    grid.add(jungleField, i + 1, y_len - j);
                }
            }
    }

    private void addPlots()
    {
        // credit to http://tutorials.jenkov.com/javafx/linechart.html

        LinearPlot boundedPlot = new LinearPlot("Bounded Map");
        LinearPlot unboundedPlot = new LinearPlot("Unbounded Map");

        boundedEngine.setLinearPlot(boundedPlot);
        unboundedEngine.setLinearPlot(unboundedPlot);

        boundedPlot.addPlotToHBox(boundedHBox);
        unboundedPlot.addPlotToHBox(unboundedHBox);
    }

    private void addTexts()
    {
        Text boundedText = new Text();
        Text unboundedText = new Text();

        boundedEngine.setTextInfo(boundedText);
        unboundedEngine.setTextInfo(unboundedText);

        boundedHBox.getChildren().add(boundedText);
        unboundedHBox.getChildren().add(unboundedText);
    }

    private void startEngines()
    {
        synchronized (this)
        {
            while (!mapsAndEnginesCreated) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Thread boundedThread = new Thread(() -> {
            try {
                while (true) {
                    boundedEngine.run();
                }
            } catch(IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        });

        Thread unboundedThread = new Thread(() -> {
            try {
                while (true) {
                    unboundedEngine.run();
                }
            } catch(IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        });

        boundedThread.setDaemon(true);
        boundedThread.start();

        unboundedThread.setDaemon(true);
        unboundedThread.start();
    }

    private void customizeExit(Stage mapStage)
    {
        mapStage.setOnCloseRequest(e -> {
            boundedEngine.saveAverageDataToFile();
            unboundedEngine.saveAverageDataToFile();
        });
    }
}
