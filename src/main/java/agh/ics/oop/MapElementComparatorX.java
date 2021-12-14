package agh.ics.oop;

import java.util.Comparator;

public class MapElementComparatorX implements Comparator<IMapElement> {

    @Override
    public int compare(IMapElement o1, IMapElement o2)
    {
        Vector2d p1 = o1.getPosition();
        Vector2d p2 = o2.getPosition();

        if (p1.x != p2.x)
            return p1.x - p2.x;

        return p1.y - p2.y;
    }
}
