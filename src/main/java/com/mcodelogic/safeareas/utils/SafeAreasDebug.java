package com.mcodelogic.safeareas.utils;

import com.mcodelogic.safeareas.KMain;

public final class SafeAreasDebug {
    private static volatile boolean enabled = false;

    private SafeAreasDebug() {
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        SafeAreasDebug.enabled = enabled;
    }

    public static void log(String source, String message) {
        if (!enabled) {
            return;
        }

        KMain.LOGGER.atInfo().log("[Debug][" + source + "] " + message);
    }
}
