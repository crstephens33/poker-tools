package util;

public class LogUtils {

    public static String convertLongMillisToSeconds(long l) {
        return String.format("%.2f", l * 0.001) + "s";
    }

    public static String inMillis(long l) {
        return l + "ms";
    }

    public static String inMillis(double d) {
        return String.format("%.5f", d) + "ms";
    }

    public static String formatPercent(double d) { return String.format("%.2f", d) + "%"; }

    public static void warn(String s) {
        System.out.println("Warning: " + s);
    }
}
