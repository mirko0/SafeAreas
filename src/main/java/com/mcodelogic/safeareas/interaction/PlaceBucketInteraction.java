package com.mcodelogic.safeareas.interaction;

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
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.PlaceFluidInteraction;
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

public class PlaceBucketInteraction extends PlaceFluidInteraction {

    public static final BuilderCodec<PlaceBucketInteraction> CUSTOM_CODEC = BuilderCodec.builder(PlaceBucketInteraction.class, PlaceBucketInteraction::new, PlaceFluidInteraction.CODEC).build();

    public void handle(PlayerRef playerRef, InteractionContext context, ItemStack heldItemStack, boolean canNotify) {
        SafeAreasDebug.log("PlaceBucketInteraction", "Blocking place fluid interaction. canNotify=" + canNotify
                + ", heldItem=" + (heldItemStack != null ? heldItemStack.getItem().getId() : "null"));
        fail(context);
        if (!canNotify) return;

        var lang = RegionManager.instance.getLang();
        var primaryMessage = lang.getMessage("ProtectionBuildPrimary");
        var secondaryMessage = lang.getMessage("ProtectionBuildSecondary");
        String itemid = heldItemStack != null ? heldItemStack.getItem().getId() : "Wood_Beech_Trunk";
        var icon = new ItemStack(itemid, 1).toPacket();
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
    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NullableDecl ItemStack itemInhand, @NonNullDecl Vector3i targetBlock, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        Store<EntityStore> store = ref.getStore();
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        Set<Region> regions = RegionManager.instance.getApi().getRegionsAt(store.getExternalData().getWorld(), targetBlock.x, targetBlock.y, targetBlock.z);
        boolean canBreak = RegionFlagResolver.resolve(regions, RegionFlag.BUILD, true);
        boolean canNotify = RegionFlagResolver.resolve(regions, RegionFlag.NOTIFICATIONS, true);
        boolean isAdmin = player != null && player.hasPermission(RegionManager.instance.getConfig().getDefaultAdminPermission());

        SafeAreasDebug.log("PlaceBucketInteraction", "interactWithBlock type=" + type
                + ", target=" + targetBlock
                + ", regions=" + regions
                + ", canBuild=" + canBreak
                + ", canNotify=" + canNotify
                + ", adminBypass=" + isAdmin);

        // User can not interact in this region
        if (!canBreak && !isAdmin) {
            handle(playerRef, context, itemInhand, canNotify);
            return;
        }

        // allow user to continue with interaction.
        SafeAreasDebug.log("PlaceBucketInteraction", "Allowing place fluid interaction at " + targetBlock);
        super.interactWithBlock(world, commandBuffer, type, context, itemInhand, targetBlock, cooldownHandler);
    }

    @Override
    protected void simulateInteractWithBlock(@NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NullableDecl ItemStack itemInHand, @NonNullDecl World world, @NonNullDecl Vector3i targetBlock) {
        Ref<EntityStore> ref = context.getEntity();
        Store<EntityStore> store = ref.getStore();
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        Set<Region> regions = RegionManager.instance.getApi().getRegionsAt(store.getExternalData().getWorld(), targetBlock.x, targetBlock.y, targetBlock.z);
        boolean canBreak = RegionFlagResolver.resolve(regions, RegionFlag.BUILD, true);
        boolean canNotify = RegionFlagResolver.resolve(regions, RegionFlag.NOTIFICATIONS, true);
        boolean isAdmin = player != null && player.hasPermission(RegionManager.instance.getConfig().getDefaultAdminPermission());

        SafeAreasDebug.log("PlaceBucketInteraction", "simulateInteractWithBlock type=" + type
                + ", target=" + targetBlock
                + ", regions=" + regions
                + ", canBuild=" + canBreak
                + ", canNotify=" + canNotify
                + ", adminBypass=" + isAdmin);

        // User can interact
        if (!canBreak && !isAdmin) {
            handle(playerRef, context, itemInHand, canNotify);
            return;
        }

        //Allow user to contineu with interaction.
        SafeAreasDebug.log("PlaceBucketInteraction", "Allowing simulated place fluid interaction at " + targetBlock);
        super.simulateInteractWithBlock(type, context, itemInHand, world, targetBlock);
    }

}
