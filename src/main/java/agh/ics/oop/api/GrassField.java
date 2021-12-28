package agh.ics.oop.api;
import java.util.*;
import java.util.stream.IntStream;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


public class GrassField implements IWorldMap, IPositionChangeObserver {
    // using guava (google library) Multimap for storing different elements at the same position
    private final Multimap<Vector2d, IMapElement> elementMultimap = ArrayListMultimap.create();
    // list for making animals move
    private final List<Animal> animalList = new ArrayList<>();
    private final MapVisualizer MV = new MapVisualizer(this);
    private final Vector2d upperCorner; // inclusive
    private final Vector2d lowerCorner; // inclusive
    // deriving from jg ratio
    private final Vector2d jungleLowerLeft; // inclusive
    private final Vector2d jungleUpperRight; // exclusive

    private Animal trackedAnimal;

    // can be public because it's final anyways
    private final boolean isBounded;
    private final int energyLoss;
    private final int plantEnergy;
    private final int startEnergy;
    private int numberOfPlants = 0;
    private int animalsPassed = 0;
    private int summaryLifetime = 0;


    public GrassField(int width, int height, double jungleRatio, int energyLoss, int plantEnergy, int startEnergy, boolean isBounded)
    {
        this.lowerCorner = new Vector2d(0, 0);
        this.upperCorner = new Vector2d(width - 1, height - 1);
        // care, casting!!!
        this.jungleLowerLeft = new Vector2d((width - (int) (jungleRatio*width))/2, (height - (int) (jungleRatio*height))/2);
        this.jungleUpperRight = new Vector2d(jungleLowerLeft.x + (int) (jungleRatio*width), jungleLowerLeft.y + (int) (jungleRatio*height));

        this.plantEnergy = plantEnergy;
        this.energyLoss = energyLoss;
        this.startEnergy = startEnergy;
        this.isBounded = isBounded;
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
        Object[] elementList = this.elementMultimap.get(position).toArray();

        if (elementList.length == 0)
            return null;
        else if (elementList.length == 1)
            return elementList[0];
        else
        {
            Animal res = (Animal) elementList[0];
            // to return animal with maximum energy
            for(Object obj: elementList)
                res = (((Animal) obj).getEnergy() > res.getEnergy()) ? (Animal) obj : res;
            return res;
        }
    }

    @Override
    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition)
    {
        this.elementMultimap.remove(oldPosition, animal);
        this.elementMultimap.put(newPosition, animal);
    }

    @Override
    public void letDayPass()
    {
        this.makeAnimalsMove();

        this.eatGrass();

        this.copulate();

        this.makeGrassGrow();

        // they lose energy at the end of the day
        this.makeAnimalsLoseEnergy();

        this.removeDeadOnes();
    }

    private void removeDeadOnes()
    {
        // care about concurrent modification exception
        Object[] keySet = elementMultimap.keySet().toArray();
        for(Object key: keySet)
        {
            // to create copy (toArray creates copy) to ensure iteration safety
            Object[] animalsAtPosition = elementMultimap.get((Vector2d) key).toArray();

            for(Object element: animalsAtPosition)
                if (element instanceof Animal && ((Animal) element).getEnergy() <= 0)
                {
                    elementMultimap.remove((Vector2d) key, element);
                    summaryLifetime += ((Animal) element).getLifetime();
                    animalsPassed++;
                    animalList.remove(element);
                }
        }
    }

    private void makeAnimalsMove()
    {
        for(Animal animal: animalList)
        {
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

    private void eatGrass()
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
                        numberOfPlants--;
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
                    energyIncrease = plantEnergy / animalsGettingFood.size();
                    for (Animal animal : animalsGettingFood)
                        animal.gainEnergy(energyIncrease);
                }
            }
        }
    }

    private void copulate()
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
                if ((double) animalsCopulating[0].getEnergy() > (double) startEnergy/2.0 && (double) animalsCopulating[0].getEnergy() > (double) startEnergy/2.0)
                {
                    Animal animal = new Animal(this, animalsCopulating[0], animalsCopulating[1]);
                    elementMultimap.put(key, animal);
                    animalList.add(animal);

                    for(int i = 0; i < 2; i++)
                        animalsCopulating[i].increaseChildrenNumber();
                }
            }
    }

    private void makeGrassGrow()
    {
        Random random = new Random();

        List<Vector2d> freeFields;
        Vector2d position;

        for(int i = 0; i < 2; i++)
        {
            freeFields = getFreeAreaFrom(false,i == 0);
            if (freeFields.size() != 0)
            {
                position = freeFields.get(random.nextInt(freeFields.size()));
                this.elementMultimap.put(position, new Grass(position));
                this.numberOfPlants++;
            }
        }
    }

    private void makeAnimalsLoseEnergy()
    {
        for(Animal animal: animalList)
            animal.loseEnergy(energyLoss);
    }

    // as jungleUpperRight is exclusive
    private boolean isInJungle(Vector2d position)
    {
        return position.follows(jungleLowerLeft) && position.precedes(jungleUpperRight.subtract(new Vector2d(1, 1)));
    }

    @Override
    // second argument is effectively ignored if value of the first is true
    public List<Vector2d> getFreeAreaFrom(boolean fromAllMap, boolean isJungle)
    {
        List<Vector2d> result = new ArrayList<>();

        for(int i = 0; i < upperCorner.x + 1; i++)
            for(int j = 0; j < upperCorner.y + 1; j++)
            {
                Vector2d position = new Vector2d(i, j);
                if ((fromAllMap || isJungle == isInJungle(position)) && !isOccupied(position))
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

    @Override
    public Vector2d getJungleUpperCorner() {
        return this.jungleUpperRight;
    }

    @Override
    public Vector2d getJungleLowerCorner() {
        return this.jungleLowerLeft;
    }

    @Override
    public int getStartEnergy()
    {
        return this.startEnergy;
    }

    @Override
    public boolean isNotBounded()
    {
        return !isBounded;
    }

    @Override
    public void conductMagicalEvent()
    {
        Random random = new Random();
        Vector2d position;
        // i < 5 because it is specified that magical event happens when there are 5 animals on the map

        List<Vector2d> freeArea = getFreeAreaFrom(true, false);

        // throw exception when not possible to add animals
        if(freeArea.size() < 5)
            throw new IllegalStateException("Cannot conduct magical event - not enough places on the map");

        for(int i = 0; i < 5; i++)
        {
            position = freeArea.get(random.nextInt(freeArea.size()));

            place(new Animal(animalList.get(i), position, startEnergy));
            freeArea.remove(position);
        }
    }

    @Override
    public int getAnimalsPassed()
    {
        return animalList.size();
    }

    @Override
    public int getNumberOfPlants() {
        return numberOfPlants;
    }

    @Override
    public double calculateAverageEnergy() {
        if (animalList.size() == 0)
            return 0;

        double sum = 0;
        double size = animalList.size();
        for(Animal animal: animalList)
            sum += animal.getEnergy();

        return sum/size;
    }

    // if not specified (no one died) - set to 0
    @Override
    public double calculateAverageLifetime() {
        return (animalsPassed == 0) ? 0 : (double) summaryLifetime/ (double) animalsPassed;
    }

    @Override
    public double calculateAverageChildrenAmount() {
        if (animalList.size() == 0)
            return 0;

        int sum = 0;
        for(Animal animal: animalList)
            sum += animal.getNumberOfChildren();

        return (double) sum / (double) animalList.size();
    }

    @Override
    public int[] getDominantGenotype()
    {
        int[] res = new int[32];
        // if no animals on the map, return invalid genotype (filled with -1)
        if (animalList.size() == 0)
        {
            Arrays.fill(res, -1);
            return res;
        }

        Map<int[], Integer> hashMap = new HashMap<>();

        for(Animal animal: animalList)
        {
            int[] genotype = animal.getGenotype();

            if (!hashMap.containsKey(genotype))
                hashMap.put(genotype, 1);
            else
            {
                Integer count = hashMap.get(genotype);
                count++;
                hashMap.put(genotype, count);
            }
        }

        Integer max = 0;
        for(int[] key: hashMap.keySet())
        {
            Integer temp = hashMap.get(key);
            if (temp > max)
            {
                max = temp;
                res = key;
            }
        }

        return res;
    }

    @Override
    public void setTrackedAnimal(Animal animal)
    {
        this.trackedAnimal = animal;
    }
}
