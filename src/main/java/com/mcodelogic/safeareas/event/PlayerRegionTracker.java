package com.mcodelogic.safeareas.event;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.mcodelogic.safeareas.api.IRegionAPI;
import com.mcodelogic.safeareas.config.KConfig;
import com.mcodelogic.safeareas.event.system.PlayerTickingSystem;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.FlagValue;
import com.mcodelogic.safeareas.model.PlayerRegionState;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.enums.RegionFlag;
import com.mcodelogic.safeareas.utils.DefaultColors;
import lombok.Getter;

import java.util.*;

public class PlayerRegionTracker {

    private final IRegionAPI regionAPI;
    @Getter
    private final Map<UUID, PlayerRegionState> states = new HashMap<>();

    @Getter
    private final PlayerTickingSystem playerTickingSystem;
    public PlayerRegionTracker(IRegionAPI regionAPI) {
        this.regionAPI = regionAPI;
        this.playerTickingSystem = new PlayerTickingSystem(this);
    }

    public void handleMove(
            UUID playerId,
            PlayerRef playerRef, Player player,
            String worldName,
            int x, int y, int z
    ) {
        PlayerRegionState state = states.computeIfAbsent(playerId, id -> new PlayerRegionState());

        if (state.isSameBlock(x, y, z)) {
            return;
        }

        state.updateBlock(x, y, z);

        Set<Region> newRegions = regionAPI.getRegionsAt(worldName, x, y, z);

        Set<Region> oldRegions = state.getCurrentRegions();

        if (newRegions.equals(oldRegions)) {
            return;
        }

        // Calculate differences
        Set<Region> entered = new HashSet<>(newRegions);
        entered.removeAll(oldRegions);

        Set<Region> exited = new HashSet<>(oldRegions);
        exited.removeAll(newRegions);

        // Fire hooks
        for (Region region : entered) {
            onEnterRegion(playerId, playerRef, player, region);
        }

        for (Region region : exited) {
            onLeaveRegion(playerId, playerRef, player,  region);
        }

        state.setCurrentRegions(newRegions);
    }

    public void handleQuit(UUID playerId) {
        states.remove(playerId);
    }

    /* Hooks – will become real events later */
    protected void onEnterRegion(UUID playerId, PlayerRef playerRef, Player player, Region region) {
        try {
            FlagValue<String> flag = (FlagValue<String>) region.getFlag(RegionFlag.GREETING);
            if (flag != null) {
                playerRef.sendMessage(
                        Message.join(
                                Message.raw("SafeAreas > ").color(DefaultColors.YELLOW.getColor()).bold(true),
                                Message.raw(flag.getValue()).color(DefaultColors.GRAY.getColor())
                        )
                );
            }
        } catch (Exception e) {
            HytaleLogger.getLogger().atInfo().log("Failed to send greeting message for region " + region.getName());
        }

    }

    protected void onLeaveRegion(UUID playerId, PlayerRef playerRef, Player player, Region region) {
        try {
            FlagValue<String> flag = (FlagValue<String>) region.getFlag(RegionFlag.FAREWELL);
            if (flag != null) {
                playerRef.sendMessage(
                        Message.join(
                                Message.raw("SafeAreas > ").color(DefaultColors.YELLOW.getColor()).bold(true),
                                Message.raw(flag.getValue()).color(DefaultColors.GRAY.getColor())
                        )
                );
            }
        } catch (Exception e) {
            HytaleLogger.getLogger().atInfo().log("Failed to send farewell message for region " + region.getName());
        }
    }


    public PlayerRegionState getState(UUID playerUUID) {
        return states.get(playerUUID);
    }

}
