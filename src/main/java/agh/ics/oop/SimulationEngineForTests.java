package agh.ics.oop;

import agh.ics.oop.gui.App;

import java.util.ArrayList;
import java.util.List;

public class SimulationEngineForTests implements IEngine, Runnable{
    private List<MoveDirection> directions;
    private final IWorldMap map;
    private final List<Animal> animals;
    private App app;
    private int i = 0;

    public SimulationEngineForTests(List<MoveDirection> directions, IWorldMap map, Vector2d[] positions)
    {
        this.directions = directions;
        this.map = map;
        this.animals = new ArrayList<>();

        if (positions.length > 0)
            for (Vector2d position: positions)
            {
                Animal zwierzomtko = new Animal(map, position, 5);
                this.map.place(zwierzomtko);
                this.animals.add(zwierzomtko);
            }
        System.out.println(map);
    }

    public List<Animal> list_animals()
    {
        return this.animals;
    }

    @Override
    public void run()
    {
        int n = this.animals.size();

        this.animals.get(i % n).move(directions.get(i));

        app.grid.setGridLinesVisible(false);
        app.grid.getChildren().clear();

        app.grid.setGridLinesVisible(true);
        app.create_and_add_axis_labels(app.grid);

        app.create_and_add_elements(app.grid);

        System.out.println(this.map);
        i++;
    }

    public void setApp(App app)
    {
        this.app = app;
    }

    public int getDirectionLength()
    {
        return this.directions.size();
    }

    public void setDirections(List<MoveDirection> directions)
    {
        this.directions = directions;
        this.i = 0;
    }
}
