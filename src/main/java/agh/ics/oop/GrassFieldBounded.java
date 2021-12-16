package agh.ics.oop;

public class GrassFieldBounded extends AbstractGrassField {

    public GrassFieldBounded(int width, int height, double jungleRatio)
    {
        super(width, height, jungleRatio);
    }

    @Override
    public boolean canMoveTo(Vector2d position)
    {
        return position.upperRight(upper_corner).equals(upper_corner) && position.lowerLeft(lower_corner).equals(lower_corner);
    }
}
