package agh.ics.oop.api;

import agh.ics.oop.gui.LinearPlot;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public interface IEngine {

    void run();

    void setGridPane(GridPane gridPane);

    void setLinearPlot(LinearPlot linearPlot);

    void changeEngineState();

    void setText(Text text);

    void addAnimalButton(Button button, Animal animal);

    void clearAnimalButtons();

    void highlightDominantGenotypeAnimals();

    void saveAverageDataToFile();

    boolean isStopped();

    void startTrackingAnimal(Animal animal);
}
