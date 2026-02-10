package com.mcodelogic.safeareas.model;

import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class PlayerRegionState {
    private Set<Region> currentRegions = Set.of();
    private int lastBlockX, lastBlockY, lastBlockZ;

    public boolean isSameBlock(int x, int y, int z) {
        return x == lastBlockX && y == lastBlockY && z == lastBlockZ;
    }

    public void updateBlock(int x, int y, int z) {
        this.lastBlockX = x;
        this.lastBlockY = y;
        this.lastBlockZ = z;
    }
}
