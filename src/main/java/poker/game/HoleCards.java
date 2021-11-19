package poker.game;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HoleCards implements Comparable<HoleCards> {

    //Represents the two hole cards held by a player. Consider embedding board/state info so that you can "sort" hole cards by value/bluff given texture
    //Actually, have a comparisons class that will do custom comparisons given preflop ranges + board etc. <--- Yes.
    //Hole card generations is done in range class.

    //Create hole cards using two Card objects
    private final Card largerCard;
    private final Card smallerCard;

    public HoleCards (Card card1, Card card2) {
        if(card1 == null || card2 == null) {
            throw new UnsupportedOperationException("Both cards must not be null");
        }
        if(card1.equals(card2)) {
            throw new UnsupportedOperationException("Hole cards cannot match");
        }
        if(card1.compareTo(card2) > 0) {
            this.largerCard = card1;
            this.smallerCard = card2;
            return;
        }
        this.largerCard = card2;
        this.smallerCard = card1;
    }

    /**
     * Creates and returns hole cards from 4 card combination string
     * @param combination
     * @return
     */
    public static HoleCards getHoleCardsFromCombination(String combination) {
        if(combination == null || combination.length() != 4)
            throw new UnsupportedOperationException("Combination string must be 4 characters long i.e. AdKd");
        Card card1 = new Card(combination.substring(0, 2));
        Card card2 = new Card(combination.substring(2, 4));
        return new HoleCards(card1, card2);
    }

    public boolean containsCard(Card toContain) {
        return toContain != null && (toContain.equals(largerCard) || toContain.equals(smallerCard));
    }

    public boolean isPocketPair() {
        return largerCard.getRankInt() == smallerCard.getRankInt();
    }

    public boolean containsRank(String rank) {
        return largerCard.getRank().equals(rank) || smallerCard.getRank().equals(rank);
    }

    public String toString() {
        return largerCard.toString() + smallerCard.toString();
    }

    @Override
    public int hashCode() {
        return largerCard.hashCode() + smallerCard.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof HoleCards) {
            HoleCards otherHoleCards = (HoleCards) other;
            return otherHoleCards.getLargerCard().equals(largerCard) && otherHoleCards.getSmallerCard().equals(smallerCard);
        }
        return false;
    }

    /**
     * Compares HoleCards objects. HoleCards object is larger than another if its largest card rank is larger than other's largest rank,
     * if the two largest cards are equal in rank then compare smaller card rank, if they are the same defer to larger card suit.
     * @param other
     * @return
     */
    public int compareTo(HoleCards other) {
        int largerComparison = largerCard.compareRank(other.getLargerCard());
        if(largerComparison != 0)
            return largerComparison;
        int smallerComparison = smallerCard.compareRank(other.getSmallerCard());
        if(smallerComparison != 0)
            return smallerComparison;
        int largerSuitComparison = largerCard.getSuit().compareTo(other.getLargerCard().getSuit());
        if(largerSuitComparison != 0)
            return largerSuitComparison;
        return smallerCard.getSuit().compareTo(other.getSmallerCard().getSuit());
    }

    public Card getLargerCard() {
        return largerCard;
    }

    public Card getSmallerCard() {
        return smallerCard;
    }

    public Set<Card> getCards() {
        return new HashSet<>(Arrays.asList(largerCard, smallerCard));
    }

}
