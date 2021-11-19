package game;

import java.util.*;

public class FiveCardHandOptimizationUtils {

    //revisit as a way more bit-based approach.

   /* private static final String STRAIGHT_FLUSH_MASK = "11111";
    private static final int STRAIGHT_FLUSH_INT = Integer.parseUnsignedInt(STRAIGHT_FLUSH_MASK);
    private static final String FOUR_OF_A_KIND_MASK = "1000000000000100000000000010000000000001000000000000";
    private static final int FOUR_OF_A_KIND_INT = Integer.parseUnsignedInt(FOUR_OF_A_KIND_MASK);*/

    public static void main(String[] args) {
        String hand = "8d7s4h5h6s";
        List<Card> cards = Card.readCardsFromString(hand);
        HandRankEncoding encoding = evaluateHand(cards);
        System.out.println(encoding.getPrettyDescription());
    }

    public static HandRankEncoding evaluateHand(List<Card> cards) {
        PreprocessedOfficialHandInfo preprocessedOfficialHandInfo = new PreprocessedOfficialHandInfo(cards);

        //straight flush/royal
        if(preprocessedOfficialHandInfo.flushSuit != null && preprocessedOfficialHandInfo.straightRank != null) { //only check if we have flush and straight
            PreprocessedOfficialHandInfo straightFlushInfo = new PreprocessedOfficialHandInfo(getFlushCards(preprocessedOfficialHandInfo.flushSuit, cards)); //use the flush cards to reprocess
            if(straightFlushInfo.straightRank != null) { //if we can make a straight from the flush cards, we have straight flush
                HandName royalOrStraight = straightFlushInfo.straightRank.equals(Card.ranks[Card.ranks.length-1]) ? HandName.ROYAL_FLUSH : HandName.STRAIGHT_FLUSH;
                return new HandRankEncoding(royalOrStraight, straightFlushInfo.straightRank, new ArrayList<>());
            }
        }

        //quads
        if(preprocessedOfficialHandInfo.topQuadRank != null)
            return new HandRankEncoding(HandName.FOUR_OF_A_KIND, preprocessedOfficialHandInfo.topQuadRank,
                    getNextHighestNCardsExcluding(new HashSet<>(Collections.singletonList(preprocessedOfficialHandInfo.topQuadRank)), 1, cards));

        //full house
        if(preprocessedOfficialHandInfo.topTripsRank != null && preprocessedOfficialHandInfo.topPairRank != null) {
            return new HandRankEncoding(HandName.FULL_HOUSE, preprocessedOfficialHandInfo.topTripsRank + FiveCardHand.TWO_RANK_DELIMITER + preprocessedOfficialHandInfo.topPairRank, new ArrayList<>());
        }

        //flush
        if(preprocessedOfficialHandInfo.flushSuit != null) {
            List<Card> flushCards = getFlushCards(preprocessedOfficialHandInfo.flushSuit, cards);
            return new HandRankEncoding(HandName.FLUSH, flushCards.get(0).getRank(), flushCards);
        }

        //straight
        if(preprocessedOfficialHandInfo.straightRank != null) {
            return new HandRankEncoding(HandName.STRAIGHT, preprocessedOfficialHandInfo.straightRank, new ArrayList<>());
        }

        //three of a kind
        if(preprocessedOfficialHandInfo.topTripsRank != null ) {
            return new HandRankEncoding(HandName.THREE_OF_A_KIND, preprocessedOfficialHandInfo.topTripsRank, getNextHighestNCardsExcluding(new HashSet<>(Collections.singletonList(preprocessedOfficialHandInfo.topTripsRank)), 2, cards));
        }

        //2 pair
        if(preprocessedOfficialHandInfo.topPairRank != null && preprocessedOfficialHandInfo.secondPairRank != null) {
            return new HandRankEncoding(HandName.TWO_PAIR, preprocessedOfficialHandInfo.topPairRank + FiveCardHand.TWO_RANK_DELIMITER + preprocessedOfficialHandInfo.secondPairRank,
                    getNextHighestNCardsExcluding(new HashSet<>(Arrays.asList(preprocessedOfficialHandInfo.topPairRank, preprocessedOfficialHandInfo.secondPairRank)), 1, cards));
        }

        //pair
        if(preprocessedOfficialHandInfo.topPairRank != null) {
            return new HandRankEncoding(HandName.ONE_PAIR, preprocessedOfficialHandInfo.topPairRank,
                    getNextHighestNCardsExcluding(new HashSet<>(Collections.singletonList(preprocessedOfficialHandInfo.topPairRank)), 3, cards));
        }

        //high card
        List<Card> highCards = getNextHighestNCardsExcluding(new HashSet<>(), 5, cards);
        return new HandRankEncoding(HandName.HIGH_CARD, highCards.get(0).getRank(), highCards);
    }

    private static List<Card> getFlushCards(String suit, List<Card> cards) {
        PriorityQueue<Card> sortedCards = new PriorityQueue<>(Comparator.reverseOrder());
        cards.forEach(card -> { if(card.getSuit().equals(suit)) sortedCards.add(card); });
        return new ArrayList<>(sortedCards);
    }

    private static BitSet getCardBitSet(List<Card> cards) {
        BitSet bitSet = new BitSet(52);
        cards.forEach(card -> bitSet.flip(getIndexForCard(card)));
        return bitSet;
    }

    /**
     * Computes index location for each card.
     * @param card
     * @return
     */
    private static int getIndexForCard(Card card) {
        int rank = card.getRankInt() - 1; //2->1, A->13
        int suit = card.getSuitInt();
        return suit * 13 + rank - 1;
    }




/**
 *
 * @param excludedRank ranks that cannot be included in the final list.
 * @param n
 * @param cards
 * @return
 */
private static List<Card> getNextHighestNCardsExcluding(Set<String> excludedRank, int n, List<Card> cards) {
    Queue<Card> remainingHighestNCards = new PriorityQueue<>();
    for(Card card : cards) {
        if(!excludedRank.contains(card.getRank())) {
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

}
