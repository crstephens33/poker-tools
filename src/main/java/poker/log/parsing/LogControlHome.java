package poker.log.parsing;

import poker.log.analysis.Analyzer;
import util.FileUtils;

import java.util.*;

public class LogControlHome {
    //Program / labelling
    private static final String PROGRAM_VERSION = "2.0.2";
    private static final String CLEAN_OUTPUT_FILE_PATTERN = "%s_clean_%sv%s.txt";
    private static final String ALIASES_FILE = "inputs\\config\\Aliases.txt";

    public static String getAliasesFile() {
        return ALIASES_FILE;
    }

    //Arguments
    private static final String PROGRAM_COMMAND = "command";
    private static final String ANALYZE_LOG_COMMAND = "analyze";
    private static final String CLEAN_LOG_COMMAND = "clean";    
    private static final String CLEAN_AND_ANALYZE_COMMAND = "full";
    private static final String MANAGE_COMMAND = "manage";
    private static final String MENU_COMMAND = "menu";
    private static final String MENU_PROMPT = "Run \"PokerLogProcessing " + MENU_COMMAND +"\" for options.";

    private static final String FILE_INDICATOR = "file";
    private static final String HIDDEN_HANDS_TITLE_TERM = "Hidden_";
    private static final String[] ACCEPTED_FILE_EXTENSION = {".txt", ".csv"};
    private static final String ARG_ERROR_KEY = "argument_error";
    private static final String INVALID_COMMAND_MSG = "Invalid command provided."; 
    
    //Processing flags/options
    protected static final String REPROCESS_LOG_FLAG = "-r";
    protected static final String HIDE_HANDS_FLAG = "-h";

    public static void main(final String[] args) {
        run(parseArgs(args));
    }

    /**
     * Calls run with an inputs map as if the program was run with "clean" argument
     */
    public static void runSimpleClean() {
        Map<String, String> inputsMap = new HashMap<>();
        inputsMap = parseArgs(new String[]{"clean"});
        run(inputsMap);
    }


    public static void run(Map<String, String> inputsMap) {        
        final List<String> inputFileNames = new ArrayList<String>();
        final List<String> cleanedOutputFiles = new ArrayList<String>();

        String command = inputsMap.get(PROGRAM_COMMAND);                
        //String inputFileLocation = command.equals(ANALYZE_LOG_COMMAND) ? FileUtils.CLEAN_LOGS_LOCATION : ""; 

        boolean hideStartingHand = inputsMap.containsKey(HIDE_HANDS_FLAG);

        for(String inputKey : inputsMap.keySet()) {
            if(inputKey.contains(FILE_INDICATOR)) {
                inputFileNames.add(inputsMap.get(inputKey));
                String plural = inputFileNames.size() == 1 ? "" : "s";
                System.out.println(inputFileNames.size() + " matching file" + plural + " detected:");
            }
        }
        String message = "Program finished. ";
        if (command.equals(CLEAN_LOG_COMMAND) || command.equals(CLEAN_AND_ANALYZE_COMMAND)) {
            inputFileNames.addAll(FileUtils.getAllInputLogFilenamesContaining("", false));
            for(String inputFileName : inputFileNames) {
                String cleanedOutputFileName = getCleanOutputFileName(inputFileName, hideStartingHand);
                List<String> cleanLines = LogCleaner.cleanLog(inputFileName, FileUtils.INPUT_LOGS_LOCATION, inputsMap);
                final FileUtils outputFileUtils = new FileUtils();
                outputFileUtils.writeToOutputFile(cleanLines, cleanedOutputFileName, FileUtils.CLEAN_LOGS_LOCATION);
                cleanedOutputFiles.add(cleanedOutputFileName);
            }
        } else if (command.equals(ANALYZE_LOG_COMMAND)) {
            analyzeLog(inputFileNames);
        } else {
            message = INVALID_COMMAND_MSG + " " + MENU_PROMPT;
        }
        System.out.println(message);
    }

    private static List<String> filterFileNames(List<String> fileNames) {
        List<String> filteredFileNames = new ArrayList<String>();
        for(String s : fileNames) {
            //if((s.contains("CS_2021.04") || s.contains("CS_2021.05")) && !s.contains("Hidden")) {
            if((s.contains("CS_2021.09") || s.contains("CS_2021.10") || s.contains("CS_2021.11") || s.contains("CS_2021.12")) && !s.contains("Hidden")) {
                filteredFileNames.add(s);
            }
        }
        return filteredFileNames;
    }

    public static void analyzeLog(final List<String> inputFileNames) {
        List<String> filteredFileNames = filterFileNames(inputFileNames);
        System.out.println(filteredFileNames.size() + " filtered files to analyze.");
        Analyzer.analyzeHands(filteredFileNames);
    }

    private static String getCleanOutputFileName(final String inputFileName, boolean hiddenHands) {
        String hidden = hiddenHands ? HIDDEN_HANDS_TITLE_TERM : "";
        String inputFileRemoveExt = inputFileName.replace(".txt", "").replace(".csv", "");
        return String.format(CLEAN_OUTPUT_FILE_PATTERN, inputFileRemoveExt, hidden, PROGRAM_VERSION);
    }

    private static Map<String, String> parseArgs(String[] args) {        
        List<String> argList = new ArrayList<String>(Arrays.asList(args));        
        Map<String, String> responseMap = new HashMap<String, String>();

        if(argList.size() == 0 || (argList.size() == 1 && !(argList.get(0).equals(MENU_COMMAND) ||
                argList.get(0).equals(CLEAN_LOG_COMMAND)))) {
            responseMap.put(ARG_ERROR_KEY, "No arguments provided. " + MENU_PROMPT);
            return responseMap;
        }                 
        String command = argList.get(0).toLowerCase();
        if (command.equals(MENU_COMMAND)) {
            System.out.println(getMenuText());
            System.exit(0);
        } 
        if (argList.size() > 1) {
            int fileCount = 0;
            responseMap.put(PROGRAM_COMMAND, command);
            for (String argument : argList) {                
                if(argument.endsWith(ACCEPTED_FILE_EXTENSION[0]) || argument.endsWith(ACCEPTED_FILE_EXTENSION[1]))
                    responseMap.put(FILE_INDICATOR + fileCount++, argument);
                else {
                    if(argument.equals(HIDE_HANDS_FLAG)) {
                        responseMap.put(HIDE_HANDS_FLAG, null);
                    } else if(argument.equals(REPROCESS_LOG_FLAG)) {
                        responseMap.put(REPROCESS_LOG_FLAG, null);
                    }
                }
            }
        } else if (argList.contains(CLEAN_LOG_COMMAND)) {
            responseMap.put(PROGRAM_COMMAND, CLEAN_LOG_COMMAND);
        }

        return responseMap;
     }

     private static String getMenuText() {
        String menuOptions=
        "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
        "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
        "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Poker menu ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
        "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
        "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
        menuOptions +=MENU_COMMAND+    "........................................................................Shows command menu\n";
        menuOptions +=CLEAN_LOG_COMMAND+" <filenames,>.................Cleans up raw log for files <filenames> (expects .csv files)\n";
        menuOptions +=ANALYZE_LOG_COMMAND+" <filenames,>...........Calculates statistics for files <filenames> (expects .txt files)\n";
        menuOptions += " Standard usages: clean *.csv , analyze cleanLogs/*.txt";
        menuOptions += "\n\n\n";
        return menuOptions;
    }
}