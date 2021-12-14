import agh.ics.oop.MoveDirection;
import agh.ics.oop.OptionsParser;
import agh.ics.oop.Animal;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnimalTest {

    @Test
    public void move_test()
    {
        Animal Pufu = new Animal();
        try
        {
            ArrayList <MoveDirection> animal_moves = OptionsParser.parse(Arrays.asList("r", "f", "f", "g", "f", "r", "p", "b", "l"));
        }
        catch (IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
