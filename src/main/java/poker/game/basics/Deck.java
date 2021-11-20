package poker.game.basics;

import java.util.*;

public class Deck {

    private static final int NUM_SHUFFLES = 1;

    private List<Card> cards = new ArrayList<>();

    public Deck() {
        fillDeck(new HashSet<>()); //fill the deck without blockers
        shuffle();
    }

    /**
     * Fills the deck without the cards provided
     * @param blockers
     */
    public Deck(Set<Card> blockers) {
        fillDeck(blockers);
        shuffle();
    }

    public void fillDeck(Set<Card> blockers) {
        for(String rank : Card.ranks) {
            for(String suit : Card.suits) {
                Card toAdd = new Card(rank + suit);
                if(!blockers.contains(toAdd))
                    cards.add(toAdd);
            }
        }
    }

    public void shuffle() {
        for(int i = 0; i < NUM_SHUFFLES; i++) {
            Collections.shuffle(cards, new Random(System.currentTimeMillis()));
        }
    }

    public List<Card> getCards() {
        return cards;
    }

    /**
     * Removes card from deck
     * @return removed card
     */
    public Card dealCard() {
        Card drawnCard = cards.get(0);
        cards.remove(0);
        return drawnCard;
    }

    public Set<Card> dealNCards(int n) {
        Set<Card> dealtCards = new HashSet<>();
        for(int i = 0 ; i < n; i++) {
            dealtCards.add(dealCard());
        }
        return dealtCards;
    }

    private static void runoutHelper(List<Card> superSet, int k, int idx, Set<Card> current, List<Set<Card>> solution) {
        //successful stop clause
        if (current.size() == k) {
            solution.add(new HashSet<>(current));
            return;
        }
        //unseccessful stop clause
        if (idx == superSet.size()) return;
        Card x = superSet.get(idx);
        current.add(x);
        //"guess" x is in the subset
        runoutHelper(superSet, k, idx+1, current, solution);
        current.remove(x);
        //"guess" x is not in the subset
        runoutHelper(superSet, k, idx+1, current, solution);
    }


    /**
     * Get all possible "choose"-sized combinations of runouts.
     * @param deck
     * @param choose
     * @return
     */
    public static List<Set<Card>> getPossibleRunouts(List<Card> deck, int choose) {
        List<Set<Card>> res = new ArrayList<>();
        runoutHelper(deck, choose, 0, new HashSet<>(), res);
        System.out.println( res.size() + " runouts collected.");
        return res;
    }
}
