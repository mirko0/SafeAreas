package com.mcodelogic.safeareas.event.flag;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.mcodelogic.safeareas.KMain;
import com.mcodelogic.safeareas.model.FlagValue;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.enums.RegionFlag;
import com.mcodelogic.safeareas.utils.RegionFlagResolver;
import com.mcodelogic.safeareas.utils.TinyMsg;

import java.util.Set;

public class GreetingAndFarewellFlag {

    public static void sendGreetingTitle(Set<Region> regions, PlayerRef playerRef) {
        try {
            String title = RegionFlagResolver.resolve(regions, RegionFlag.GREETING_TITLE, null);
            String subTitle = RegionFlagResolver.resolve(regions, RegionFlag.GREETING_SUBTITLE, null);
            if (title == null) return;
            showTitle(playerRef, TinyMsg.parse(title), TinyMsg.parse(subTitle == null ? "SafeAreas" : subTitle));
        } catch (Exception e) {
            KMain.LOGGER.atWarning().log("Failed to send greeting title!");
            e.printStackTrace();
        }
    }

    public static void sendFarewellTitle(Set<Region> regions, PlayerRef playerRef) {
        try {
            String title = RegionFlagResolver.resolve(regions, RegionFlag.FAREWELL_TITLE, null);
            String subTitle = RegionFlagResolver.resolve(regions, RegionFlag.FAREWELL_SUBTITLE, null);
            if (title == null) return;
            showTitle(playerRef, TinyMsg.parse(title), TinyMsg.parse(subTitle == null ? "SafeAreas" : subTitle));
        } catch (Exception e) {
            KMain.LOGGER.atWarning().log("Failed to send farewell title!");
            e.printStackTrace();
        }
    }

    private static void showTitle(PlayerRef playerRef, Message titleMessage, Message subTitleMessage) {
        EventTitleUtil.showEventTitleToPlayer(
                playerRef,
                titleMessage,
                subTitleMessage,
                false,
                null,
                1.2f,
                0.2f,
                0.3f
        );
    }


    public static void sendGreetingMessage(Region region, PlayerRef playerRef) {
        try {
            FlagValue<String> flag = (FlagValue<String>) region.getFlag(RegionFlag.GREETING);
            if (flag != null) {
                Message msg = TinyMsg.parse("<yellow><bold>SafeAreas</bold> > </yellow><gray>" + flag.getValue() + "</gray>");
                playerRef.sendMessage(msg);
            }
        } catch (Exception e) {
            HytaleLogger.getLogger().atInfo().log("Failed to send greeting message for region " + region.getName());
        }
    }

    public static void sendFarewellMessage(Region region, PlayerRef playerRef) {
        try {
            FlagValue<String> flag = (FlagValue<String>) region.getFlag(RegionFlag.FAREWELL);
            if (flag != null) {
                Message msg = TinyMsg.parse("<yellow><bold>SafeAreas</bold> > </yellow><gray>" + flag.getValue() + "</gray>");
                playerRef.sendMessage(msg);
            }
        } catch (Exception e) {
            HytaleLogger.getLogger().atInfo().log("Failed to send farewell message for region " + region.getName());
        }
    }
}
