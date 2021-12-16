package agh.ics.oop;

public class Grass extends AbstractWorldMapElement{

    public Grass(Vector2d position)
    {
        this.position = position;
        this.orientation = MapDirection.GRASS;
    }

    public static String getImagePath()
    {
        return "/grass.jpg";
    }
}
