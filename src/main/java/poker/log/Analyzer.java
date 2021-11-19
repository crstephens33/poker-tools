package poker.log;

import java.util.*;

import util.*;


public class Analyzer {

    static final FileUtils fileUtils = new FileUtils();
    public static void analyzeHands(List<String> filenames) {
        Set<HandHistory> allHandHistories = buildHandHistories(filenames);        
        Collection<HandHistory> historiesToProcess = processHandHistories(allHandHistories);                
        System.out.println("Number of unique hand histories detected: " + historiesToProcess.size());
        //fileUtils.writeToOutputFile(HandHistory.collectLinesFromHandHistories(historiesToProcess), "SunRunHandsOver100.txt", FileUtils.ANALYSIS_LOCATION);
        //String[] playerNames = new String[]{"Cole Stephens", "Sam Schrader", "Cole Ford", "Jack Betcher", "Jack Stephens", "Dylan Russian", "Joe Delory", "Jake"};
        String[] playerNames = new String[]{"Cole Stephens", "Cole Ford"};
        for(String player : playerNames) {
            String formattedPlayer = Player.buildPlayerNameKey(player);
            System.out.println(getStatistics(historiesToProcess, new VPIPCalculator(formattedPlayer)));
            System.out.println(getStatistics(historiesToProcess, new RFIResponseCalculator(formattedPlayer)));
            System.out.println(getStatistics(historiesToProcess, new ThreeBetResponseAfterRFICalculator(formattedPlayer)));
            System.out.println();
        }
    }

    private static String getStatistics(Collection<HandHistory> handHistories, StatCalculator calculator) {        
        calculator.calculateStatsFromHandHistories(handHistories);
        return calculator.getResults();
    }  

    private static Collection<HandHistory> processHandHistories(Collection<HandHistory> handHistories) {
        Set<HandHistory> uniqueHistories = new HashSet<>();        
        for(HandHistory history : handHistories) {
            //if(history.getWinningPot() > 10000)
                uniqueHistories.add(history);
        }
        return uniqueHistories;
    }

    private static boolean coleDealtInPreflop(HandHistory handHistory) {
        return playerDealtInPreflop(handHistory, "Cole Stephens");
    }

    private static boolean samDealtInPreflop(HandHistory handHistory) {
        return playerDealtInPreflop(handHistory, "Sam Schrader");
    }

    private static boolean playerDealtInPreflop(HandHistory handHistory, String playerName) {
        String player = Player.buildPlayerNameKey(playerName);
        return handHistory.getPreflopPlayers().contains(player);
    }

    private static boolean playerSawFlop(HandHistory handHistory, String playerName) {
        String player = Player.buildPlayerNameKey(playerName);
        return handHistory.getFlopPlayers().contains(player);
    }

    private static Set<HandHistory> buildHandHistories(List<String> filenames) {
        Set<HandHistory> allHandHistories = new HashSet<>();        
        for(String fileName : filenames) {
            List<String> logLines = fileUtils.readFile(fileName);
            allHandHistories.addAll(getHandHistoriesFromFileLines(logLines, fileName));
        } 
        return allHandHistories;
    }

    private static List<HandHistory> getHandHistoriesFromFileLines(List<String> lines, String fileName) {
        List<HandHistory> handHistories = new ArrayList<>();        
        ArrayList<String> handLines = new ArrayList<String>();        
        for(int index = 0; index < lines.size(); index++) {
            handLines = new ArrayList<>();
            if(lines.get(index).contains(LogUtils.startingHandPrefix)) {
                while(index < lines.size() - 1) { //once we reach the next hand, exit
                    handLines.add(lines.get(index));
                    index++;
                    if(lines.get(index).contains(LogUtils.endingHandPrefix)) { //hand is over, now look to see if anyone showed
                        handLines.add(lines.get(index));
                        for(int showsIndex = index + 1; showsIndex < lines.size(); showsIndex++) {
                            String showLine = lines.get(showsIndex);
                            if(!LogUtils.lineContainsShowsCards(showLine)) {
                                break;
                            }
                            handLines.add(showLine);
                        }
                        break;
                    }
                } 
                HandHistory history = new HandHistory(handLines, fileName);
                if(!history.ignoreHand()) {                    
                    handHistories.add(history);
                }
            }
        }
        //System.out.println("Number of total hand histories detected: " + handHistories.size() + ", file: " + fileName);
        return handHistories;
    }    

    
}
