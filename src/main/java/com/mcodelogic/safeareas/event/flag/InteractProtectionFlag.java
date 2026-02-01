package com.mcodelogic.safeareas.event.flag;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.mcodelogic.safeareas.event.RegionFlagResolver;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.enums.RegionFlag;
import com.mcodelogic.safeareas.utils.DefaultColors;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Collections;
import java.util.Set;

public class InteractProtectionFlag extends EntityEventSystem<EntityStore, UseBlockEvent.Pre> {
    public InteractProtectionFlag() {
        super(UseBlockEvent.Pre.class);
    }

    @Override
    public void handle(int index, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                       @NonNullDecl Store<EntityStore> store,
                       @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
                       @NonNullDecl UseBlockEvent.Pre event) {
        if (event.isCancelled()) return;
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (player == null || playerRef == null) return;

        Vector3i targetBlock = event.getTargetBlock();
        Set<Region> regions = RegionManager.instance.getApi().getRegionsAt(player.getWorld(), targetBlock.x, targetBlock.y, targetBlock.z);

        boolean canUse = RegionFlagResolver.resolve(regions, RegionFlag.INTERACT, true);

        if (!canUse && !player.hasPermission(RegionManager.instance.getConfig().getDefaultAdminPermission())) {
            event.setCancelled(true);
            boolean canNotify = RegionFlagResolver.resolve(regions, RegionFlag.NOTIFICATIONS, true);
            if (!canNotify) return;

            var primaryMessage = Message.raw("Interaction Disabled!").color(DefaultColors.RED.getColor());
            var secondaryMessage = Message.raw("You cannot interact in this region!").color(DefaultColors.GRAY.getColor());
            var icon = new ItemStack("Furniture_Crude_Chest_Small", 1).toPacket();

            NotificationUtil.sendNotification(playerRef.getPacketHandler(), primaryMessage, secondaryMessage, (ItemWithAllMetadata) icon);
        }


    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Collections.singleton(RootDependency.first());
    }
}
