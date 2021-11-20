package poker.game.basics.hand_evaluation;

import poker.game.basics.Card;

import java.util.Comparator;
import java.util.List;

public class HandRankEncoding implements Comparable<HandRankEncoding> {

    /**
     * The name of the hand, i.e. "Full house".
     */
    public final String handName;

    /**
     * Card values used in the hand ranking. I.e., "A" in AAA98 three of a kind.
     */
    public final String rank;

    /**
     * Expected to be sorted in descending order for tiebreaking
     */
    public final List<Card> auxiliaryCardsForTiebreaking;

    public HandRankEncoding(HandName handName, String rank, List<Card> auxiliaryCardsForTiebreaking) {
        this.handName = handName.label;
        this.rank = rank;
        this.auxiliaryCardsForTiebreaking = auxiliaryCardsForTiebreaking;
        this.auxiliaryCardsForTiebreaking.sort(Comparator.reverseOrder()); //ensure it is in descending order.
    }

    @Override
    public boolean equals(Object other) {
        HandRankEncoding otherHand = (HandRankEncoding) other;
        return handName.equals(otherHand.handName) && rank.equals(otherHand.rank) && auxiliaryCardsForTiebreaking.equals(otherHand.auxiliaryCardsForTiebreaking);
    }

    @Override
    public int hashCode() {
        return handName.hashCode() * 13 + rank.hashCode() * 31 + auxiliaryCardsForTiebreaking.hashCode() * 3;
    }

    @Override
    public String toString() {
        return handName + " " + rank + " " + auxiliaryCardsForTiebreaking.toString();
    }

    public String getPrettyDescription() {
        switch (HandName.valueOf(handName)) {
            case ROYAL_FLUSH: return handName;
            case STRAIGHT_FLUSH:
                case FLUSH:
                case STRAIGHT: return handName + ", " + Card.getName(Card.getValue(rank), false) + " high.";
            case FOUR_OF_A_KIND:
                case THREE_OF_A_KIND:
                case ONE_PAIR: return handName + ", " + Card.getName(Card.getValue(rank), true) + ".";
            case FULL_HOUSE:
            case TWO_PAIR: {
                String term = (HandName.valueOf(handName) == HandName.FULL_HOUSE) ? " over " : " and ";
                return handName + ", " + Card.getName(Card.getValue(rank.split(FiveCardHand.TWO_RANK_DELIMITER)[0]), true) + term +
                        Card.getName(Card.getValue(rank.split(FiveCardHand.TWO_RANK_DELIMITER)[1]), true) + ".";
            }
            default: return HandName.HIGH_CARD + ", " + Card.getName(Card.getValue(rank), false) + ".";
        }
    }

    public int compareTo(HandRankEncoding other) {
        if(other == null) return 1;
        //first check if the hand names are not the same - pick the higher one if so
        if(!handName.equals(other.handName)) {
            return FiveCardHand.HAND_RANKINGS_MAP.get(HandName.valueOf(handName)).compareTo(FiveCardHand.HAND_RANKINGS_MAP.get(HandName.valueOf(other.handName)));
        }
        //hand names are the same - full house vs full house or pair vs pair etc.
        int rankComparison;
        //need special boat and two pair rank comparison. 77766 encoded as "7-6, []", 7766K encoded as "7-6, [K]".
        if(handName.equals(HandName.FULL_HOUSE.label) || handName.equals(HandName.TWO_PAIR.label)) {
            int twoRankComparison = twoRankCompareHelper(rank, other.rank);
            if(twoRankComparison != 0) //if a winner determined, return the winner
                return twoRankComparison;
            //no winner, go to tiebreaking procedures...
            //no tiebreaking for full houses - no kickers.
            if(handName.equals(HandName.FULL_HOUSE.label))
                return 0;
        } else {
            rankComparison = Card.getValue(rank).compareTo(Card.getValue(other.rank));
            if (rankComparison != 0) //if ranks are different, use that
                return rankComparison;
        }
        //if ranks are the same, do high tiebreaking procedures
        //if no auxiliary tiebreaking data exists, hands tie
        if(auxiliaryCardsForTiebreaking == null || auxiliaryCardsForTiebreaking.size() == 0)
            return 0;

        //if high card tiebreaker exists, use it
        Integer thisRank, otherRank;
        int highCardComparison;
        for (int i = 0; i < auxiliaryCardsForTiebreaking.size(); i++) {
            thisRank = auxiliaryCardsForTiebreaking.get(i).getRankInt();
            otherRank = other.auxiliaryCardsForTiebreaking.get(i).getRankInt();
            highCardComparison = thisRank.compareTo(otherRank);
            if(highCardComparison != 0)
                return highCardComparison;
        }
        //if we have not found a high card tiebreaker, hands are even.
        return 0;
    }


    /**
     * Given two strings each one separated by the TWO_RANK_DELIMITER, compares the ranks based on first rank then second.
     * @param thisRank
     * @param otherRank
     * @return
     */
    private int twoRankCompareHelper(String thisRank, String otherRank) {
        String[] theseRanks = thisRank.split(FiveCardHand.TWO_RANK_DELIMITER);
        String[] otherRanks = otherRank.split(FiveCardHand.TWO_RANK_DELIMITER);
        int higherRankComparison = Card.getValue(theseRanks[0]).compareTo(Card.getValue(otherRanks[0]));
        if(higherRankComparison != 0) //if first rank is split, use that
            return higherRankComparison;
        return Card.getValue(theseRanks[1]).compareTo(Card.getValue(otherRanks[1])); //ok to return 0 if first ranks and second ranks match
    }
}
