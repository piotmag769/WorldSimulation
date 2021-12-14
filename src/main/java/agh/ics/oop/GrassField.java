package agh.ics.oop;

import static java.lang.Math.sqrt;

public class GrassField extends AbstractWorldMap{

    public MapBoundary boundary = new MapBoundary();

    public GrassField(int n)
    {
        int x, y;
        Vector2d temp;
        int bound = (int) sqrt(10*n);

        this.upper_corner = new Vector2d(0, 0);
        this.lower_corner = new Vector2d(bound, bound);

        for(int i = 0; i < n; i++)
        {
            do
            {
                x = (int)(Math.random()*(bound + 1));
                y = (int)(Math.random()*(bound + 1));
                temp = new Vector2d(x, y);
            } while(isOccupied(temp));

            Grass grass = new Grass(temp);
            this.hashmap.put(temp, grass);
            this.boundary.add_to_sets(grass);
        }

        this.update_bounds();
    }


    @Override
    public boolean place(Animal animal)
    {
        boolean check = super.place(animal);

        if (check)
        {
            // no need to remove from set, as potential grass is the same as new animal in terms of position
            boundary.add_to_sets(animal);
            animal.addObserver(boundary);
        }

        return true;
    }

    @Override
    public boolean canMoveTo(Vector2d position)
    {
        Object obj = this.objectAt(position);
        return obj == null || obj instanceof Grass;
    }


    @Override
    public void update_bounds()
    {
        this.upper_corner = this.boundary.getUpperCorner();
        this.lower_corner = this.boundary.getLowerCorner();
    }
}
