package poker.log.analysis.statistics.statdefinitions.rfi.conditions;

import poker.log.analysis.statistics.Condition;
import poker.log.hand_history.HandHistory;

public class RFICondition extends Condition {

    public boolean evaluate(HandHistory history, String player) {
        return history.getActions().didPlayerRFI(player);
    }

    public RFICondition() {
        super("RFI");
    }
}
