package agh.ics.oop;
import java.util.*;

public abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver {

    protected Map<Vector2d, IMapElement> hashmap = new LinkedHashMap<>();
    protected MapVisualizer MV = new MapVisualizer(this);
    protected Vector2d upper_corner;
    protected Vector2d lower_corner;

    abstract public boolean canMoveTo(Vector2d position);

    abstract public void update_bounds();

    public boolean place(Animal animal)
    {
        Vector2d position = animal.getPosition();

        if (this.canMoveTo(position))
        {
            animal.addObserver(this);
            this.hashmap.remove(position);
            this.hashmap.put(position, animal);
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
        return this.hashmap.get(position);
    }

    @Override
    public void positionChanged(IMapElement element, Vector2d oldPosition, Vector2d newPosition)
    {
        this.hashmap.remove(oldPosition);
        this.hashmap.remove(newPosition);  // remove possible grass
        this.hashmap.put(newPosition, element);
    }

    @Override
    public String toString()
    {
        this.update_bounds();
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
