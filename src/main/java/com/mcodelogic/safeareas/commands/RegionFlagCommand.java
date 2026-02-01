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

public class RegionFlagCommand extends CommandBase {

    @Nonnull
    private final RequiredArg<World> worldArg;
    private final RequiredArg<String> regionName;
    private final RequiredArg<String> flagNameArg;
    private final RequiredArg<String> flagValueArg;

    private final RegionManager manager;

    public RegionFlagCommand(RegionManager manager) {
        super("flag", "Add flag to region.");
        this.manager = manager;
        this.worldArg = this.withRequiredArg("world", "Selected world.", ArgTypes.WORLD);
        this.regionName = this.withRequiredArg("name", "Region name.", ArgTypes.STRING);
        this.flagNameArg = this.withRequiredArg("flag", "Flag name.", ArgTypes.STRING);
        this.flagValueArg = this.withRequiredArg("value", "Flag value.", ArgTypes.STRING);
        this.setAllowsExtraArguments(true);
        this.requirePermission(KCommandUtil.permissionFromCommand("region", "flag"));
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        String rawArgs = CommandUtil.stripCommandName(commandContext.getInputString()).trim();
        World world = (World) this.worldArg.get(commandContext);
        String regionName = this.regionName.get(commandContext);
        String flagName = this.flagNameArg.get(commandContext).toUpperCase();
        String[] rawArgsSplit = rawArgs.split(" ");
        // get all arguments after 3rd one
        String flagValue = String.join(" ", java.util.Arrays.copyOfRange(rawArgsSplit, 4, rawArgsSplit.length));
        flagValue = flagValue.trim();
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
        Serializable value = flag.getValueType().equals(Boolean.class) ? parseBoolean(flagValue) : flagValue;
        if (flagValue.equalsIgnoreCase("remove")) {
            region.getFlags().remove(flag);
            commandContext.sendMessage(Message.raw("Flag " + flagName + " removed from region " + region.getName()).color(Color.GREEN));
        }else {
            region.setFlag(flag, value);
            commandContext.sendMessage(Message.raw("Flag " + flagName + " set to " + value + " for region " + region.getName()).color(Color.GREEN));
        }
        manager.getApi().save(region);
    }

    private Boolean parseBoolean(String value) {
        if (value.equalsIgnoreCase("on")) return true;
        if (value.equalsIgnoreCase("off")) return false;
        if (value.equalsIgnoreCase("deny")) return false;
        if (value.equalsIgnoreCase("enabled")) return true;
        if (value.equalsIgnoreCase("allow")) return true;
        if (value.equalsIgnoreCase("disabled")) return false;
        return Boolean.parseBoolean(value);
    }
}
