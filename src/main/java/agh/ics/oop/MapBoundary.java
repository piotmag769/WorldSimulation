package agh.ics.oop;

import java.util.SortedSet;
import java.util.TreeSet;

public class MapBoundary implements IPositionChangeObserver{

    private SortedSet<IMapElement> x_axis_set = new TreeSet<>(new MapElementComparatorX());
    private SortedSet<IMapElement> y_axis_set = new TreeSet<>(new MapElementComparatorY());

    @Override
    public void positionChanged(IMapElement element, Vector2d oldPosition, Vector2d newPosition)
    {
        this.x_axis_set.remove(element);
        this.y_axis_set.remove(element);
        element.setPosition(newPosition);
        this.x_axis_set.add(element);
        this.y_axis_set.add(element);
        element.setPosition(oldPosition);
    }

    public void add_to_sets(IMapElement element)
    {
        this.x_axis_set.add(element);
        this.y_axis_set.add(element);
    }

    public Vector2d getUpperCorner()
    {
        return new Vector2d(this.x_axis_set.last().getPosition().x, this.y_axis_set.last().getPosition().y);
    }

    public Vector2d getLowerCorner()
    {
        return new Vector2d(this.x_axis_set.first().getPosition().x, this.y_axis_set.first().getPosition().y);
    }
}
