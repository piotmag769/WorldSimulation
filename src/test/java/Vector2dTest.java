import agh.ics.oop.Vector2d;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class Vector2dTest {

    @Test
    void test_toString()
    {
        Assertions.assertEquals(new Vector2d(1, 5).toString(), "(1,5)");
    }

    @Test
    void test_preceeds()
    {
        Assertions.assertFalse(new Vector2d(2, 6).precedes(new Vector2d(1, 5)));
        Assertions.assertTrue(new Vector2d(1, 5).precedes(new Vector2d(2, 6)));
        Assertions.assertTrue(new Vector2d(3, 4).precedes(new Vector2d(3, 4)));
    }

    @Test
    void test_follows()
    {
        Assertions.assertFalse(new Vector2d(1, 5).follows(new Vector2d(2, 6)));
        Assertions.assertTrue(new Vector2d(2, 6).follows(new Vector2d(1, 5)));
        Assertions.assertTrue(new Vector2d(3, 4).follows(new Vector2d(3, 4)));
    }

    @Test
    void test_upperRight()
    {
        Assertions.assertEquals(new Vector2d(1, 5).upperRight(new Vector2d(-1, 6)), new Vector2d(1, 6));
    }

    @Test
    void test_lowerLeft()
    {
        Assertions.assertEquals(new Vector2d(1, 5).lowerLeft(new Vector2d(-1, 6)), new Vector2d(-1, 5));
    }

    @Test
    void test_add()
    {
        Assertions.assertEquals(new Vector2d(5, 8), new Vector2d(4, 5).add(new Vector2d(1, 3)));
    }

    @Test
    void test_substract()
    {
        Assertions.assertEquals(new Vector2d(5, 8), new Vector2d(4, 5).subtract(new Vector2d(-1, -3)));
    }

    @Test
    void test_equals()
    {
        Assertions.assertTrue(new Vector2d(5, 7).equals(new Vector2d(5, 7)));
        Assertions.assertFalse(new Vector2d(5, 7).equals(new Vector2d(7, 9)));
    }

    @Test
    void test_opposite()
    {
        Assertions.assertEquals(new Vector2d(-4, -5), new Vector2d(4, 5).opposite());
    }

}
