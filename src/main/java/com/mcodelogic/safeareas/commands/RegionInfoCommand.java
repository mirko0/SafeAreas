package com.mcodelogic.safeareas.commands;

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
import com.mcodelogic.safeareas.utils.KCommandUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegionInfoCommand extends CommandBase {

    @Nonnull
    private final RequiredArg<World> worldArg;
    private final RequiredArg<String> regionName;

    private final RegionManager manager;

    public RegionInfoCommand(RegionManager manager) {
        super("info", manager.getLang().get("CommandDescRegionInfo"));
        this.manager = manager;
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
            commandContext.sendMessage(manager.getLang().getMessage("CommandRegionInfoNotFound"));
            return;
        }
        Region region = regions.stream().filter(r -> r.getName().equals(regionName)).findFirst().orElse(null);
        if (region == null) return;

        var lang = manager.getLang();
        RegionType type = region.getType();
        Map<RegionFlag, FlagValue<?>> flags = region.getFlags();
        SelectionArea area = region.getArea();
        int priority = region.getPriority();
        String name = region.getName();
        String worldName = region.getWorldName();

        String minString;
        String maxString;
        if (type.equals(RegionType.CUBOID)) {
            minString = (int) area.getMinX() + ", " + (int) area.getMinY() + ", " + (int) area.getMinZ();
            maxString = (int) area.getMaxX() + ", " + (int) area.getMaxY() + ", " + (int) area.getMaxZ();
        } else if (type.equals(RegionType.AREA)) {
            minString = (int) area.getMinX() + ", " + (int) area.getMinZ();
            maxString = (int) area.getMaxX() + ", " + (int) area.getMaxZ();
        } else {
            minString = "";
            maxString = "";
        }

        StringBuilder builder = new StringBuilder();
        flags.forEach((flag, value) -> {
            if (flag != null) builder.append(flag.name()).append(": ").append(value.getValue()).append(", ");
        });
        String flagsString = builder.toString();
        if (!flagsString.isEmpty()) flagsString = flagsString.substring(0, flagsString.length() - 2);

        List<Message> parts = new ArrayList<>();
        parts.add(lang.getMessage("CommandRegionInfoHeader", name));
        parts.add(lang.getMessage("CommandRegionInfoName", name));
        parts.add(lang.getMessage("CommandRegionInfoWorld", worldName));
        parts.add(lang.getMessage("CommandRegionInfoPriority", priority));
        parts.add(lang.getMessage("CommandRegionInfoType", type.name()));
        if (!type.equals(RegionType.GLOBAL)) {
            parts.add(lang.getMessage("CommandRegionInfoArea", minString, maxString));
        }
        parts.add(flagsString.isEmpty()
                ? lang.getMessage("CommandRegionInfoNoFlags")
                : lang.getMessage("CommandRegionInfoFlags", flagsString));

        commandContext.sendMessage(Message.join(parts.toArray(new Message[0])));
    }
}
