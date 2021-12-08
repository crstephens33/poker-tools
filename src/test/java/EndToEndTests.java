import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import poker.log.parsing.LogControlHome;
import util.FileUtils;
import util.TestFileUtils;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static util.TestFileUtils.putTestRawFile;

public class EndToEndTests {

    @Test
    public void testFileCleanE2E() {
        //create test file in input (use a sample old file)
        putTestRawFile(FileUtils.INPUT_LOGS_LOCATION, TestFileUtils.TEST_RAW_LOG_FILENAME);

        //call clean command
        System.out.println("Calling \"clean\" program.");
        LogControlHome.runSimpleClean();

        System.out.println("Checking file added to clean logs location");
        //check to make sure file got added to clean-logs (get all filenames, check for str contains. won't know actual file name bc of versioning)
        List<String> cleanFileNames = FileUtils.getFilePathsInDirectoryContainingString(FileUtils.CLEAN_LOGS_LOCATION, "", false);
        boolean foundMockFileInCleanLogs = filenameExistsInList(TestFileUtils.TEST_RAW_LOG_FILENAME, cleanFileNames);
        assertTrue(foundMockFileInCleanLogs, "Mock test file was not found in clean logs location after cleaning.");

        System.out.println("Checking file added to raw logs location");
        //check to make sure file got moved to raw-logs
        List<String> rawFileNames = FileUtils.getFilePathsInDirectoryContainingString(FileUtils.RAW_LOGS_LOCATION, "", false);
        boolean foundMockFileInRawLogs = filenameExistsInList(TestFileUtils.TEST_RAW_LOG_FILENAME, rawFileNames);
        assertTrue(foundMockFileInRawLogs, "Mock test file was not found in raw logs location after cleaning.");

        //make sure the file isn't present in staging!
        List<String> stagingFileNames = FileUtils.getFilePathsInDirectoryContainingString(FileUtils.INPUT_LOGS_LOCATION,
                TestFileUtils.TEST_FILE_INDICATOR_TOKEN, false);
        assertEquals(stagingFileNames.size(), 0, "Test file(s) found in staging.");
    }

    @AfterEach
    public void deleteAllTestFiles() {
        System.out.println("Deleting cleaned test files");
        //delete the test files from clean-logs.
        TestFileUtils.deleteAllTestFilesInPath(FileUtils.CLEAN_LOGS_LOCATION);

        System.out.println("Deleting raw test files");
        //delete the test files from raw-logs.
        TestFileUtils.deleteAllTestFilesInPath(FileUtils.RAW_LOGS_LOCATION);
    }

    private static boolean filenameExistsInList(String filenameToCheck, Collection<String> filenames) {
        for(String filename : filenames) {
            if(filename.contains(TestFileUtils.getNameWithoutExtension(filenameToCheck)))
                return true;
        }
        return false;
    }
}
