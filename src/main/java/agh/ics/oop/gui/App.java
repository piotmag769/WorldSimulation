package agh.ics.oop.gui;

import agh.ics.oop.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class App extends Application{
    // temporary fields to simplify the code
    private Vector2d lower_corner;
    private Vector2d upper_corner;

    private int x_len;
    private int y_len;

    private IWorldMap map;
    private int moveDelay = 1000;
    private SimulationEngine engine;
    public GridPane grid;
    public Stage primaryStage;
    public int energyLoss;


    //TODO all class
    @Override
    public void init() throws Exception
    {
        try
        {
            ArrayList<MoveDirection> directions = OptionsParser.parse(getParameters().getRaw());
            GrassFieldBounded map = new GrassFieldBounded(3, 5, 0.5, 1, 2, 8);
            Vector2d[] positions = {new Vector2d(4, 4), new Vector2d(7, 7)};

            this.engine = new SimulationEngine(directions, map, positions);
            this.engine.setApp(this);
            this.map = map;

            // temporary fields to simplify the code
            this.lower_corner = map.getLowerCorner();
            this.upper_corner = map.getUpperCorner();

            this.x_len = upper_corner.x - lower_corner.x + 1;
            this.y_len = upper_corner.y - lower_corner.y + 1;


        }
        catch (IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        // get data from user

        //gui part
        this.primaryStage = primaryStage;

        this.grid = new GridPane();

        grid.setGridLinesVisible(true);

        this.create_and_add_axis_labels(grid);

        this.create_and_add_elements(grid);

        this.set_col_row_sizes(grid);

        Scene scene = new Scene(grid, 500, 500);

        primaryStage.setScene(scene);
        primaryStage.show();

//        Thread engineThread = new Thread(new Runnable(){
//            @Override
//            public void run()
//            {
//                for(int i = 0; i < engine.getDirectionLength(); i++)
//                {
//                    try {
//                        Thread.sleep(moveDelay);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    Platform.runLater(engine);
//                }
//            }
//        }
//        );
//
//        engineThread.setDaemon(true);
//        engineThread.start();
    }

    public void create_and_add_axis_labels(GridPane grid)
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

    public void create_and_add_elements(GridPane grid)
    {

        // filling map
        for(int i = 0; i < x_len; i++)
            for(int j = 0; j < y_len; j++)
            {
                // buttons[i][j] refers to position (i + lower_corner.x, j + lower_corner.y) on the map
                Object obj = map.objectAt(new Vector2d(lower_corner.x + i, lower_corner.y + j));

                if (obj != null)
                {
                    Labeled node = GuiElementButton.createElement((IMapElement) obj);
                    GridPane.setHalignment(node, HPos.CENTER);
                    grid.add(node, i + 1, y_len - j);
                }
            }

        TextField textField = new TextField();
        grid.add(textField, 1, y_len + 1, 3, 1);

        Button btnStart=new Button("\u25B6");
        GridPane.setHalignment(btnStart, HPos.CENTER);
        grid.add(btnStart, 0, y_len + 1);

        btnStart.setOnAction(new EventHandler<javafx.event.ActionEvent>()
            {
                @Override public void handle (ActionEvent e)
                {
                    try {
                        String[] args = textField.getText().split(" ");
                        engine.setDirections(OptionsParser.parse(List.of(args)));
                        Thread engineThread = new Thread(new Runnable(){
                            @Override
                            public void run()
                            {
                                for(int i = 0; i < engine.getDirectionLength(); i++)
                                {
                                    try {
                                        Thread.sleep(moveDelay);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Platform.runLater(engine);
                                }
                            }
                        }
                        );

                        engineThread.setDaemon(true);
                        engineThread.start();
                    }
                    catch(IllegalArgumentException exception)
                    {
                        System.out.println(exception.getMessage());
                    }
                }
            }
        );

    }

    public void set_col_row_sizes(GridPane grid)
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
        for(int i = 0; i < y_len + 1 + 1 /* plus button row */; i++)
        {
            RowConstraints row1 = new RowConstraints();
            row1.setPercentHeight(100);
//            row1.setFillHeight(Boolean.TRUE);
            grid.getRowConstraints().add(row1);

        }
    }
}
