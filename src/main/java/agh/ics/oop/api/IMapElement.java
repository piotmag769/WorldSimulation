package agh.ics.oop.api;

public interface IMapElement {

    Vector2d getPosition();

    MapDirection getOrientation();

    void setPosition(Vector2d position);
}
