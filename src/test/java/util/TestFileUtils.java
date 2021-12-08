package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TestFileUtils {

    public static final String TEST_FILE_INDICATOR_TOKEN = "0.0.0.0.0.0.0.0.0.0";

    public static final String TEST_RAW_PATH = "src/test/resources/log";

    public static final String TEST_RAW_LOG_FILENAME = "TEST_CS_2021.12.04_CF_HU.csv";

    public static void main(String[] args) {
       // deleteAllTestFilesInPath("src/test/resources/logtest");
    }

    public static String getTestFileName(String filename) {
        return TEST_FILE_INDICATOR_TOKEN + filename;
    }

    //Copy a raw test file and move it to the desired location
    public static void putTestRawFile(String path, String filename) {
        String filepathAndName = TEST_RAW_PATH + "/" + TEST_RAW_LOG_FILENAME;
        try {
            System.out.println("Moving test file to " + path);
            Files.copy(Paths.get(filepathAndName), Paths.get(path + "/" + getTestFileName(filename)));
        } catch (IOException e) {
            System.out.println("File copy failed: " + e);
            System.exit(1); //fail now if file copy fails
        }
    }

    public static void deleteAllTestFilesInPath(String path) {
        deleteFilesContainingStringInPath(TEST_FILE_INDICATOR_TOKEN, path);
    }

    /**
     * Deletes a test file given a name and location. ONLY DELETES TEST FILES.
     * @param filename
     * @param path
     * @return
     */
    private static boolean deleteTestFile(String filename, String path) {
        try {
            if(filename.contains(TEST_FILE_INDICATOR_TOKEN)) {
                Files.delete(Paths.get(path + "/" + filename));
                return true;
            } else {
                System.out.println("Not deleting file [" + filename + "] as test indicator not present in filename.");
            }
        } catch (IOException e) {
            System.out.println("File delete failed: " + e);
            System.exit(1);
        }
        return false;
    }

    private static void deleteFilesContainingStringInPath(String containingString, String path) {
        List<String> filenames = FileUtils.getFilePathsInDirectoryContainingString(path, containingString, false);
        filenames.forEach(file -> deleteTestFile(file, path));
    }

    public static String getNameWithoutExtension(String filename) {
        return filename.substring(0, filename.indexOf(".csv"));
    }
}
