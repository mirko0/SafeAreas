package com.mcodelogic.safeareas.event;

import com.mcodelogic.safeareas.model.FlagValue;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.enums.RegionFlag;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

public final class RegionFlagResolver {

    private RegionFlagResolver() {}

    @SuppressWarnings("unchecked")
    public static <T> T resolve(
            Collection<Region> regions,
            RegionFlag flag,
            T defaultValue
    ) {
        return regions.stream()
                .sorted(Comparator.comparingInt(Region::getPriority).reversed())
                .map(r -> (FlagValue<T>) r.getFlag(flag))
                .filter(Objects::nonNull)
                .filter(FlagValue::isExplicitlySet)
                .map(FlagValue::getValue)
                .findFirst()
                .orElse(defaultValue);
    }
}
