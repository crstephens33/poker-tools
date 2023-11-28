package poker.log.hand_history;

public enum TableSize {

    HEADS_UP(2), THREE_HANDED(3), FOUR_HANDED(4), FIVE_HANDED(5), SIX_HANDED(6), SEVEN_HANDED(7), EIGHT_HANDED(8),
    NINE_HANDED(9), TEN_HANDED(10), ALL_SIZES(-1);

    public final int size;

    private TableSize(int size){
        this.size = size;
    }
}
