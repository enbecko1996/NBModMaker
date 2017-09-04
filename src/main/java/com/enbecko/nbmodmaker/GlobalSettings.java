package com.enbecko.nbmodmaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GlobalSettings {
    private static final List<Log.LogEnums> log = new ArrayList<Log.LogEnums>();

    public static boolean shouldLog(Log.LogEnums logEnum) {
        if (log.contains(logEnum)) {
            return true;
        }
        return false;
    }

    public static void setDebugMode() {
        log.clear();
        Collections.addAll(log, Log.LogEnums.values());
        GlobalRenderSetting.putRenderMode(GlobalRenderSetting.RenderMode.DEBUG);
    }

    public static void setLiveMode() {
        log.clear();
        GlobalRenderSetting.putRenderMode(GlobalRenderSetting.RenderMode.LIVE);
    }

    public static void addLogEnum(Log.LogEnums logEnum) {
        if (!log.contains(logEnum))
            log.add(logEnum);
    }

    public static void removeLogEnum(Log.LogEnums logEnum) {
        if (log.contains(logEnum))
            log.remove(logEnum);
    }

    public static int unitGridSize() {
        return 32;
    }
}

