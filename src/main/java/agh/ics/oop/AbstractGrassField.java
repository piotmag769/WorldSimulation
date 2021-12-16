package agh.ics.oop;
import java.util.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public abstract class AbstractGrassField implements IWorldMap, IPositionChangeObserver {
    //TODO all class

    // using google library - Multimap for storing different elements at the same position
    protected Multimap<Vector2d, IMapElement> elementMultimap = ArrayListMultimap.create();
    protected MapVisualizer MV = new MapVisualizer(this);
    protected Vector2d upper_corner;
    protected Vector2d lower_corner;
    // instead of jg ratio
    protected final Vector2d jungleLowerLeft; // inclusive
    protected final Vector2d jungleUpperRight; // exclusive


    public AbstractGrassField(int width, int height, double jungleRatio)
    {
        this.lower_corner = new Vector2d(0, 0);
        this.upper_corner = new Vector2d(width - 1, height - 1);
        this.jungleLowerLeft = new Vector2d((width - (int) jungleRatio*width)/2 , (height - (int) jungleRatio*height)/2);
        this.jungleUpperRight = new Vector2d(jungleLowerLeft.x + (int) jungleRatio*width, jungleLowerLeft.y + (int) jungleRatio*height);
    }

    abstract public boolean canMoveTo(Vector2d position);

    public boolean place(Animal animal)
    {
        Vector2d position = animal.getPosition();

        if (this.canMoveTo(position))
        {
            animal.addObserver(this);
            this.elementMultimap.put(position, animal);
            return true;
        }

        throw new IllegalArgumentException(animal.getPosition().toString() + " is not legal position");
    }


    public boolean isOccupied(Vector2d position)
    {
        return this.objectAt(position) != null;
    }


    public Object objectAt(Vector2d position)
    {
        Collection<IMapElement> elementList = this.elementMultimap.get(position);

        if (!elementList.isEmpty())
            return elementList.toArray()[0];

        return null;
    }

    @Override
    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition)
    {
        this.elementMultimap.remove(oldPosition, animal);
        this.elementMultimap.put(newPosition, animal);
    }

    public void let_day_pass()
    {
        this.remove_dead_ones();

        this.make_animals_move();

        this.make_grass_grow();
    }

    public void remove_dead_ones()
    {
        for(Vector2d key: elementMultimap.keySet())
        {
            Object[] animals_at_position = elementMultimap.get(key).toArray();

            for(Object animal: animals_at_position)
                if (((Animal) animal).getEnergy() == 0)
                    elementMultimap.remove(key, animal);
        }
    }

    public void make_animals_move()
    {
        for(Vector2d key: elementMultimap.keySet())
        {
            for(IMapElement element: elementMultimap.get(key))
            {
                if (element instanceof Animal animal)
                {
                    animal.loseEnergy();

                    int move = animal.chooseGen();
                    if (move == 0)
                        animal.move(MoveDirection.FORWARD);
                    else if (move == 4)
                        animal.move(MoveDirection.BACKWARD);
                    else
                        for (int i = 0; i < move; i++)
                            animal.move(MoveDirection.RIGHT);
                }
            }
        }
    }

    public void make_grass_grow()
    {
        int x, y;
        Vector2d position;
        Random random = new Random();

        if(!isJungleFull())
        {
            do {
                x = random.ints(1, jungleLowerLeft.x, jungleUpperRight.x).toArray()[0];
                y = random.ints(1, jungleLowerLeft.y, jungleUpperRight.y).toArray()[0];
                position = new Vector2d(x, y);
            } while (!elementMultimap.get(position).isEmpty());

            this.elementMultimap.put(position, new Grass(position));
        }

        //TODO
        if(!isStepFull())
        {
            do {
                x = random.ints(1, jungleLowerLeft.x, jungleUpperRight.x).toArray()[0];
                y = random.ints(1, jungleLowerLeft.y, jungleUpperRight.y).toArray()[0];
                position = new Vector2d(x, y);
            } while (!elementMultimap.get(position).isEmpty());

            this.elementMultimap.put(position, new Grass(position));
        }


    }

    private boolean isJungleFull()
    {
        for(int i = jungleLowerLeft.x; i < jungleUpperRight.x; i++)
            for(int j = jungleLowerLeft.y; j < jungleUpperRight.y; j++)
                if (elementMultimap.get(new Vector2d(i, j)).isEmpty())
                    return true;

        return false;
    }

    private boolean isStepFull()
    {
        for(int i = 0; i < jungleLowerLeft.x; i++)
            for(int j = 0; j < jungleLowerLeft.y; j++)
                if (elementMultimap.get(new Vector2d(i, j)).isEmpty())
                    return true;

        for(int i = jungleUpperRight.x; i < upper_corner.x; i++)
            for(int j = jungleUpperRight.y; j < upper_corner.y; j++)
                if (elementMultimap.get(new Vector2d(i, j)).isEmpty())
                    return true;

        return false;
    }

    @Override
    public String toString()
    {
        return this.MV.draw(this.lower_corner, this.upper_corner);
    }

    public Vector2d get_upper_corner()
    {
        return upper_corner;
    }

    public Vector2d get_lower_corner()
    {
        return lower_corner;
    }
}
