package poker.game;

import poker.log.HandHistory;
import util.LogUtils;

import java.util.*;

public class Simulation {

    public static final int SIMULATION_COUNT = 1712304; //48 choose 5

    /**
     * Return array of hand1 win%, tie%, and hand2 win%
     * @param hand1
     * @param hand2
     * @return
     */
    public static double[] determineHandEquity(HoleCards hand1, HoleCards hand2, Set<Card> initialBoard) {
        Set<Card> deckBlockers = new HashSet<>();
        deckBlockers.addAll(hand1.getCards());
        deckBlockers.addAll(hand2.getCards());
        deckBlockers.addAll(initialBoard);

        double hand1Wins = 0.0;
        double hand2Wins = 0.0;
        double ties = 0.0;
        double runs = 0.0;
        Deck deck = new Deck(deckBlockers);
        long totalHandEvaluationTime = 0L;
        long otherComputationTotal = 0L;

        FiveCardHand fiveCardHand1 = null;
        FiveCardHand fiveCardHand2 = null;

        long runoutTime = System.currentTimeMillis();
        List<Set<Card>> possibleRunouts = Deck.getPossibleRunouts(deck.getCards(), 5 - initialBoard.size());
        long runoutTimeEnd = System.currentTimeMillis();
        System.out.println("Total runout time: " + LogUtils.convertLongMillisToSeconds(runoutTimeEnd - runoutTime));
        for(Set<Card> runout : possibleRunouts) {
            long otherComputation = System.currentTimeMillis();
            List<Card> hand1List = new ArrayList<>(runout);
            hand1List.addAll(hand1.getCards());
            List<Card> hand2List = new ArrayList<>(runout);
            hand2List.addAll(hand2.getCards());
            long otherComputation2 = System.currentTimeMillis();
            otherComputationTotal += otherComputation2 - otherComputation;

            long preComputation = System.currentTimeMillis();
            fiveCardHand1 = new FiveCardHand(hand1List);
            fiveCardHand2 = new FiveCardHand(hand2List);
            long postComputation = System.currentTimeMillis();
            totalHandEvaluationTime += (postComputation - preComputation);

            otherComputation = System.currentTimeMillis();
            int winner = fiveCardHand1.compareTo(fiveCardHand2);

            if(winner > 0)
                hand1Wins += 1.0;
            else if(winner < 0)
                hand2Wins += 1.0;
            else
                ties += 1.0;

            runs += 1.0;
            otherComputation2 = System.currentTimeMillis();
            otherComputationTotal += otherComputation2 - otherComputation;
        }
        System.out.println("Avg hand evaluation time: " + LogUtils.inMillis(1.0* totalHandEvaluationTime / possibleRunouts.size()));
        System.out.println("Avg other computation time: " + LogUtils.inMillis(1.0 * otherComputationTotal / possibleRunouts.size()));
        System.out.println("Total hand evaluation time: " + LogUtils.convertLongMillisToSeconds(totalHandEvaluationTime));
        System.out.println("Total other computation time: " + LogUtils.convertLongMillisToSeconds(otherComputationTotal));
        return new double[] { (hand1Wins / runs) * 100.0, (ties / runs) * 100.0, (hand2Wins / runs) * 100.0};
    }

    /**
     * Get the flop hit rate for different types of hands.
     * @param holeCardsList
     * @return
     */
    public static Map<HoleCards, Map<String, Double>> getFlopHitChart(List<HoleCards> holeCardsList) {
        Map<HoleCards, Map<String, Double>> holeCardsToHandFrequenciesMap = new HashMap<>();

        Set<Card> blockers = new HashSet<>();
        holeCardsList.forEach(holeCard -> blockers.addAll(holeCard.getCards()));
        if(blockers.size() % 2 != 0) {
            LogUtils.warn("odd number of blockers, possible repeated cards in hole cards.");
        }

        Deck deck = new Deck(blockers);

        List<Set<Card>> possibleFlops = Deck.getPossibleRunouts(deck.getCards(), 3);
        HandRankEncoding handRankEncoding = null;
        String handName = null;
        for(HoleCards holeCards : holeCardsList) {
            holeCardsToHandFrequenciesMap.put(holeCards, new HashMap<>());
            double flopCount = possibleFlops.size() * 1.0;
            Map<String, Double> handCountMap = holeCardsToHandFrequenciesMap.get(holeCards);
            for(Set<Card> flop : possibleFlops) {
                List<Card> hand = new ArrayList<>();
                hand.addAll(flop);
                hand.addAll(holeCards.getCards());
                handRankEncoding = FiveCardHand.evaluateHandValue(hand);
                handName = handRankEncoding.handName;
                Double handCount = handCountMap.getOrDefault(handName, 0.0);
                handCountMap.put(handName, handCount + 1.0);
            }
            handCountMap.replaceAll((t, v) -> handCountMap.get(t) / flopCount); //divide all counts by to get the frequencies, mult. by 100 to get %
        }
        System.out.println(holeCardsToHandFrequenciesMap);
        return holeCardsToHandFrequenciesMap;
    }

}
