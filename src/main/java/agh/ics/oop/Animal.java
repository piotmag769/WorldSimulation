package agh.ics.oop;

import java.util.*;

public class Animal extends WorldMapElement {
    private final IWorldMap map;
    private final List<IPositionChangeObserver> observers= new ArrayList<>();
    private int energy;
    private final int[] genotype;

    // constructor related to first animals
    public Animal(IWorldMap map, Vector2d position, int StartEnergy)
    {
        Random random = new Random();
        MapDirection[] temp = MapDirection.values();
        this.orientation = temp[random.nextInt(temp.length - 1)]; // without grass
        this.map = map;
        this.position = position;
        // genotype of length 32 - sorted genes from 0 to 7
        this.genotype = Arrays.stream(random.ints(32, 0, 8).toArray()).sorted().toArray();
        this.energy = StartEnergy;
    }

    // constructor related to magical event animals
    public Animal(Animal animal, Vector2d position, int startEnergy)
    {
        Random random = new Random();
        MapDirection[] temp = MapDirection.values();
        this.orientation = temp[random.nextInt(temp.length - 1)];
        this.map = animal.map; // class - passed by reference, but we want to get the reference
        this.position = position;
        this.genotype = Arrays.copyOf(animal.genotype, animal.genotype.length); // genotype has to be the same
        this.energy = startEnergy; // magical animals get full energy
    }

    // constructor related to procreation
    public Animal(IWorldMap map, Animal parent1, Animal parent2)
    {
        Random random = new Random();
        MapDirection[] temp = MapDirection.values();
        this.orientation = temp[random.nextInt(temp.length - 1)];
        this.map = map;
        this.position = parent1.position;

        // using energy to give a birth to a baby
        int E1 = parent1.energy/4;
        int E2 = parent2.energy/4;
        parent1.energy -= E1;
        parent2.energy -= E2;
        this.energy = (E1 + E2) / 4;

        int side = (random.nextInt() % 2); // side we cut dom genotype from -> 0 - left, 1 - right

        // procreation

        // choosing dominant animal
        Animal dom = parent2, sub = parent1;
        if (parent1.energy > parent2.energy)
        {
            dom = parent1;
            sub = parent2;
        }

        // calculating cut place
        int cut_place = dom.energy/(dom.energy + sub.energy) * 32;

        // genotype of length 32
        int[] first, second;

        // getting parts of genotypes to join
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

        // joining genotypes and sorting result
        int[] res = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, res, first.length, second.length);
        Arrays.sort(res);
        this.genotype = res;
    }

    public boolean isAt(Vector2d position)
    {
        return position.equals(this.position);
    }

    public void move(MoveDirection direction)
    {
        switch (direction) {
            case FORWARD -> {
                Vector2d newPosition = this.position.add(this.orientation.toUnitVector());
                // if all moves are allowed on this map - for unbounded grass field
                if(this.map.isNotBounded())
                {
                    newPosition = correctPositionForUnboundedMap(newPosition);
                    if (!this.map.canMoveTo(position))
                        throw new RuntimeException("move to " + newPosition + "not allowed on unbounded map - check your code");
                }

                if (this.map.canMoveTo(newPosition))
                {
                    this.positionChanged(this.position, newPosition);
                    this.position = newPosition;
                }
            }
            case BACKWARD -> {
                Vector2d newPosition = this.position.subtract(this.orientation.toUnitVector());

                if(this.map.isNotBounded())
                {
                    newPosition = correctPositionForUnboundedMap(newPosition);
                    if (!this.map.canMoveTo(position))
                        throw new RuntimeException("move to " + newPosition + "not allowed on unbounded map - check your code");
                }

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

    private Vector2d correctPositionForUnboundedMap(Vector2d newPosition)
    {
        int width = this.map.getUpperCorner().x - this.map.getLowerCorner().x + 1;
        int height =  this.map.getUpperCorner().y - this.map.getLowerCorner().y + 1;
        int x = (newPosition.x < this.map.getLowerCorner().x) ? newPosition.x + width: newPosition.x % width;
        int y = (newPosition.y < this.map.getLowerCorner().y) ? newPosition.y + height: newPosition.y % height;

        return new Vector2d(x, y);
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

    public int chooseGene()
    {
        return this.genotype[new Random().nextInt(this.genotype.length)];
    }

    public void loseEnergy(int energyLoss)
    {
        this.energy -= energyLoss; // daily energy decrease
    }

    public int getEnergy()
    {
        return energy;
    }

    public void gainEnergy(int energyGain)
    {
        this.energy += energyGain;
    }
}
