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
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.CycleBlockGroupInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.enums.RegionFlag;
import com.mcodelogic.safeareas.utils.RegionFlagResolver;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Set;

public class CycleBlockInteraction extends CycleBlockGroupInteraction {

    public static final BuilderCodec<CycleBlockInteraction> CUSTOM_CODEC = BuilderCodec.builder(CycleBlockInteraction.class, CycleBlockInteraction::new, SimpleBlockInteraction.CODEC).documentation("Attempts to cycle the target block through its block set.").build();

    @Override
    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NullableDecl ItemStack heldItemStack, @NonNullDecl Vector3i targetBlock, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        Store<EntityStore> store = ref.getStore();
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        Set<Region> regions = RegionManager.instance.getApi().getRegionsAt(store.getExternalData().getWorld(), targetBlock.x, targetBlock.y, targetBlock.z);
        boolean canBreak = RegionFlagResolver.resolve(regions, RegionFlag.BUILD, true);
        boolean canNotify = RegionFlagResolver.resolve(regions, RegionFlag.NOTIFICATIONS, true);

        // User can not interact in this region
        if (canBreak || player.hasPermission(RegionManager.instance.getConfig().getDefaultAdminPermission())) {
            super.interactWithBlock(world, commandBuffer, type, context, heldItemStack, targetBlock, cooldownHandler);
            return;
        }

        if (canNotify) {
            var lang = RegionManager.instance.getLang();
            var primaryMessage = lang.getMessage("ProtectionInteractPrimary");
            var secondaryMessage = lang.getMessage("ProtectionInteractSecondary");
            var icon = new ItemStack("Furniture_Crude_Chest_Small", 1).toPacket();

            NotificationUtil.sendNotification(playerRef.getPacketHandler(), primaryMessage, secondaryMessage, (ItemWithAllMetadata) icon);
        }

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

        // User can not interact in this region
        if (canBreak || player.hasPermission(RegionManager.instance.getConfig().getDefaultAdminPermission())) {
            super.simulateInteractWithBlock(type, context, itemInHand, world, targetBlock);
            return;
        }
        if (canNotify) {
            var lang = RegionManager.instance.getLang();
            var primaryMessage = lang.getMessage("ProtectionInteractPrimary");
            var secondaryMessage = lang.getMessage("ProtectionInteractSecondary");
            var icon = new ItemStack("Furniture_Crude_Chest_Small", 1).toPacket();

            NotificationUtil.sendNotification(playerRef.getPacketHandler(), primaryMessage, secondaryMessage, (ItemWithAllMetadata) icon);
        }
    }
}
