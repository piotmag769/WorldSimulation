import agh.ics.oop.Animal;
import agh.ics.oop.GrassField;
import agh.ics.oop.RectangularMap;
import agh.ics.oop.Vector2d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RectangularMapTest {
    @Test
    void canMovetoTest()
    {
        RectangularMap RM = new RectangularMap(5, 5);
        Animal A = new Animal(RM, new Vector2d(4, 3));
        RM.place(A);
        assertFalse(RM.canMoveTo(new Vector2d(4, 6)));
        assertTrue(RM.canMoveTo(new Vector2d(4, 4)));
    }

    @Test
    void place_plus_objectAtTest()
    {
        RectangularMap RM = new RectangularMap(5, 5);
        Animal A = new Animal(RM, new Vector2d(4, 3));
        RM.place(A);
        assertEquals(A, RM.objectAt(A.getPosition()));
    }

    @Test
    void isOccupiedTest()
    {
        RectangularMap RM = new RectangularMap(5, 5);
        Animal A = new Animal(RM, new Vector2d(4, 3));
        RM.place(A);
        assertTrue(RM.isOccupied(new Vector2d(4,3)));
    }

    @Test
    void place_exception() throws IllegalArgumentException
    {
        GrassField RM = new GrassField(10);
        Animal A = new Animal(RM, new Vector2d(4, 3));
        RM.place(A);
        try {
            RM.place(A);
        }
        catch (IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
