package agh.ics.oop.api;

public class Grass extends WorldMapElement {

    public Grass(Vector2d position)
    {
        this.position = position;
        this.orientation = MapDirection.GRASS;
    }
}
