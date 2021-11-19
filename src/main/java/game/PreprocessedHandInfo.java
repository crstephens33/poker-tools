package game;

import java.util.List;

public abstract class PreprocessedHandInfo {

    protected List<Card> cards;

    public PreprocessedHandInfo(){}

    public PreprocessedHandInfo(List<Card> cards) {
        this.cards = cards;
        processCards(cards);
    }

    protected void processCards(List<Card> cards) {
        int[][] vectors = getCardCountVector(cards);
        processRanks(vectors[0]);
        processSuits(vectors[1]);
    }

    private int[][] getCardCountVector(List<Card> cards) {
        //index of rank count = rank. so 2's count will exist at i = 2.
        int[] rankCountVector = new int[Card.getValue(Card.ranks[Card.ranks.length-1]) + 1]; //size of vector is as large as the largest rank + 1 so index exists
        int[] suitCountVector = new int[Card.suits.length];
        cards.forEach(card -> {
            rankCountVector[card.getRankInt()] = rankCountVector[card.getRankInt()] + 1;
            suitCountVector[card.getSuitInt()] = suitCountVector[card.getSuitInt()] + 1;
        });
        return new int[][]{rankCountVector, suitCountVector};
    }

    protected abstract void processSuits(int[] suitCountVector);

    protected abstract void processRanks(int[] rankCountVector);
}
