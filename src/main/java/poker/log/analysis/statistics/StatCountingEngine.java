package poker.log.analysis.statistics;

import poker.log.hand_history.HandHistory;
import poker.log.hand_history.Player;
import poker.log.hand_history.TableSize;

import java.util.*;

public class StatCountingEngine {

    private String playerName;
    private StatCountingResult statCountingResult;
    private StatCounter statCounter;


    public StatCountingEngine(String playerName) {
        this.playerName = Player.buildPlayerNameKey(playerName);
    }

    private void setStatCounter(StatCounter statCounter) {
        statCounter.setPlayerName(playerName);
        this.statCounter = statCounter;
        this.statCountingResult = null;
    }

    private String getConditionCountsWithPercent() {
        StringBuilder builder = new StringBuilder();
        builder.append(statCounter.statName).append(" results for ").append(playerName).append("\n");
        builder.append(statCounter.opportunityCondition.getName()).append(" count: ").append(statCountingResult.countsForCondition.get(statCounter.opportunityCondition.getName()));
        builder.append("\n");
        int opportunities = statCountingResult.countsForCondition.get(statCounter.opportunityCondition.getName());
        for(String conditionName : statCountingResult.countsForCondition.keySet()) {
            if(!conditionName.equals(statCounter.opportunityCondition.getName())) {
                int count = statCountingResult.countsForCondition.get(conditionName);
                builder.append(conditionName).append(" count: ").append(count).append(" (").append(getPct(count, opportunities)).append(")\n");
            }
        }
        return builder.toString();
    }

    public String getStatLines(StatCounter statCounter, List<HandHistory> handHistories, TableSize tableSize) {
        setStatCounter(statCounter);
        calculateStatsFromHandHistories(handHistories, tableSize);
        return getConditionCountsWithPercent();
    }
    
    private void calculateStatsFromHandHistories(Collection<HandHistory> handHistories, TableSize tableSize) {
        statCountingResult = statCounter.updateCountsForHands(tableSize, handHistories, false);
    }
    
//    protected abstract String getResultsForTableSize(TableSize tableSize);
//
//    protected abstract String getResultsForAllPositions();
//
//    protected abstract void calculateSingleHistory(HandHistory handHistory, Integer tableSize);
//
//    protected abstract Collection<Integer> getTableSizes();



    protected void incrementListEntry(Map<String, List<String>> map, String key, String valueToAdd) {        
        List<String> list = map.getOrDefault(key, new ArrayList<>());
        list.add(valueToAdd);
        map.put(key, list);
    }  

    protected static String getPct(int numerator, int denominator) {
        if(numerator == 0 || denominator == 0) return "0%";
        return pctFmt((double) numerator / (double) denominator);
    }

    protected static String pctFmt(Double d) {
        return String.format("%.1f", (d)*100.0) + "%";
    }

    protected static Double defaultNull(Double d) {
        return d == null ? 0.0 : d;
    }

    protected void combineResults(TableSize tableSize, StringBuilder result, Map<String, List<String>> shownCardsByAction) {
        for(String actionTableSize : shownCardsByAction.keySet()) {
            if(actionTableSize.contains(""+tableSize.toString())) {
                result
                    .append(actionTableSize)
                    .append("-handed: ")
                    .append(shownCardsByAction.get(actionTableSize))
                    .append("\n");
            }
        }
    }
}
