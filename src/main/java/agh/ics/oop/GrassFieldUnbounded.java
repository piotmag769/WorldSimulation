package agh.ics.oop;

public class GrassFieldUnbounded extends AbstractGrassField {

    public GrassFieldUnbounded(int width, int height, double jungleRatio)
    {
        super(width, height, jungleRatio);
    }

    @Override
    public boolean canMoveTo(Vector2d position)
    {
        return true;
    }
}
