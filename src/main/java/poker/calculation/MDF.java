package poker.calculation;

public class MDF {

    private static final String NO_ARGS = "noargs";
    private static final String ALL_DOUBLES = "alldoubles";

    private static final String BET = "bet";
    private static final String POT = "pot";
    private static final String ALPHA = "alpha";

    private static final String provideArgs = "Please provide arguments betsize and potsize before bet. Example: \"java MDF 50 100\"";
    private static final String undefinedCase = "Undefined use case.";

    public static void main(String[] args) {
        String action = parseArgs(args);
        System.out.println(run(args, action));
    }


    public static double getMdfGivenBetAndPotBeforeBet(double bet, double potBeforeBet) {
        return potBeforeBet / (potBeforeBet + bet);
    }

    private static String run(String[] args, String action) {        
        if (action.equals(NO_ARGS)) {
            return provideArgs;
        } else if (action.equals(ALL_DOUBLES)) {
            double mdf = getMdfGivenBetAndPotBeforeBet(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
            return formatDoubleAsPercent(mdf);
        }
        return undefinedCase;
    }

    private static String formatDoubleAsPercent(double d) {
        return String.format("%.0f%%", d * 100);
    }

    private static String parseArgs(String[] args) {
        if(args.length == 0) {
            return NO_ARGS;
        }
        boolean allDoubles = true;
        for(int i = 0; i < args.length; i++) {
            if(!isDouble(args[i])) {
                allDoubles = false;
            }
        }
        return allDoubles ? ALL_DOUBLES : null;
    }

    private static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}