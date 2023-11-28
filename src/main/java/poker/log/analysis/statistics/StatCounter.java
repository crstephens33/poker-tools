package poker.log.analysis.statistics;

import poker.log.hand_history.Actions;
import poker.log.hand_history.HandHistory;
import poker.log.hand_history.TableSize;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class StatCounter {
    protected String statName;
    protected Condition opportunityCondition;
    protected Collection<? extends Condition> potentialOutcomeConditions;
    protected String playerName;

    public StatCounter(Condition opportunityCondition, Collection<Condition> potentialOutcomeConditions, String playerName) {
        this.opportunityCondition = opportunityCondition;
        this.potentialOutcomeConditions = potentialOutcomeConditions;
        this.playerName = playerName;
    }

    public StatCounter() {}

    public StatCounter(String statName){
        this.statName = statName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Call count() for each criteria that applies to the provided handHistory
     * @param handHistories
     * @param exclusiveCriteria when true, only evaluates the first criteria that matches
     */
    public StatCountingResult updateCountsForHands(TableSize tableSize, Collection<HandHistory> handHistories, boolean exclusiveCriteria) {
        Map<String, Integer> conditionCount = new HashMap<>();
        Map<String, List<String>> shownCardsByCondition = new HashMap<>();
        handHistories.forEach( handHistory -> {
            /**
             * If the opportunity criteria to define the stat exists...
             (i.e., we want to calculate whether they 3-bet, so we check if they are facing a raise)
             */
            if (opportunityCondition.evaluate(handHistory, playerName)) {
                StatUtils.incrementIntegerValue(conditionCount, opportunityCondition.getName());
                potentialOutcomeConditions.forEach(outcomeCondition -> { // iterate through each potential outcome.
                    if (outcomeCondition.evaluate(handHistory, playerName)) { // if the outcome matches, update the count for that Criteria
                        StatUtils.incrementIntegerValue(conditionCount, outcomeCondition.getName());
                        String shownCards = checkShownCards(handHistory.getActions(), playerName);
                        if(shownCards != null)
                            StatUtils.appendItemToMapList(shownCardsByCondition, outcomeCondition.getName(), shownCards);
                        if (exclusiveCriteria) return;
                    }
                });
            }
        });
        return new StatCountingResult(tableSize, conditionCount, shownCardsByCondition);
    }

    protected String checkShownCards(Actions actions, String playerName) {
        if(actions.playerShowedCards(playerName)) {
            return actions.getPlayersShownCards(playerName);
        }
        return null;
    }
}
