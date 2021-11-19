import game.Card;
import game.FiveCardHand;
import game.FiveCardHandOptimizationUtils;
import game.HandRankEncoding;
import org.junit.jupiter.api.Test;
import util.OutputVerifier;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HandTest {

    @Test
    public void testIndifferenceToOrdering() throws Exception {
        //shuffle the same set of cards five times and make sure the rank object is the same.
        Map<String, List<String>> handStringToExpectedHandDetails = OutputVerifier.parseFileForInputsAndExpectedOutputs("src/test/resources/hand/ExpectedHands.txt");
        for(String handString : handStringToExpectedHandDetails.keySet()) {
            List<Card> cards = Card.readCardsFromString(handString);
            HandRankEncoding description = FiveCardHand.evaluateHandValue(cards);
            System.out.println(handString + ": " + description);
            for(int i = 0; i < 50; i++) {
                Collections.shuffle(cards);
                HandRankEncoding shuffledDescription = FiveCardHand.evaluateHandValue(cards);
                assertEquals(description, shuffledDescription);
            }
        }
    }

    @Test
    public void testSanity() {
        //52 card hand should have royal flush
        FiveCardHand hand = new FiveCardHand(Card.readCardsFromString("5c5c"));
        assertNull(hand.getHandRankEncoding());
    }

    @Test
    public void testHands() throws Exception {
        Map<String, List<String>> handStringToExpectedHandDetails = OutputVerifier.parseFileForInputsAndExpectedOutputs("src/test/resources/hand/ExpectedHands.txt");
        for(String handString : handStringToExpectedHandDetails.keySet()) {
            List<Card> cards = Card.readCardsFromString(handString);
            Set<String> expectedTerms = new HashSet<>(handStringToExpectedHandDetails.get(handString));
            HandRankEncoding description = FiveCardHand.evaluateHandValue(cards);
            System.out.println(handString + ": " + description);
            assert(expectedTerms.contains(description.handName));
            assert(expectedTerms.contains(description.rank));
            String auxCardString = Card.getHandStringFromCards(description.auxiliaryCardsForTiebreaking);
            if(auxCardString.length() > 0)
                assert(expectedTerms.contains(auxCardString));
            else
                assert(expectedTerms.size() == 2);
        }
    }

    @Test
    public void testHandWinners() throws Exception {
        Map<String, List<String>> handsAndWinnerIndicator = OutputVerifier.parseFileForInputsAndExpectedOutputs("src/test/resources/hand/ExpectedHandWinners.txt");
        for(String handString : handsAndWinnerIndicator.keySet()) {
            String[] hands = handString.split(" v ");
            FiveCardHand hand1 = new FiveCardHand(Card.readCardsFromString(hands[0]));
            FiveCardHand hand2 = new FiveCardHand(Card.readCardsFromString(hands[1]));
            int actualWinner = hand1.compareTo(hand2);
            int expectedWinner = Integer.parseInt(handsAndWinnerIndicator.get(handString).get(0));
            assertEquals(expectedWinner, actualWinner);
            String prefix = hand1 + " v " + hand2 + ", ";
            if(actualWinner == 0) {
                System.out.println(prefix + "tie.");
                continue;
            }
            String winningHand = actualWinner == 1 ? hand1.toString() : hand2.toString();
            System.out.println(prefix + "winner: " + winningHand);
        }
    }

    @Test
    public void testHandsLookNice() throws Exception {
        Map<String, List<String>> handsAndWinnerIndicator = OutputVerifier.parseFileForInputsAndExpectedOutputs("src/test/resources/hand/ExpectedHands.txt");
        for(String handString : handsAndWinnerIndicator.keySet()) {
            FiveCardHand hand1 = new FiveCardHand(Card.readCardsFromString(handString));
            System.out.println(handString + ": " + hand1.getHandRankEncoding().getPrettyDescription());
        }
    }
}
