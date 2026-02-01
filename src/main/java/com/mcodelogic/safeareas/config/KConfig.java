package com.mcodelogic.safeareas.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.Getter;

@Getter
public class KConfig {

    public static final BuilderCodec<KConfig> CODEC = BuilderCodec.builder(KConfig.class, KConfig::new)

            .append(new KeyedCodec<String>("DefaultAdminPermission", Codec.STRING),
                    (config, value, extra) -> config.DefaultAdminPermission = value,
                    (config, extra) -> config.DefaultAdminPermission)
            .add()

            .append(new KeyedCodec<Boolean>("DisableTitles", Codec.BOOLEAN),
                    (config, value, extra) -> config.DisableTitles = value,
                    (config, extra) -> config.DisableTitles)
            .add()

            .build();

    private String DefaultAdminPermission = "safezones.admin";
    private Boolean DisableTitles = false;

    public KConfig() {
    }

}