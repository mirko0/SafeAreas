package com.mcodelogic.safeareas.manager;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.mcodelogic.safeareas.event.flag.GreetingAndFarewellFlag;
import com.mcodelogic.safeareas.manager.api.IRegionAPI;
import com.mcodelogic.safeareas.event.system.PlayerTickingSystem;
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
        if (state.isSameBlock(x, y, z)) return;
        state.updateBlock(x, y, z);

        Set<Region> newRegions = regionAPI.getRegionsAt(worldName, x, y, z);
        Set<Region> oldRegions = state.getCurrentRegions();

        if (newRegions.equals(oldRegions)) return;

        // Calculate differences
        Set<Region> entered = new HashSet<>(newRegions);
        entered.removeAll(oldRegions);

        Set<Region> exited = new HashSet<>(oldRegions);
        exited.removeAll(newRegions);

        // Fire hooks
        for (Region region : entered) {
            onEnterRegion(playerId, playerRef, player, region);
        }
        onEnterRegions(playerId, playerRef, player, entered);
        for (Region region : exited) {
            onLeaveRegion(playerId, playerRef, player,  region);
        }
        onLeaveRegions(playerId, playerRef, player, exited);

        state.setCurrentRegions(newRegions);

    }

    public void handleQuit(UUID playerId) {
        states.remove(playerId);
    }

    protected void onEnterRegions(UUID playerId, PlayerRef playerRef, Player player, Set<Region> regions) {
        GreetingAndFarewellFlag.sendGreetingTitle(regions, playerRef);
    }
    protected void onLeaveRegions(UUID playerId, PlayerRef playerRef, Player player, Set<Region> regions) {
        GreetingAndFarewellFlag.sendFarewellTitle(regions, playerRef);
    }

    /* Hooks – will become real events later */
    protected void onEnterRegion(UUID playerId, PlayerRef playerRef, Player player, Region region) {
       GreetingAndFarewellFlag.sendGreetingMessage(region, playerRef);
    }

    protected void onLeaveRegion(UUID playerId, PlayerRef playerRef, Player player, Region region) {
        GreetingAndFarewellFlag.sendFarewellMessage(region, playerRef);
    }

    public PlayerRegionState getState(UUID playerUUID) {
        return states.get(playerUUID);
    }

}
