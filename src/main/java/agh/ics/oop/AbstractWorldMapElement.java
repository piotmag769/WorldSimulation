package agh.ics.oop;

public abstract class AbstractWorldMapElement implements IMapElement{
    protected Vector2d position;
    protected MapDirection orientation;

    public Vector2d getPosition()
    {
        return this.position;
    }

    public MapDirection getOrientation()
    {
        return this.orientation;
    }

    public void setPosition(Vector2d position)
    {
        this.position = position;
    }
}
