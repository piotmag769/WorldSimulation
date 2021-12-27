package agh.ics.oop.api;

import agh.ics.oop.gui.App;
import javafx.application.Application;

import java.util.List;
import java.util.Random;

public class SimulationEngine implements Runnable, IEngine{
    private final IWorldMap map;
    private final boolean isMagical;
    private int magicalEventsHappened = 0;
    private Application app;

    public SimulationEngine(IWorldMap map, int animalsAtStart, boolean isMagical)
    {
        Vector2d upperBound = map.getUpperCorner();
        Vector2d lowerBound = map.getLowerCorner();

        if ((upperBound.y - lowerBound.y + 1)*(upperBound.x - lowerBound.x + 1) < animalsAtStart)
            throw new IllegalArgumentException("There is not enough place for animals on the map");

        Vector2d position;

        Random random = new Random();
        int startEnergy = map.getStartEnergy();

        this.map = map;
        this.isMagical = isMagical;

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
        if(isMagical && magicalEventsHappened < 3 && this.map.countAnimals() == 5)
        {
            // TODO communicate user with GUI about magical event
            System.out.println("Whoosh! Magic happened");
            this.map.conductMagicalEvent();
            magicalEventsHappened++;
        }

        this.map.letDayPass();
        System.out.println(map);
        System.out.println(map.countAnimals());
    }

    @Override
    public void setApp(App app)
    {
        this.app = app;
    }
}
