package com.mcodelogic.safeareas.api;

import com.mcodelogic.safeareas.model.SelectionArea;

public final class SelectionAreaFactory {

    private SelectionAreaFactory() {}

    public static SelectionArea global() {
        return new SelectionArea();
    }

    public static SelectionArea area(double minX, double minZ, double maxX, double maxZ) {
        SelectionArea a = new SelectionArea();
        a.setMinX(Math.min(minX, maxX));
        a.setMaxX(Math.max(minX, maxX));
        a.setMinZ(Math.min(minZ, maxZ));
        a.setMaxZ(Math.max(minZ, maxZ));
        return a;
    }

    public static SelectionArea cuboid(
            double minX, double minY, double minZ,
            double maxX, double maxY, double maxZ
    ) {
        SelectionArea a = new SelectionArea();
        a.setMinX(Math.min(minX, maxX));
        a.setMaxX(Math.max(minX, maxX));
        a.setMinY(Math.min(minY, maxY));
        a.setMaxY(Math.max(minY, maxY));
        a.setMinZ(Math.min(minZ, maxZ));
        a.setMaxZ(Math.max(minZ, maxZ));
        return a;
    }

    public static SelectionArea cylinder(
            double centerX, double centerZ,
            double minY, double maxY,
            int radius
    ) {
        SelectionArea a = new SelectionArea();
        a.setCenterX(centerX);
        a.setCenterZ(centerZ);
        a.setMinY(minY);
        a.setMaxY(maxY);
        a.setRadius(radius);
        a.recompute();
        return a;
    }

    public static SelectionArea cylinderArea(
            double centerX, double centerZ,
            int radius
    ) {
        SelectionArea a = new SelectionArea();
        a.setCenterX(centerX);
        a.setCenterZ(centerZ);
        a.setRadius(radius);
        a.recompute();
        return a;
    }
}
