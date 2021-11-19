package poker.log;

import java.util.*;

public abstract class StatCalculator {    

    protected String playerName;

    protected String statisticName;

    public StatCalculator(String playerName) {
        this.playerName = Player.buildPlayerNameKey(playerName);
    }  

    public String getResults() {
        StringBuilder builder = new StringBuilder();
        builder.append(statisticName).append(" results for ").append(playerName).append("\n");
        for(Integer tableSize : getTableSizes()) {
            builder.append(getResultsForTableSize(tableSize)).append("\n");
        }
        builder.append(getResultsForAllPositions());

        return builder.toString();
    }
    
    
    public void calculateStatsFromHandHistories(Collection<HandHistory> handHistories) {
        for(HandHistory handHistory : handHistories) {
            Actions actions = handHistory.getActions();
            Integer tableSize = handHistory.getPreflopPlayers().size(); //how many players were dealt cards preflop
            calculateSingleHistory(handHistory, actions, tableSize);
        }
    }
    
    protected abstract String getResultsForTableSize(Integer tableSize);

    protected abstract String getResultsForAllPositions();

    protected abstract void calculateSingleHistory(HandHistory handHistory, Actions actions, Integer tableSize);

    protected abstract Collection<Integer> getTableSizes();

    protected void incrementDoubleValue(Map<Integer, Double> map, Integer key) {        
        map.put(key, map.getOrDefault(key, 0.0) + 1.0);
    }  

    //don't think there is a way around having this code - need specified types to allow a "put" to work.
    protected void incrementDoubleValue(Map<Position, Double> map, Position key) {        
        map.put(key, map.getOrDefault(key, 0.0) + 1.0);
    }  

    protected void incrementListEntry(Map<String, List<String>> map, String key, String valueToAdd) {        
        List<String> list = map.getOrDefault(key, new ArrayList<String>()); 
        list.add(valueToAdd);
        map.put(key, list);
    }  

    protected static String pctFmt(Double d) {
        return String.format("%.1f", (d)*100.0) + "%";
    }

    protected static Double defaultNull(Double d) {
        return d == null ? 0.0 : d;
    }
}
