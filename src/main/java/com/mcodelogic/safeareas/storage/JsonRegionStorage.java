package com.mcodelogic.safeareas.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcodelogic.safeareas.model.Region;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.*;
import java.util.*;

public class JsonRegionStorage implements IRegionStorage {

    private final Path baseDir;
    private final Gson gson;

    public JsonRegionStorage(Path baseDir) {
        this.baseDir = baseDir;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    @Override
    public void save(Region region) {

        try {
            Path worldDir = baseDir.resolve(region.getWorldName().toString());
            Files.createDirectories(worldDir);

            Path file = worldDir.resolve(region.getName().toLowerCase() + ".json");

            try (Writer writer = Files.newBufferedWriter(file)) {
                gson.toJson(region, writer);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to save region " + region.getName(), e);
        }
    }

    @Override
    public void delete(UUID regionId) {
        // RegionManager should already know world + name
        // Storage should only delete the file when asked explicitly
        // Recommended: manager passes the Path or Region object instead
        throw new UnsupportedOperationException(
                "Delete requires region world + name. Call delete(Region) instead."
        );
    }

    @Override
    public void delete(Region region) {
        try {
            Path file = baseDir
                    .resolve(region.getWorldName().toString())
                    .resolve(region.getName().toLowerCase() + ".json");

            Files.deleteIfExists(file);

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete region " + region.getName(), e);
        }
    }

    @Override
    public Collection<Region> loadAll() {

        List<Region> regions = new ArrayList<>();

        if (!Files.exists(baseDir)) {
            return regions;
        }

        try {
            Files.walk(baseDir, 2)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> {

                        try (Reader reader = Files.newBufferedReader(path)) {
                            Region region = gson.fromJson(reader, Region.class);
                            if (region != null) {
                                regions.add(region);
                            }
                        } catch (Exception e) {
                            System.err.println("Failed to load region file: " + path);
                            e.printStackTrace();
                        }
                    });

        } catch (IOException e) {
            throw new RuntimeException("Failed to load regions", e);
        }

        return regions;
    }
}
