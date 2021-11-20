package poker.game.calculation;

import poker.game.basics.*;
import poker.game.basics.hand_evaluation.*;
import util.LogUtils;

import java.util.*;

public class FlopSee {

    public static void main(String[] args) {
        String hand1 = "Ts9s";
        HoleCards holeCard1 = HoleCards.getHoleCardsFromCombination(hand1);
        //HoleCards holeCard2 = HoleCards.getHoleCardsFromCombination(hand2);
        Map<HoleCards, Map<String, Double>> map = getFlopHitChartForHoleCards(Arrays.asList(holeCard1));
        printMap(map);
    }

    public static void updateFlopHitChartForFlop(HoleCards holeCards, Set<Card> flop, HandRankEncoding encoding, Map<String, Double> hitChart) {
        Set<String> floppedHands = getTypesOfHandForFlop(holeCards, flop); //unofficial hands
        floppedHands.add(encoding.handName); //official hand
        for(String handName : floppedHands) { //iterate over all existing hand terms
            hitChart.put(handName, hitChart.getOrDefault(handName, 0.0) + 1.0);
        }
    }

    /**
     * Returns the set of handTypes that apply to the hole cards + flop combination.
     * @param holeCards
     * @param flop
     * @return
     */
    private static Set<String> getTypesOfHandForFlop(HoleCards holeCards, Set<Card> flop) {
        Set<String> handNameSet = new HashSet<>();
        List<Card> allCards = new ArrayList<>(flop);
        allCards.addAll(holeCards.getCards());
        //find a way later to get around re-processing. Maybe store the preprocessed info somewhere
        PreprocessedAuxiliaryHandInfo preprocessedAuxiliaryHandInfo = new PreprocessedAuxiliaryHandInfo(allCards, holeCards, flop, new PreprocessedOfficialHandInfo(allCards));

        if(preprocessedAuxiliaryHandInfo.flushDraw) {
            handNameSet.add(HandName.FD.label);
        } else if(preprocessedAuxiliaryHandInfo.backdoorFlushDraw) {
            handNameSet.add(HandName.BDFD.label);
        }

        if(preprocessedAuxiliaryHandInfo.oesd) {
            handNameSet.add(HandName.OESD.label);
        } else if(preprocessedAuxiliaryHandInfo.backdoorStraightDraw) {
            handNameSet.add(HandName.BDSD.label);
            if(preprocessedAuxiliaryHandInfo.backdoorFlushDraw)
                handNameSet.add(HandName.BDSDFD.label);
        }

        if(preprocessedAuxiliaryHandInfo.set) {
            handNameSet.add(HandName.SET.label);
        } else if(preprocessedAuxiliaryHandInfo.trips) {
            handNameSet.add(HandName.TRIPS.label);
        }

        if(preprocessedAuxiliaryHandInfo.overpair)
            handNameSet.add(HandName.OVERPAIR.label);
        return handNameSet;
    }

    //THE PROJECT is really about determining the "cutoff points" for bets, calls, folds, bluffs on different boards and ranges.
    /**
     * Get the flop hit rate for different types of hands.
     * @param holeCardsList
     * @return
     */
    public static Map<HoleCards, Map<String, Double>> getFlopHitChartForHoleCards(List<HoleCards> holeCardsList) {
        Map<HoleCards, Map<String, Double>> holeCardsToHandFrequenciesMap = new HashMap<>();

        Set<Card> blockers = new HashSet<>();
        holeCardsList.forEach(holeCard -> blockers.addAll(holeCard.getCards()));
        if(blockers.size() % 2 != 0) {
            LogUtils.warn("odd number of blockers, possible repeated cards in hole cards.");
        }

        Deck deck = new Deck(blockers);

        List<Set<Card>> possibleFlops = Deck.getPossibleRunouts(deck.getCards(), 3);
        HandRankEncoding handRankEncoding;
        for(HoleCards holeCards : holeCardsList) {
            holeCardsToHandFrequenciesMap.put(holeCards, new HashMap<>());
            double flopCount = possibleFlops.size() * 1.0;
            Map<String, Double> handCountMap = holeCardsToHandFrequenciesMap.get(holeCards);
            for(HandName handName : HandName.values()) {
                handCountMap.put(handName.label, 0.0);
            }
            for(Set<Card> flop : possibleFlops) {
                List<Card> hand = new ArrayList<>();
                hand.addAll(flop);
                hand.addAll(holeCards.getCards());
                handRankEncoding = FiveCardHand.evaluateHandValue(hand);
                updateFlopHitChartForFlop(holeCards, flop, handRankEncoding, handCountMap);
            }
            handCountMap.replaceAll((t, v) -> (handCountMap.get(t) / flopCount) * 100.0); //divide all counts by to get the frequencies, mult. by 100 to get %
        }
        return holeCardsToHandFrequenciesMap;
    }

    /**
     * Print map of
     * @param map
     */
    private static void printMap(Map<HoleCards, Map<String, Double>> map) {
        List<HoleCards> holeCards = new ArrayList<>(map.keySet());
        holeCards.sort(null);
        List<String> handLabels = new ArrayList<>();
        String formatString = "%-20s";
        for(HoleCards holeCardKeys : map.keySet()) {
            if(handLabels.size() == 0) {
                handLabels = new ArrayList<>(map.get(holeCardKeys).keySet());
                handLabels.sort(new HandComparator()); //alphabetical for now - will sort by rank later
            }
        }
        System.out.printf(formatString, "");
        for(HoleCards hc : holeCards) {
            System.out.print(hc + "\t");
        }
        System.out.println();
        StringBuilder output = new StringBuilder();
        for(String label : handLabels) {
            System.out.printf(formatString, label);
            for(HoleCards hc : holeCards) {
                output.append(LogUtils.formatPercent(map.get(hc).get(label))).append("\t");
            }
            System.out.println(output);
            output = new StringBuilder();
        }
    }
    //need custom comparator for labels

    private static class HandComparator implements Comparator<String> {

        public HandComparator(){}

        public int compare(String one, String two) {
            return -1 * Double.compare(FiveCardHand.HAND_RANKINGS_MAP.getOrDefault(HandName.getValue(one), 0.0), FiveCardHand.HAND_RANKINGS_MAP.getOrDefault(HandName.getValue(two), 0.0));
        }
    }
}
