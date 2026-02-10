package com.mcodelogic.safeareas.manager.api;

import com.hypixel.hytale.server.core.universe.world.World;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.PlayerPositionCoords;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.XYZ;
import com.mcodelogic.safeareas.model.enums.RegionType;
import com.mcodelogic.safeareas.utils.RegionUtils;
import com.mcodelogic.safeareas.utils.SelectionAreaFactory;
import lombok.AllArgsConstructor;

import java.util.*;

@AllArgsConstructor
public class SimpleRegionAPI implements IRegionAPI {

    public RegionManager manager;


    @Override
    public Region createGlobalRegion(String worldName, String name, int priority) {
        return manager.createInternal(worldName, name, RegionType.GLOBAL,
                SelectionAreaFactory.global(), priority);
    }

    @Override
    public Region createAreaRegion(
            String worldName, String name,
            double minX, double minZ,
            double maxX, double maxZ,
            int priority
    ) {
        return manager.createInternal(worldName, name, RegionType.AREA,
                SelectionAreaFactory.area(minX, minZ, maxX, maxZ), priority);
    }

    @Override
    public Region createCuboidRegion(
            String worldName, String name,
            double minX, double minY, double minZ,
            double maxX, double maxY, double maxZ,
            int priority
    ) {
        return manager.createInternal(worldName, name, RegionType.CUBOID,
                SelectionAreaFactory.cuboid(minX, minY, minZ, maxX, maxY, maxZ),
                priority);
    }

    @Override
    public Region createCylinderRegion(
            String worldName, String name,
            double centerX, double centerZ,
            double minY, double maxY,
            int radius,
            int priority
    ) {
        return manager.createInternal(worldName, name, RegionType.CYLINDER,
                SelectionAreaFactory.cylinder(centerX, centerZ, minY, maxY, radius),
                priority);
    }

    @Override
    public Region createCylinderAreaRegion(
            String worldName, String name,
            double centerX, double centerZ,
            int radius,
            int priority
    ) {
        return manager.createInternal(worldName, name, RegionType.CYLINDER_AREA,
                SelectionAreaFactory.cylinderArea(centerX, centerZ, radius),
                priority);
    }

    @Override
    public void deleteRegion(UUID regionId) {
        Region removed = manager.getRegionsById().remove(regionId);
        manager.getStorage().delete(removed);
        manager.getRegionsByWorld().get(removed.getWorldName()).remove(removed);
    }

    @Override
    public Region getRegion(UUID regionId) {
        return manager.getRegionsById().get(regionId);
    }

    @Override
    public Region getRegionByName(String worldName, String name) {
        Set<Region> regions = manager.getRegionsByWorld().get(worldName);
        if (regions == null) return null;

        String lower = name.toLowerCase();

        for (Region region : regions) {
            if (region.getName().equalsIgnoreCase(lower)) {
                return region;
            }
        }
        return null;
    }

    @Override
    public Set<Region> getRegionsAt(World world, double x, double y, double z) {
        return getRegionsAt(world.getName(), x, y ,z);
    }

    @Override
    public Set<Region> getRegionsAt(String worldName, double x, double y, double z) {
        Set<Region> regions = manager.getRegionsByWorld().get(worldName);
        if (regions == null) return Set.of();

        Set<Region> result = new HashSet<>();

        for (Region region : regions) {
            if (RegionUtils.contains(region, x, y, z)) {
                result.add(region);
            }
        }

        return result;
    }

    @Override
    public Collection<Region> getAllRegions() {
        return manager.getRegionsById().values();
    }

    @Override
    public Collection<Region> getAllRegionsInWorld(World world) {
        return manager.getRegionsByWorld().getOrDefault(world.getName(), Set.of());
    }

    @Override
    public void selectPositionOne(UUID playerUUID, String worldName, double x, double y, double z) {
        PlayerPositionCoords playerPositionCoords = manager.getPlayerPositions().computeIfAbsent(playerUUID, k -> new PlayerPositionCoords());
        playerPositionCoords.setWorldName(worldName);
        playerPositionCoords.setPosOne(new XYZ(x, y, z));
        manager.getPlayerPositions().put(playerUUID, playerPositionCoords);
    }

    @Override
    public void selectPositionTwo(UUID playerUUID, String worldName, double x, double y, double z) {
        PlayerPositionCoords playerPositionCoords = manager.getPlayerPositions().computeIfAbsent(playerUUID, k -> new PlayerPositionCoords());
        playerPositionCoords.setWorldName(worldName);
        playerPositionCoords.setPosTwo(new XYZ(x, y, z));
        manager.getPlayerPositions().put(playerUUID, playerPositionCoords);
    }

    @Override
    public void save(Region region) {
        manager.getStorage().save(region);
    }
}
