package agh.ics.oop.api;

import java.util.List;


public interface IWorldMap {

    boolean canMoveTo(Vector2d position);

    boolean place(Animal animal);

    boolean isOccupied(Vector2d position);

    Object objectAt(Vector2d position);

    Vector2d getUpperCorner();

    Vector2d getLowerCorner();

    Vector2d getJungleUpperCorner();

    Vector2d getJungleLowerCorner();

    int getAnimalsPassed();

    int getStartEnergy();

    int getNumberOfPlants();

    double calculateAverageEnergy();

    double calculateAverageLifetime();

    double calculateAverageChildrenAmount();

    int[] getDominantGenotype();

    void letDayPass();

    boolean isNotBounded();

    void conductMagicalEvent();

    void setTrackedAnimal(Animal animal);

    List<Vector2d> getFreeAreaFrom(boolean fromAllMap, boolean isJungle);
}