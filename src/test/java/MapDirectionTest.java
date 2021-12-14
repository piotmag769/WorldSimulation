import agh.ics.oop.MapDirection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapDirectionTest {
    @Test
    void test_next()
    {
        MapDirection[] arg = {MapDirection.NORTH, MapDirection.SOUTH, MapDirection.EAST, MapDirection.WEST};
        MapDirection[] res = {MapDirection.EAST, MapDirection.WEST, MapDirection.SOUTH, MapDirection.NORTH};
        for(int i = 0; i < 4; i++)
            assertEquals(arg[i].next(), res[i]);
    }

    @Test
    void test_previous()
    {
        MapDirection[] arg = {MapDirection.NORTH, MapDirection.SOUTH, MapDirection.EAST, MapDirection.WEST};
        MapDirection[] res = {MapDirection.WEST, MapDirection.EAST, MapDirection.NORTH, MapDirection.SOUTH};
        for(int i = 0; i < 4; i++)
            assertEquals(arg[i].previous(), res[i]);
    }
}
