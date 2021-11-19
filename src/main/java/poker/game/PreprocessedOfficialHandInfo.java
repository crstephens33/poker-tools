package poker.game;

import java.util.List;

public class PreprocessedOfficialHandInfo extends PreprocessedHandInfo {
    public String topQuadRank;
    public String topTripsRank;
    public String topPairRank;
    public String secondPairRank;
    public String flushSuit;
    public String highCardForFourCardHands;
    public String straightRank;

    public PreprocessedOfficialHandInfo(List<Card> cards) {
        super(cards);
    }

    protected void processSuits(int[] suitCountVector) {
        for(int i = 0; i < suitCountVector.length; i++) {
            int count = suitCountVector[i];
            if (count == 5) {
                flushSuit = Card.suits[i];
                return;
            }
        }
    }

    protected void processRanks(int[] rankCountVector) {
        int straightCount = 0;
        String tempStraightRank = null;
        for(int i = rankCountVector.length - 1; i > 1; i--) {
            int count = rankCountVector[i];
            String rank = Card.getRankFromInt(i);
            switch (count) {
                case 0: straightCount = 0; tempStraightRank = null; break;
                case 4: if (topQuadRank == null) topQuadRank = rank; break;
                case 3: if(topTripsRank != null && topPairRank == null) topPairRank = rank; //top pair rank is smaller trips if two trips
                    if (topTripsRank == null) topTripsRank = rank; break;
                case 2: {
                    if (topPairRank != null && secondPairRank == null)
                        secondPairRank = rank;
                    if (topPairRank == null)
                        topPairRank = rank;
                }
                case 1: {
                    if (highCardForFourCardHands == null)
                        highCardForFourCardHands = rank;
                }
            }
            if(count > 0) {
                if(tempStraightRank == null)
                    tempStraightRank = rank;
                straightCount++;
                if(straightCount == 5)
                    straightRank = tempStraightRank;
            }
        }
        //wheel condition - intentionally hardcoded values.
        if(straightRank == null && rankCountVector[rankCountVector.length - 1] > 0) { //no straight but we have an ace
            //if we have 5432 then we have the wheel
            for(int i = 5; i >= 2; i--) {
                if(rankCountVector[i] == 0)
                    return;
            }
            straightRank = "5";
        }
    }
}