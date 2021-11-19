package range;

import game.Card;
import game.HoleCards;

import java.util.*;

public class RangeOperations {


    public static List<Card> removeBlockersFromList(List<Card> list, Set<Card> blockers){
        List<Card> listAfterRemoval = new ArrayList<>();
        for(int i = list.size() - 1; i >= 0; i--) { //move "right to left" so shifting doesnt skip elements
            if(!blockers.contains(list.get(i))) //instead of removing from list, add all non-blocked cards to new list
                listAfterRemoval.add(list.get(i));
        }
        return listAfterRemoval;
    }

    public static Range removeBlockersFromRange(Range range, Set<Card> blockers) {
        Set<HoleCards> rangeCombinations = range.getRangeCombinations();
        Range rangeAfterRemoval = new Range();
        rangeCombinations.forEach(combination -> { //either add all the unblocked combos or copy all then removed blocked
            if(!blockers.contains(combination.getLargerCard()) && !blockers.contains(combination.getSmallerCard())) {
                rangeAfterRemoval.addToRange(combination);
            }
        });
        return rangeAfterRemoval;
    }
}
