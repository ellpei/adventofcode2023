import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Day08 {
    // https://adventofcode.com/2023/day/8
    private static final String fileName = "input-08.txt";
    private static final String testFileName = "testinput-08.txt";

    public static void main(String[] args) {
        List<String> inputs = ElfHelper.readInputLines(fileName);
        String instructions = inputs.remove(0);
        HashMap<String, Node> network = getNetwork(inputs);

        System.out.println("ANSWER PART 1:" + countStepsNeeded(instructions, network));
        System.out.println("ANSWER PART 2: LCM(" + countGhostStepsNeeded(instructions, network) + ")");

    }

    // Part 1: Start from AAA and go to ZZZ
    private static int countStepsNeeded(String instructions, HashMap<String, Node> network) {
        Node currentNode = network.get("AAA");
        boolean found = false;
        int stepsTaken = 0;
        while (!found) {
            for (int i = 0; i < instructions.length(); i++) {
                if (Objects.equals(currentNode.from, "ZZZ")) {
                    System.out.println("Found ZZZ after steps: " + stepsTaken);
                    found = true;
                    break;
                }
                stepsTaken++;
                char instruction = instructions.charAt(i); // L or R
                if (instruction == 'L') {
                    currentNode = network.get(currentNode.left);
                } else if (instruction == 'R') {
                    currentNode = network.get(currentNode.right);
                }
            }
        }

        return stepsTaken;
    }

    // Part 2: Start from each node that ends on A, stop condition is each node ends on Z
    private static List<Integer> countGhostStepsNeeded(String instructions, HashMap<String, Node> network) {
        List<Node> startNodes = getNodesEndingWith(network.values(), "A");
        List<Integer> stepsNeeded = new ArrayList<>();
        System.out.println("Number of starting nodes: " + startNodes.size());
        for (Node node : startNodes) {
            Node currentNode = node;
            boolean found = false;
            int stepsTaken = 0;
            while (!found) {
                for (int i = 0; i < instructions.length(); i++) {
                    // Check if all the current nodes end with Z
                    if (currentNode.from.endsWith("Z")) {
                        found = true;
                        break;
                    }
                    stepsTaken++;
                    char instruction = instructions.charAt(i); // L or R
                    if (instruction == 'L') {
                        currentNode = network.get(currentNode.left);
                    } else if (instruction == 'R') {
                        currentNode = network.get(currentNode.right);
                    }
                }
            }
            stepsNeeded.add(stepsTaken);
        }
        return stepsNeeded;
    }

    private static List<Node> getNodesEndingWith(Collection<Node> nodes, String suffix) {
        return nodes.stream().filter(node -> node.from.endsWith(suffix)).toList();
    }

    private static class Node {
        private String from;
        private String left;
        private String right;

        @Override
        public String toString() {
            return from + "=(" + left + "," + right + ")";
        }
    }

    private static HashMap<String, Node> getNetwork(List<String> inputs) {
        HashMap<String, Node> network = new HashMap<>();
        for (String input : inputs) {
            if (input.isBlank()) {
                continue;
            }
            String[] parts = input.split("=");
            String from = parts[0].trim();
            String leftRight = parts[1].replaceAll("[\\(\\)]", "");
            String[] leftRightSplitted = leftRight.split(",");
            Node node = new Node();
            node.from = from;
            node.left = leftRightSplitted[0].trim();
            node.right = leftRightSplitted[1].trim();
            network.put(from, node);
        }
        return network;
    }

}
