package com.mcodelogic.safeareas.manager;

import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.mcodelogic.safeareas.KMain;
import com.mcodelogic.safeareas.api.IRegionAPI;
import com.mcodelogic.safeareas.config.KConfig;
import com.mcodelogic.safeareas.event.PlayerJoinLeaveEvent;
import com.mcodelogic.safeareas.event.PlayerRegionTracker;
import com.mcodelogic.safeareas.event.flag.*;
import com.mcodelogic.safeareas.model.PlayerPositionCoords;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.SelectionArea;
import com.mcodelogic.safeareas.model.enums.RegionType;
import com.mcodelogic.safeareas.storage.IRegionStorage;
import com.mcodelogic.safeareas.storage.JsonRegionStorage;
import lombok.Getter;

import java.nio.file.Path;
import java.util.*;

public class RegionManager{

    @Getter
    private final Map<UUID, PlayerPositionCoords> playerPositions = new HashMap<>();

    @Getter
    private final Map<UUID, Region> regionsById = new HashMap<>();
    @Getter
    private final Map<String, Set<Region>> regionsByWorld = new HashMap<>();
    @Getter
    private final IRegionStorage storage;

    @Getter
    private final IRegionAPI api;

    @Getter
    private final PlayerRegionTracker tracker;

    @Getter
    private final KConfig config;

    public static RegionManager instance;
    public RegionManager(KMain pluginMain) {
        instance = this;
        this.config = pluginMain.getConfiguration();
        Path dataDirectory = pluginMain
                .getDataDirectory()
                .resolve("regions");

        this.storage = new JsonRegionStorage(dataDirectory);
        loadAll();
        this.api = new SimpleRegionAPI(this);
        this.tracker = new PlayerRegionTracker(api);
        registerSystems(pluginMain);
        pluginMain.getEntityStoreRegistry().registerSystem(tracker.getPlayerTickingSystem());
    }

    public void registerSystems(KMain main) {
        main.getEntityStoreRegistry().registerSystem(new BlockDamageProtectionFlag());
        main.getEntityStoreRegistry().registerSystem(new BreakProtectionFlag());
        main.getEntityStoreRegistry().registerSystem(new BuildProtectionFlag());
        main.getEntityStoreRegistry().registerSystem(new CraftProtectionFlag());
        main.getEntityStoreRegistry().registerSystem(new DamageProtectionFlag());
        main.getEntityStoreRegistry().registerSystem(new DropItemProtectionFlag());
        main.getEntityStoreRegistry().registerSystem(new InteractProtectionFlag());
        main.getEntityStoreRegistry().registerSystem(new MobSpawnProtectionFlag());
    }

    public void registerEvents(KMain main) {
        main.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, PlayerJoinLeaveEvent::onLeave);
        main.getEventRegistry().registerGlobal(PlayerReadyEvent.class, PlayerJoinLeaveEvent::onJoin);
    }

    private void loadAll() {
        for (Region region : getStorage().loadAll()) {
            region.getArea().recompute();
            registerRegion(region, false);
        }
    }

    protected Region createInternal(
            String wordName,
            String name,
            RegionType type,
            SelectionArea area,
            int priority
    ) {
        Region region = new Region();
        region.setId(UUID.randomUUID());
        region.setWorldName(wordName);
        region.setName(name.toLowerCase());
        region.setType(type);
        region.setPriority(priority);
        region.setArea(area);

        registerRegion(region, true);
        return region;
    }

    /**
     * Registers a region in the manager by storing it in the internal maps and optionally saving it.
     *
     * This method adds the specified region to an internal map keyed by its unique identifier
     * and to another map that groups regions by their world name. If the `save` parameter is true,
     * the region is persisted using the selected storage mechanism.
     *
     * @param region The region to be registered. This includes its unique identifier, world name,
     *               and other metadata.
     * @param save   Whether to persist the region to the storage. If true, the region is saved
     *               using the storage implementation; otherwise, it is only registered in memory.
     */
    protected void registerRegion(Region region, boolean save) {
        regionsById.put(region.getId(), region);
        regionsByWorld
                .computeIfAbsent(region.getWorldName(), w -> new HashSet<>())
                .add(region);

        if (save) {
            storage.save(region);
        }
    }
}
