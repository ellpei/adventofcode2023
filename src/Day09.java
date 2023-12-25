import java.util.*;

public class Day09 {
    // https://adventofcode.com/2023/day/9
    private static final String fileName = "input-09.txt";
    private static final String testFileName = "testinput-09.txt";

    public static void main(String[] args) {
        List<String> inputs = ElfHelper.readInputLines(fileName);
        System.out.println("ANSWER PART 1: " + sumExtrapolatedValues(inputs));
    }

    private static Long getLast(List<Long> list) {
        return list.get(list.size()-1);
    }

    private static Long sumExtrapolatedValues(List<String> input) {
        long sum = 0;
        for (String line : input) {
            ArrayList<Long> numbers = new ArrayList<>(parseNumbers(line));
            sum += getNextSequence(numbers);
        }
        return sum;
    }

    private static Long getNextSequence(List<Long> numbers) {
        if (numbers.stream().allMatch(n -> n == 0)) {
            return 0L;
        }
        ArrayList<Long> res = new ArrayList<>();
        for (int i = 0; i < numbers.size()-1; i++) {
            res.add(numbers.get(i+1) - numbers.get(i));
        }
        return getLast(numbers) + getNextSequence(res);
    }

    private static List<Long> parseNumbers(String line) {
       return Arrays.stream(line.split(" "))
               .filter(v -> v.length() > 0)
               .map(Long::parseLong)
               .toList();
    }

}
