package com.mcodelogic.safeareas.utils;

import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.SelectionArea;

public final class RegionUtils {

    public static boolean contains(Region region, double x, double y, double z) {
        SelectionArea a = region.getArea();

        switch (region.getType()) {
            case GLOBAL:
                return true;
            case AREA:
                return x >= a.getMinX() && x <= a.getMaxX()
                    && z >= a.getMinZ() && z <= a.getMaxZ();
            case CUBOID:
                return x >= a.getMinX() && x <= a.getMaxX()
                    && y >= a.getMinY() && y <= a.getMaxY()
                    && z >= a.getMinZ() && z <= a.getMaxZ();

            case CYLINDER:
                if (y < a.getMinY() || y > a.getMaxY()) return false;
                return distanceSquaredXZ(a, x, z) <= a.getRadiusSquared();

            case CYLINDER_AREA:
                return distanceSquaredXZ(a, x, z) <= a.getRadiusSquared();

            default:
                return false;
        }
    }

    private static double distanceSquaredXZ(SelectionArea a, double x, double z) {
        double dx = x - a.getCenterX();
        double dz = z - a.getCenterZ();
        return dx * dx + dz * dz;
    }
}
