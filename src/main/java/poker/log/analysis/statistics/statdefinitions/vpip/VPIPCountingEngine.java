//package poker.log.analysis.statistics.statdefinitions.vpip;
//
//import poker.log.analysis.statistics.StatCountingEngine;
//import poker.log.analysis.statistics.StatUtils;
//import poker.log.hand_history.HandHistory;
//import poker.log.hand_history.Position;
//
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//
//public class VPIPCountingEngine extends StatCountingEngine {
//
//    private final Integer allSizes = 1000;
//
//    private Map<Integer, Map<Position, Double>> dealtByPlayersPreflop = new HashMap<>();
//    private Map<Integer, Map<Position, Double>> vPIPByPlayersPreflop = new HashMap<>();
//
//    public VPIPCountingEngine(String playerName) {
//        super(playerName);
//        statisticName = "VPIP";
//    }
//
//    protected void calculateSingleHistory(HandHistory handHistory, Integer tableSize) {
//        Position position = handHistory.getPositionForPlayer(playerName);
//        if(handHistory.getPreflopPlayers().contains(playerName)) {
//            Map<Position, Double> dealtByPreflopForSize = dealtByPlayersPreflop.getOrDefault(tableSize, new HashMap<>());
//            StatUtils.incrementDoubleValue(dealtByPreflopForSize, position);
//            dealtByPlayersPreflop.put(tableSize, dealtByPreflopForSize);
//        }
//        if(handHistory.getActions().didPlayerVPIPPreFlop(playerName)) {
//            Map<Position, Double> vpipByPositionForSize = vPIPByPlayersPreflop.getOrDefault(tableSize, new HashMap<>());
//            StatUtils.incrementDoubleValue(vpipByPositionForSize, position);
//            vPIPByPlayersPreflop.put(tableSize, vpipByPositionForSize);
//        }
//    }
//
//    protected String getResultsForTableSize(Integer tableSize){
//        String handed = tableSize + "-handed ";
//        if(handed.contains("2"))
//            handed = "Heads up ";
//        if(handed.contains(""+ allSizes))
//            handed = "All sizes ";
//        StringBuilder resultBuilder = new StringBuilder();
//        for(Position position : dealtByPlayersPreflop.get(tableSize).keySet()) {
//            if(position == null) continue;
//
//            Double totalDealt = dealtByPlayersPreflop.getOrDefault(tableSize, new HashMap<>()).get(position);
//            Double handsVPIPd = vPIPByPlayersPreflop.getOrDefault(tableSize, new HashMap<>()).get(position);
//            handsVPIPd = handsVPIPd == null ? 0.0 : handsVPIPd;
//            resultBuilder.append(handed + "["+position+"] hands dealt in : " + totalDealt + "\n");
//            resultBuilder.append(handed + "["+position+"] VPIP: " + handsVPIPd + "\n");
//            resultBuilder.append(handed + "["+position+"] VPIP%: " + String.format("%.1f", (handsVPIPd / totalDealt)*100.0) + "%" + "\n\n");
//        }
//        return resultBuilder.toString();
//    }
//
//    protected String getResultsForAllPositions() {
//        dealtByPlayersPreflop.put(allSizes, new HashMap<>());
//        vPIPByPlayersPreflop.put(allSizes, new HashMap<>());
//        for(Integer tableSize : dealtByPlayersPreflop.keySet()) {
//            Map<Position, Double> dealtForSize = dealtByPlayersPreflop.getOrDefault(tableSize, new HashMap<>());
//            for(Map.Entry<Position, Double> entry : dealtForSize.entrySet()) { //for every position, add by all the hands dealt at that position
//                dealtByPlayersPreflop.get(allSizes).put(entry.getKey(), dealtByPlayersPreflop.get(allSizes).getOrDefault(entry.getKey(), 0.0) + entry.getValue());
//            }
//            Map<Position, Double> vpipForSize = vPIPByPlayersPreflop.getOrDefault(tableSize, new HashMap<>());
//            for(Map.Entry<Position, Double> entry : vpipForSize.entrySet()) { //for every position, add by all the hands dealt at that position
//                vPIPByPlayersPreflop.get(allSizes).put(entry.getKey(), vPIPByPlayersPreflop.get(allSizes).getOrDefault(entry.getKey(), 0.0) + entry.getValue());
//            }
//        }
//        return getResultsForTableSize(allSizes);
//    }
//
//    protected Collection<Integer> getTableSizes() {
//        return dealtByPlayersPreflop.keySet();
//    }
//
//}
