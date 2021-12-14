package agh.ics.oop;

public class RectangularMap extends AbstractWorldMap{

    public RectangularMap(int width, int height)
    {
        this.upper_corner = new Vector2d(width - 1, height - 1);
        this.lower_corner = new Vector2d(0, 0);
    }

    @Override
    public void update_bounds(){}

    @Override
    public boolean canMoveTo(Vector2d position)
    {
        return !this.isOccupied(position) && position.upperRight(upper_corner).equals(upper_corner) && position.lowerLeft(lower_corner).equals(lower_corner);
    }
}
