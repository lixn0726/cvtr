package io.vortex.cvtr;

public class StringSupport {

    public static boolean isBlankStr(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static String upperFirst(String origin) {
        if (StringSupport.isBlankStr(origin)) {
            return origin;
        }
        return origin.substring(0, 1).toUpperCase() + origin.substring(1);
    }

    public static String lowerFirst(String origin) {
        if (StringSupport.isBlankStr(origin)) {
            return origin;
        }
        return origin.substring(0, 1).toLowerCase() + origin.substring(1);
    }

    public static String emptyStr() {
        return "";
    }
}
