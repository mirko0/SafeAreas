package com.mcodelogic.safeareas.model;

import com.mcodelogic.safeareas.model.enums.RegionFlag;
import com.mcodelogic.safeareas.model.enums.RegionType;
import lombok.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Region {

    private UUID id;
    private String worldName;

    private String name;
    private RegionType type;
    private int priority = 0;

    private SelectionArea area;

    private Map<RegionFlag, FlagValue<?>> flags = new EnumMap<>(RegionFlag.class);


    public <T> void setFlag(RegionFlag flag, T value) {
        flags.put(flag, new FlagValue<>(value, true));
    }

    public FlagValue<?> getFlag(RegionFlag flag) {
        return flags.get(flag);
    }

}