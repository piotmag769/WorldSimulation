package agh.ics.oop.api;

import agh.ics.oop.gui.LinearPlot;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public interface IEngine {

    void run();

    void setGridPane(GridPane gridPane);

    void setLinearPlot(LinearPlot linearPlot);

    void changeEngineState();

    void setTrackingBox(VBox trackingBox);
}
