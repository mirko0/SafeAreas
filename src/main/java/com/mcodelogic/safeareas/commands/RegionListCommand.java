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
import java.util.HashSet;
import java.util.Set;

public class RegionListCommand extends CommandBase {

    @Nonnull
    private final RequiredArg<World> worldArg;

    private final RegionManager manager;
    public RegionListCommand(RegionManager manager) {
        this.manager = manager;
        super("list", "List regions in selected world.");
        this.worldArg = this.withRequiredArg("world", "Selected world.", ArgTypes.WORLD);
        this.requirePermission(KCommandUtil.permissionFromCommand("region", "list"));
    }
    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        World world = (World) this.worldArg.get(commandContext);
        Set<Region> regions = manager.getRegionsByWorld().get(world.getName());
        if (regions == null) regions = new HashSet<>();

        StringBuilder regionsBuilder = new StringBuilder();
        regions.forEach(r -> regionsBuilder.append(r.getName()).append(", "));

        commandContext.sendMessage(Message.raw("Regions (" + regions.size() + "): " + regionsBuilder.toString()));
    }
}
