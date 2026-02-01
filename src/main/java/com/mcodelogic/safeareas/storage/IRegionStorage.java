package com.mcodelogic.safeareas.storage;

import com.mcodelogic.safeareas.model.Region;

import java.util.Collection;
import java.util.UUID;

public interface IRegionStorage {

    void save(Region region);

    void delete(UUID regionId);

    void delete(Region region);

    Collection<Region> loadAll();
}
