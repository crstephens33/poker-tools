package poker.log;

import java.util.*;
import util.*;

public class LogCleaner {
    public static List<String> cleanLog(final String inputFileName, final String outPutFileName, final Map<String, String> options) {
        final FileUtils fileUtils = new FileUtils();                
        final ArrayList<String> cleanInputFileLines = new ArrayList<String>(
                LogUtils.cleanupFormatting(fileUtils.readFile("", inputFileName), options));
        final List<String> potSizeAndHandCounts = LogUtils.addPotSizeAndHandCounts(cleanInputFileLines);
        
        fileUtils.moveFromHomeToRaw(inputFileName); 
        
        return potSizeAndHandCounts; //may add steps in between, hence keeping variable name
    }
}
