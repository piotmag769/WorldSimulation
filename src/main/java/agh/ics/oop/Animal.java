package agh.ics.oop;

import java.util.*;

public class Animal extends AbstractWorldMapElement{
    private IWorldMap map;
    private List<IPositionChangeObserver> observers= new ArrayList<>();
    /*private*/ public int energy;
    private static int energyLoss;
    private final int[] genotype;

    // constructor related to first animals or magical event animals
    public Animal(IWorldMap map, Vector2d position, int StartEnergy)
    {
        Random random = new Random();
        MapDirection[] temp = MapDirection.values();
        this.orientation = temp[random.ints(1, 0, temp.length - 1).toArray()[0]]; // without grass
        this.map = map;
        this.position = position;
        this.genotype = Arrays.stream(random.ints(32, 0, 8).toArray()).sorted().toArray();
        // genotype of length 32 - sorted genes from 0 to 7
        this.energy = StartEnergy;
    }

    public Animal(IWorldMap map, Animal parent1, Animal parent2)
    {
        Random random = new Random();
        MapDirection[] temp = MapDirection.values();
        this.orientation = temp[random.ints(1, 0, temp.length - 1).toArray()[0]];
        this.map = map;
        this.position = parent1.position;
        // using energy to give a birth to a baby
        int E1 = parent1.energy/4;
        int E2 = parent2.energy/4;
        parent1.energy -= E1;
        parent2.energy -= E2;
        this.energy = (E1 + E2) / 4;
        //

        int side = (random.nextInt() % 2); // side we cut dom genotype from -> 0 - left, 1 - right

        // procreation
        Animal dom = parent2, sub = parent1;
        if (parent1.energy > parent2.energy)
        {
            dom = parent1;
            sub = parent2;
        }

        int cut_place = dom.energy/(dom.energy + sub.energy) * 32;

        int[] first, second;
        //genotype of length 32
        if (side == 0)
        {
            first = Arrays.copyOfRange(dom.genotype, 0, cut_place);
            second = Arrays.copyOfRange(sub.genotype, cut_place, 32);
        }
        else
        {
            first = Arrays.copyOfRange(sub.genotype, 0, 32 - cut_place);
            second = Arrays.copyOfRange(dom.genotype, 32 - cut_place, 32);
        }

        int[] res = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, res, first.length, second.length);
        Arrays.sort(res);
        this.genotype = res;
    }

    public boolean isAt(Vector2d position)
    {
        return position.equals(this.position);
    }

    //TODO move
    public void move(MoveDirection direction)
    {
        switch (direction) {
            case FORWARD -> {
                Vector2d newPosition = this.position.add(this.orientation.toUnitVector());
                if (this.map.canMoveTo(newPosition))
                {
                    this.positionChanged(this.position, newPosition);
                    this.position = newPosition;
                }
            }
            case BACKWARD -> {
                Vector2d newPosition = this.position.subtract(this.orientation.toUnitVector());
                if (this.map.canMoveTo(newPosition))
                {
                    this.positionChanged(this.position, newPosition);
                    this.position = newPosition;
                }
            }
            case RIGHT -> this.orientation = this.orientation.next();
            case LEFT -> this.orientation = this.orientation.previous();
        }
    }

    public void addObserver(IPositionChangeObserver observer)
    {
        this.observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer)
    {
        this.observers.remove(observer);
    }

    private void positionChanged(Vector2d oldPosition, Vector2d newPosition)
    {
        for(IPositionChangeObserver observer: observers)
            observer.positionChanged(this, oldPosition, newPosition);
    }

    public int chooseGen()
    {
        return this.genotype[new Random().ints(1, 0, this.genotype.length).toArray()[0]];
    }

    public void loseEnergy()
    {
        this.energy -= energyLoss; // daily energy decrease
    }

    public void setEnergyLoss(int energyLoss)
    {
        Animal.energyLoss = energyLoss;
    }

    public int getEnergy()
    {
        return energy;
    }
}
