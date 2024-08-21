package io.vortex.cvtr;

public class SpecialCharacters {

    public static String space() {
        return " ";
    }

    public static String tabSpace() {
        return "    ";
    }

    public static String twoTabSpace() {
        return multiTabSpace(2);
    }

    public static String multiTabSpace(int count) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < count; i++) {
            res.append(tabSpace());
        }
        return res.toString();
    }

    public static String lineSeparator() {
        return System.lineSeparator();
    }

}
