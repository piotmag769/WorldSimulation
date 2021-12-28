package agh.ics.oop.api;

import agh.ics.oop.filemanagement.FileManager;
import agh.ics.oop.gui.App;
import agh.ics.oop.gui.LinearPlot;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SimulationEngine implements Runnable, IEngine{
    private final IWorldMap map;
    private final boolean isMagical;
    private int magicalEventsHappened = 0;
    private final App app;
    private GridPane gridPane;
    private VBox trackingBox;
    private FileManager fileManager;
    private boolean isStopped = false;
    private final int MOVE_DELAY = 1000;
    private LinearPlot linearPlot;
    private int dayCount = 0;

    public SimulationEngine(IWorldMap map, int animalsAtStart, boolean isMagical, App app, String filename)
    {
        Vector2d upperBound = map.getUpperCorner();
        Vector2d lowerBound = map.getLowerCorner();

        if ((upperBound.y - lowerBound.y + 1)*(upperBound.x - lowerBound.x + 1) < animalsAtStart)
            throw new IllegalArgumentException("*************\nThere is not enough place for animals on the map\n*************");

        Vector2d position;

        Random random = new Random();
        int startEnergy = map.getStartEnergy();

        this.map = map;
        this.isMagical = isMagical;
        this.app = app;
        this.fileManager = new FileManager(filename);

        List<Vector2d> freeArea = map.getFreeAreaFrom(true, false);

        for(int i = 0; i < animalsAtStart; i++)
        {
            // each animal has to be at different position at the start
            position = freeArea.get(random.nextInt(freeArea.size()));

            this.map.place(new Animal(map, position, startEnergy));

            freeArea.remove(position);
        }
    }

    @Override
    public void run()
    {
        try {
            Thread.sleep(MOVE_DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (this)
        {
            while(isStopped) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // conduct magical event if conditions are met
        if(isMagical && magicalEventsHappened < 3 && this.map.getAnimalsPassed() == 5)
        {
            System.out.println("Whoosh! Magic happened");
            this.map.conductMagicalEvent();
            magicalEventsHappened++;
            Platform.runLater(() -> {
                // TODO communicate user with GUI about magical event
            });
        }

        this.map.letDayPass();
        dayCount++;

        Number[] statistic =new Number[]{
                map.getAnimalsPassed(),
                map.getNumberOfPlants(),
                map.calculateAverageEnergy(),
                map.calculateAverageLifetime(),
                map.calculateAverageChildrenAmount()
        };

        Platform.runLater(() -> updateGui(statistic));
        fileManager.writeDayToFile(statistic);
    }

    @Override
    public void setGridPane(GridPane gridPane)
    {
        this.gridPane = gridPane;
    }

    @Override
    public void changeEngineState()
    {
        synchronized (this)
        {
            this.isStopped = !isStopped;
            notifyAll();
        }
    }

    @Override
    public void setLinearPlot(LinearPlot linearPlot)
    {
        this.linearPlot = linearPlot;
    }

    @Override
    public void setTrackingBox(VBox trackingBox) {
        this.trackingBox = trackingBox;
    }

    private void updateDominantGenotype()
    {
        trackingBox.getChildren().clear();

        String string = Arrays.toString(map.getDominantGenotype());
        string = string.substring(1, string.length() - 1);

        StringBuilder res =  new StringBuilder();

        for(int i = 0; i < string.length(); i++)
        {
            char curr = string.charAt(i);
            if (curr != ' ' && curr != ',')
                res = res.append(curr);
        }

        Text text = new Text("Dominant genotype:\n" + res);

        trackingBox.getChildren().add(text);
    }

    private void updateGui(Number[] statistic)
    {
        this.linearPlot.updatePlot(dayCount, statistic);

        updateDominantGenotype();

        this.gridPane.setGridLinesVisible(false);
        this.gridPane.getChildren().clear();
        this.app.createAndAddAxisLabels(this.gridPane);
        this.app.createAndAddElements(this.gridPane, this.map);
        this.gridPane.setGridLinesVisible(true);
    }
}
