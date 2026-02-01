package com.mcodelogic.safeareas.utils;

public class KCommandUtil {

    public static String permissionFromCommand(String commandName) {
        return "safeareas.commands." + commandName;
    }
    public static String permissionFromCommand(String commandName, String subCommandName) {
        return "safeareas.commands." + commandName + "." + subCommandName;
    }
}
