package com.mcodelogic.safeareas.event.system;


import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.mcodelogic.safeareas.manager.PlayerRegionTracker;
import com.mcodelogic.safeareas.model.PlayerRegionState;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Tracks player movement via ticking and handles region changes
 * using the Region enter/leave cache.
 */
public class PlayerTickingSystem extends EntityTickingSystem<EntityStore> {
    private final PlayerRegionTracker regionTracker;

    public PlayerTickingSystem(PlayerRegionTracker tracker) {
        this.regionTracker = tracker;
    }

    @Override
    public void tick(float v,
                     int index,
                     @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNullDecl Store<EntityStore> store,
                     @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) return;
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
        if (uuidComponent == null) return;
        UUID playerId = uuidComponent.getUuid();
        World world = commandBuffer.getExternalData().getWorld();
        String worldId = world.getName();

        Vector3d playerPos = playerRef.getTransform().getPosition();
        int x = (int) playerPos.getX();
        int y = (int) playerPos.getY();
        int z = (int) playerPos.getZ();

        // Ignore sub-block movement
        PlayerRegionState state = regionTracker.getState(playerId);
        if (state != null) {
            int lastBlockX = state.getLastBlockX();
            int lastBlockY = state.getLastBlockY();
            int lastBlockZ = state.getLastBlockZ();
            if (lastBlockX == x && lastBlockY == y && lastBlockZ == z) return;
        }

        // Region enter / leave detection
        regionTracker.handleMove(playerId, playerRef, player, worldId, x, y, z);
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }
}