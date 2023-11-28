//package poker.log.analysis.statistics.statdefinitions.threebet;
//
//import poker.game.basics.Card;
//import poker.log.analysis.statistics.StatCountingEngine;
//import poker.log.analysis.statistics.StatUtils;
//import poker.log.hand_history.Actions;
//import poker.log.hand_history.HandHistory;
//
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//
//
//public class ThreeBetResponseAfterRFICountingEngine extends StatCountingEngine {
//
//    private final Map<Integer, Double> threeBetFacedAfterRFIByTableSize = new HashMap<>();
//    private final Map<Integer, Double> foldToThreeBetAfterRFIByTableSize = new HashMap<>();
//    private final Map<Integer, Double> flatThreeBetByTableSize = new HashMap<>();
//    private final Map<Integer, Double> fourBetByTableSize = new HashMap<>();
//
//    private final String fold = "fold";
//    private final String flat = "flat";
//    private final String fourBet = "fourBet";
//
//
//    public ThreeBetResponseAfterRFICountingEngine(String playerName) {
//        super(playerName);
//        statisticName = "ThreeBet Response ";
//    }
//
//
//    //find a clever way to generalize checking the criteria of "opportunity" for the statistic, finding action,
//    // labelling the action, and updating the counts of a map for that action key, in a neat way so that
//    // to add a new stat, one only needs to define a function critera, outcomes, and keys
//    protected void calculateSingleHistory(HandHistory handHistory, Integer tableSize) {
//        String action = null;
//        Actions actions = null;
//        String shownCards = checkShownCards( actions, playerName);
//        if(actions.didPlayerRFI(playerName) && actions.isThreeBetPot()) {
//            StatUtils.incrementDoubleValue(threeBetFacedAfterRFIByTableSize, tableSize);
//        }
//        if(actions.didPlayerFoldToThreeBetAfterRFI(playerName)) {
//            StatUtils.incrementDoubleValue(foldToThreeBetAfterRFIByTableSize, tableSize);
//            action = fold;
//        }
//        if(actions.didPlayerCallThreeBetAfterRFI(playerName)) {
//            StatUtils.incrementDoubleValue(flatThreeBetByTableSize, tableSize);
//            action = flat;
//        }
//        if(actions.didPlayerFourBetAfterRFI(playerName)) {
//            StatUtils.incrementDoubleValue(fourBetByTableSize, tableSize);
//            action = fourBet;
//        }
//
//        if(action != null && shownCards != null) {
//            incrementListEntry(shownCardsByAction, action+" "+tableSize, Card.convertToStartingHand(shownCards));
//        }
//    }
//
//    protected String getResultsForTableSize(Integer tableSize){
//        StringBuilder result = new StringBuilder();
//        String handed = tableSize + "-handed ";
//        Double totalTimesThreeBetAfterRFI = threeBetFacedAfterRFIByTableSize.get(tableSize);
//        Double foldToThreeBet = defaultNull(foldToThreeBetAfterRFIByTableSize.get(tableSize));
//        Double foldToThreeBetPct = foldToThreeBet / totalTimesThreeBetAfterRFI;
//        Double flatThreeBet = defaultNull(flatThreeBetByTableSize.get(tableSize));
//        Double flatThreeBetPct = flatThreeBet / totalTimesThreeBetAfterRFI;
//        Double fourBet = defaultNull(fourBetByTableSize.get(tableSize));
//        Double fourBetPct = fourBet / totalTimesThreeBetAfterRFI;
//
//        result.append(handed)
//                .append("Times RFI and faced 3 bet: ")
//                .append(totalTimesThreeBetAfterRFI)
//                .append("\n")
//                .append(handed)
//                .append("Fold: (")
//                .append(pctFmt(foldToThreeBetPct))
//                .append(") Call (")
//                .append(pctFmt(flatThreeBetPct))
//                .append(") 4-Bet: (")
//                .append(pctFmt(fourBetPct))
//                .append("\n");
//        combineResults(tableSize, result, shownCardsByAction);
//        return result.toString();
//    }
//
//    protected String getResultsForAllPositions() {
//        /*dealtByPlayersPreflop.put(allSizes, new HashMap<>());
//        vPIPByPlayersPreflop.put(allSizes, new HashMap<>());
//        for(Integer tableSize : dealtByPlayersPreflop.keySet()) {
//            Map<Position, Double> dealtForSize = dealtByPlayersPreflop.get(tableSize);
//            for(Map.Entry<Position, Double> entry : dealtForSize.entrySet()) { //for every position, add by all the hands dealt at that position
//                dealtByPlayersPreflop.get(allSizes).put(entry.getKey(), dealtByPlayersPreflop.get(allSizes).getOrDefault(entry.getKey(), 0.0) + entry.getValue());
//            }
//            Map<Position, Double> vpipForSize = vPIPByPlayersPreflop.get(tableSize);
//            for(Map.Entry<Position, Double> entry : vpipForSize.entrySet()) { //for every position, add by all the hands dealt at that position
//                vPIPByPlayersPreflop.get(allSizes).put(entry.getKey(), vPIPByPlayersPreflop.get(allSizes).getOrDefault(entry.getKey(), 0.0) + entry.getValue());
//            }
//        }
//        return getResultsForTableSize(allSizes);*/
//        return "";
//    }
//
//    protected Collection<Integer> getTableSizes() {
//        return threeBetFacedAfterRFIByTableSize.keySet();
//    }
//}
