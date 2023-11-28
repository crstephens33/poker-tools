package poker.log.analysis.statistics.statdefinitions.rfi.conditions;

import poker.log.analysis.statistics.Condition;
import poker.log.hand_history.HandHistory;

public class OpenLimpPreCondition extends Condition {

    @Override
    public boolean evaluate(HandHistory history, String playerName) {
        return history.getActions().didPlayerOpenLimp(playerName);
    }

    public OpenLimpPreCondition() {
        super("Open limp pre");
    }
}
