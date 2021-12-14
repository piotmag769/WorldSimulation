package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;

public class OptionsParser {
    public static ArrayList<MoveDirection> parse(List<String> moves)  throws IllegalArgumentException
    {
        ArrayList<MoveDirection> animal_moves = new ArrayList<>();
        for (String move: moves)
        {
            switch (move) {
                case "f", "forward" -> animal_moves.add(MoveDirection.FORWARD);
                case "b", "backward" -> animal_moves.add(MoveDirection.BACKWARD);
                case "r", "right" -> animal_moves.add(MoveDirection.RIGHT);
                case "l", "left" -> animal_moves.add(MoveDirection.LEFT);
                default -> throw new IllegalArgumentException(move + " is not legal move specification");
            };
        }

        return animal_moves;
    }
}
