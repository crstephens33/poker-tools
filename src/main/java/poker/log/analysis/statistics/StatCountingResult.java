package poker.log.analysis.statistics;

import poker.log.hand_history.TableSize;

import java.util.List;
import java.util.Map;

public class StatCountingResult {

    public TableSize tableSize;

    public Map<String, Integer> countsForCondition;

    public Map<String, List<String>> holeCardsKnownForCondition;

    public StatCountingResult(TableSize tableSize, Map<String, Integer> countsForCondition, Map<String, List<String>> holeCardsKnownForCondition) {
        this.tableSize = tableSize;
        this.countsForCondition = countsForCondition;
        this.holeCardsKnownForCondition = holeCardsKnownForCondition;
    }
}
