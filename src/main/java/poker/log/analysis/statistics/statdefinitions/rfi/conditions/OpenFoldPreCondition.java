package poker.log.analysis.statistics.statdefinitions.rfi.conditions;

import poker.log.analysis.statistics.Condition;
import poker.log.hand_history.HandHistory;

public class OpenFoldPreCondition extends Condition {

    public boolean evaluate(HandHistory history, String playerName) {
        return history.getActions().didPlayerOpenFold(playerName);
    }

    public OpenFoldPreCondition() {
        super("Open fold pre");
    }
}
