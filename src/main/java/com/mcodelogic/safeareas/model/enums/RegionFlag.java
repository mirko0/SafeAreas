package com.mcodelogic.safeareas.model.enums;

public enum RegionFlag {

    BUILD(Boolean.class),
    BREAK(Boolean.class),
    BLOCK_DAMAGE(Boolean.class),
    PVP(Boolean.class),
    IMMORTAL(Boolean.class),
    DROP_ITEMS(Boolean.class),

    INTERACT(Boolean.class),
    CRAFTING(Boolean.class),
//    DAMAGE_MOBS(Boolean.class),
//    ENTER(Boolean.class),

//    FLY(Boolean.class),

    NOTIFICATIONS(Boolean.class),
    MOB_SPAWN(Boolean.class),
    MOB_SPAWN_IGNORE_FROZEN(Boolean.class),
    FALL_DAMAGE(Boolean.class),

    GREETING(String.class),
    FAREWELL(String.class),

    GREETING_TITLE(String.class),
    FAREWELL_TITLE(String.class),
    GREETING_SUBTITLE(String.class),
    FAREWELL_SUBTITLE(String.class),
    ;

    private final Class<?> valueType;

    RegionFlag(Class<?> valueType) {
        this.valueType = valueType;
    }

    public Class<?> getValueType() {
        return valueType;
    }
}
