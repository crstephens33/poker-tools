package poker.log.hand_history;

import poker.log.parsing.LogUtils;

import java.util.*;
import java.lang.Override;

public class HandHistory {
    
    public final static String PREFLOP_STREET = "preflop";
    public final static String FLOP_STREET = "flop";
    public final static String TURN_STREET = "turn";
    public final static String RIVER_STREET = "river";
    public final static String[] streets = new String[]{FLOP_STREET, TURN_STREET, RIVER_STREET};

    private final Actions actions = new Actions();
    public Actions getActions() { return actions; }

    private Map<String, Position> playerPositions = new HashMap<>();
    private List<String> handLines;
    private String fileTitle;
    private String lastThreeLines;
    private String handNumber = null;
    private Set<String> preflopPlayers = new HashSet<>();  
    private Set<String> flopPlayers = new HashSet<>();
    private Set<String> turnPlayers = new HashSet<>();
    private Set<String> riverPlayers = new HashSet<>();
    private Double winningPot = 0.0;  
    private Set<String> handWinners = new HashSet<>();
    private boolean ignoreHand = false;

    public HandHistory(List<String> handLines, String fileTitle) {
        this.handLines = handLines; 
        this.fileTitle = fileTitle;
        analyzeHand();       
    }

    public void analyzeHand() {
        if(handLines == null) {
            System.out.println("No lines to analyze");
            return;
        }
        Map<String, Set<String>> streetToPlayers = buildStreetMap();
        String currentStreet = PREFLOP_STREET;
        Map<String, Integer> initialPositions = new HashMap<>();
        for(String line : handLines) {
            currentStreet = updateStreet(line, currentStreet);
            updatePlayersInStreet(line, currentStreet, streetToPlayers);
            
            //once we have gone all the way around preflop - i.e., we see the SB act after already being added, then we know all the positions 
            if(currentStreet.equals(PREFLOP_STREET) && playerPositions.size() == 0 &&
            initialPositions.containsKey(LogUtils.parseNameFromLine(line))) {
                playerPositions = calculateFinalPositions(initialPositions);
            } 

            if(currentStreet.equals(PREFLOP_STREET))
                updatePlayerPositions(line, initialPositions);

               
            actions.processLine(line, currentStreet);
            checkHandNumber(line);
            checkPotCollection(line);
            checkAnomalies(line);
        }

        lastThreeLines = getLastNLines(handLines, 3);

        if(ignoreHand) {
            System.out.println("Ignoring hand " + handNumber + ", (" + fileTitle + ")");
        }        
    }

    private Map<String, Position> calculateFinalPositions(Map<String, Integer> initialPositions) {
        Map<String, Position> finalPositions = new HashMap<>();
        Position[] positions = Positions.positions;
        boolean isHeadsUpGame = initialPositions.keySet().size() == 2;
        if(isHeadsUpGame) //heads up condition
            positions = Positions.headsUpPositions;

        for(Map.Entry<String, Integer> entry : initialPositions.entrySet()) {
            int index = entry.getValue();
            if(!isHeadsUpGame && index > 1) //full ring game where position is not SB or BB
                index += 10 - initialPositions.entrySet().size(); //update position accordingly. i.e. in 4-handed game, 3rd position is actually 8th position

            finalPositions.put(entry.getKey(), positions[index]);
        }
        return finalPositions;
    }

    private String getLastNLines(List<String> lines, int n) {
        String toReturn = "";
        for(int i = lines.size() - 1; i >= 0 && i > lines.size() - 1 - n; i--) {
            toReturn = toReturn += lines.get(i);
        }
        return toReturn;
    }

    private void checkPotCollection(String line) {
        Double potCollection = LogUtils.isLinePotCollection(line);
        if(potCollection != null) {
            winningPot += potCollection;
            String winner = LogUtils.parseNameFromLine(line);            
            handWinners.add(winner);
        }
    }

    private void checkAnomalies(String line) {
        if(line.contains("null")) {
            ignoreHand = true;            
        }
    }

    public Position getPositionForPlayer(String playerName) {
        return playerPositions.get(playerName);
    }

    private void checkHandNumber(String line) {
        if(handNumber == null && line.toLowerCase().contains("starting hand #")) {
            handNumber = line.split(" ")[3].replace("#", "");            
        }
    }

    public List<String> getHandLines() { return handLines; }

    public void setHandLines(List<String> handLines) { this.handLines = handLines; }

    public double getWinningPot() { return winningPot; }

    public boolean ignoreHand(){ return ignoreHand; }

    public Set<String> getHandWinners(){ return handWinners; }

    public static List<String> collectLinesFromHandHistories(Collection<HandHistory> handHistories) {
        List<String> lines = new LinkedList<>();
        for(HandHistory history : handHistories) {
            lines.addAll(history.getHandLines());            
        }
        return lines;
    }

    public String toString() { return handLines.toString(); }

    public String getLastThreeLines() { return lastThreeLines; }

    public String getHandNumber() {
        return handNumber;
    }

    public Set<String> getPreflopPlayers() {
        return preflopPlayers;
    }

    public Set<String> getFlopPlayers() {
        return flopPlayers;
    }

    public Set<String> getTurnPlayers() {
        return turnPlayers;
    }

    public Set<String> getRiverPlayers() {
        return riverPlayers;
    }

    private Map<String, Set<String>> buildStreetMap() {
        Map<String, Set<String>> map = new HashMap<>();
        map.put(PREFLOP_STREET, preflopPlayers);
        map.put(FLOP_STREET, flopPlayers);
        map.put(TURN_STREET, turnPlayers);
        map.put(RIVER_STREET, riverPlayers);
        return map;
    }

    /* 
    * Populates initialPositions map with the player positions, where in non-HU game SBPlayer -> 0, BBPlayer -> 1, next 2, etc.
    */
    private void updatePlayerPositions(String line, Map<String, Integer> initialPositions) {
        if(LogUtils.isPlayerAction(line)) {
            String name = LogUtils.parseNameFromLine(line);
            if(!initialPositions.containsKey(name)) //don't update a player once we've seen them already
                initialPositions.put(name, initialPositions.keySet().size());
        }
    }

    private void updatePlayersInStreet(String line, String street, Map<String, Set<String>> map) {
        if(LogUtils.isPlayerAction(line)) {
            map.get(street).add(LogUtils.parseNameFromLine(line));            
        }
    }

    private String updateStreet(String line, String existingStreet) {                
        for(String street : streets) {
            if(line.toLowerCase().contains(street + ": ")) {                
                return street; 
            }
        }
        return existingStreet;
    }

    /**
     * Two hands are equal only if they are both Hands, the final pot is the same, and the last line is the same.
     */
    @Override
    public boolean equals(Object other) {
        if(!(other.getClass().equals(HandHistory.class))) 
            return false;
        HandHistory otherHand = (HandHistory) other;            
        return lastThreeLines.equals(otherHand.getLastThreeLines()) && winningPot == otherHand.getWinningPot() && handNumber.equals(otherHand.getHandNumber());
    }

    @Override
    public int hashCode() {
        return lastThreeLines.hashCode() + winningPot.hashCode() + handNumber.hashCode();
    }
}