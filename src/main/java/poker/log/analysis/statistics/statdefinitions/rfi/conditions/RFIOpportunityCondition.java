package poker.log.analysis.statistics.statdefinitions.rfi.conditions;

import poker.log.analysis.statistics.Condition;
import poker.log.hand_history.HandHistory;

public class RFIOpportunityCondition extends Condition {

    public boolean evaluate(HandHistory handHistory, String playerName) {
        return handHistory.getActions().couldPlayerRFI(playerName);
    }

    public RFIOpportunityCondition() {
        super("Opportunity to RFI");
    }
}
