package game;

import java.util.List;
import java.util.Set;

public class PreprocessedAuxiliaryHandInfo extends PreprocessedHandInfo {

    private HoleCards holeCards;
    private Set<Card> flop;

    private boolean alreadyFlush;
    private boolean alreadyStraight;
    private boolean alreadyThreeOfAKind;

    public boolean flushDraw;
    public boolean backdoorFlushDraw;
    public boolean backdoorStraightDraw;
    public boolean oesd;
    public boolean set;
    public boolean trips;
    public boolean overpair;

    private void initializeKnownInfo(PreprocessedOfficialHandInfo officialHandInfo) {
        alreadyFlush = officialHandInfo.flushSuit != null;
        alreadyThreeOfAKind = officialHandInfo.topTripsRank != null && officialHandInfo.topPairRank == null; //trips but not boat
        alreadyStraight = officialHandInfo.straightRank != null;

    }

    public PreprocessedAuxiliaryHandInfo(List<Card> allCards, HoleCards holeCards, Set<Card> flop, PreprocessedOfficialHandInfo officialHandInfo) {
        this.holeCards = holeCards;
        this.flop = flop;
        initializeKnownInfo(officialHandInfo);
        processCards(allCards);
    }

    protected void processSuits(int[] suitCountVector) {
        if(!alreadyFlush) {
            for (int count : suitCountVector) {
                if (count == 4)
                    flushDraw = true;
                else if (count == 3)
                    backdoorFlushDraw = true;
            }
        }
    }

    protected void processRanks(int[] rankCountVector) {
        int straightCount = 0;
        String tempStraightRank = null;
        int highBoardCard = -1;
        for(int i = rankCountVector.length - 1; i > 1; i--) {
            int count = rankCountVector[i];
            String rank = Card.getRankFromInt(i);

            //set or trips - remember we can have three of a kind on the flop (trips flop) but not have trips or set
            if(count == 3 && alreadyThreeOfAKind) { //3 count but not a boat
                if(holeCards.containsRank(Card.getRankFromInt(i))) { //if hole cards contains card, not trips flop
                    if(holeCards.isPocketPair()) { //if pocket pair, then it's a set
                         set = true;
                    } else {
                        trips = true;
                    }
                }

            }
            if(highBoardCard < 0 && count > 0) {
                highBoardCard = i;
                if(holeCards.isPocketPair() && count == 2 && i <= holeCards.getLargerCard().getRankInt()) {
                    overpair = true;
                }
            }
            if(!alreadyStraight) {
                if (count > 0) {
                    if (tempStraightRank == null)
                        tempStraightRank = rank;
                    straightCount++;
                    if (straightCount == 3)
                        backdoorStraightDraw = true;
                    if (straightCount == 4 && i < 11) { //A thru J is not an openender. Bottom card must be a T or lower. 5 thru 2 is open ender.
                        oesd = true;
                    }
                } else {
                    straightCount = 0;
                }
            }
        }
    }
}