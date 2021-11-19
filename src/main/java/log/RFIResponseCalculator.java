package log;

import java.util.*;

public class RFIResponseCalculator extends StatCalculator {

    private Map<Integer, Double> rfiFacedByPlayersPreflop = new HashMap<>();    
    private Map<Integer, Double> foldToRFIByPlayersPreflop = new HashMap<>();
    private Map<Integer, Double> callRFIByPlayersPreflop = new HashMap<>();
    private Map<Integer, Double> threeBetRFIByPlayersPreflop = new HashMap<>();

    public RFIResponseCalculator(String playerName) {
        super(playerName);
        statisticName = "RFI Response";
    }

    protected void calculateSingleHistory(HandHistory handHistory, Actions actions, Integer tableSize) {
        if(actions.rfiBeforePlayer(playerName)) {                            
            incrementDoubleValue(rfiFacedByPlayersPreflop, tableSize);
        }
        if(actions.playerFoldedToRFI(playerName)) {                
            incrementDoubleValue(foldToRFIByPlayersPreflop, tableSize);            
        }
        if(actions.didPlayerCallRFI(playerName)) {               
            incrementDoubleValue(callRFIByPlayersPreflop, tableSize);             
        }
        if(actions.didPlayerThreeBet(playerName)) {
            incrementDoubleValue(threeBetRFIByPlayersPreflop, tableSize);            
        }
    }

    protected String getResultsForTableSize(Integer tableSize){
        String result = "";
        String handed = tableSize + "-handed ";
        Double totalRFIFaced = rfiFacedByPlayersPreflop.get(tableSize);
        Double foldToRFI = defaultNull(foldToRFIByPlayersPreflop.get(tableSize));
        Double foldRFIPct = foldToRFI / totalRFIFaced;            
        Double callRFI = defaultNull(callRFIByPlayersPreflop.get(tableSize));
        Double callRFIPct = callRFI / totalRFIFaced;
        Double threeBet = defaultNull(threeBetRFIByPlayersPreflop.get(tableSize));
        Double threeBetPct = threeBet / totalRFIFaced;
        
        result = result + handed + "RFI Faced: " + totalRFIFaced + "\n";
        result = result + handed + "folded to RFI: " + foldToRFI + " (" + pctFmt(foldRFIPct) + ")\n";
        result = result + handed + "called RFI: " + callRFI + " (" + pctFmt(callRFIPct) + ")\n";
        result = result + handed + "3-bet: " + threeBet + " (" + pctFmt(threeBetPct) + ")\n";        
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
        return rfiFacedByPlayersPreflop.keySet();
    }
    
}
