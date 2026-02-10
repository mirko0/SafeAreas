package com.mcodelogic.safeareas.event.system;

import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.mcodelogic.safeareas.manager.RegionManager;

public class PlayerJoinLeaveEvent {

    public static void onLeave(PlayerDisconnectEvent event) {
        RegionManager.instance.getTracker().handleQuit(event.getPlayerRef().getUuid());
    }

    public static void onJoin(PlayerReadyEvent event) {

    }
}
