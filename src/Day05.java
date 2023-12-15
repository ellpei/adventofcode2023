import com.sun.tools.javac.Main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day05 {
    // https://adventofcode.com/2023/day/5
    private static final String fileName = "input-05.txt";
    private static final String testFileName = "testinput-05.txt";
    private static final String outFileName = "output-05.txt";


    public static void main(String[] args) {
        List<List<Long>>[] inputs = readInput(fileName);
        System.out.println("ANSWER PART 1:" + getLowestLocationNumber(inputs));
        getLowestLocationNumberSeedRange(inputs, outFileName); // prints results to output file
        System.out.println("ANSWER PART 2:" + getLowestValueFromResult(outFileName));
    }

    // Part 1
    private static Long getLowestLocationNumber(List<List<Long>>[] inputs) {
        List<Long> seeds = inputs[0].get(0);
        System.out.println("Seeds: " + seeds);
        Long lowestLocationNumber = Long.MAX_VALUE;
        for (Long seed : seeds) {
            Long location = getLocation(seed, inputs);
            if (location < lowestLocationNumber) {
                lowestLocationNumber = location;
            }
        }

        return lowestLocationNumber;
    }

    public static void appendLongToFile(String filePath, long value) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(String.valueOf(value));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Long getLocation(Long seed, List<List<Long>>[] inputs) {
        Long soil = getBestDestinationNumber(seed, inputs[1]);
        Long fertilizer = getBestDestinationNumber(soil, inputs[2]);
        Long water = getBestDestinationNumber(fertilizer, inputs[3]);
        Long light = getBestDestinationNumber(water, inputs[4]);
        Long temperature = getBestDestinationNumber(light, inputs[5]);
        Long humidity = getBestDestinationNumber(temperature, inputs[6]);
        return getBestDestinationNumber(humidity, inputs[7]);
    }

    // Part 2
    private static void getLowestLocationNumberSeedRange(List<List<Long>>[] inputs, String fileName) {
        List<Long> seedValues = inputs[0].get(0);
        Long minObserved = Long.MAX_VALUE;
        for (int i = 0; i < seedValues.size(); i+=2) {
            Long seedStart = seedValues.get(i);
            Long seedRange = seedValues.get(i+1);
            for (Long seed = seedStart; seed < seedStart + seedRange; seed++) {
                Long location =  getLocation(seed, inputs);
                if (location < minObserved) {
                    minObserved = location;
                    appendLongToFile("output-05.txt", location);
                }
            }
        }
    }

    private static Long getBestDestinationNumber(Long sourceValue, List<List<Long>> mappings) {
        Long res = -1L;
        for (List<Long> mapping : mappings) {
            Long destinationNumber = getDestinationNumber(sourceValue, mapping.get(0), mapping.get(1), mapping.get(2));
            if (destinationNumber != -1) {
                res = destinationNumber;
            }
        }
        return res == -1 ? sourceValue : res;
    }

    private static Long getDestinationNumber(Long sourceValue, Long destinationRangeStart,
        Long sourceRangeStart, Long rangeLength) {
        if (sourceValue >= sourceRangeStart && sourceValue < sourceRangeStart + rangeLength) {
            Long dstValue = sourceValue - sourceRangeStart;
            Long result = destinationRangeStart + dstValue;
            return result;
        }
        return -1L;
    }

    private static List<Long> parseNumbers(String input) {
        try {
            return Arrays.stream(input.split(" ")).filter(s -> !s.isEmpty())
                .map(Long::parseLong).collect(Collectors.toList());
        } catch (NumberFormatException e) {
            return List.of();
        }
    }

    private static Long getLowestValueFromResult(String fileName) {
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(fileName);
        Long lowestValue = Long.MAX_VALUE;

        if (inputStream != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    Long value = Long.parseLong(line);
                    if (value < lowestValue) {
                        lowestValue = value;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("File not found: " + fileName);
        }
        return lowestValue;
    }

    private static List<List<Long>>[] readInput(String fileName) {
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(fileName);
        List<List<Long>>[] inputs = new ArrayList[8];
        Arrays.setAll(inputs, i -> new ArrayList<>());

        boolean toSoil = false;
        boolean toFertilizer = false;
        boolean toWater = false;
        boolean toLight = false;
        boolean toTemperature = false;
        boolean toHumidity = false;
        boolean toLocation = false;

        if (inputStream != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("seeds:")) {
                        inputs[0].add(parseNumbers(line.split(":")[1]));
                    } else if (line.startsWith("seed-to-soil map:")) {
                        toSoil = true;
                    } else if (line.startsWith("soil-to-fertilizer map:")) {
                        toSoil = false;
                        toFertilizer = true;
                    } else if (line.startsWith("fertilizer-to-water map:")) {
                        toFertilizer = false;
                        toWater = true;
                    } else if (line.startsWith("water-to-light map:")) {
                        toWater = false;
                        toLight = true;
                    } else if (line.startsWith("light-to-temperature map:")) {
                        toLight = false;
                        toTemperature = true;
                    } else if (line.startsWith("temperature-to-humidity map:")) {
                        toTemperature = false;
                        toHumidity = true;
                    } else if (line.startsWith("humidity-to-location map:")) {
                        toHumidity = false;
                        toLocation = true;
                    }

                    if(toSoil && parseNumbers(line).size() > 0) {
                        inputs[1].add(parseNumbers(line));
                    } else if (toFertilizer && parseNumbers(line).size() > 0) {
                        inputs[2].add(parseNumbers(line));
                    } else if (toWater && parseNumbers(line).size() > 0) {
                        inputs[3].add(parseNumbers(line));
                    } else if (toLight && parseNumbers(line).size() > 0) {
                        inputs[4].add(parseNumbers(line));
                    } else if (toTemperature && parseNumbers(line).size() > 0) {
                        inputs[5].add(parseNumbers(line));
                    } else if (toHumidity && parseNumbers(line).size() > 0) {
                        inputs[6].add(parseNumbers(line));
                    } else if (toLocation && parseNumbers(line).size() > 0) {
                        inputs[7].add(parseNumbers(line));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("File not found: " + fileName);
        }
        return inputs;
    }


}
