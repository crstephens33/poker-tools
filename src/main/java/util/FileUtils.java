package util;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import java.nio.file.StandardCopyOption;
import java.nio.file.Paths;

public class FileUtils {

    private FileWriter fileWriter;
    public static String CLEAN_LOGS_LOCATION = "cleanLogs\\";
    public static String ANALYSIS_LOCATION = "analysis\\";
    public static String RAW_LOGS_LOCATION = "rawLogs\\";


    public FileUtils(String fileName) { 
        this.setFileName(fileName);       
    }

    public FileUtils() {      
    }

    public void setFileName(String fileName) {
        try {
            if(this.fileWriter != null) {
                this.fileWriter.close();
            }
        } catch (IOException e) {
            System.out.println("Failed to close previous fileWriter: " + e.toString());
            return;
        }
        try {            
            this.fileWriter = new FileWriter(fileName);
        } catch (IOException e) {
            System.out.println("Failed to set fileWriter name to \"" + fileName + "\"");
        }
    }

    public void fileWrite(String s) {
    //System.out.println(s);
        try { 
            fileWriter.write(s + "\n");
        } catch (IOException e) {
            System.out.println("file write failed: " + s);
        }
    }

    public boolean moveFromHomeToRaw(String fileName) {
        return move(fileName, "", RAW_LOGS_LOCATION);
    }

    public boolean move(String fileName, String sourcePath, String destinationPath) {
        try {
            Files.move(Paths.get(sourcePath + fileName), Paths.get(destinationPath + fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("File movement failed");
            return false;
        }
        return true;
    }

    public void close() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("file closing failed.");
        }

    }

    public void writeToOutputFile(List<String> linesToWrite, String outputFileName, String outputPath) {
        setFileName(outputPath + outputFileName);
        for (final String outputFileLine : linesToWrite)
            fileWrite(outputFileLine);
        close();
    }

    public boolean closeWriter() {
        try {
            this.fileWriter.close();
        } catch (IOException e) {
            System.out.println("Failed to close fileWriter: " + e.toString());
            return false;
        }
        return true;
    }
               
    public void printCollection(Collection<? extends Object> toPrint) {
        System.out.print("[");
        for(Object server : toPrint) {
            System.out.print(server.toString() + ", ");
        }
        System.out.print("]");
        System.out.println();
    }

    public List<String> readFile(String fileName) {
        return readFile("", fileName);
    }

    public List<String> readFile(String location, String fileName) {
        List<String> fileLines = new LinkedList<String>();
        try {
            File myObj = new File(location + fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
              String line = myReader.nextLine();
              fileLines.add(line);
            }
            myReader.close();
          } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
          }
          return fileLines;
    }

    public static Collection<String> getFileNamesInDirectoryContainingString(String path, String contains) {        
        Collection<String> fileNames = new ArrayList<String>();
        File folder = new File(path);
        System.out.println("Looking in folder " + folder.getName());
        File[] listOfFiles = folder.listFiles();
        for(File file : listOfFiles) {
            String name = file.getName();
            System.out.println("Found file " + name);
            if(name.contains(contains))
                fileNames.add(name);
        }
        System.out.println(fileNames);
        return fileNames;
    }
    
    


}