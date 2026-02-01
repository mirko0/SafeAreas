package com.mcodelogic.safeareas.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandUtil;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.world.World;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.enums.RegionFlag;
import com.mcodelogic.safeareas.utils.KCommandUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegionRemoveFlagCommand extends CommandBase {

    @Nonnull
    private final RequiredArg<World> worldArg;
    private final RequiredArg<String> regionName;
    private final RequiredArg<String> flagNameArg;

    private final RegionManager manager;

    public RegionRemoveFlagCommand(RegionManager manager) {
        super("removeflag", "Remove a flag from region.");
        this.manager = manager;
        this.worldArg = this.withRequiredArg("world", "Selected world.", ArgTypes.WORLD);
        this.regionName = this.withRequiredArg("name", "Region name.", ArgTypes.STRING);
        this.flagNameArg = this.withRequiredArg("flag", "Flag name.", ArgTypes.STRING);
        this.setAllowsExtraArguments(true);
        this.requirePermission(KCommandUtil.permissionFromCommand("region", "removeflag"));
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        World world = this.worldArg.get(commandContext);
        String regionName = this.regionName.get(commandContext);
        String flagName = this.flagNameArg.get(commandContext).toUpperCase();

        Set<Region> regions = manager.getRegionsByWorld().get(world.getName());
        if (regions == null) regions = new HashSet<>();
        if (regions.isEmpty() || regions.stream().noneMatch(r -> r.getName().equals(regionName))) {
            commandContext.sendMessage(Message.raw("Region not found!").color(Color.red));
            return;
        }
        Region region = regions.stream().filter(r -> r.getName().equals(regionName)).findFirst().orElse(null);
        if (region == null) return;
        RegionFlag[] values = RegionFlag.values();
        List<String> flags = List.of(values).stream().map(Enum::name).toList();
        if (!flags.contains(flagName)) {
            commandContext.sendMessage(Message.raw("Invalid flag! Available Flags: " + flags).color(Color.RED));
            return;
        }
        RegionFlag flag = RegionFlag.valueOf(flagName);
        region.getFlags().remove(flag);
        manager.getApi().save(region);
        commandContext.sendMessage(Message.raw("Flag " + flagName + " removed from region " + region.getName()).color(Color.GREEN));
    }

}
