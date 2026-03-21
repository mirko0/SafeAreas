package com.mcodelogic.safeareas.interaction;


import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.enums.RegionFlag;
import com.mcodelogic.safeareas.utils.RegionFlagResolver;
import com.mcodelogic.safeareas.utils.SafeAreasDebug;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;
import java.util.Set;

public class UseBlockInteraction extends com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.UseBlockInteraction {

    @Nonnull
    public static final BuilderCodec<UseBlockInteraction> CUSTOM_CODEC = BuilderCodec.builder(UseBlockInteraction.class, UseBlockInteraction::new, SimpleBlockInteraction.CODEC).documentation("Attempts to use the target block, executing interactions on it if any.").build();

    public void handle(PlayerRef playerRef, InteractionContext context, ItemStack heldItemStack, boolean canNotify) {
        SafeAreasDebug.log("UseBlockInteraction", "Blocking use block interaction. canNotify=" + canNotify
                + ", heldItem=" + (heldItemStack != null ? heldItemStack.getItem().getId() : "null"));
        if (!canNotify) return;

        var lang = RegionManager.instance.getLang();
        var primaryMessage = lang.getMessage("ProtectionBuildPrimary");
        var secondaryMessage = lang.getMessage("ProtectionBuildSecondary");
        String itemid = heldItemStack != null ? heldItemStack.getItem().getId() : "Wood_Beech_Trunk";
        var icon = new ItemStack(itemid, 1).toPacket();
        NotificationUtil.sendNotification(playerRef.getPacketHandler(), primaryMessage, secondaryMessage, (ItemWithAllMetadata) icon);
    }


    @Override
    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NullableDecl ItemStack itemInHand, @NonNullDecl Vector3i targetBlock, @NonNullDecl CooldownHandler cooldownHandler) {
        if (type == InteractionType.Primary || type == InteractionType.Secondary) {
            SafeAreasDebug.log("UseBlockInteraction", "Skipping protection for direct interaction type=" + type + " at " + targetBlock);
            super.interactWithBlock(world, commandBuffer, type, context, itemInHand, targetBlock, cooldownHandler);
            return;
        }

        Ref<EntityStore> ref = context.getEntity();
        Store<EntityStore> store = ref.getStore();
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        Set<Region> regions = RegionManager.instance.getApi().getRegionsAt(store.getExternalData().getWorld(), targetBlock.x, targetBlock.y, targetBlock.z);
        boolean canBreak = RegionFlagResolver.resolve(regions, RegionFlag.INTERACT, true);
        boolean canNotify = RegionFlagResolver.resolve(regions, RegionFlag.NOTIFICATIONS, true);
        boolean isAdmin = player != null && player.hasPermission(RegionManager.instance.getConfig().getDefaultAdminPermission());

        SafeAreasDebug.log("UseBlockInteraction", "interactWithBlock type=" + type
                + ", target=" + targetBlock
                + ", regions=" + regions
                + ", canInteract=" + canBreak
                + ", canNotify=" + canNotify
                + ", adminBypass=" + isAdmin);

        if (canBreak || isAdmin) {
            SafeAreasDebug.log("UseBlockInteraction", "Allowing use block interaction at " + targetBlock);
            super.interactWithBlock(world, commandBuffer, type, context, itemInHand, targetBlock, cooldownHandler);
            return;
        }

        if (canNotify) {
            SafeAreasDebug.log("UseBlockInteraction", "Blocking use block interaction and sending notification.");
            var lang = RegionManager.instance.getLang();
            var primaryMessage = lang.getMessage("ProtectionInteractPrimary");
            var secondaryMessage = lang.getMessage("ProtectionInteractSecondary");
            var icon = new ItemStack("Furniture_Crude_Chest_Small", 1).toPacket();

            NotificationUtil.sendNotification(playerRef.getPacketHandler(), primaryMessage, secondaryMessage, (ItemWithAllMetadata) icon);
            return;
        }
        SafeAreasDebug.log("UseBlockInteraction", "Blocking use block interaction without notification.");
    }

    @Override
    protected void simulateInteractWithBlock(@NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NullableDecl ItemStack itemInHand, @NonNullDecl World world, @NonNullDecl Vector3i targetBlock) {
        if (type == InteractionType.Primary || type == InteractionType.Secondary) {
            SafeAreasDebug.log("UseBlockInteraction", "Skipping simulated protection for direct interaction type=" + type + " at " + targetBlock);
            super.simulateInteractWithBlock(type, context, itemInHand, world, targetBlock);
            return;
        }

        Ref<EntityStore> ref = context.getEntity();
        Store<EntityStore> store = ref.getStore();
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        Set<Region> regions = RegionManager.instance.getApi().getRegionsAt(store.getExternalData().getWorld(), targetBlock.x, targetBlock.y, targetBlock.z);
        boolean canBreak = RegionFlagResolver.resolve(regions, RegionFlag.INTERACT, true);
        boolean canNotify = RegionFlagResolver.resolve(regions, RegionFlag.NOTIFICATIONS, true);
        boolean isAdmin = player != null && player.hasPermission(RegionManager.instance.getConfig().getDefaultAdminPermission());

        SafeAreasDebug.log("UseBlockInteraction", "simulateInteractWithBlock type=" + type
                + ", target=" + targetBlock
                + ", regions=" + regions
                + ", canInteract=" + canBreak
                + ", canNotify=" + canNotify
                + ", adminBypass=" + isAdmin);

        if (canBreak || isAdmin) {
            SafeAreasDebug.log("UseBlockInteraction", "Allowing simulated use block interaction at " + targetBlock);
            super.simulateInteractWithBlock(type, context, itemInHand, world, targetBlock);
            return;
        }

        if (canNotify) {
            SafeAreasDebug.log("UseBlockInteraction", "Blocking simulated use block interaction and sending notification.");
            var lang = RegionManager.instance.getLang();
            var primaryMessage = lang.getMessage("ProtectionInteractPrimary");
            var secondaryMessage = lang.getMessage("ProtectionInteractSecondary");
            var icon = new ItemStack("Furniture_Crude_Chest_Small", 1).toPacket();

            NotificationUtil.sendNotification(playerRef.getPacketHandler(), primaryMessage, secondaryMessage, (ItemWithAllMetadata) icon);
            return;
        }
        SafeAreasDebug.log("UseBlockInteraction", "Blocking simulated use block interaction without notification.");
    }
}
