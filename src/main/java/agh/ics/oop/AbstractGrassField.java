package agh.ics.oop;
import java.util.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


public abstract class AbstractGrassField implements IWorldMap, IPositionChangeObserver {
    // using guava (google library) Multimap for storing different elements at the same position
    protected Multimap<Vector2d, IMapElement> elementMultimap = ArrayListMultimap.create();
    // list for making animals move
    protected List<Animal> animalList = new ArrayList<>();
    protected MapVisualizer MV = new MapVisualizer(this);
    protected Vector2d upperCorner; // inclusive
    protected Vector2d lowerCorner; // inclusive
    // deriving from jg ratio
    protected final Vector2d jungleLowerLeft; // inclusive
    protected final Vector2d jungleUpperRight; // exclusive
    protected final int energyLoss;
    protected final int energyGain;
    protected final int startEnergy;


    public AbstractGrassField(int width, int height, double jungleRatio, int energyLoss, int energyGain, int startEnergy)
    {
        this.lowerCorner = new Vector2d(0, 0);
        this.upperCorner = new Vector2d(width - 1, height - 1);
        // care, casting
        this.jungleLowerLeft = new Vector2d((int) (jungleRatio*width/2), (int) (jungleRatio*height/2));
        this.jungleUpperRight = new Vector2d(jungleLowerLeft.x + (int) (jungleRatio*width), jungleLowerLeft.y + (int) (jungleRatio*height));
        this.energyGain = energyGain;
        this.energyLoss = energyLoss;
        this.startEnergy = startEnergy;
    }

    @Override
    public boolean canMoveTo(Vector2d position)
    {
        return position.precedes(upperCorner) && position.follows(lowerCorner);
    }

    @Override
    public boolean place(Animal animal)
    {
        Vector2d position = animal.getPosition();

        if (this.canMoveTo(position))
        {
            animal.addObserver(this);
            this.elementMultimap.put(position, animal);
            this.animalList.add(animal);
            return true;
        }

        throw new IllegalArgumentException(animal.getPosition().toString() + " is not legal position");
    }

    @Override
    public boolean isOccupied(Vector2d position)
    {
        return this.objectAt(position) != null;
    }

    @Override
    public Object objectAt(Vector2d position)
    {
        Collection<IMapElement> elementList = this.elementMultimap.get(position);

        if (!elementList.isEmpty())
            return elementList.toArray()[0];

        return null;
    }

    @Override
    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition)
    {
        this.elementMultimap.remove(oldPosition, animal);
        this.elementMultimap.put(newPosition, animal);
    }

    public void let_day_pass()
    {
        this.removeDeadOnes();

        this.makeAnimalsMove();

        this.eatGrass();

        this.copulate();

        this.makeGrassGrow();
    }

    public void removeDeadOnes()
    {
        // care about concurrent modification exception
        Object[] keySet = elementMultimap.keySet().toArray();
        for(Object key: keySet)
        {
            // to create copy (toArray creates copy) to ensure iteration safety
            Object[] animalsAtPosition = elementMultimap.get((Vector2d) key).toArray();

            for(Object element: animalsAtPosition)
                if (element instanceof Animal && ((Animal) element).getEnergy() == 0)
                {
                    elementMultimap.remove((Vector2d) key, element);
                    animalList.remove(element);
                }
        }
    }

    public void makeAnimalsMove()
    {
        for(Animal animal: animalList)
        {
            animal.loseEnergy(energyLoss);

            int move = animal.chooseGene();
            if (move == 0)
                animal.move(MoveDirection.FORWARD);
            else if (move == 4)
                animal.move(MoveDirection.BACKWARD);
            else {
                for (int i = 0; i < move; i++)
                    animal.move(MoveDirection.RIGHT);
            }
        }
    }

    public void eatGrass()
    {
        boolean isGrassOnTheField;
        int maxEnergy, energyIncrease;
        for(Vector2d key: elementMultimap.keySet())
        {
            // to create copy (toArray creates copy) to ensure iteration safety
            Object[] elementsAtPosition = elementMultimap.get(key).toArray();
            isGrassOnTheField = false;

            // if there is a possibility of grass and animal being on the field (to not remove grass without reason)
            if (elementsAtPosition.length > 1)
            {
                // seek for grass
                for (Object element : elementsAtPosition)
                    if (element instanceof Grass)
                    {
                        isGrassOnTheField = true;
                        elementMultimap.remove(key, element);
                        break;
                    }

                // make the animal(s) eat grass
                if (isGrassOnTheField)
                {
                    List<Animal> animalsGettingFood = new ArrayList<>();
                    maxEnergy = 0;

                    // seeking for animals with maximum energy
                    for (Object element : elementMultimap.get(key))
                        maxEnergy = Math.max(maxEnergy, ((Animal) element).getEnergy());

                    for (Object element : elementMultimap.get(key))
                        if (((Animal) element).getEnergy() == maxEnergy)
                            animalsGettingFood.add((Animal) element);

                    // and parting food between them
                    energyIncrease = energyGain / animalsGettingFood.size();
                    for (Animal animal : animalsGettingFood)
                        animal.gainEnergy(energyIncrease);
                }
            }
        }
    }

    public void copulate()
    {
        int animalWithLessEnergy;
        for(Vector2d key: elementMultimap.keySet())
            // if there is more than one animal on the field
            if (elementMultimap.get(key).size() > 1)
            {
                Object[] elementsAtPosition = elementMultimap.get(key).toArray();

                Animal[] animalsCopulating = new Animal[]{(Animal) elementsAtPosition[0], (Animal) elementsAtPosition[1]};
                animalWithLessEnergy = (animalsCopulating[0].getEnergy() > animalsCopulating[1].getEnergy()) ? 0 : 1;

                // looking for animals with maximum energy
                for(int i = 2; i < elementsAtPosition.length; i++)
                {
                    if (((Animal) elementsAtPosition[i]).getEnergy() > animalsCopulating[animalWithLessEnergy].getEnergy())
                    {
                        animalsCopulating[animalWithLessEnergy] = (Animal) elementsAtPosition[i];
                        animalWithLessEnergy = (animalsCopulating[0].getEnergy() > animalsCopulating[1].getEnergy()) ? 0 : 1;
                    }
                }

                // creating new animal if conditions for copulation are met
                if (animalsCopulating[0].getEnergy() > startEnergy/2 && animalsCopulating[0].getEnergy() > startEnergy/2)
                    elementMultimap.put(key, new Animal(this, animalsCopulating[0], animalsCopulating[1]));
            }
    }

    public void makeGrassGrow()
    {
        Random random = new Random();

        List<Vector2d> freeFields;
        Vector2d position;

        for(int i = 0; i < 2; i++)
        {
            freeFields = getFreeAreaFrom(i == 0);
            if (freeFields.size() != 0)
            {
                position = freeFields.get(random.nextInt(freeFields.size()));
                this.elementMultimap.put(position, new Grass(position));
            }
        }
    }

    // as jungleUpperRight is exclusive
    private boolean isInJungle(Vector2d position)
    {
        return position.follows(jungleLowerLeft) && position.precedes(jungleUpperRight.subtract(new Vector2d(1, 1)));
    }

    private List<Vector2d> getFreeAreaFrom(boolean isJungle)
    {
        List<Vector2d> result = new ArrayList<>();

        for(int i = 0; i < upperCorner.x + 1; i++)
            for(int j = 0; j < upperCorner.y + 1; j++)
            {
                Vector2d position = new Vector2d(i, j);
                if (isJungle == isInJungle(position) && !isOccupied(position))
                    result.add(position);
            }

        return result;
    }

    @Override
    public String toString()
    {
        return this.MV.draw(this.lowerCorner, this.upperCorner);
    }

    @Override
    public Vector2d getUpperCorner()
    {
        return upperCorner;
    }

    @Override
    public Vector2d getLowerCorner()
    {
        return lowerCorner;
    }
}
