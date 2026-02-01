package com.mcodelogic.safeareas.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor@NoArgsConstructor@Getter@Setter
public class SelectionArea {

    // Cuboid / Area
    private double minX, minY, minZ;
    private double maxX, maxY, maxZ;

    // Cylinder / Sphere
    private double centerX, centerY, centerZ;
    private int radius;
    private transient int radiusSquared;

    public void recompute() {
        this.radiusSquared = radius * radius;
    }
}
