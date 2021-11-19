import poker.game.Card;
import poker.game.Deck;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class DeckTest {

    @Test
    public void testDeckCreation() {
        Set<Card> dealtCards = new HashSet<>();
        Deck deck = new Deck();
        for(int i = 0; i < 52; i++) {
            Card dealtCard = deck.dealCard();
            assert(!dealtCards.contains(dealtCard));
            dealtCards.add(dealtCard);
        }
    }

    @Test
    public void testDeckBlockers() {
        Deck blockersDeck = new Deck();
        Set<Card> blockers = new HashSet<>();
        //deal 10 blockers
        int blockerCount = 10;
        for(int i = 0; i < blockerCount; i++) {
            blockers.add(blockersDeck.dealCard());
        }

        System.out.println("Blockers dealt: " + blockers.toString());

        Deck deck = new Deck(blockers);
        for(int i = 0; i < 52 - blockerCount; i++) {
            Card dealtCard = deck.dealCard();
            assert(!blockers.contains(dealtCard));
        }
    }
}
