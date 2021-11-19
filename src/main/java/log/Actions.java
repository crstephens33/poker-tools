package log;

import java.util.*;

public class Actions {
    
    //Types
    private final static String folds = LogUtils.foldsIndicator.trim();
    private final static String calls = LogUtils.callsIndicator.trim();
    private final static String bets = LogUtils.betsIndicator.trim();
    private final static String raises = LogUtils.raisesIndicator.trim();
    private final static String posts = LogUtils.postsIndicator.trim();

    //PREFLOP
    private List<String> openFoldPlayers = new ArrayList<>();
    private String openLimpPlayer = null;
    private List<String> overLimpPlayers = new ArrayList<>();
    private String rfiPlayer = null;
    private String rfiPosition = null;    
    
    private List<String> coldCallRfiPlayers = new ArrayList<>();
    private List<String> coldCallRfiPositions = new ArrayList<>();
    private List<String> foldToRFIPlayers = new ArrayList<>();

    private String threeBetPlayer = null;
    private List<String> coldCallThreeBetPlayers = new ArrayList<>();
    private List<String> coldCallThreeBetPositions = new ArrayList<>();    
    private List<String> coldFoldToThreeBetPlayers = new ArrayList<>();
    private String coldFourBetPlayer = null;

    private List<String> foldToReRaiseAfterLimping = new ArrayList<>();
    private List<String> callReRaiseAfterLimping = new ArrayList<>();

    private String callThreeBetAfterRfiPlayer = null;
    private String foldToThreeBetAfterRfiPlayer = null;
    private String fourBetAfterRfiPlayer = null;

    private String fiveBetPlayer = null;
    private String foldToFourBetAfterThreeBetPlayer = null;
    private String foldToColdFourBetAfterRFIPlayer = null;
    private String callColdFourBetAfterRFIPlayer = null;
    private String callFourBetAfterThreeBetPlayer = null;

    // SHOWDOWN
    private Map<String, String> shownDownCards = new HashMap<String, String>();

    public Actions() {
    }

    public void processLine(String line, String street) {
        checkRepresentation();
        String parsedName = LogUtils.parseNameFromLine(line);
        if(parsedName != null) {
            String currentPlayer = Player.buildPlayerNameKey(parsedName);
            if(LogUtils.lineContainsShowsCards(line)) {
                String cards = LogUtils.parseShownCardsFromLine(line);
                shownDownCards.put(currentPlayer, cards);
            }
            if(LogUtils.isPlayerAction(line)) {
                String currentAction = LogUtils.parseActionFromLine(line).toLowerCase().trim(); 
                if(currentAction.equals(posts)) { //don't do anything on a POST
                    return;
                }
                if(street.equals(HandHistory.PREFLOP_STREET)) {
                    processPreflop(currentAction, currentPlayer);
                }
            }
        }
    }

    private void processPreflop(String currentAction, String currentPlayer){
        if (rfiPlayer == null) { //unopened pot so far
            if (isFold(currentAction)) {
                openFoldPlayers.add(currentPlayer);
            } else if (isCall(currentAction)) {
                if (openLimpPlayer == null) { //if you just call in an unopened pot, either open-limping or over-limping
                    openLimpPlayer = currentPlayer;
                } else {
                    overLimpPlayers.add(currentPlayer);
                }
            } else if (isBetOrRaise(currentAction)) {
                rfiPlayer = currentPlayer;
            }
        } else {
            if (threeBetPlayer == null) { //single raised pot so far
                if (isFold(currentAction)) {
                    foldToRFIPlayers.add(currentPlayer);
                } else if (isCall(currentAction)) {
                    coldCallRfiPlayers.add(currentPlayer);
                } else if (isBetOrRaise(currentAction)) {
                    threeBetPlayer = currentPlayer;
                }
            } else { //three bet pot
                if (fourBetAfterRfiPlayer == null && coldFourBetPlayer == null) { //not a four bet pot yet though
                    if (currentPlayer.equals(rfiPlayer)) { //if current player was the initial raiser
                        if (isFold(currentAction)) {
                            foldToThreeBetAfterRfiPlayer = currentPlayer;
                        } else if (isCall(currentAction)) {
                            callThreeBetAfterRfiPlayer = currentPlayer;
                        } else if (isBetOrRaise(currentAction)) {
                            fourBetAfterRfiPlayer = currentPlayer;
                        }
                        //if player open limped
                    /*} else if ((openLimpPlayer != null && openLimpPlayer.equals(currentPlayer)) || overLimpPlayers.contains(currentPlayer)) {
                        if (isFold(currentAction)) {
                            foldToThreeBetAfterRfiPlayer = currentPlayer;
                        } else if (isCall(currentAction)) {
                            callThreeBetAfterRfiPlayer = currentPlayer;
                        } else if (isBetOrRaise(currentAction)) {
                            fourBetAfterRfiPlayer = currentPlayer;
                        }*/ //ignore limping for now
                    } else { //if current player was NOT the initial raiser or limper
                        if (isFold(currentAction)) {
                            coldFoldToThreeBetPlayers.add(currentPlayer);
                        } else if (isCall(currentAction)) {
                            coldCallThreeBetPlayers.add(currentPlayer);
                        } else if (isBetOrRaise(currentAction)) {
                            coldFourBetPlayer = currentPlayer;
                        }
                    }
                } else { //four bet pot

                }
            }
        }
    }

    //player VPIPd if they did NOT open fold, AND 
    public boolean didPlayerVPIPPreFlop(String player) {
        player = Player.buildPlayerNameKey(player);
        return !openFoldPlayers.contains(player) && (didPlayerEverLimp(player) || didPlayerRFI(player)
        || didPlayerCallRFI(player) || didPlayerThreeBet(player) || didPlayerFourBet(player)); 
    }

    public boolean didPlayerEverLimp(String player) {
        return player.equals(openLimpPlayer) || overLimpPlayers.contains(player);
    }

    public boolean didPlayerRFI(String player) {
        return player.equals(rfiPlayer);
    }

    public boolean didPlayerCallRFI(String player) {
        return coldCallRfiPlayers.contains(player);
    }

    public boolean didPlayerThreeBet(String player) {
        return player.equals(threeBetPlayer);
    }

    public boolean didPlayerFourBet(String player) {
        return player.equals(fourBetAfterRfiPlayer) || player.equals(coldFourBetPlayer);
    }

    public boolean isThreeBetPot() {
        return threeBetPlayer != null;
    }

    public String getThreeBettor() {
        return threeBetPlayer;
    }

    public boolean didPlayerCallThreeBetAfterRFI(String player) {
        return player.equals(callThreeBetAfterRfiPlayer);
    }

    public boolean didPlayerFoldToThreeBetAfterRFI(String player) {
        return player.equals(foldToThreeBetAfterRfiPlayer);
    }

    public boolean didPlayerFourBetAfterRFI(String player) {
        return player.equals(fourBetAfterRfiPlayer);
    }

    public boolean rfiBeforePlayer(String player) { //player didnt fold, RFI, or limp, and either 3Bet, fold to RFI, or called RFI.
        return !player.equals(rfiPlayer) && !openFoldPlayers.contains(player) && 
            (player.equals(threeBetPlayer) || coldCallRfiPlayers.contains(player) || foldToRFIPlayers.contains(player)); 
    }

    public boolean playerFoldedToRFI(String player) {
        return foldToRFIPlayers.contains(player);
    }

    public boolean playerShowedCards(String player) {
        return shownDownCards.keySet().contains(player);
    }

    public String getPlayersShownCards(String player) {
        return shownDownCards.get(player);
    }

    private Set<String> getAllLimpers() {
        Set<String> limpers = new HashSet<String>();
        limpers.addAll(overLimpPlayers);
        limpers.add(openLimpPlayer);
        return limpers;
    }

    



    private boolean isFold(String action) {
        return action.equals(folds);
    }

    private boolean isBetOrRaise(String action) {
        return action.equals(bets) || action.equals(raises);
    }

    private boolean isCall(String action) {
        return action.equals(calls);
    }    

    private void checkRepresentation(){
        assert(rfiPlayer != null && !rfiPlayer.equals(threeBetPlayer)); //RFI player should never be the same as threeBetPlayer
        assert(threeBetPlayer != null && !rfiPlayer.equals(fourBetAfterRfiPlayer)); //three better should never be the same as fourBetPlayer
    }
}