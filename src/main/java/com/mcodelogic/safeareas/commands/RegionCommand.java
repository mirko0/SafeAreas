package com.mcodelogic.safeareas.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.utils.KCommandUtil;

public class RegionCommand extends AbstractCommandCollection {
    private final RegionManager manager;
    public RegionCommand(RegionManager manager) {
        super("region", "Manage servers regions");
        this.manager = manager;
        this.addSubCommand(new RegionListCommand(manager));
        this.addSubCommand(new RegionRemoveFlagCommand(manager));
        this.addSubCommand(new RegionRenameCommand(manager));
        this.addSubCommand(new RegionCreateCommand(manager));
        this.addSubCommand(new RegionDeleteCommand(manager));
        this.addSubCommand(new RegionFlagCommand(manager));
        this.addSubCommand(new RegionFlagsCommand());
        this.addSubCommand(new RegionPriorityCommand(manager));
        this.addSubCommand(new RegionInfoCommand(manager));
        this.addSubCommand(new RegionUICommand(manager));
        this.requirePermission(KCommandUtil.permissionFromCommand("region"));
    }
}
