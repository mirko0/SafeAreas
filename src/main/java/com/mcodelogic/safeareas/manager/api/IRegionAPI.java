package com.mcodelogic.safeareas.manager.api;

import com.hypixel.hytale.server.core.universe.world.World;
import com.mcodelogic.safeareas.model.Region;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * Represents an API for managing and interacting with regions in a world-based environment.
 * This interface defines methods for creating, retrieving, and deleting regions with
 * various shapes such as global, area, cuboid, and cylindrical regions. It also provides
 * methods for querying and accessing regions based on their location or unique identifiers.
 */
public interface IRegionAPI {

    /**
     * Create a new global region within the specified world.
     *
     * @param worldName the name of the world where the region is to be created
     * @param name the unique name of the region
     * @param priority the priority value of the region, used to determine order of precedence
     */
    Region createGlobalRegion(String worldName, String name, int priority);

    /**
     * Creates a new area-based region within the specified world and bounds.
     *
     * @param worldName the name of the world where the region is to be created
     * @param name the unique name of the region
     * @param minX the minimum X-coordinate of the region
     * @param minZ the minimum Z-coordinate of the region
     * @param maxX the maximum X-coordinate of the region
     * @param maxZ the maximum Z-coordinate of the region
     * @param priority the priority value of the region, used to determine order of precedence
     * @return the created {@link Region} object representing the area-based region
     */
    Region createAreaRegion(
            String worldName,
            String name,
            double minX, double minZ,
            double maxX, double maxZ,
            int priority
    );

    /**
     *
     */
    Region createCuboidRegion(
            String worldName,
            String name,
            double minX, double minY, double minZ,
            double maxX, double maxY, double maxZ,
            int priority
    );

    /**
     *
     */
    Region createCylinderRegion(
            String worldName,
            String name,
            double centerX, double centerZ,
            double minY, double maxY,
            int radius,
            int priority
    );

    /**
     * Creates a cylindrical area region that spans the entire Y-axis of the given world.
     *
     * @param worldName the name of the world where the region will be created
     * @param name the unique name of the region
     * @param centerX the X-coordinate of the center of the cylindrical region
     * @param centerZ the Z-coordinate of the center of the cylindrical region
     * @param radius the radius of the cylindrical region
     * @param priority the priority of the region, with higher values indicating higher priority
     * @return the created cylindrical area region
     */
    Region createCylinderAreaRegion(
            String worldName,
            String name,
            double centerX, double centerZ,
            int radius,
            int priority
    );

    /**
     * Deletes a region identified by the given unique identifier.
     * If the region with the specified ID does not exist, no action is performed.
     *
     * @param regionId the unique identifier of the region to delete
     */
    void deleteRegion(UUID regionId);

    /**
     *
     */
    Region getRegion(UUID regionId);

    /**
     * Retrieves a region by its name within a specified world.
     *
     * @param worldName the name of the world the region is located in
     * @param name the unique name of the region to be retrieved
     * @return the region corresponding to the given name and world, or null if no such region exists
     */
    Region getRegionByName(String worldName, String name);

    /**
     * Retrieves the set of regions located at the specified coordinates in the provided world.
     *
     * @param world the world in which to search for regions
     * @param x the x-coordinate of the location
     * @param y the y-coordinate of the location
     * @param z the z-coordinate of the location
     * @return a set of regions that include the specified location, or an empty set if no regions are found
     */
    Set<Region> getRegionsAt(World world, double x, double y, double z);

    /**
     *
     */
    Set<Region> getRegionsAt(String worldName, double x, double y, double z);

    /**
     * Retrieves all regions managed by the API.
     *
     * @return a collection of all regions, including global, area, cuboid, and cylindrical regions,
     *         currently managed by the API.
     */
    Collection<Region> getAllRegions();

    Collection<Region> getAllRegionsInWorld(World world);


    void selectPositionOne(UUID playerUUID, String worldName, double x, double y, double z);
    void selectPositionTwo(UUID playerUUID, String worldName, double x, double y, double z);

    void save(Region region);

}

