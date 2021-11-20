package poker.log.parsing;

import poker.log.hand_history.Player;

import java.util.*;

public class LogUtils extends LogControlHome {
    public static final String startingHandPrefix = "-- starting hand";
    public static final String endingHandPrefix = "-- ending hand";
    
    public static final String betsIndicator = "bets";
    public static final String checksIndicator = "checks";
    public static final String foldsIndicator = "folds";
    public static final String raisesIndicator = "raises";
    public static final String allInIndicator = "and go all in";
    public static final String callsIndicator = "calls";
    public static final String postsIndicator = "posts";
    public static final String collectedIndicator = "collected";
    public static final String winsIndicator = "wins";

    private static final boolean replaceAliases = true;
    private static final int hashLength = 10; //perhaps make this dynamic; read the length of the hash from the file
    private static final String dateIdentifier = "," + Calendar.getInstance().get(Calendar.YEAR);
    private static final String createdTheGameContents = "created the game with a stack of";
    private static final String adminApprovedWithContents = "The admin approved the player \"";
    private static final String joinedGameWithContents = "joined the game with a stack of";
    private static final String quitsTheGameWith = "quits the game with";
    private static final String requestsSeat = "requested a seat";
    private static final String stackModificationContents = "with a stack of";        
    private static final String showsIndicator = " shows a";

     /**
     * Requiring ArrayList here due to time complexity for accessing individual elements
     * @param logLines
     * @return
     */
    public static List<String> addPotSizeAndHandCounts(ArrayList<String> logLines) {
        List<String> output = new LinkedList<>();         
        Map<String, Player> namesToPlayers = new HashMap<String, Player>();
        //process the hand
        int index = 0;
        while(index < logLines.size()) {            
            String currentLine = logLines.get(index);            
            if(lineContainsNewPlayer(currentLine)) {
                output.add(currentLine);
                addNewPlayer(currentLine, namesToPlayers);                                
            }
            else if(logLines.get(index).startsWith(startingHandPrefix)) {
                ArrayList<String> handLines = new ArrayList<String>();                 
                while(index < logLines.size() && !logLines.get(index).contains(endingHandPrefix)) {
                    handLines.add(logLines.get(index++));
                }                
                List<String> handOutput = buildHand(handLines, namesToPlayers);
                for(String line : handOutput) {
                    output.add(line);
                }
                if(index < logLines.size())
                    output.add(logLines.get(index)); //add ending hand line
            } else { //any other line can be added
                output.add(logLines.get(index));
            }            
            index++;
        }
        //modify the logs
        return output;
    }

    /**
     * Returns null if there was no collection, the collection amount if there was one
     * @param line
     * @return
     */
    public static Double isLinePotCollection(String line) {
        boolean collected = line.contains(collectedIndicator);
        boolean wins = line.contains(winsIndicator);
        if(!collected && !wins)
            return null;
        if(collected) {
            String[] tokens = line.split(" ");
            for(int i = 0; i < tokens.length; i++) {
                if(tokens[i].equals(collectedIndicator)) {
                    return Double.valueOf(tokens[i + 1]);
                }
            }
        }
        if(wins) {
            String[] tokens = line.split(" ");
            for(int i = 0; i < tokens.length; i++) {
                if(tokens[i].equals(winsIndicator)) {
                    return Double.valueOf(tokens[i + 1]);
                }
            }
        }
        return null;
    }
    
    private static List<String> buildHand(ArrayList<String> handLines, Map<String, Player> namesToPlayers) {
        List<String> output = new LinkedList<String>();
        Map<String, Player> playersInHand = new HashMap<String, Player>();
        Map<Player, Double> lastContributionPerStreet = new HashMap<Player, Double>();
        double potSize = 0.0;
        for(String line : handLines) {
            String name = parseNameFromLine(line);
            if(name != null && !containsPlayerEntry(line)) {
                Player playerMentionedInLine = namesToPlayers.get(name);
                playersInHand.put(name, playerMentionedInLine);
                if(isPlayerChipContribution(line)) {
                    double contribution = parseChipAmountFromLine(line);
                    lastContributionPerStreet.put(playerMentionedInLine, contribution);                    
                }                
            }    
            if(isLineStreet(line)) {
                for(Double lastContributionPerPlayer : lastContributionPerStreet.values()) {
                    potSize += lastContributionPerPlayer;
                }
                int index = handLines.indexOf(line); 
                handLines.set(index, appendPotSizeToLine(line, potSize));
                lastContributionPerStreet.clear();
            }                                
        }
        output.add(addNumberOfPlayersToStartingHand(handLines.get(0), playersInHand.size()));
        for(int i = 1; i < handLines.size(); i++) {
            output.add(handLines.get(i));
        }
        return output;
    }

    /**
     * For every line in inputFileLines, run formatting rules to make log more readable.
     * Replaces every known alias with a full name.
     * @param inputFileLines - the lines to be cleaned up
     * @return formatted lines
     */
    public static List<String> cleanupFormatting(List<String> inputFileLines, Map<String, String> options) {
        if(!options.containsKey(REPROCESS_LOG_FLAG) )
            Collections.reverse(inputFileLines);
        List<String> cleanInput = new LinkedList<String>();
        for(String inputFileLine : inputFileLines) {
            String formattedInputFileLine = formatLine(inputFileLine, options);
            if(formattedInputFileLine != null)
                cleanInput.add(formattedInputFileLine);
            else {
                System.out.println("Skipping line <" + inputFileLine + ">");
            }
        }
        return cleanInput;
    }

    private static String formatLine(String inputLine, Map<String, String> options) {
        boolean hideStartingHand = options.containsKey(HIDE_HANDS_FLAG);
        String outputLine = new String(inputLine);        

        //Remove timestanps
        int dateStart = outputLine.indexOf(dateIdentifier);
        if(dateStart > 0) {
            outputLine = outputLine.substring(0, dateStart);                
        }

        //Remove player hashes
        while(outputLine.contains("@")) {
            int ampersandStart = outputLine.indexOf("@");
            if(ampersandStart > 0) {
                int hashFinish = ampersandStart + 3 + hashLength;
                outputLine = outputLine.substring(0, ampersandStart - 1) + outputLine.substring(hashFinish, outputLine.length());
            }
        }

        //Remove beginning and trailing quotes on action hands
        if(outputLine.startsWith("\"\"\""))
            outputLine = outputLine.substring(2, outputLine.length() - 1);

        //Remove beginning and trailing quotes on description hands
        if(outputLine.startsWith("\"") && outputLine.endsWith("\""))
            outputLine = outputLine.substring(1, outputLine.length() - 1);    

        //Remove remaining double quotes
        if(outputLine.contains("\"\"")) {            
            outputLine = outputLine.replaceAll("\"\"", "\"");            
        }

        //Update aliases - if line has two quotes (alias exists) isolate alias and replace with fullname.
        if(replaceAliases && outputLine.contains("\"") && countMatches(outputLine, "\"") == 2){
            outputLine = replaceAliasWithFullName(outputLine);
        }

        //Replace Suit symbols with characters
        outputLine = outputLine.replaceAll("♣", "c");
        outputLine = outputLine.replaceAll("♦", "d");
        outputLine = outputLine.replaceAll("♥", "h");
        outputLine = outputLine.replaceAll("♠", "s");

        //Remove own hand from being shown if desired    
        if(hideStartingHand && outputLine.startsWith("Your hand is")) {
            outputLine = "Your hand is (hidden by PokerLogProcessing)";
        }
        return outputLine;
    }

    private static void addNewPlayer(String line, Map<String, Player> players) {
        String fullName = parseNameFromLine(line);
        Player newPlayer = new Player(fullName);
        newPlayer.setStack(parseStackFromPlayerAddedLine(line));
        players.put(fullName, newPlayer);
    }

    private static boolean lineContainsNewPlayer(String line) {
        return line.contains(createdTheGameContents) || line.contains(adminApprovedWithContents) || line.contains(joinedGameWithContents);
    }

    private static int countMatches(final String str, final String subStr) {
        if (str.length() == 0) {
            return 0;
        }
        int count = 0;
        // We could also call str.toCharArray() for faster look ups but that would generate more garbage.
        for (int i = 0; i < str.length(); i++) {
            if (subStr.equals(str.substring(i, i+1))) {
                count++;
            }
        }
        return count;
    }

    /** 
     * Given a line of the log that has an alias, replace it with a full name if the mapping exists. Otherwise leave the alias.
     */
    private static String replaceAliasWithFullName(String line) {
        int firstQuoteIndex = line.indexOf("\"");
        String first = line.substring(0, firstQuoteIndex + 1);
        int secondQuoteIndex = firstQuoteIndex + 1 + line.substring(firstQuoteIndex + 1, line.length()).indexOf("\"");
        String second = line.substring(secondQuoteIndex, line.length()); 
        String alias = line.substring(firstQuoteIndex + 1, secondQuoteIndex);
        String fullname = Player.getAliases().get(alias.toUpperCase());
        fullname = fullname == null ? alias : fullname;        
        line = new String(first + fullname + second); 
        return line;
    }

    public static String parseNameFromLine(String line) {
        if(!(line.contains("\"") && countMatches(line, "\"") == 2)){
            return null;
        }
        int firstQuoteIndex = line.indexOf("\"");        
        int secondQuoteIndex = firstQuoteIndex + 1 + line.substring(firstQuoteIndex + 1, line.length()).indexOf("\"");
        return Player.buildPlayerNameKey(line.substring(firstQuoteIndex + 1, secondQuoteIndex));
    }

    private static double parseStackFromPlayerAddedLine(String line) {
        if(!line.contains(stackModificationContents)) {
            return 0.0;
        }
        int start = line.indexOf(stackModificationContents) + stackModificationContents.length() + 1;
        return Double.valueOf(line.substring(start, line.length() - 1).replace(".", ""));
    }

    private static String addNumberOfPlayersToStartingHand(String startingHandLine, int numPlayers) {
        if(startingHandLine.contains("-handed") || startingHandLine.contains("Heads up")) 
            return startingHandLine;
        String numberOfPlayers = startingHandLine.substring(0, startingHandLine.length() - 3) + " [" + numPlayers + "-handed] --";
        numberOfPlayers = numberOfPlayers.replace("2-handed", "Heads up");
        return numberOfPlayers;
    }

    public static boolean isPlayerChipContribution(String line) {
        return (line.contains(betsIndicator) || line.contains(raisesIndicator) || line.contains(callsIndicator) || line.contains(postsIndicator));
    }

    public static boolean isPlayerAction(String line) {
        String[] tokens = line.split(" ");
        Set<String> tokenSet = new HashSet<>(Arrays.asList(tokens));
        return (tokenSet.contains(callsIndicator) || tokenSet.contains(foldsIndicator) || tokenSet.contains(betsIndicator) ||
            tokenSet.contains(raisesIndicator) || tokenSet.contains(checksIndicator) || isPlayerPost(line));
    }

    public static boolean isPlayerCall(String line) {
        return line.contains(callsIndicator);
    }

    public static boolean isPlayerBet(String line) { //early versions of log had "bet" replaced with raise
        return line.contains(betsIndicator) || line.contains(raisesIndicator);
    }

    public static boolean isPlayerRaise(String line) {
        return line.contains(raisesIndicator);
    }

    public static boolean isPlayerFold(String line) {
        return line.contains(foldsIndicator);
    }

    public static boolean isPlayerCheck(String line) {
        return line.contains(checksIndicator);
    }

    public static boolean isPlayerPost(String line) {
        return line.contains(postsIndicator);
    }

    public static String parseActionFromLine(String line) {
        String name = parseNameFromLine(line);
        String noNameLine = line.toUpperCase().replace("\"" + name + "\" ", "");
        String[] tokens = noNameLine.split(" ");
        return tokens[0].trim().toLowerCase();
    }

    private static double parseChipAmountFromLine(String line) {      
        line = line.replace(allInIndicator, "");
        line = line.trim();
        String[] splitLine = line.split(" ");
        String amount = splitLine[splitLine.length - 1];
        return Double.parseDouble(amount);
    }

    public static boolean isLineStreet(String line) {
        line = line.toLowerCase();
        return (line.contains("flop: ") || line.contains("turn: ") || line.contains("river: "));
    }

    private static String appendPotSizeToLine(String line, double potSize) {
        if(line.contains("<Pot:")) //don't append the pot if it already has it
           return line; 
        return line + " <Pot: " + potSize + ">";
    }

    private static boolean containsPlayerEntry(String line) {        
        return line.contains(adminApprovedWithContents) || line.contains(joinedGameWithContents) || line.contains(quitsTheGameWith) || line.contains(requestsSeat) || line.contains(createdTheGameContents);
    }  
    
    public static boolean lineContainsShowsCards(String line) {
        return line.contains(showsIndicator);
    }

    public static String parseShownCardsFromLine(String line) {
        String[] split = line.split(showsIndicator);
        return split[1].replace(".", "").trim();
    }
}