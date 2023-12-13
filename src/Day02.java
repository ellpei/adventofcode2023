import com.sun.tools.javac.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day02 {
    // https://adventofcode.com/2023/day/2
    private static final String fileName = "input-02.txt";
    private static final InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(fileName);


    private static final Map<Color, Integer> MAX_VALUES = Map.of(
        Color.RED, 12,
        Color.GREEN, 13,
        Color.BLUE, 14
    );

    enum Color {
        RED,
        GREEN,
        BLUE
    }

    public static void main(String[] args) {
        List<String> inputs = ElfHelper.readInputLines(fileName);
        System.out.println("ANSWER PART 1:" + getSumPossibleIds(inputs));
        System.out.println("ANSWER PART 2:" + getSumPowersMinimum(inputs));
    }

    // Part 1
    private static int getSumPossibleIds(List<String> inputs) {
        int sumPossibleIds = 0;
        for (int i = 1; i <= inputs.size(); i++) {
            boolean gameIsPossible = true;
            String[] splitted = inputs.get(i-1).split(":");
            String[] gamesSubsets = splitted[1].split(";");
            for (String gameSubset : gamesSubsets) {
                String[] gameSubsetSplit = gameSubset.split(",");
                for (String gameSubsetSplitElement : gameSubsetSplit) {
                    int number = getNumber(gameSubsetSplitElement);
                    Color color = getColor(gameSubsetSplitElement);
                    if (number > MAX_VALUES.get(color)) {
                        gameIsPossible = false;
                    }
                }
            }
            if (gameIsPossible) {
                sumPossibleIds += i;
            }
        }
        return sumPossibleIds;
    }

    // Part 2
    private static int getSumPowersMinimum(List<String> inputs) {
        int sumPowers = 0;
        for (String input : inputs) {
            boolean gameIsPossible = true;
            String[] splitted = input.split(":");
            String[] gamesSubsets = splitted[1].split(";");
            int maxRed = 0;
            int maxGreen = 0;
            int maxBlue = 0;
            for (String gameSubset : gamesSubsets) {
                String[] gameSubsetSplit = gameSubset.split(",");
                for (String gameSubsetSplitElement : gameSubsetSplit) {
                    int number = getNumber(gameSubsetSplitElement);
                    Color color = getColor(gameSubsetSplitElement);
                    if (color == Color.RED && number > maxRed) {
                        maxRed = number;
                    } else if (color == Color.GREEN && number > maxGreen) {
                        maxGreen = number;
                    } else if (color == Color.BLUE && number > maxBlue) {
                        maxBlue = number;
                    }
                }
            }
            sumPowers += maxRed * maxGreen * maxBlue;
        }
        return sumPowers;
    }

    private static int getNumber(String input) {
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(input);
        matcher.find();
        String result = matcher.group(1);
        return Integer.parseInt(result);
    }

    private static Color getColor(String input) {
        if (input.contains("red")) {
            return Color.RED;
        } else if (input.contains("green")) {
            return Color.GREEN;
        } else if (input.contains("blue")) {
            return Color.BLUE;
        }
        return null;
    }
}
