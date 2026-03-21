package com.mcodelogic.safeareas.interaction;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
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

public class ReplaceInteraction extends com.hypixel.hytale.server.core.modules.interaction.interaction.config.none.ReplaceInteraction {

    public static final BuilderCodec<ReplaceInteraction> CUSTOM_CODEC = BuilderCodec.builder(ReplaceInteraction.class, ReplaceInteraction::new, com.hypixel.hytale.server.core.modules.interaction.interaction.config.none.ReplaceInteraction.CODEC).build();


    public void handle(PlayerRef playerRef, InteractionContext context, ItemStack heldItemStack, boolean canNotify) {
        SafeAreasDebug.log("ReplaceInteraction", "Blocking replace interaction. canNotify=" + canNotify
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
    protected void tick0(boolean firstRun, float time, @NonNullDecl InteractionType type, InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        Store<EntityStore> store = ref.getStore();
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) super.tick0(firstRun, time, type, context, cooldownHandler);

        BlockPosition targetBlock = context.getTargetBlock();
        if (targetBlock == null && playerRef != null) {
            var playerPos = playerRef.getTransform().getPosition();
            targetBlock = new BlockPosition((int) playerPos.x, (int) playerPos.y, (int) playerPos.z);
        }

        Set<Region> regions = RegionManager.instance.getApi().getRegionsAt(store.getExternalData().getWorld(), targetBlock.x, targetBlock.y, targetBlock.z);
        boolean canBreak = RegionFlagResolver.resolve(regions, RegionFlag.BUILD, true);
        boolean canNotify = RegionFlagResolver.resolve(regions, RegionFlag.NOTIFICATIONS, true);
        boolean isAdmin = player != null && player.hasPermission(RegionManager.instance.getConfig().getDefaultAdminPermission());

        SafeAreasDebug.log("ReplaceInteraction", "tick0 type=" + type
                + ", firstRun=" + firstRun
                + ", target=(" + targetBlock.x + ", " + targetBlock.y + ", " + targetBlock.z + ")"
                + ", regions=" + regions
                + ", canBuild=" + canBreak
                + ", canNotify=" + canNotify
                + ", adminBypass=" + isAdmin);

        // User can not interact in this region
        if (!canBreak && !isAdmin) {
            ItemStack itemInHand = player.getInventory().getItemInHand();
            handle(playerRef, context, itemInHand, canNotify);
            return;
        }

        SafeAreasDebug.log("ReplaceInteraction", "Allowing replace interaction.");
        super.tick0(firstRun, time, type, context, cooldownHandler);
    }

    @Override
    protected void simulateTick0(boolean firstRun, float time, @NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        Store<EntityStore> store = ref.getStore();
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) super.tick0(firstRun, time, type, context, cooldownHandler);


        BlockPosition targetBlock = context.getTargetBlock();
        if (targetBlock == null) {
            var playerPos = playerRef.getTransform().getPosition();
            targetBlock = new BlockPosition((int) playerPos.x, (int) playerPos.y, (int) playerPos.z);
        }

        Set<Region> regions = RegionManager.instance.getApi().getRegionsAt(store.getExternalData().getWorld(), targetBlock.x, targetBlock.y, targetBlock.z);
        boolean canBreak = RegionFlagResolver.resolve(regions, RegionFlag.BUILD, true);
        boolean canNotify = RegionFlagResolver.resolve(regions, RegionFlag.NOTIFICATIONS, true);
        boolean isAdmin = player != null && player.hasPermission(RegionManager.instance.getConfig().getDefaultAdminPermission());

        SafeAreasDebug.log("ReplaceInteraction", "simulateTick0 type=" + type
                + ", firstRun=" + firstRun
                + ", target=(" + targetBlock.x + ", " + targetBlock.y + ", " + targetBlock.z + ")"
                + ", regions=" + regions
                + ", canBuild=" + canBreak
                + ", canNotify=" + canNotify
                + ", adminBypass=" + isAdmin);

        // User can not interact in this region
        if (!canBreak && !isAdmin) {
            ItemStack itemInHand = player.getInventory().getItemInHand();
            handle(playerRef, context, itemInHand, canNotify);
            return;
        }

        SafeAreasDebug.log("ReplaceInteraction", "Allowing simulated replace interaction.");
        super.tick0(firstRun, time, type, context, cooldownHandler);
    }
}
