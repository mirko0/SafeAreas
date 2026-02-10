package com.mcodelogic.safeareas.config;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.Getter;

@Getter
public class KConfig {
    public static final BuilderCodec<KConfig> CODEC = ConfigCodecBuilder.create(KConfig.class, KConfig::new);

    @ConfigField("DefaultAdminPermission")
    private String defaultAdminPermission = "safezones.admin";

    public KConfig() {
    }

}