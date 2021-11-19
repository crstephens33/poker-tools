package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class OutputVerifier {

    public abstract void compareOutputsForInput(Map<String, List<String>> inputToExpectedOutputs);

    /**
     * Given a file path, parses the file and finds the input string left of the colon and comma separated values on that line
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Map<String, List<String>> parseFileForInputsAndExpectedOutputs(String filePath) throws IOException {
        FileReader fileReader = new FileReader(filePath);
        BufferedReader bufRead = new BufferedReader(fileReader);
        String line;
        Map<String, List<String>> inputToExpectedOutputs = new HashMap<>();
        while ((line = bufRead.readLine()) != null) {
            String[] inputAndExpectedOutputs = line.split(":");
            String input = inputAndExpectedOutputs[0].trim();

            String[] expectedOutputs = new String[0];
            if(inputAndExpectedOutputs.length > 1) { //only trim and split on comma if there are some elements
                expectedOutputs = inputAndExpectedOutputs[1].trim().split(", ");
            }
            inputToExpectedOutputs.put(input, Arrays.asList(expectedOutputs));
        }
        return inputToExpectedOutputs;
    }
}
