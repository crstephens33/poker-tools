package poker.log.analysis.statistics;

import poker.log.hand_history.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatUtils {

    public static void incrementIntegerValue(Map<String, Integer> map, String key) {
        map.put(key, map.getOrDefault(key, 0) + 1);
    }

    public static void incrementDoubleValue(Map<Integer, Double> map, Integer key) {
        map.put(key, map.getOrDefault(key, 0.0) + 1.0);
    }

    public static void incrementDoubleValue(Map<String, Double> map, String key) {
        map.put(key, map.getOrDefault(key, 0.0) + 1.0);
    }

    public static void appendItemToMapList(Map<String, List<String>> map, String key, String toAppend) {
        List<String> list = map.getOrDefault(key, new ArrayList<>());
        list.add(toAppend);
        map.put(key, list);
    }

    //don't think there is a way around having this code - need specified types to allow a "put" to work.
    public static void incrementDoubleValue(Map<Position, Double> map, Position key) {
        map.put(key, map.getOrDefault(key, 0.0) + 1.0);
    }
}
