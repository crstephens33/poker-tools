package poker.log.parsing;

import util.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LogCleaner {
    public static List<String> cleanLog(final String inputFileName, final String outPutFileName, final Map<String, String> options) {
        final FileUtils fileUtils = new FileUtils();
        System.out.printf("Cleaning file [%s]%n", inputFileName);
        final ArrayList<String> cleanInputFileLines = new ArrayList<>(
                LogUtils.cleanupFormatting(fileUtils.readFile(inputFileName), options));
        final List<String> potSizeAndHandCounts = LogUtils.addPotSizeAndHandCounts(cleanInputFileLines);
        
        fileUtils.moveFromInputsToRaw(inputFileName);
        
        return potSizeAndHandCounts; //may add steps in between, hence keeping variable name
    }
}
