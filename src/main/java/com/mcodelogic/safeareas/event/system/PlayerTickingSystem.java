package com.mcodelogic.safeareas.event.system;


import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.mcodelogic.safeareas.Constants;
import com.mcodelogic.safeareas.config.KConfig;
import com.mcodelogic.safeareas.event.PlayerRegionTracker;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.enums.RegionType;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;

/**
 * Tracks player movement via ticking and handles region changes
 * using the Region enter/leave cache.
 */
public class PlayerTickingSystem extends EntityTickingSystem<EntityStore> {
    private final PlayerRegionTracker regionTracker;
    /** Last shown title per player */
    private final Map<UUID, Message> titles = new HashMap<>();

    /** Last block position per player */
    private final Map<UUID, BlockPos> lastPositions = new HashMap<>();

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
        Player player = store.getComponent(ref, Player.getComponentType());

        UUID playerId = playerRef.getUuid();
        String worldId = player.getWorld().getName();

        int x = (int) playerRef.getTransform().getPosition().getX();
        int y = (int) playerRef.getTransform().getPosition().getY();
        int z = (int) playerRef.getTransform().getPosition().getZ();

        BlockPos last = lastPositions.get(playerId);

        // Ignore sub-block movement
        if (last != null && last.x == x && last.y == y && last.z == z) {
            return;
        }

        lastPositions.put(playerId, new BlockPos(x, y, z));

        // 🔥 Region enter / leave detection
        regionTracker.handleMove(playerId, playerRef, player, worldId, x, y, z);

        // Update title based on current regions
        if (!RegionManager.instance.getConfig().getDisableTitles()) updateTitle(playerRef, playerId);
    }

    private void updateTitle(PlayerRef playerRef, UUID playerId) {

        Set<Region> regions = regionTracker
                .getStates().get(playerId)
                .getCurrentRegions();

        Message defaultTitle = Message.raw("Wilderness").color(Color.GREEN);
        Message title = defaultTitle;

        // Choose highest priority region (if you support priorities)
        Region bestRegion = regions.stream()
                .max(Comparator.comparingInt(Region::getPriority))
                .orElse(null);


        if (bestRegion != null) {
            if (bestRegion.getType() == RegionType.GLOBAL) {
                title = defaultTitle;
            } else {
                title = Message.raw(bestRegion.getName()).color(Color.WHITE);
            }
        }

            Message current = titles.get(playerId);

        if (current != null && current.getRawText().equals(title.getRawText())) {
            return;
        }

        titles.put(playerId, title);

        EventTitleUtil.showEventTitleToPlayer(
                playerRef,
                title,
                Message.raw(Constants.PLUGIN_NAME),
                false,
                null,
                1.2f,
                0.2f,
                0.3f
        );
    }

    /**
     * Cleanup when a player despawns or disconnects.
     */
    public void handlePlayerRemove(UUID playerId) {
        titles.remove(playerId);
        lastPositions.remove(playerId);
        regionTracker.handleQuit(playerId);
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }

    /**
     * Simple immutable block position record.
     */
    private static final class BlockPos {
        final int x, y, z;

        BlockPos(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}