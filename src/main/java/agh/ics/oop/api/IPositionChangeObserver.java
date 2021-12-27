package agh.ics.oop.api;

public interface IPositionChangeObserver {

    void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition);
}
