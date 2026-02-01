package com.mcodelogic.safeareas.commands;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.world.World;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.FlagValue;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.SelectionArea;
import com.mcodelogic.safeareas.model.enums.RegionFlag;
import com.mcodelogic.safeareas.model.enums.RegionType;
import com.mcodelogic.safeareas.utils.DefaultColors;
import com.mcodelogic.safeareas.utils.KCommandUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegionInfoCommand extends CommandBase {

    @Nonnull
    private final RequiredArg<World> worldArg;
    private final RequiredArg<String> regionName;

    private final RegionManager manager;

    public RegionInfoCommand(RegionManager manager) {
        this.manager = manager;
        super("info", "Preview Region information.");
        this.worldArg = this.withRequiredArg("world", "Selected world.", ArgTypes.WORLD);
        this.regionName = this.withRequiredArg("name", "Region name.", ArgTypes.STRING);
        this.requirePermission(KCommandUtil.permissionFromCommand("region", "info"));
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        World world = (World) this.worldArg.get(commandContext);
        String regionName = this.regionName.get(commandContext);
        Set<Region> regions = manager.getRegionsByWorld().get(world.getName());
        if (regions == null) regions = new HashSet<>();
        if (regions.isEmpty() || regions.stream().noneMatch(r -> r.getName().equals(regionName))) {
            commandContext.sendMessage(Message.raw("Region not found!").color(Color.red));
            return;
        }
        Region region = regions.stream().filter(r -> r.getName().equals(regionName)).findFirst().orElse(null);
        if (region == null) return;

        RegionType type = region.getType();
        Map<RegionFlag, FlagValue<?>> flags = region.getFlags();
        SelectionArea area = region.getArea();
        int priority = region.getPriority();
        String name = region.getName();
        String worldName = region.getWorldName();
        String minString = area.getMinX() + ", " + area.getMinY() + ", " + area.getMinZ();
        String maxString = area.getMinX() + ", " + area.getMinY() + ", " + area.getMinZ();
        if (type.equals(RegionType.AREA)) {
            minString = area.getMinX() + ", " + area.getMinZ();
            maxString = area.getMinX() + ", " + area.getMinZ();
        }


        StringBuilder builder = new StringBuilder();
        flags.forEach((flag, value) -> {
            if (flag != null) builder.append(flag.name()).append(": ").append(value.getValue()).append(", ");
        }
        );
        String flagsString = builder.toString();
        if (!flagsString.isEmpty()) flagsString = flagsString.substring(0, flagsString.length() - 2);


        Message finalMessage = Message.join(
                Message.raw("==========================================\n").color(DefaultColors.GRAY.getColor()),
                Message.join(Message.raw("Name: ").bold(true).color(DefaultColors.ORANGE.getColor()), Message.raw(name + "\n").color(DefaultColors.GRAY.getColor())),
                Message.join(Message.raw("WorldName: ").bold(true).color(DefaultColors.ORANGE.getColor()), Message.raw(worldName + "\n").color(DefaultColors.GRAY.getColor())),
                Message.join(Message.raw("Priority: ").bold(true).color(DefaultColors.ORANGE.getColor()), Message.raw(priority + "\n").color(DefaultColors.GRAY.getColor())),
                Message.join(Message.raw("Type: ").bold(true).color(DefaultColors.ORANGE.getColor()), Message.raw(type.name() + "\n").color(DefaultColors.GRAY.getColor())),
                (!type.equals(RegionType.GLOBAL) ? Message.join(Message.raw("Area: ").bold(true).color(DefaultColors.ORANGE.getColor()), Message.raw(minString + " - " + maxString + "\n").color(DefaultColors.GRAY.getColor())) : Message.raw("")),
                Message.join(Message.raw("Flags: ").bold(true).color(DefaultColors.ORANGE.getColor()), Message.raw(flagsString + "\n").color(DefaultColors.GRAY.getColor())),
                Message.raw("==========================================").color(DefaultColors.GRAY.getColor())
        );

        commandContext.sendMessage(finalMessage);
    }
}
