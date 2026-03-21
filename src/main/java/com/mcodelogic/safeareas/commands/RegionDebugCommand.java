package com.mcodelogic.safeareas.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.utils.KCommandUtil;
import com.mcodelogic.safeareas.utils.SafeAreasDebug;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class RegionDebugCommand extends CommandBase {
    private final RequiredArg<String> enabledArg;

    public RegionDebugCommand(RegionManager manager) {
        super("debug", "Enable or disable SafeAreas debug logging.");
        this.enabledArg = this.withRequiredArg("enabled", "true/false, on/off, yes/no", ArgTypes.STRING);
        this.requirePermission(KCommandUtil.permissionFromCommand("region", "debug"));
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        String rawValue = enabledArg.get(commandContext);
        Boolean enabled = parseBoolean(rawValue);
        if (enabled == null) {
            commandContext.sendMessage(Message.raw("Usage: /region debug <true|false>"));
            return;
        }

        SafeAreasDebug.setEnabled(enabled);
        commandContext.sendMessage(Message.raw("SafeAreas debug logging is now " + (SafeAreasDebug.isEnabled() ? "enabled" : "disabled") + "."));
    }

    private Boolean parseBoolean(String value) {
        if (value == null) {
            return null;
        }

        return switch (value.toLowerCase()) {
            case "true", "on", "yes", "allow", "enable", "enabled", "1" -> true;
            case "false", "off", "no", "deny", "disable", "disabled", "0" -> false;
            default -> null;
        };
    }
}
