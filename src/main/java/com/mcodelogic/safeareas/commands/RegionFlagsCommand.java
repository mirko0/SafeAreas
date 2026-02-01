package com.mcodelogic.safeareas.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.mcodelogic.safeareas.model.enums.RegionFlag;
import com.mcodelogic.safeareas.utils.KCommandUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.awt.*;

public class RegionFlagsCommand extends CommandBase {
    public RegionFlagsCommand() {
        super("flags", "Show all available flags for regions");
        this.requirePermission(KCommandUtil.permissionFromCommand("region", "flags"));
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        RegionFlag[] values = RegionFlag.values();
        StringBuilder builder = new StringBuilder();
        for (RegionFlag flag : values) {
            builder.append(flag.name()).append(", ");
        }

        commandContext.sendMessage(Message.join(
                Message.raw("Available flags: \n" ).color(Color.GREEN),
                Message.raw(builder.toString()).color(Color.WHITE)
        ));
    }
}
