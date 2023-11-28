package poker.log.analysis.statistics;

import poker.log.hand_history.HandHistory;

public abstract class Condition {

    protected String name;

    public String getName() {
        return this.name;
    }

    public Condition(String name) {
        this.name = name;
    }

    public abstract boolean evaluate(HandHistory history, String playerName);

    public void count() {
        // TODO not yet implemented
    }
}
