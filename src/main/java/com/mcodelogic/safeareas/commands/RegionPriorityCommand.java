package com.mcodelogic.safeareas.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.world.World;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.utils.KCommandUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class RegionPriorityCommand extends CommandBase {

    @Nonnull
    private final RequiredArg<World> worldArg;
    private final RequiredArg<String> regionName;
    private final RequiredArg<Integer> flagNameArg;

    private final RegionManager manager;

    public RegionPriorityCommand(RegionManager manager) {
        super("priority", manager.getLang().get("CommandDescRegionPriority"));
        this.manager = manager;
        this.worldArg = this.withRequiredArg("world", "Selected world.", ArgTypes.WORLD);
        this.regionName = this.withRequiredArg("name", "Region name.", ArgTypes.STRING);
        this.flagNameArg = this.withRequiredArg("priority", "Region Priority (Higher Priority Wins).", ArgTypes.INTEGER);
        this.setAllowsExtraArguments(true);
        this.requirePermission(KCommandUtil.permissionFromCommand("region", "priority"));
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        World world = this.worldArg.get(commandContext);
        String regionName = this.regionName.get(commandContext);
        Integer priority = this.flagNameArg.get(commandContext);

        Set<Region> regions = manager.getRegionsByWorld().get(world.getName());
        if (regions == null) regions = new HashSet<>();
        var lang = manager.getLang();
        if (regions.isEmpty() || regions.stream().noneMatch(r -> r.getName().equals(regionName))) {
            commandContext.sendMessage(lang.getMessage("CommandRegionNotFound"));
            return;
        }
        Region region = regions.stream().filter(r -> r.getName().equals(regionName)).findFirst().orElse(null);
        if (region == null) return;

        region.setPriority(priority);
        manager.getApi().save(region);
        commandContext.sendMessage(lang.getMessage("CommandRegionPrioritySet", priority, region.getName()));
    }
}
