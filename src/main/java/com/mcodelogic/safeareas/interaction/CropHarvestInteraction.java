package com.mcodelogic.safeareas.interaction;

import com.hypixel.hytale.builtin.adventure.farming.interactions.HarvestCropInteraction;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
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

import java.util.Set;

public class CropHarvestInteraction extends HarvestCropInteraction {

    public static final BuilderCodec<CropHarvestInteraction> CUSTOM_CODEC = BuilderCodec.builder(CropHarvestInteraction.class, CropHarvestInteraction::new, HarvestCropInteraction.CODEC).build();


    public void handle(PlayerRef playerRef, InteractionContext context, ItemStack heldItemStack, boolean canNotify) {
        SafeAreasDebug.log("CropHarvestInteraction", "Blocking harvest interaction. canNotify=" + canNotify
                + ", heldItem=" + (heldItemStack != null ? heldItemStack.getItem().getId() : "null"));
        fail(context);
        if (!canNotify) return;

        var lang = RegionManager.instance.getLang();
        var primaryMessage = lang.getMessage("ProtectionInteractPrimary");
        var secondaryMessage = lang.getMessage("ProtectionInteractSecondary");
        var icon = new ItemStack("Furniture_Crude_Chest_Small", 1).toPacket();

        NotificationUtil.sendNotification(playerRef.getPacketHandler(), primaryMessage, secondaryMessage, (ItemWithAllMetadata) icon);
    }

    public void fail(InteractionContext context) {
        context.getState().state = InteractionState.Failed;
        InteractionManager manager = context.getInteractionManager();
        if (manager != null && context.getChain() != null) {
            manager.cancelChains(context.getChain());
        }
    }


    @Override
    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NullableDecl ItemStack heldItemStack, @NonNullDecl Vector3i targetBlock, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        Store<EntityStore> store = ref.getStore();
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        if (playerRef == null) super.interactWithBlock(world, commandBuffer, type, context, heldItemStack, targetBlock, cooldownHandler);

        Set<Region> regions = RegionManager.instance.getApi().getRegionsAt(store.getExternalData().getWorld(), targetBlock.x, targetBlock.y, targetBlock.z);
        boolean canBreak = RegionFlagResolver.resolve(regions, RegionFlag.INTERACT, true);
        boolean canNotify = RegionFlagResolver.resolve(regions, RegionFlag.NOTIFICATIONS, true);
        boolean isAdmin = player != null && player.hasPermission(RegionManager.instance.getConfig().getDefaultAdminPermission());

        SafeAreasDebug.log("CropHarvestInteraction", "interactWithBlock type=" + type
                + ", target=" + targetBlock
                + ", regions=" + regions
                + ", canInteract=" + canBreak
                + ", canNotify=" + canNotify
                + ", adminBypass=" + isAdmin);

        // User ca n not interact in this region
        if (!canBreak && !isAdmin) {
            handle(playerRef, context, heldItemStack, canNotify);
            return;
        }

        SafeAreasDebug.log("CropHarvestInteraction", "Allowing harvest interaction at " + targetBlock);
        super.interactWithBlock(world, commandBuffer, type, context, heldItemStack, targetBlock, cooldownHandler);
    }

    @Override
    protected void simulateInteractWithBlock(@NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NullableDecl ItemStack itemInHand, @NonNullDecl World world, @NonNullDecl Vector3i targetBlock) {
        Ref<EntityStore> ref = context.getEntity();
        Store<EntityStore> store = ref.getStore();
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        if (playerRef == null)
            super.simulateInteractWithBlock(type, context, itemInHand, world, targetBlock);

        Set<Region> regions = RegionManager.instance.getApi().getRegionsAt(store.getExternalData().getWorld(), targetBlock.x, targetBlock.y, targetBlock.z);
        boolean canBreak = RegionFlagResolver.resolve(regions, RegionFlag.INTERACT, true);
        boolean canNotify = RegionFlagResolver.resolve(regions, RegionFlag.NOTIFICATIONS, true);
        boolean isAdmin = player != null && player.hasPermission(RegionManager.instance.getConfig().getDefaultAdminPermission());

        SafeAreasDebug.log("CropHarvestInteraction", "simulateInteractWithBlock type=" + type
                + ", target=" + targetBlock
                + ", regions=" + regions
                + ", canInteract=" + canBreak
                + ", canNotify=" + canNotify
                + ", adminBypass=" + isAdmin);

        // User can not interact in this region
        if (!canBreak && !isAdmin) {
            handle(playerRef, context, itemInHand, canNotify);
            return;
        }
        SafeAreasDebug.log("CropHarvestInteraction", "Allowing simulated harvest interaction at " + targetBlock);
        super.simulateInteractWithBlock(type, context, itemInHand, world, targetBlock);

    }
}
