import java.util.*;
import java.util.stream.Collectors;

public class Day10 {
    // https://adventofcode.com/2023/day/10
    private static final String fileName = "input-10.txt";
    private static final String testFileName = "testinput-10.txt";
    private static final HashMap<Character, Set<Direction>> possibleDirections = new HashMap<>(Map.of(
            // | is a vertical pipe connecting north and south.
            '|', Set.of(Direction.NORTH, Direction.SOUTH),
            // - is a horizontal pipe connecting east and west.
            '-', Set.of(Direction.WEST, Direction.EAST),
            // L is a 90-degree bend connecting north and east.
            'L', Set.of(Direction.NORTH, Direction.EAST),
            // J is a 90-degree bend connecting north and west.
            'J', Set.of(Direction.NORTH, Direction.WEST),
            // 7 is a 90-degree bend connecting south and west.
            '7', Set.of(Direction.SOUTH, Direction.WEST),
            // F is a 90-degree bend connecting south and east.
            'F', Set.of(Direction.SOUTH, Direction.EAST)
    ));

    public static void main(String[] args) {
        List<String> inputs = ElfHelper.readInputLines(fileName);
        //System.out.println("ANSWER PART 1: " + countMaxSteps(inputs)); // 6842
        System.out.println("ANSWER PART 2: " + countTilesEnclosedByLoop(inputs));
    }

    // Part 1
    private static int countMaxSteps(List<String> input) {
        char[][] inputGraph = parseGraph(input);
        Coordinates startCoordinates = findS(inputGraph);
        inputGraph[startCoordinates.row][startCoordinates.col] = inferStartLocationSymbol(inputGraph, startCoordinates);
        int res = bfs(inputGraph, startCoordinates);
        printGraph(inputGraph);
        return res;
    }

    private static Coordinates findS(char[][] inputGraph) {
        for (int row = 0; row < inputGraph.length; row++) {
            for (int col = 0; col < inputGraph[row].length; col++) {
                if (inputGraph[row][col] == 'S') {
                    return new Coordinates(row, col);
                }
            }
        }
        throw new RuntimeException("Could not find S");
    }

    // Part 2
    private static int countTilesEnclosedByLoop(List<String> input) {
        char[][] inputGraph = parseGraph(input);
        Coordinates startCoordinates = findS(inputGraph);
        inputGraph[startCoordinates.row][startCoordinates.col] = inferStartLocationSymbol(inputGraph, startCoordinates);
        Set<Coordinates> mainLoopCoords = getMainLoop(inputGraph, startCoordinates);
        System.out.println("main loop:" + mainLoopCoords);
        printGraph(inputGraph);
        int sumEnclosedArea = 0;

        for (int row = 0; row < inputGraph.length; row++) {
            for (int col = 0; col < inputGraph[row].length; col++) {
                Coordinates coordinates = new Coordinates(row, col);
                if (!mainLoopCoords.contains(coordinates) && getValueAt(inputGraph, coordinates) != 'I' && getValueAt(inputGraph, coordinates) != '0') {
                    System.out.println("Starting to traverse area at (" + coordinates.row + "," +  coordinates.col);
                    int res = traverseArea(inputGraph, mainLoopCoords, coordinates);
                    sumEnclosedArea += res;
                }
            }
        }
        System.out.println("--------------");
        printGraph(inputGraph);
        return sumEnclosedArea;
    }

    private static void markAreaWith(char[][] inputGraph, Set<Coordinates> toBeMarked, char newValue) {
        for (Coordinates coordinates : toBeMarked) {
            if (isValid(inputGraph, coordinates)) {
                inputGraph[coordinates.row][coordinates.col] = newValue;
            }
        }
    }

    private static void printGraph(char[][] inputGraph) {
        for (char[] chars : inputGraph) {
            for (int col = 0; col < chars.length; col++) {
                System.out.print(chars[col]);
            }
            System.out.println();
        }
    }

    private static boolean isValid(char[][] inputGraph, Coordinates coordinates) {
        return coordinates.row >= 0 && coordinates.col >= 0 && coordinates.row < inputGraph.length && coordinates.col < inputGraph[coordinates.row].length;
    }

    private static boolean isInside(char[][] inputGraph, Set<Coordinates> mainLoop, Coordinates coordinates) {
        int countIntersections = 0;
        int row = coordinates.row;
        int col = coordinates.col;
        while (row < inputGraph.length && col < inputGraph[row].length) {
            Coordinates current = new Coordinates(row, col);
            char valueAt = getValueAt(inputGraph, current);
            // filter out corners symbols that wouldn't actually be crossed
            if (mainLoop.contains(current) && valueAt != 'L' && valueAt != '7') {
                countIntersections++;
            }
            row++;
            col++;
        }

        return countIntersections % 2 == 1;
    }

    private static char[][] copyArray(char[][] original) {
        int rows = original.length;
        int cols = original[0].length;

        char[][] copy = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            copy[i] = Arrays.copyOf(original[i], cols);
        }

        return copy;
    }

    private static Set<Coordinates> getMainLoop(char[][] inputGraph, Coordinates startCoordinates) {
        char[][] copy = copyArray(inputGraph);
        Set<Coordinates> res = new HashSet<>();

        bfs(copy, startCoordinates); // will mark visited as X in inputGraph
        for (int row = 0; row < copy.length; row++) {
            for (int col = 0; col < copy[row].length; col++) {
                Coordinates coordinate = new Coordinates(row, col);
                if (getValueAt(copy, coordinate) == 'X') {
                    res.add(coordinate);
                }
            }
        }
        return res;
    }

    private static int traverseArea(char[][] inputGraph, Set<Coordinates> mainLoop, Coordinates coordinates) {
        Queue<Coordinates> queue = new ArrayDeque<>();
        queue.add(coordinates);
        Set<Coordinates> seen = new HashSet<>();
        boolean isAllValid = true;
        System.out.println("Queue size: " + queue.size() + " Seen size: " + seen.size());

        while (!queue.isEmpty()) {
            Coordinates current = queue.remove();
             if (!isValid(inputGraph, current)) {
                // hit a border, this is no longer valid
                isAllValid = false;
                continue;
            }
            char valueAtPosition = getValueAt(inputGraph, current);
            if (!seen.contains(current) && !queue.contains(current)
                     && !mainLoop.contains(current) && valueAtPosition != 'I'
                     && valueAtPosition != '0') {
                seen.add(current);
                Coordinates north = new Coordinates(current.row - 1, current.col);
                if (!seen.contains(north) && !queue.contains(north)) {
                    queue.add(north);
                }

                Coordinates east = new Coordinates(current.row, current.col + 1);
                if (!seen.contains(east) && !queue.contains(east)) {
                    queue.add(east);
                }

                Coordinates south = new Coordinates(current.row + 1, current.col);
                if (!seen.contains(south) && !queue.contains(south)) {
                    queue.add(south);
                }

                Coordinates west = new Coordinates(current.row, current.col - 1);
                if (!seen.contains(west) && !queue.contains(west)) {
                    queue.add(west);
                }

            }
        }
        if (isAllValid && isInside(inputGraph, mainLoop, coordinates)) {
            markAreaWith(inputGraph, seen, 'I');
            return seen.size();
        }
        markAreaWith(inputGraph, seen, '0');
        return 0;
    }

    private static char inferStartLocationSymbol(char[][] inputGraph, Coordinates startCoordinates) {
        Set<Direction> directions = new HashSet<>();
        if (getNorthCoordinates(startCoordinates).isPresent()) {
            Coordinates northCoordinates = getNorthCoordinates(startCoordinates).get();
            if (getPossibleDirections(getValueAt(inputGraph, northCoordinates)).contains(Direction.SOUTH)) {
                directions.add(Direction.NORTH);
            }
        }

        if (getEastCoordinates(inputGraph, startCoordinates).isPresent()) {
            Coordinates eastCoordinates = getEastCoordinates(inputGraph, startCoordinates).get();
            if (getPossibleDirections(getValueAt(inputGraph, eastCoordinates)).contains(Direction.WEST)) {
                directions.add(Direction.EAST);
            }
        }

        if (getSouthCoordinates(inputGraph, startCoordinates).isPresent()) {
            Coordinates southCoordinates = getSouthCoordinates(inputGraph, startCoordinates).get();
            if (getPossibleDirections(getValueAt(inputGraph, southCoordinates)).contains(Direction.NORTH)) {
                directions.add(Direction.SOUTH);
            }
        }

        if (getWestCoordinates(startCoordinates).isPresent()) {
            Coordinates westCoordinates = getWestCoordinates(startCoordinates).get();
            if (getPossibleDirections(getValueAt(inputGraph, westCoordinates)).contains(Direction.EAST)) {
                directions.add(Direction.WEST);
            }
        }

        Set<Character> possibleSymbols = possibleDirections.entrySet().stream().filter(entry -> entry.getValue().equals(directions)).map(Map.Entry::getKey).collect(Collectors.toSet());
        System.out.println("Matched with possible symbols for S: " + possibleSymbols);
        if (possibleSymbols.size() == 1) {
            return possibleSymbols.iterator().next();
        } else {
            throw new RuntimeException("More than 1 match :(");
        }
    }

    private static char getValueAt(char[][] inputGraph, Coordinates coordinates) {
        return inputGraph[coordinates.row][coordinates.col];
    }

    private static int bfs(char[][] inputGraph, Coordinates startCoordinates) {
        int maxDistance = 0;
        Queue<Pair> queue = new ArrayDeque<>();
        queue.add(new Pair(startCoordinates, 0));
        while (!queue.isEmpty()) {
            Pair current = queue.remove();
            Coordinates coordinates = current.coordinates;
            int currentDepth = current.depth;
            if (getValueAt(inputGraph, coordinates) != 'X') {
                maxDistance = Integer.max(maxDistance, currentDepth);
                List<Coordinates> destinations = getPossibleDestinations(inputGraph, coordinates);
                int depth = currentDepth + 1;
                queue.addAll(destinations.stream().map(destination -> new Pair(destination, depth)).toList());
                inputGraph[coordinates.row][coordinates.col] = 'X'; // mark visited
            }
        }
        return maxDistance;
    }

    private static Set<Direction> getPossibleDirections(char symbol) {
        return possibleDirections.get(symbol) == null ? Collections.emptySet() : possibleDirections.get(symbol);
    }

    private static Optional<Coordinates> getNorthCoordinates(Coordinates coordinates) {
        return coordinates.row - 1 >= 0 ?
                Optional.of(new Coordinates(coordinates.row - 1, coordinates.col))
                : Optional.empty();
    }

    private static Optional<Coordinates> getEastCoordinates(char[][] inputGraph, Coordinates coordinates) {
        return isValid(inputGraph, new Coordinates(coordinates.row, coordinates.col + 1)) ?
                Optional.of(new Coordinates(coordinates.row, coordinates.col + 1))
                : Optional.empty();
    }

    private static Optional<Coordinates> getSouthCoordinates(char[][] inputGraph, Coordinates coordinates) {
        return coordinates.row + 1 < inputGraph.length ?
                Optional.of(new Coordinates(coordinates.row + 1, coordinates.col))
                : Optional.empty();
    }

    private static Optional<Coordinates> getWestCoordinates(Coordinates coordinates) {
        return coordinates.col - 1 >= 0 ?
                Optional.of(new Coordinates(coordinates.row, coordinates.col - 1))
                : Optional.empty();
    }

    private static List<Coordinates> getPossibleDestinations(char[][] inputGraph, Coordinates current) {
        List<Coordinates> destinations = new ArrayList<>();
        Set<Direction> possibleDirections = getPossibleDirections(getValueAt(inputGraph, current));
        if (possibleDirections == null) {
            return Collections.emptyList();
        }
        if (getNorthCoordinates(current).isPresent() && possibleDirections.contains(Direction.NORTH)) {
            Coordinates northCoordinates = getNorthCoordinates(current).get();
            if (isNotVisited(inputGraph, northCoordinates)) {
                destinations.add(northCoordinates);
            }
        }

        if (getEastCoordinates(inputGraph, current).isPresent() && possibleDirections.contains(Direction.EAST)) {
            Coordinates eastCoordinates = getEastCoordinates(inputGraph, current).get();
            if (isNotVisited(inputGraph, eastCoordinates)) {
                destinations.add(eastCoordinates);
            }
        }

        if (getSouthCoordinates(inputGraph, current).isPresent() && possibleDirections.contains(Direction.SOUTH)) {
            Coordinates southCoordinates = getSouthCoordinates(inputGraph, current).get();
            if (isNotVisited(inputGraph, southCoordinates)) {
                destinations.add(southCoordinates);
            }
        }

        if (getWestCoordinates(current).isPresent() && possibleDirections.contains(Direction.WEST)) {
            Coordinates westCoordinates = getWestCoordinates(current).get();
            if (isNotVisited(inputGraph, westCoordinates)) {
                destinations.add(westCoordinates);
            }
        }

        return destinations;
    }

    private static boolean isNotVisited(char[][] inputGraph, Coordinates coordinates) {
        return getValueAt(inputGraph, coordinates) != 'X';
    }

    private static class Coordinates {
        int row;
        int col;

        protected Coordinates(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Coordinates)) {
                return false;
            }
            Coordinates other = (Coordinates) o;
            return this.row == other.row && this.col == other.col;
        }

        @Override
        public int hashCode() {
            int prime = 31;
            int result = 1;

            result = prime * result + this.row;
            result = prime * result + this.col;

            return result;
        }
    }

    private enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }
    private record Pair(Coordinates coordinates, int depth) {}

    private static char[][] parseGraph(List<String> inputs) {
        char[][] res = new char[inputs.size()][inputs.get(0).length()];
        for (int i = 0; i < inputs.size(); i++) {
            res[i] = inputs.get(i).toCharArray();

        }
        return res;
    }
}
