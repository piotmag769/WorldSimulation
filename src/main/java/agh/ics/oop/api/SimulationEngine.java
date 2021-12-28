package agh.ics.oop.api;

import agh.ics.oop.filemanagement.FileManager;
import agh.ics.oop.gui.App;
import agh.ics.oop.gui.LinearPlot;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SimulationEngine implements Runnable, IEngine{
    private final IWorldMap map;

    private final App app;
    private GridPane gridPane;
    private Text textInfo;
    private LinearPlot linearPlot;

    private final FileManager fileManager;
    private final List<Button> animalButtonsList = new ArrayList<>();
    private final List<Animal> animalsAssignedToButtons = new ArrayList<>();

    private boolean isStopped = false;
    private final boolean isMagical;

    private final int MOVE_DELAY = 1000;
    private int dayCount = 0;
    private int magicalEventsHappened = 0;

    // for tracked animal
    private boolean isAnimalTracked = false;

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
            this.map.conductMagicalEvent();
            magicalEventsHappened++;

            Platform.runLater(() -> {
                Scene scene = new Scene(new Label("Whoosh! Magic happened"));
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
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

        Platform.runLater(() -> {
            updateGui(statistic);
            updateTrackedInfo();
        });

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
    public void setTextInfo(Text textInfo) {
        this.textInfo = textInfo;
    }

    @Override
    public void addAnimalButton(Button button, Animal animal) {
        animalButtonsList.add(button);
        animalsAssignedToButtons.add(animal);
    }

    @Override
    public void clearAnimalButtons()
    {
        animalButtonsList.clear();
        animalsAssignedToButtons.clear();
    }

    @Override
    public void highlightDominantGenotypeAnimals() {
        int[] genotype = map.getDominantGenotype();
        for(int i = 0; i < animalButtonsList.size(); i++)
            if (Arrays.equals(animalsAssignedToButtons.get(i).getGenotype(), genotype))
                animalButtonsList.get(i).setStyle("-fx-background-color: #ff0000");
    }

    @Override
    public void setLinearPlot(LinearPlot linearPlot)
    {
        this.linearPlot = linearPlot;
    }

    private void updateDominantGenotype()
    {
        String string = Arrays.toString(map.getDominantGenotype());
        string = string.substring(1, string.length() - 1);

        StringBuilder res =  new StringBuilder();

        for(int i = 0; i < string.length(); i++)
        {
            char curr = string.charAt(i);
            if (curr != ' ' && curr != ',')
                res.append(curr);
        }

        textInfo.setText("Dominant genotype:\n" + res);
    }

    private void updateTrackedInfo()
    {
        if (!isAnimalTracked)
            return;

        textInfo.setText(textInfo.getText() + map.getTrackedInfo());
    }

    private void updateGui(Number[] statistic)
    {
        this.linearPlot.updatePlot(dayCount, statistic);

        updateDominantGenotype();

        this.gridPane.setGridLinesVisible(false);
        this.gridPane.getChildren().clear();
        this.app.createAndAddAxisLabels(this.gridPane);
        this.app.createAndAddElements(this.gridPane, this.map, this);
        this.gridPane.setGridLinesVisible(true);
    }

    @Override
    public void saveAverageDataToFile()
    {
        this.fileManager.writeDayToFile(linearPlot.getAverageData());
    }

    @Override
    public boolean isStopped()
    {
        return this.isStopped;
    }

    @Override
    public void startTrackingAnimal(Animal animal) {
        animal.setTrackedAncestor(animal);
        isAnimalTracked = true;
        map.resetTrackedValues(animal);
    }
}
