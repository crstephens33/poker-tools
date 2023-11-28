//package poker.log.analysis.statistics.statdefinitions.rfiresponse;
//
//import poker.game.basics.Card;
//import poker.log.analysis.statistics.StatCountingEngine;
//import poker.log.analysis.statistics.StatUtils;
//import poker.log.hand_history.Actions;
//import poker.log.hand_history.HandHistory;
//
//import java.util.*;
//
//public class RFIResponseCountingEngine extends StatCountingEngine {
//
//    private final Map<Integer, Double> rfiFacedByPlayersPreflop = new HashMap<>();
//    private final Map<Integer, Double> foldToRFIByPlayersPreflop = new HashMap<>();
//    private final Map<Integer, Double> callRFIByPlayersPreflop = new HashMap<>();
//    private final Map<Integer, Double> threeBetRFIByPlayersPreflop = new HashMap<>();
//
//    private final String fold = "fold";
//    private final String call = "call";
//    private final String threeBet = "threeBet";
//
//    public RFIResponseCountingEngine(String playerName) {
//        super(playerName);
//        statisticName = "RFI Response";
//    }
//
//    protected void calculateSingleHistory(HandHistory handHistory, Integer tableSize) {
//        Actions actions = handHistory.getActions();
//        // this is what will be replaced by the criteria engine
//        String action = null;
//        String shownCards = checkShownCards(actions, playerName);
//        if(actions.rfiBeforePlayer(playerName)) {
//            StatUtils.incrementDoubleValue(rfiFacedByPlayersPreflop, tableSize);
//        }
//        if(actions.playerFoldedToRFI(playerName)) {
//            StatUtils.incrementDoubleValue(foldToRFIByPlayersPreflop, tableSize);
//            action = fold;
//        }
//        if(actions.didPlayerCallRFI(playerName)) {
//            StatUtils.incrementDoubleValue(callRFIByPlayersPreflop, tableSize);
//            action = call;
//        }
//        if(actions.didPlayerThreeBet(playerName)) {
//            StatUtils.incrementDoubleValue(threeBetRFIByPlayersPreflop, tableSize);
//            action = threeBet;
//        }
//        if(action != null && shownCards != null) {
//            incrementListEntry(shownCardsByAction, action+" "+tableSize, Card.convertToStartingHand(shownCards));
//        }
//    }
//
//    protected String getResultsForTableSize(Integer tableSize){
//        String result = "";
//        String handed = tableSize + "-handed ";
//        Double totalRFIFaced = rfiFacedByPlayersPreflop.get(tableSize);
//        Double foldToRFI = defaultNull(foldToRFIByPlayersPreflop.get(tableSize));
//        Double foldRFIPct = foldToRFI / totalRFIFaced;
//        Double callRFI = defaultNull(callRFIByPlayersPreflop.get(tableSize));
//        Double callRFIPct = callRFI / totalRFIFaced;
//        Double threeBet = defaultNull(threeBetRFIByPlayersPreflop.get(tableSize));
//        Double threeBetPct = threeBet / totalRFIFaced;
//
//        result = result + handed + "RFI Faced: " + totalRFIFaced + "\n";
//        result = result + handed + "folded to RFI: " + foldToRFI + " (" + pctFmt(foldRFIPct) + ")\n";
//        result = result + handed + "called RFI: " + callRFI + " (" + pctFmt(callRFIPct) + ")\n";
//        result = result + handed + "3-bet: " + threeBet + " (" + pctFmt(threeBetPct) + ")\n";
//        return result;
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
//        return rfiFacedByPlayersPreflop.keySet();
//    }
//
//}
