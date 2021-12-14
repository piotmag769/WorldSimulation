package agh.ics.oop;

public interface IPositionChangeObserver {

    void positionChanged(IMapElement element, Vector2d oldPosition, Vector2d newPosition);
}
