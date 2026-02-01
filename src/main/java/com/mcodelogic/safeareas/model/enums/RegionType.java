package com.mcodelogic.safeareas.model.enums;

public enum RegionType {

    GLOBAL,          // Entire world
    AREA,            // XZ only (full Y range)
    CUBOID,          // XYZ
    CYLINDER,        // XZ radius + Y range
    CYLINDER_AREA    // XZ radius, full Y
}

