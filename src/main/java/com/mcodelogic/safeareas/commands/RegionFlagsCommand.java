package com.mcodelogic.safeareas.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.enums.RegionFlag;
import com.mcodelogic.safeareas.utils.KCommandUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class RegionFlagsCommand extends CommandBase {
    public RegionFlagsCommand() {
        super("flags", RegionManager.instance.getLang().get("CommandDescRegionFlags"));
        this.requirePermission(KCommandUtil.permissionFromCommand("region", "flags"));
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        RegionFlag[] values = RegionFlag.values();
        StringBuilder builder = new StringBuilder();
        for (RegionFlag flag : values) {
            builder.append(flag.name()).append(", ");
        }

        commandContext.sendMessage(RegionManager.instance.getLang().getMessage("CommandRegionFlagsAvailable", builder.toString()));
    }
}
