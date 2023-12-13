import com.sun.tools.javac.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Day04 {
    // https://adventofcode.com/2023/day/4
    private static final String fileName = "input-04.txt";
    private static final String testFileName = "testinput-04.txt";

    public static void main(String[] args) {
        List<String> lines = ElfHelper.readInputLines(fileName);
        System.out.println("ANSWER PART 1:" + getTotalPoints(lines));
        System.out.println("ANSWER PART 2:" + getNumberOfScratchCards( lines));
    }

    // Part 1
    private static double getTotalPoints(List<String> input) {
        double sumPoints = 0;
        for (String line : input) {
            List<Integer> intersection = getIntersection(line);
            int numWinners = intersection.size();
            sumPoints += numWinners > 1 ? Math.pow(2, numWinners-1) : numWinners == 1 ? 1 : 0;
        }
        return sumPoints;
    }

    // Expects a line like: "Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53"
    private static List<Integer> getIntersection(String line) {
        List<Integer> intersection = new ArrayList<>();
        int colonIndex = line.indexOf(':');
        int pipeIndex = line.indexOf('|');
        if (colonIndex != -1 && pipeIndex != -1) {
            String winningSection = line.substring(colonIndex + 1, pipeIndex).trim();
            String actualSection = line.substring(pipeIndex).trim();
            List<Integer> winningNumbers = getNumbers(winningSection);
            List<Integer> actualNumbers = getNumbers(actualSection);

            for (int number : actualNumbers) {
                if (winningNumbers.contains(number)) {
                    intersection.add(number);
                }
            }
        } else {
            System.out.println("Substring not found");
        }
        return intersection;
    }

    // Part 2
    private static int getNumberOfScratchCards(List<String> input) {
        int[] cardCount = new int[input.size()];
        Arrays.fill(cardCount, 1);

        for (int cardNumber = 0; cardNumber < input.size(); cardNumber++) {
            List<Integer> intersection = getIntersection(input.get(cardNumber));
            int numWinners = intersection.size();
            for (int i = 0; i < numWinners; i++) {
                cardCount[cardNumber + i + 1] += cardCount[cardNumber];
            }
        }
        return Arrays.stream(cardCount).sum();
    }

    private static List<Integer> getNumbers(String source) {
        List<Integer> numbers = new ArrayList<>();
        String[] splitted = source.split(" ");
        for (String s : splitted) {
            if (s.matches("\\d+")) {
                numbers.add(Integer.parseInt(s));
            }
        }
        return numbers;
    }

}
