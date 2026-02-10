package com.mcodelogic.safeareas;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.util.Config;
import com.mcodelogic.safeareas.commands.RegionCommand;
import com.mcodelogic.safeareas.commands.TestCommand;
import com.mcodelogic.safeareas.config.KConfig;
import com.mcodelogic.safeareas.manager.RegionManager;
import lombok.Getter;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.awt.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;


public class KMain extends JavaPlugin {
    public static final HytaleLogger LOGGER = HytaleLogger.getLogger();
    private final Config<KConfig> pluginConfiguration;
    @Getter
    private RegionManager manager;

    public KMain(@NonNullDecl JavaPluginInit init) {
        super(init);
        pluginConfiguration = this.withConfig(Constants.PLUGIN_NAME_FULL, KConfig.CODEC);
    }

    @Override
    protected void setup() {

        super.setup();
        LOGGER.at(Level.INFO).log("Initializing " + Constants.PLUGIN_NAME_FULL + " Plugin...");
        LOGGER.at(Level.INFO).log("Saving Configuration");
        try {
            if (!new File(getDataDirectory().resolve(Constants.PLUGIN_NAME_FULL + ".json").toUri()).exists()) {
                pluginConfiguration.save();
            }
            pluginConfiguration.load();
        } catch (Exception e) {
            Logger.getLogger(KMain.class.getName()).log(Level.SEVERE, "Error while loading configuration", e);
        }
        manager = new RegionManager(this);
        getCommandRegistry().registerCommand(new RegionCommand(manager));
        getCommandRegistry().registerCommand(new TestCommand());
    }

    @Override
    protected void start() {
        LOGGER.at(Level.INFO).log("Starting " + Constants.PLUGIN_NAME_FULL + " Plugin...");
    }

    @Override
    protected void shutdown() {
        LOGGER.at(Level.INFO).log("Stopping " + Constants.PLUGIN_NAME_FULL + " Plugin...");
        manager.getPlayerPositions().clear();
        manager.getRegionsById().clear();
        manager.getRegionsByWorld().clear();
        manager.getTracker().getStates().clear();
    }

    public KConfig getConfiguration() {
        return pluginConfiguration.get();
    }
}
