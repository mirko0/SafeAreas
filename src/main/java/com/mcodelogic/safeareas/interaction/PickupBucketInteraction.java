package com.mcodelogic.safeareas.interaction;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.RefillContainerInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.enums.RegionFlag;
import com.mcodelogic.safeareas.utils.RegionFlagResolver;
import com.mcodelogic.safeareas.utils.SafeAreasDebug;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Set;

public class PickupBucketInteraction extends RefillContainerInteraction {

    public static final BuilderCodec<PickupBucketInteraction> CUSTOM_CODEC = BuilderCodec.builder(PickupBucketInteraction.class, PickupBucketInteraction::new, RefillContainerInteraction.CODEC).build();

    @Override
    protected void firstRun(@NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        Store<EntityStore> store = ref.getStore();
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        BlockPosition targetBlock = context.getTargetBlock();
        if (playerRef == null) {
            SafeAreasDebug.log("PickupBucketInteraction", "Skipping refill protection because playerRef was null.");
            return;
        }

        double x, y, z;

        if (targetBlock == null) {
            Vector3d position = playerRef.getTransform().getPosition();
            x = position.x;
            y = position.y;
            z = position.z;
        } else {
            x = targetBlock.x;
            y = targetBlock.y;
            z = targetBlock.z;
        }

        Set<Region> regions = RegionManager.instance.getApi().getRegionsAt(store.getExternalData().getWorld(), x, y , z);
        boolean canBreak = RegionFlagResolver.resolve(regions, RegionFlag.INTERACT, true);
        boolean canNotify = RegionFlagResolver.resolve(regions, RegionFlag.NOTIFICATIONS, true);
        boolean isAdmin = player != null && player.hasPermission(RegionManager.instance.getConfig().getDefaultAdminPermission());

        SafeAreasDebug.log("PickupBucketInteraction", "firstRun type=" + type
                + ", target=(" + x + ", " + y + ", " + z + ")"
                + ", regions=" + regions
                + ", canInteract=" + canBreak
                + ", canNotify=" + canNotify
                + ", adminBypass=" + isAdmin);

        if (canBreak || isAdmin) {
            SafeAreasDebug.log("PickupBucketInteraction", "Allowing refill container interaction.");
            super.firstRun(type, context, cooldownHandler);
            return;
        }

        if (canNotify) {
            SafeAreasDebug.log("PickupBucketInteraction", "Blocking refill container interaction and sending notification.");
            var lang = RegionManager.instance.getLang();
            var primaryMessage = lang.getMessage("ProtectionInteractPrimary");
            var secondaryMessage = lang.getMessage("ProtectionInteractSecondary");
            var icon = new ItemStack("Furniture_Crude_Chest_Small", 1).toPacket();

            NotificationUtil.sendNotification(playerRef.getPacketHandler(), primaryMessage, secondaryMessage, (ItemWithAllMetadata) icon);
            return;
        }

        SafeAreasDebug.log("PickupBucketInteraction", "Blocking refill container interaction without notification.");

    }

}
