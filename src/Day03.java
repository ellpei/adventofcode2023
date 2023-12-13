import com.sun.tools.javac.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Day03 {
    // https://adventofcode.com/2023/day/3
    private static final String fileName = "input-03.txt";
    private static final int MAX_ROWS = 140;
    private static final int MAX_COLS = 140;

    public static void main(String[] args) {
        char[][] schematic = readInput(fileName, MAX_ROWS, MAX_COLS);
        System.out.println("ANSWER PART 1:" + sumEngineParts(schematic));
        System.out.println("ANSWER PART 2:" + sumGearRatios(schematic));
    }

    // Part 1
    private static int sumEngineParts(char[][] schematic) {
        int sumEngineParts = 0;
        for (int row = 0; row < schematic.length; row ++) {
            String currentDigit = "";
            boolean anyAdjacent = false;
            for (int col = 0; col < schematic[row].length; col++) {

                if (Character.isDigit(schematic[row][col])) {
                    currentDigit = currentDigit.concat(String.valueOf(schematic[row][col]));
                    anyAdjacent = anyAdjacent || checkAdjacent(schematic, row, col);
                }
                // number can end on a non digit or end of line
                if (col == schematic[row].length -1 || !Character.isDigit(schematic[row][col])) {
                    if (anyAdjacent) {
                        sumEngineParts += Double.parseDouble(currentDigit);
                    }
                    currentDigit = "";
                    anyAdjacent = false;
                }
            }
        }
        return sumEngineParts;
    }

    private static boolean checkAdjacent(char[][] schematic, int row, int col) {
        return isSpecialCharacter(schematic, row, col+1)
            || isSpecialCharacter(schematic, row, col-1)
            || isSpecialCharacter(schematic, row+1, col)
            || isSpecialCharacter(schematic, row-1, col)
            || isSpecialCharacter(schematic, row+1, col+1)
            || isSpecialCharacter(schematic, row+1, col-1)
            || isSpecialCharacter(schematic, row-1, col+1)
            || isSpecialCharacter(schematic, row-1, col-1);
    }

    private static boolean isSpecialCharacter(char[][] schematic, int row, int col) {
        if (row >= 0 && row < MAX_ROWS && col >= 0 && col < MAX_COLS) {
            char c = schematic[row][col];
            return c != '.' && !Character.isDigit(c);
        }
        return false;
    }

    // Part 2
    private static int sumGearRatios(char[][] schematic) {
        int sumGearRatio = 0;
        for (int row = 0; row < schematic.length; row++) {
            for (int col = 0; col < schematic[row].length; col++) {
                if (schematic[row][col] == '*') {
                    sumGearRatio += getGearRatio(schematic, row, col);
                }
            }
        }
        return sumGearRatio;
    }

    private static int getGearRatio(char[][] schematic, int row, int col) {
        List<Integer> neighboringDigits = new ArrayList<>();

        String left = getDigitLeft(schematic, row, col-1);
        if (left.length() > 0) {
            neighboringDigits.add(Integer.parseInt(left));
        }
        String right = getDigitRight(schematic, row, col+1);
        if (right.length() > 0) {
            neighboringDigits.add(Integer.parseInt(right));
        }
        // up
        if (row-1 >= 0 && Character.isDigit(schematic[row-1][col])) {
            String up = getDigitLeft(schematic, row-1, col) + getDigitRight(schematic, row-1, col+1);
            neighboringDigits.add(Integer.parseInt(up));
        } else {
            String diagonalLeftUp = getDigitLeft(schematic, row-1, col-1);
            if (diagonalLeftUp.length() > 0) {
                neighboringDigits.add(Integer.parseInt(diagonalLeftUp));
            }
            String diagonalRightUp = getDigitRight(schematic, row-1, col+1);
            if (diagonalRightUp.length() > 0) {
                neighboringDigits.add(Integer.parseInt(diagonalRightUp));
            }
        }

        // down
        if (Character.isDigit(schematic[row+1][col])) {
            String down = getDigitLeft(schematic, row+1, col) + getDigitRight(schematic, row+1, col+1);
            neighboringDigits.add(Integer.parseInt(down));
        } else {
            String diagonalLeftDown = getDigitLeft(schematic, row+1, col-1);
            if (diagonalLeftDown.length() > 0) {
                neighboringDigits.add(Integer.parseInt(diagonalLeftDown));
            }

            String diagonalRightDown = getDigitRight(schematic, row+1, col+1);
            if (diagonalRightDown.length() > 0) {
                neighboringDigits.add(Integer.parseInt(diagonalRightDown));
            }
        }

        if (neighboringDigits.size() == 2) {
            return neighboringDigits.get(0) * neighboringDigits.get(1);
        }
        return 0;
    }

    private static String getDigitLeft(char[][] schematic, int row, int col) {
        if (row < 0 || row >= schematic.length || col < 0 || col >= schematic[row].length) {
            return "";
        }
        if (Character.isDigit(schematic[row][col])) {
            if (col == 0) {
                return String.valueOf(schematic[row][col]);
            }
            return getDigitLeft(schematic, row, col - 1) + schematic[row][col];
        }
        return "";
    }

    private static String getDigitRight(char[][] schematic, int row, int col) {
        if (row < 0 || row >= schematic.length) {
            return "";
        }
        if (Character.isDigit(schematic[row][col])) {
            if (col == schematic[row].length - 1) {
                return String.valueOf(schematic[row][col]);
            }
            return schematic[row][col] + getDigitRight(schematic, row, col + 1);
        }
        return "";
    }

    private static char[][] readInput(String fileName, int maxRows, int maxCols) {
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(fileName);
        char[][] schematic = new char[maxRows][maxCols];
        int i = 0;
        if (inputStream != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    char[] buffer = new char[maxCols];
                    line.getChars(0, line.length(), buffer, 0);
                    schematic[i] = buffer;
                    i++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("File not found: " + fileName);
        }
        return schematic;
    }
}
