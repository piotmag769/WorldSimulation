package agh.ics.oop.filemanagement;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public record FileManager(String filename) {

    public FileManager(String filename) {
        this.filename = filename;
        try (PrintWriter output = new PrintWriter(new FileWriter(filename))) {
            output.println("Animals,Plants,Average_Energy,Average_Lifetime,Average_Children"); // header
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeDayToFile(Number[] statistic) {
        String string = Arrays.toString(statistic);
        string = string.substring(1, string.length() - 1);

        StringBuilder res = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            char curr = string.charAt(i);
            if (curr != ' ')
                res.append(curr);
        }

        // to not care about manually closing the program and file (inefficient, I know)
        try (PrintWriter output = new PrintWriter(new FileWriter(filename, true))) {
            output.println(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
