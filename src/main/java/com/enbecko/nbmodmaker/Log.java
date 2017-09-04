package com.enbecko.nbmodmaker;

public class Log {
    public static void d(LogEnums logEnums, String txt) {
        d(logEnums, txt, 3);
    }

    private static void d(LogEnums logEnums, String txt, int pos) {
        if (GlobalSettings.shouldLog(logEnums)) {
            System.out.println("\nDEBUG: " + logEnums + ", " + Thread.currentThread().getStackTrace()[pos] + "\n"+txt);
        }
    }

    public static void d(LogEnums logEnums, Object txt) {
        if (txt != null)
            d(logEnums, txt.toString(), 3);
        else
            d(logEnums, "null", 3);
    }

    public enum LogEnums {
        CONTENTHOLDER, BLOCKSETTING, MATH, GEOMETRY, MINECRAFT, RENDERING, ETC;
    }
}

