package com.mcodelogic.safeareas.commands;

import com.hypixel.hytale.server.core.Message;
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
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class RegionRenameCommand extends CommandBase {

    @Nonnull
    private final RequiredArg<World> worldArg;
    private final RequiredArg<String> regionName;
    private final RequiredArg<String> newRegionName;

    private final RegionManager manager;
    public RegionRenameCommand(RegionManager manager) {
        this.manager = manager;
        super("rename", "Rename existing region.");
        this.worldArg = this.withRequiredArg("world", "Selected world.", ArgTypes.WORLD);
        this.regionName = this.withRequiredArg("name", "Region name.", ArgTypes.STRING);
        this.newRegionName = this.withRequiredArg("new_name", "New region name.", ArgTypes.STRING);
        this.requirePermission(KCommandUtil.permissionFromCommand("region", "rename"));
    }
    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        World world = (World) this.worldArg.get(commandContext);
        String regionName = this.regionName.get(commandContext);
        String newRegionName = this.newRegionName.get(commandContext);
        Set<Region> regions = manager.getRegionsByWorld().get(world.getName());
        if (regions == null) regions = new HashSet<>();
        if (regions.isEmpty() || regions.stream().noneMatch(r -> r.getName().equals(regionName))) {
            commandContext.sendMessage(Message.raw("Region not found!").color(Color.red));
            return;
        }
        Region region = regions.stream().filter(r -> r.getName().equals(regionName)).findFirst().orElse(null);
        if (region == null) {
            commandContext.sendMessage(Message.raw("Region not found!").color(Color.red));
            return;
        }
        manager.getApi().deleteRegion(region.getId());
        region.setName(newRegionName);
        manager.getApi().save(region);
        manager.getRegionsById().put(region.getId(), region);
        manager.getRegionsByWorld().get(world.getName()).add(region);

        commandContext.sendMessage(Message.raw("Region " + regionName + " renamed to: " + newRegionName + ".").color(Color.GREEN));
    }
}
