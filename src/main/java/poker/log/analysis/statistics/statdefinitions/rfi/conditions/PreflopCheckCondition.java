package poker.log.analysis.statistics.statdefinitions.rfi.conditions;

import poker.log.analysis.statistics.Condition;
import poker.log.hand_history.HandHistory;

public class PreflopCheckCondition extends Condition {

    public boolean evaluate(HandHistory history, String playerName) {
        return history.getActions().didPlayerCheckPreflop(playerName);
    }

    public PreflopCheckCondition() {
        super("Checks pre");
    }
}
