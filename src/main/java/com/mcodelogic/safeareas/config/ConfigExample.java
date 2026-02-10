package com.mcodelogic.safeareas.config;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.Getter;

/**
 * Example configuration class demonstrating various uses of @ConfigField annotation.
 * This file shows how the ConfigCodecBuilder eliminates boilerplate.
 */
@Getter
public class ConfigExample {

    // Automatically builds the codec by scanning all @ConfigField annotations
    public static final BuilderCodec<ConfigExample> CODEC = 
        ConfigCodecBuilder.create(ConfigExample.class, ConfigExample::new);

    // Basic usage - key name matches field name
    @ConfigField
    private String serverName = "MyServer";

    // Custom key name in config file
    @ConfigField("max_players")
    private int maxPlayers = 100;

    // Auto-detected types
    @ConfigField
    private boolean enablePvP = true;

    @ConfigField
    private double experienceMultiplier = 1.5;

    @ConfigField
    private long sessionTimeout = 3600000L;

    @ConfigField
    private float movementSpeed = 1.0f;

    // Explicit type specification (optional, for clarity)
    @ConfigField(value = "admin_password", type = ConfigField.CodecType.STRING)
    private String adminPassword = "changeme";

    // For custom complex types, use CUSTOM and provide the codec class
    // Example: @ConfigField(type = ConfigField.CodecType.CUSTOM, customCodec = MyComplexType.class)
    // Note: The customCodec class must have a public static CODEC field

    public ConfigExample() {
    }
}
