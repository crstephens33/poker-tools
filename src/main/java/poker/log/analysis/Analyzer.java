package poker.log.analysis;

import poker.log.hand_history.*;
import poker.log.parsing.LogUtils;
import util.FileUtils;

import java.util.*;

public class Analyzer {

    private static final String SORT_OPTION = "--sort";
    private static final String SORT_PREFLOP = "-preflop";
    private static final String SORT_POT = "-pot";

    private static final FileUtils fileUtils = new FileUtils();

    public static void main(String[] args) {
        Set<String> argSet = new HashSet<>(Arrays.asList(args));
        if (args.length < 1) {
            System.out.println("Requires at least 1 arg: string for filename to contain");
            System.exit(1);
        }
        String path = FileUtils.CLEAN_LOGS_LOCATION;
        String contains = args[0];

        List<String> filenames = FileUtils.getFilePathsInDirectoryContainingString(path, contains);
        Comparator<HandHistory> comparator = null;
        if(argSet.contains(SORT_OPTION)) {
            if(argSet.contains(SORT_POT)) {
                comparator = new HandHistoryComparators.PotSizeComparator().reversed();
            } else if (argSet.contains("placeholder")) {
                comparator = null;
            } else { //default to sorting on pot size
                comparator = new HandHistoryComparators.PotSizeComparator();
            }
            outputSortedHistories(filenames, comparator, "SortedFileOutput.txt");
        } else {
            analyzeHands(filenames);
        }
    }

    public static void outputSortedHistories(List<String> filenames, Comparator<HandHistory> comparator, String outputFilename) {
        List<HandHistory> sortedHistories = sortHandHistoriesFromFiles(filenames, comparator);
        writeHandHistoriesToOutputFile(sortedHistories, outputFilename);
    }

    public static List<HandHistory> sortHandHistoriesFromFiles(List<String> filenames, Comparator<HandHistory> comparator) {
        List<HandHistory> histories = getFilteredHistoriesFromFilenames(filenames);
        Collections.sort(histories, comparator);
        return histories;
    }

    public static List<HandHistory> getFilteredHistoriesFromFilenames(List<String> filenames) {
        Set<HandHistory> allHandHistories = buildHandHistoriesFromFileNames(filenames);
        List<HandHistory> historiesToProcess = processHandHistories(allHandHistories);
        return historiesToProcess;
    }

    public static void writeHandHistoriesToOutputFile(List<HandHistory> histories, String filename) {
        fileUtils.writeToOutputFile(HandHistory.collectLinesFromHandHistories(histories), filename, FileUtils.ANALYSIS_LOCATION);
    }

    public static void analyzeHands(List<String> filenames) {
        List<HandHistory> historiesToProcess = getFilteredHistoriesFromFilenames(filenames);
        System.out.println("Number of unique hand histories detected: " + historiesToProcess.size());
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

    private static List<HandHistory> processHandHistories(Collection<HandHistory> handHistories) {
        Set<HandHistory> uniqueHistories = new HashSet<>();
        for(HandHistory history : handHistories) {
            if(history.getWinningPot() > 1000)
                uniqueHistories.add(history);
        }
        return new ArrayList<>(uniqueHistories);
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

    private static Set<HandHistory> buildHandHistoriesFromFileNames(List<String> filenames) {
        Set<HandHistory> allHandHistories = new HashSet<>();        
        for(String fileName : filenames) {
            List<String> logLines = fileUtils.readFile(fileName);
            allHandHistories.addAll(buildHandHistoriesFromFileLines(logLines, fileName));
        } 
        return allHandHistories;
    }

    private static List<HandHistory> buildHandHistoriesFromFileLines(List<String> lines, String fileName) {
        List<HandHistory> handHistories = new ArrayList<>();        
        ArrayList<String> handLines;
        for(int index = 0; index < lines.size(); index++) {
            handLines = new ArrayList<>();
            if(lines.get(index).contains(poker.log.parsing.LogUtils.startingHandPrefix)) {
                while(index < lines.size() - 1) { //once we reach the next hand, exit
                    handLines.add(lines.get(index));
                    index++;
                    if(lines.get(index).contains(poker.log.parsing.LogUtils.endingHandPrefix)) { //hand is over, now look to see if anyone showed
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
