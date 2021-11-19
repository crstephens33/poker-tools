package game;

import java.util.*;

public class FiveCardHand implements Comparable<FiveCardHand> {

    //Used in splitting ranks for full houses and two pairs
    public static final String TWO_RANK_DELIMITER = "-";

    public static final Map<HandName, Double> HAND_RANKINGS_MAP = initializeHandRankingsMap();

    private List<Card> cards;
    private HandRankEncoding handRankEncoding;


    public static void main(String[] args) {
        List<Card> cards = Card.readCardsFromString("Ac5c4d3d2h");
        new FiveCardHand(cards);
    }


    /**
     * Can be used to evaluate a hand preflop, flop, turn, or river given provided cards
     * @param cards
     */
    public FiveCardHand(List<Card> cards) {
        try {
            verifyHand(cards);
        } catch (Exception e) {
            System.out.println("Not creating hand: " + e.toString());
            return;
        }

        this.cards = cards;
        handRankEncoding = FiveCardHandOptimizationUtils.evaluateHand(cards);
    }

    private boolean verifyHand(List<Card> cards) {
        if(cards == null || cards.size() == 0) {
            throw new InputMismatchException("Cards empty or null");
        }
        if(cards.size() > 52)
            throw new InputMismatchException("Too many cards");

        if(cards.size() > 7)
            System.out.println("Warning: more than 7 cards found in hand " + cards.toString());

        HashSet<Card> cardSet = new HashSet<>();
        cards.forEach(card -> {
            if(cardSet.contains(card)) {
                throw new InputMismatchException("Duplicate cards " + card);
            }
            cardSet.add(card);
        });
        return true;
    }


    public int compareTo(FiveCardHand otherHand) {
        return handRankEncoding.compareTo(otherHand.getHandRankEncoding());
    }

    public String toString() {
        return Card.getHandStringFromCards(cards);
    }

    public HandRankEncoding getHandRankEncoding() {
        return handRankEncoding;
    }

    private static Map<HandName, Double> initializeHandRankingsMap(){
        Map<HandName, Double> map = new HashMap<>();
        map.put(HandName.ROYAL_FLUSH, 10.0);
        map.put(HandName.STRAIGHT_FLUSH, 9.0);
        map.put(HandName.FOUR_OF_A_KIND, 8.0);
        map.put(HandName.FULL_HOUSE, 7.0);
        map.put(HandName.FLUSH, 6.5);
        map.put(HandName.NFD, 6.4);
        map.put(HandName.FD, 6.3);
        map.put(HandName.BDSDFD, 6.2);
        map.put(HandName.BDNFD, 6.15);
        map.put(HandName.BDFD, 6.1);
        map.put(HandName.STRAIGHT, 5.5);
        map.put(HandName.OESD, 5.4);
        map.put(HandName.BDSD, 5.1);
        map.put(HandName.SET, 4.5);
        map.put(HandName.TRIPS, 4.4);
        map.put(HandName.THREE_OF_A_KIND, 4.0);
        map.put(HandName.TOP_TWO, 3.75);
        map.put(HandName.TWO_PAIR, 3.5);
        map.put(HandName.BOTTOM_TWO, 3.2);
        map.put(HandName.OVERPAIR, 2.7);
        map.put(HandName.TOP_PAIR, 2.5);
        map.put(HandName.ONE_PAIR, 2.0);
        map.put(HandName.TWO_OVERS, 2.0);
        map.put(HandName.HIGH_CARD, 1.0);
        return map;
    }

    public static HandRankEncoding evaluateHandValue(List<Card> cards) {
        return FiveCardHandOptimizationUtils.evaluateHand(cards);
    }

    /**
     * Determine the best 5-card hand from the given list of cards.
     * TODO: convert to using bit vectors to check hand ranks. 52-boolean vector, scan for patterns.
     * @param cards
     * @return
     *
    public static HandRankEncoding evaluateHandValue(List<Card> cards) {
        HandRankEncoding handRankEncoding = null;
        int cardCount = cards.size();
        boolean[] bitArray = getCardBitArray(cards);


        if(cardCount > 3) {
            String fourOfAKindRank = containsNOfAKind(countsByRank, 4);
            if (fourOfAKindRank != null) {
                Set<Card> excluded = findCardsToExcludeFromHighCard(cards, new HashSet<>(Collections.singletonList(fourOfAKindRank)));
                List<Card> highCard = getNextHighestNCardsExcluding(excluded, 1, cards);
                return new HandRankEncoding(FOUR_OF_A_KIND, fourOfAKindRank, highCard);
            }
        }


        if(cardCount > 4) {
            handRankEncoding = findFullHouseDetails(countsByRank);
            if(handRankEncoding != null) { return handRankEncoding; }

            handRankEncoding = findFlushDetails(descendingCards);
            if (handRankEncoding != null) {
                return handRankEncoding;
            }
        }

        if(cardCount > 4) {
            handRankEncoding = findStraightDetails(ascendingCards, null);
            if (handRankEncoding != null) {
                return handRankEncoding;
            }
        }

        if(cardCount > 2) {
            String threeOfAKindRank = containsNOfAKind(countsByRank, 3);
            if (threeOfAKindRank != null) {
                Set<Card> excluded = findCardsToExcludeFromHighCard(cards, new HashSet<>(Collections.singletonList(threeOfAKindRank)));
                List<Card> highCard = getNextHighestNCardsExcluding(excluded, 2, cards);
                return new HandRankEncoding(THREE_OF_A_KIND, threeOfAKindRank, highCard);
            }
        }

        if(cardCount > 3) {
            handRankEncoding = findTwoPairDetails(countsByRank, descendingCards);
            if (handRankEncoding != null) {
                return handRankEncoding;
            }
        }

        if(cardCount > 1) {
            handRankEncoding = findPairDetails(countsByRank, ascendingCards);
            if (handRankEncoding != null) {
                return handRankEncoding;
            }
        }

        cards.sort(Comparator.reverseOrder());
        return new HandRankEncoding(HIGH_CARD, cards.get(0).getRank(), cards);
    }



    /**
     * Assume list is ordered in DESCENDING order
     * @param excluded cards that cannot be included in the final list.
     * @param n
     * @param cards
     * @return
     *
    private static List<Card> getNextHighestNCardsExcluding(Set<Card> excluded, int n, List<Card> cards) {
        Queue<Card> remainingHighestNCards = new PriorityQueue<Card>();
        for(Card card : cards) {
            if(!excluded.contains(card)) {
                if(remainingHighestNCards.size() < n)
                    remainingHighestNCards.add(card);
                else if (card.getRankInt() > remainingHighestNCards.peek().getRankInt()) {
                    remainingHighestNCards.poll();
                    remainingHighestNCards.add(card);
                }
            }
        }
        List<Card> toReturn = new ArrayList<>(remainingHighestNCards);
        toReturn.sort(Comparator.reverseOrder()); //see if it sorts the right way. Want high to low.
        return toReturn;
    }

    private static Set<Card> findCardsToExcludeFromHighCard(List<Card> cards, Set<String> excludedRanks) {
        Set<Card> excluded = new HashSet<>();
        cards.forEach(card -> {
            if(excludedRanks.contains(card.getRank()))
                excluded.add(card);
        });
        return excluded;
    }


    /**
     * Checks to see if five cards of the same suit are present, and finds the highest card if so
     * @param cards
     * @return
     *
    private static HandRankEncoding findFlushDetails(List<Card> cards) {
        Map<String, List<Card>> suitMap = new HashMap<>();
        for(Card card : cards) { //already in descending order
            String suit = card.getSuit();
            List<Card> flushCards = suitMap.get(suit);
            if(!suitMap.containsKey(suit)) {
                suitmap.put(HandName.suit, new ArrayList<>(Collections.singletonList(card)));
            } else if (flushCards.size() == 4) {
                flushCards.add(card);
                return new HandRankEncoding(FLUSH, flushCards.get(0).getRank(), flushCards);
            } else {
                flushCards.add(card);
                suitmap.put(HandName.suit, flushCards);
            }
        }
        return null;
    }


    private static String containsNOfAKind(int[] vector, int n) {
        for(int i = vector.length - 1; i >= 0; i--) {
            if(vector[i] == n)
                return Card.getRankFromInt(i);
        }
        return null;
    }

    private static int[] getCardCountVector(List<Card> cards) {
        int[] vector = new int[Card.getValue(Card.ranks[Card.ranks.length-1]) + 1]; //size of vector is as large as the largest rank + 1 so index exists
        cards.forEach(card -> {
            vector[card.getRankInt()] = vector[card.getRankInt()] + 1;
        });
        return vector;
    }

    private static HandRankEncoding findFullHouseDetails(int[] vector) {
        String three = containsNOfAKind(vector, 3);
        if(three == null) {
            return null;
        }
        String pair = containsNOfAKind(vector, 2);
        if(pair == null)
            return null;
        return new HandRankEncoding(FULL_HOUSE, three+TWO_RANK_DELIMITER+pair, new ArrayList<>());
    }

    private static HandRankEncoding findTwoPairDetails(int[] vector, List<Card> cards) {
        Set<Card> excludedCards = new HashSet<>();
        String firstPair = containsNOfAKind(vector, 2);
        if(firstPair == null) {
            return null;
        }
        String secondPair = null;
        for(int index = Card.getValue(firstPair) - 1; index >= 0; index--) { //start looking for 2nd pair after index of first pair
            if(vector[index] == 2)
                secondPair = Card.getRankFromInt(index);

        }
        if(secondPair == null)
            return null;

        String finalSecondPair = secondPair;
        cards.forEach(card -> { //get set of cards involved in the pairs, so that high card can be determined correctly
            if(card.getRank().equals(firstPair) || card.getRank().equals(finalSecondPair)) {
                excludedCards.add(card);
            }
        });

        String largerPair = Card.getValue(firstPair) > Card.getValue(secondPair) ? firstPair : secondPair;
        String smallerPair = largerPair.equals(firstPair) ? secondPair : firstPair;
        List<Card> highCard = getNextHighestNCardsExcluding(excludedCards, 1, cards);
        return new HandRankEncoding(TWO_PAIR, largerPair+TWO_RANK_DELIMITER+smallerPair, highCard);
    }

    private static HandRankEncoding findPairDetails(int[] vector, List<Card> cards) {
        String pair = containsNOfAKind(vector, 2);
        if(pair == null)
            return null;

        Set<Card> excludedCards = new HashSet<>();
        cards.forEach( card -> { //get set of cards involved in the pairs, so that high card can be determined correctly
            if(card.getRank().equals(pair)) {
                excludedCards.add(card);
            }
        });

        List<Card> highCards = getNextHighestNCardsExcluding(excludedCards, 3, cards);
        return new HandRankEncoding(ONE_PAIR, pair, highCards);
    }


    /** Needs to be in ascending order
     *
     * @param cards
     * @return
     *
    private static HandRankEncoding findStraightDetails(List<Card> cards, Boolean wheel) {
        if(wheel == null)
            wheel = false;
        String straightRank = straightHelper(cards, wheel);
        if(straightRank != null)
            return new HandRankEncoding(STRAIGHT, straightRank, new ArrayList<>());

        // If we have not found a straight but there is an ace in the deck, re-call the method with the ace moved to the front
        if(cards.get(cards.size()-1).getRankInt() == Card.getValue("A")) {//if there is an Ace
            List<Card> wheelStraight = new ArrayList<>(Collections.singletonList(cards.get(cards.size()-1)));
            cards.forEach(card -> {
                if(!card.getRank().equals("A")) //added the ace, now add all the non aces.
                    wheelStraight.add(card);
            });
            return findStraightDetails(wheelStraight, true);
        }
        return null;
    }

    /**
     * Return the rank of the high card of the straight, null if no straight is found
     * @param cards
     * @return
     *
    private static String straightHelper(List<Card> cards, boolean wheel) {
        int count = 0;
        for(int i = 0; i < cards.size() - 1; i++) {
            int currentRank = cards.get(i).getRankInt();
            if(wheel && currentRank == 14)
                currentRank = 1;
            int nextRank = cards.get(i + 1).getRankInt();
            if(nextRank == currentRank + 1) {
                count++;
            } else if(nextRank > currentRank + 1) {
                count = 0;
            }
            if(count == 4) {
                return cards.get(i + 1).getRank();
            }
        }
        return null;
    }
*/

}