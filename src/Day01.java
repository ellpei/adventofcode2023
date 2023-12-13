import com.sun.tools.javac.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Day01 {
    // https://adventofcode.com/2023/day/1
    private static final String fileName = "input-01.txt";
    private static final Map<String, Integer>  NUMBERS = Map.of(
        "one", 1,
        "two", 2,
        "three", 3,
        "four", 4,
        "five", 5,
        "six", 6,
        "seven", 7,
        "eight", 8,
        "nine", 9
    );

    public static void main(String[] args) {
        List<String> inputs = ElfHelper.readInputLines(fileName);
        System.out.println("Part 1.1 ANSWER:" + getSumCalibrationValuesDigitsOnly(inputs));
        System.out.println("Part 1.2 ANSWER:" + getSumCalibrationValuesWordsIncluded(inputs));
    }

    private static Integer getSumCalibrationValuesDigitsOnly(List<String> lines) {
        int sum = 0;
        for(String line : lines) {
            sum += getCalibrationValueDigitsOnly(line);
        }
        return sum;
    }

    private static Integer getSumCalibrationValuesWordsIncluded(List<String> lines) {
        int sum = 0;
        for(String line : lines) {
            sum += getCalibrationValueWordsIncluded(line);
        }
        return sum;
    }

    // For part 1 of the puzzle, we only need to get the first and last digits of each line
    private static Integer getCalibrationValueDigitsOnly(String line) {
        Character firstDigit = null;
        Character lastDigit = null;
        for (int i = 0; i < line.length(); i++) {
            char frontChar = line.charAt(i);
            char backChar = line.charAt(line.length() - i - 1);
            if (Character.isDigit(frontChar) && firstDigit == null) {
                firstDigit = frontChar;
            }
            if (Character.isDigit(backChar) && lastDigit == null) {
                lastDigit = backChar;
            }
            if (firstDigit != null && lastDigit != null) {
                break;
            }

        }
        assert firstDigit != null;
        assert lastDigit != null;
        return Integer.parseInt(firstDigit + lastDigit.toString());
    }

    private static List<String> possibleMatches(String chars) {
        List<String> possibleKeys = new ArrayList<>();
        for (String key : NUMBERS.keySet()) {
            if (key.startsWith(chars)) {
                possibleKeys.add(key);
            }
        }
        return possibleKeys;
    }

    // For part 2 of the puzzle, we need to get the first and last digits of each line
    protected static Integer getCalibrationValueWordsIncluded(String line) {
        Integer firstDigit = null;
        Integer lastDigit = null;
        for (int i = 0; i < line.length(); i++) {
            char frontChar = line.charAt(i);

                if (!Character.isDigit(frontChar)) {
                    int len = 1;
                    while(len < 8 && i+len <= line.length()) {
                        char[] partialLine = new char[len];
                        line.getChars(i, i+len, partialLine, 0);
                        List<String> possibleKeys = possibleMatches(new String(partialLine));
                        if (possibleKeys.isEmpty()) {
                            break;
                        }
                        if (possibleKeys.size() == 1 && possibleKeys.get(0).equals(new String(partialLine))) {
                            if (firstDigit == null) {
                                firstDigit = NUMBERS.get(possibleKeys.get(0));
                            }
                            lastDigit = NUMBERS.get(possibleKeys.get(0));
                            break;
                        }
                        len++;
                    }
                } else {
                    if (firstDigit == null) {
                        firstDigit = Integer.parseInt(frontChar + "");
                    }
                    lastDigit = Integer.parseInt(frontChar + "");
                }
        }
        assert firstDigit != null;
        assert lastDigit != null;
        return Integer.parseInt(firstDigit + lastDigit.toString());
    }
}
