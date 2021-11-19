package log;

import java.util.*;
import game.*;


public class ThreeBetResponseAfterRFICalculator extends StatCalculator {

    private final Map<Integer, Double> threeBetFacedAfterRFIByTableSize = new HashMap<>();
    private final Map<Integer, Double> foldToThreeBetAfterRFIByTableSize = new HashMap<>();
    private final Map<Integer, Double> flatThreeBetByTableSize = new HashMap<>();
    private final Map<Integer, Double> fourBetByTableSize = new HashMap<>();

    private final String fold = "fold";
    private final String flat = "flat";
    private final String fourBet = "fourBet";
    private Map<String, List<String>> shownCardsByActionTableSize = new HashMap<>();

    public ThreeBetResponseAfterRFICalculator(String playerName) {
        super(playerName);
        statisticName = "ThreeBet Response ";
    }

    protected void calculateSingleHistory(HandHistory handHistory, Actions actions, Integer tableSize) {
        String shownCards = null;
        String action = null;
        if(actions.playerShowedCards(playerName)) {
            shownCards = actions.getPlayersShownCards(playerName);
        }
        if(actions.didPlayerRFI(playerName) && actions.isThreeBetPot()) {
            incrementDoubleValue(threeBetFacedAfterRFIByTableSize, tableSize);
        }
        if(actions.didPlayerFoldToThreeBetAfterRFI(playerName)) {
            incrementDoubleValue(foldToThreeBetAfterRFIByTableSize, tableSize);
            action = fold;
        }
        if(actions.didPlayerCallThreeBetAfterRFI(playerName)) {               
            incrementDoubleValue(flatThreeBetByTableSize, tableSize);             
            action = flat;
        }
        if(actions.didPlayerFourBetAfterRFI(playerName)) {
            incrementDoubleValue(fourBetByTableSize, tableSize);            
            action = fourBet;
        }

        if(action != null && shownCards != null) {
            incrementListEntry(shownCardsByActionTableSize, action+" "+tableSize, Card.convertToStartingHand(shownCards));
        }
    }

    protected String getResultsForTableSize(Integer tableSize){
        String result = "";
        String handed = tableSize + "-handed ";
        Double totalTimesThreeBetAfterRFI = threeBetFacedAfterRFIByTableSize.get(tableSize);
        Double foldToThreeBet = defaultNull(foldToThreeBetAfterRFIByTableSize.get(tableSize));
        Double foldToThreeBetPct = foldToThreeBet / totalTimesThreeBetAfterRFI;            
        Double flatThreeBet = defaultNull(flatThreeBetByTableSize.get(tableSize));
        Double flatThreeBetPct = flatThreeBet / totalTimesThreeBetAfterRFI;
        Double fourBet = defaultNull(fourBetByTableSize.get(tableSize));
        Double fourBetPct = fourBet / totalTimesThreeBetAfterRFI;
        
        result = result + handed + "Times RFI and faced 3 bet: " + totalTimesThreeBetAfterRFI + "\n";
        result = result + handed + "Fold: (" + pctFmt(foldToThreeBetPct) + ") Call (" + pctFmt(flatThreeBetPct) + ") 4-Bet: (" + pctFmt(fourBetPct) + ")\n";
        for(String actionTableSize : shownCardsByActionTableSize.keySet()) {
            if(actionTableSize.contains(""+tableSize)) {
                result = result + actionTableSize + "-handed: " + shownCardsByActionTableSize.get(actionTableSize) + "\n";
            }
        }
        return result;
    }

    protected String getResultsForAllPositions() {        
        /*dealtByPlayersPreflop.put(allSizes, new HashMap<>());
        vPIPByPlayersPreflop.put(allSizes, new HashMap<>());
        for(Integer tableSize : dealtByPlayersPreflop.keySet()) {
            Map<Position, Double> dealtForSize = dealtByPlayersPreflop.get(tableSize);
            for(Map.Entry<Position, Double> entry : dealtForSize.entrySet()) { //for every position, add by all the hands dealt at that position
                dealtByPlayersPreflop.get(allSizes).put(entry.getKey(), dealtByPlayersPreflop.get(allSizes).getOrDefault(entry.getKey(), 0.0) + entry.getValue());
            }
            Map<Position, Double> vpipForSize = vPIPByPlayersPreflop.get(tableSize);
            for(Map.Entry<Position, Double> entry : vpipForSize.entrySet()) { //for every position, add by all the hands dealt at that position
                vPIPByPlayersPreflop.get(allSizes).put(entry.getKey(), vPIPByPlayersPreflop.get(allSizes).getOrDefault(entry.getKey(), 0.0) + entry.getValue());
            }
        }
        return getResultsForTableSize(allSizes);*/
        return "";
    }

    protected Collection<Integer> getTableSizes() {
        return threeBetFacedAfterRFIByTableSize.keySet();
    }    
}
