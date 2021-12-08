package poker.log.parsing;

import util.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LogCleaner {
    public static List<String> cleanLog(final String uncleanedFileName, final String filePath, final Map<String, String> options) {
        final FileUtils fileUtils = new FileUtils();
        System.out.printf("Cleaning file [%s]%n", uncleanedFileName);
        final ArrayList<String> cleanInputFileLines = new ArrayList<>(
                LogUtils.cleanupFormatting(fileUtils.readFile(filePath + "/" + uncleanedFileName), options));
        final List<String> potSizeAndHandCounts = LogUtils.addPotSizeAndHandCounts(cleanInputFileLines);
        
        fileUtils.moveFromInputsToRaw(uncleanedFileName);
        
        return potSizeAndHandCounts; //may add steps in between, hence keeping variable name
    }
}
