import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Day07 {
    // https://adventofcode.com/2023/day/7
    private static final String fileName = "input-07.txt";
    private static final String testFileName = "testinput-07.txt";
    private static final String cardOrder = "AKQJT98765432";
    private static final List<Predicate<String>> hands = List.of(
        Day07::isFiveOfAKind,
        Day07::isFourOfAKind,
        Day07::isFullHouse,
        Day07::isThreeOfAKind,
        Day07::isTwoPair,
        Day07::isOnePair,
        Day07::isHighCard
    );

    public static void main(String[] args) {
        List<String> inputs = ElfHelper.readInputLines(fileName);
        // Part 1
        System.out.println("ANSWER PART 1:" + getTotalWinnings(inputs));
    }

    private record Card(String hand, int bid) {

        @Override
            public String toString() {
                return hand + " " + bid;
            }
        }

    // Part 1
    private static Integer getTotalWinnings(List<String> inputs) {
        int totalWinnings = 0;
        List<Card> cards = new ArrayList<>(inputs.stream()
            .map(s -> new Card(s.split(" ")[0], Integer.parseInt(s.split(" ")[1])))
            .toList());
        cards.sort(new CustomComparator());
        int rank = 1;
        for (Card card : cards) {
            totalWinnings += card.bid * rank;
            System.out.println("hand: " + card.hand + " " + card.bid + "*" + rank);
            rank++;
        }
        return totalWinnings;
    }

    public static class CustomComparator implements Comparator<Card> {
        /* -1 if o1 is less than the o2, 0 of o1 equals o2, 1 if o1 is bigger than o2 */
        @Override
        public int compare(Card card1, Card card2) {
            int rank1 = -1;
            int rank2 = -1;
            for (int i = 0; i < hands.size(); i++) {
                // get the first hand matched
                if (rank1 == -1 && hands.get(i).test(card1.hand)) {
                    rank1 = i;
                }
                if (rank2 == -1 && hands.get(i).test(card2.hand)) {
                    rank2 = i;
                }
            }
            if (rank1 == rank2) {
                return compareCardByCard(card1, card2);
            }
            if (rank1 > rank2) {
                return -1;
            }
            return 1;
        }

        protected int compareCardByCard(Card card1, Card card2) {
            for (int i = 0; i < card1.hand.toCharArray().length; i++) {
                int i1 = cardOrder.indexOf(card1.hand.toCharArray()[i]);
                int i2 = cardOrder.indexOf(card2.hand.toCharArray()[i]);
                if (i1 < i2) {
                    return 1;
                }
                if (i1 > i2) {
                    return -1;
                }
            }
            return 0;
        }
    }

    private static boolean isFiveOfAKind(String hand) {
        return isXOfAKind(hand, 5);
    }

    private static boolean isFourOfAKind(String hand) {
        return isXOfAKind(hand, 4);
    }


    private static boolean isFullHouse(String hand) {
        return isXOfAKind(hand, 3) && isXOfAKind(hand, 2);
    }

    private static boolean isThreeOfAKind(String hand) {
        return isXOfAKind(hand, 3);
    }

    private static boolean isTwoPair(String hand) {
        int uniquePairs = 0;
        List<Character> visited = new ArrayList<>();
        for (char c : hand.toCharArray()) {
            if (!visited.contains(c) && containsNumberOf(hand, c, 2)) {
                uniquePairs++;
                visited.add(c);
            }
        }
        return uniquePairs == 2;
    }

    private static boolean isOnePair(String hand) {
        return isXOfAKind(hand, 2);
    }

    private static boolean isHighCard(String hand) {
        return isXOfAKind(hand, 1);
    }

    private static boolean isXOfAKind(String hand, int count) {
        for (char c : hand.toCharArray()) {
            if (containsNumberOf(hand, c, count)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsNumberOf(String hand, char x, int count) {
        return hand.chars().filter(ch -> ch == x).count() == count;
    }


}
