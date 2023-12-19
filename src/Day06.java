import java.util.Arrays;
import java.util.List;

public class Day06 {
    // https://adventofcode.com/2023/day/6
    private static final String fileName = "input-06.txt";
    private static final String testFileName = "testinput-06.txt";

    public static void main(String[] args) {
        List<String> inputs = ElfHelper.readInputLines(fileName);
        // Part 1
        List<Integer> times = getNumbersPart1(inputs.get(0));
        List<Integer> distanceRecords = getNumbersPart1(inputs.get(1));
        System.out.println("ANSWER PART 1:" + getNumberOfWaysToWinMultiplied(times, distanceRecords));

        // Part 2
        Long time = getNumberPart2(inputs.get(0));
        Long recordDistance = getNumberPart2(inputs.get(1));
        System.out.println("ANSWER PART 2:" + getNumberOfWaysToWin(time, recordDistance));
    }

    // Part 1
    private static Integer getNumberOfWaysToWinMultiplied(List<Integer> times, List<Integer> distanceRecords) {
        int result = 0;
        for (int i = 0; i < times.size(); i++) {
            int numberOfWaysToWin = 0;
            int time = times.get(i);
            int recordDistance = distanceRecords.get(i);

            for (int duration = 0; duration <= time; duration++) {
                // speed is 1 millimeter per millisecond held
                int distance = duration * (time - duration);
                if (distance > recordDistance) {
                    numberOfWaysToWin++;
                }
            }
            result = result == 0 ? numberOfWaysToWin : result * numberOfWaysToWin;
        }
        return result;
    }

    // Part 2
    private static Long getNumberOfWaysToWin(Long time, Long recordDistance) {
        long numberOfWaysToWin = 0;
        for (int duration = 0; duration <= time; duration++) {
            // speed is 1 millimeter per millisecond held
            long distance = duration * (time - duration);
            if (distance > recordDistance) {
                numberOfWaysToWin++;
            }
        }
        return numberOfWaysToWin;
    }

    private static List<Integer> getNumbersPart1(String line) {
        return  Arrays.stream(line.split(":")[1].split(" "))
            .map(String::trim).filter(s -> !s.isEmpty()).map(Integer::parseInt).toList();
    }

    private static Long getNumberPart2(String line) {
        return Long.parseLong(line.split(":")[1].replaceAll("\\s+",""));
    }

}
