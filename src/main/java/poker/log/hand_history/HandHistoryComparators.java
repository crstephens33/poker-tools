package poker.log.hand_history;

import java.util.Comparator;

public class HandHistoryComparators {

    public static class PotSizeComparator implements Comparator<HandHistory> {
        public int compare(HandHistory a, HandHistory b) {
            return (int) a.getWinningPot() - (int) b.getWinningPot();
        }
    }
}
