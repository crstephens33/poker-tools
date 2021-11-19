package log;

import java.util.*;
import util.*;

public class Player {

    private String alias;

    private String fullName;

    private static Map<String, String> aliases = null;

    //counts # of hands in which they were dealt hole cards
    private double stackSize;
    //private Map<Action, Integer> actionCount;
    

    public Player(String fullName) {
        this.fullName = fullName;
    }

    public String getStackString() {
        return "[" + String.valueOf(stackSize) + "]";
    }

    /** 
     * Decreases stack size by a given amount
     * @param amountDecreased
     * @return the player's stack size after subtracting the amount decreased.
     */
    public double decreaseStack(double amountDecreased) {
        stackSize -= amountDecreased;
        return stackSize;
    }

    /** 
     * Increases stack size by a given amount
     * @param amountIncreased
     * @return the player's stack size after adding the amount Increased.
     */
    public double increaseStack(double amountIncreased) {
        stackSize += amountIncreased;
        return stackSize;
    }

    public double setStack(double newStack) {
        stackSize = newStack;
        return stackSize;
    }

    //You should create a way to read these from a file. format per line could include (alias, alias, alias:fullname). create entry for each combination
    public static Map<String, String> getAliases() {
        if(aliases == null) {
            aliases = loadAliasesFromFile();
        }
        return aliases;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Player) {
            Player other = (Player) o;
            return this.fullName.equals(other.getFullName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }
    
    public String getFullName() {
        return fullName;
    }

    public void setAlias(String alias){
        this.alias = alias;
    }

    public String getAlias(){
        return alias;
    }

    private static Map<String, String> loadAliasesFromFile() {
        HashMap<String, String> aliasToFullName = new HashMap<String, String>();
        FileUtils fileUtils = new FileUtils();
        List<String> aliasLines = fileUtils.readFile(Poker.getAliasesFile());
        for(String line : aliasLines) {
            String[] aliasesAndFullname = line.split(":");
            String[] aliases = aliasesAndFullname[0].split(",");            
            String fullName = aliasesAndFullname[1].trim();
            for(String alias : aliases){
                aliasToFullName.put(buildPlayerNameKey(alias), fullName);
            }
            aliasToFullName.put(buildPlayerNameKey(fullName), fullName); //works well for re-processing cleaned files for the full name to work as a key
        }        
        return aliasToFullName;
    }

    /**
     * Very important - player name keys must all be the same, and generated the same. Any changes to key must be used here.
     * @param name
     * @return
     */
    public static String buildPlayerNameKey(String name) {
        return name.trim().toUpperCase();
    }
}