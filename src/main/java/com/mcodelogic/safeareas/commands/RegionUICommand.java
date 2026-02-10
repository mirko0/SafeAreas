package com.mcodelogic.safeareas.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.mcodelogic.safeareas.utils.KCommandUtil;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.page.RegionListPage;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class RegionUICommand extends AbstractPlayerCommand {

    public RegionUICommand(RegionManager manager) {
        super("ui", manager.getLang().get("CommandDescRegionUi"));
        this.requirePermission(KCommandUtil.permissionFromCommand("region", "ui"));
    }

    @Override
    protected void execute(
            @NonNullDecl CommandContext commandContext,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PlayerRef playerRef,
            @NonNullDecl World world
    ) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player != null) {
            player.getPageManager().openCustomPage(ref, store, new RegionListPage(playerRef));
        }
    }
}
