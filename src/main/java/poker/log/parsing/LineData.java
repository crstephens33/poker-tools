package poker.log.parsing;

public class LineData {

    //do some eval to see if we're actually saving runtime here.

    private String line;

    private boolean isPreflop;
    private boolean isFlop;
    private boolean isTurn;
    private boolean isRiver;

    private boolean potAppended;
}
