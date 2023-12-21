import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class Day07 {
    // https://adventofcode.com/2023/day/7
    private static final String fileName = "input-07.txt";
    private static final String testFileName = "testinput-07.txt";
    private static final String cardOrder1 = "AKQJT98765432";
    private static final String cardOrder2 = "AKQT98765432J";
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
        System.out.println("ANSWER PART 1:" + getTotalWinningsPart1(inputs));
        System.out.println("ANSWER PART 2:" + getTotalWinningsPart2(inputs));
    }

    private record Card(String hand, int bid) {
        @Override
        public String toString() {
            return hand + " " + bid;
        }
    }

    private static Integer getTotalWinningsPart1(List<String> inputs) {
        List<Card> cards = parseCards(inputs);
        cards.sort(new Part1Comparator());
        return calculateTotalWinnings(cards);
    }

    private static Integer getTotalWinningsPart2(List<String> inputs) {
        List<Card> cards = parseCards(inputs);
        cards.sort(new Part2Comparator());
        return calculateTotalWinnings(cards);
    }

    private static Integer calculateTotalWinnings(List<Card> cards) {
        int totalWinnings = 0;
        int rank = 1;
        for (Card card : cards) {
            totalWinnings += card.bid * rank;
            rank++;
        }
        return totalWinnings;
    }

    public static class Part1Comparator implements Comparator<Card> {
        /* -1 if o1 is less than the o2, 0 of o1 equals o2, 1 if o1 is bigger than o2 */
        @Override
        public int compare(Card card1, Card card2) {
            int rank1 = getBestRank(card1.hand); // 0 is highest
            int rank2 = getBestRank(card2.hand);

            if (rank1 == rank2) {
                return compareCardByCard(card1, card2, cardOrder1);
            }
            if (rank1 > rank2) {
                return -1;
            }
            return 1;
        }

    }

    private static int getBestRank(String hand) {
        int rank = -1;
        for (int i = 0; i < hands.size(); i++) {
            if (hands.get(i).test(hand)) {
                rank = i;
                break;
            }
        }
        return rank;
    }

    public static class Part2Comparator implements Comparator<Card> {
        /* -1 if o1 is less than the o2, 0 of o1 equals o2, 1 if o1 is bigger than o2 */
        @Override
        public int compare(Card card1, Card card2) {
            int rank1 = getBestRankForPermutations(card1.hand, getBestRank(card1.hand));
            int rank2 = getBestRankForPermutations(card2.hand, getBestRank(card2.hand));

            if (rank1 == rank2) {
                return compareCardByCard(card1, card2, cardOrder2);
            }
            if (rank1 > rank2) {
                return -1;
            }
            return 1;
        }

        private int getBestRankForPermutations(String hand, int maxRank) {
            int rank = maxRank;
            if (hand.contains("J") && maxRank > 0) {
                for (char c : cardOrder2.substring(0, cardOrder2.length()-1).toCharArray()) {
                    for (int i = 0; i < hand.toCharArray().length; i++) {
                        if (hand.toCharArray()[i] == 'J') {
                            String permutated = hand.substring(0, i) + c + hand.substring(i + 1);
                            int rankForPermutation = getBestRank(permutated);
                            rank = Integer.min(Integer.min(rank, rankForPermutation),
                                getBestRankForPermutations(permutated, rank));
                        }
                    }
                }
            }
            return rank;
        }

    }

    protected static int compareCardByCard(Card card1, Card card2, String cardOrder) {
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

    private static List<Card> parseCards(List<String> inputs) {
        return new ArrayList<>(inputs.stream()
            .map(s -> new Card(s.split(" ")[0], Integer.parseInt(s.split(" ")[1])))
            .toList());
    }

}
