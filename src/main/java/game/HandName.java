package game;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum HandName {

     //refactor to rank the handnames here and include the unofficial rankings

     //official hands
     ROYAL_FLUSH("Royal flush"),
     STRAIGHT_FLUSH("Straight flush"),
     FOUR_OF_A_KIND("Four of a kind"),
     FULL_HOUSE("Full house"),
     FLUSH("Flush"),
     STRAIGHT("Straight"),
     THREE_OF_A_KIND("Three of a kind"),
     TWO_PAIR("Two pair"),
     ONE_PAIR("Pair"),
     HIGH_CARD("High card"),

     //additional labels
     SET("Set"),
     TRIPS("Trips"),
     TOP_TWO("Top two"),
     BOTTOM_TWO("Bottom two"),
     NFD("NFD"),
     FD("FD"),
     OESD("OESD"),
     OVERPAIR("Overpair"),
     TOP_PAIR("Top pair"),
     SECOND_PAIR("Second pair"),
     THIRD_PAIR("Second pair"),
     TWO_OVERS("Two overs"),
     GUTSHOTBDFD("Gutshot+BDFD"),
     GUTSHOT("Gutshot"),
     BDSDFD("BDSDFD"),
     BDSD("BDSD"),
     BDNFD("BDNFD"),
     BDFD("BDFD");

     public final String label;

     HandName(String label) {
         this.label = label;
    }

     private static final Map<String,HandName> ENUM_MAP;
     
     static {
          Map<String,HandName> map = new HashMap<>();
          for (HandName instance : HandName.values()) {
               map.put(instance.label.toLowerCase(),instance);
          }
          ENUM_MAP = Collections.unmodifiableMap(map);
     }

     public static HandName getValue(String name) {
          return ENUM_MAP.get(name.toLowerCase());
     }
}
