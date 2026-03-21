package com.mcodelogic.safeareas.event.flag;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.entity.Frozen;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.enums.RegionFlag;
import com.mcodelogic.safeareas.utils.RegionFlagResolver;
import com.mcodelogic.safeareas.utils.SafeAreasDebug;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MobSpawnProtectionFlagVTwo extends RefSystem<EntityStore> {

    private static void debug(String message) {
        SafeAreasDebug.log("MobSpawnProtectionFlagVTwo", message);
    }

    @Nullable
    public Query<EntityStore> getQuery() {
        return Query.and(NPCEntity.getComponentType(), TransformComponent.getComponentType());
    }

    public void onEntityAdded(@Nonnull Ref<EntityStore> ref,
                              @Nonnull AddReason reason,
                              @Nonnull Store<EntityStore> store,
                              @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        try {
            if (reason != AddReason.SPAWN) {
                debug("Ignoring entity add because reason was " + reason + " for ref=" + ref);
                return;
            }

            NPCEntity npcEntity = store.getComponent(ref, NPCEntity.getComponentType());
            TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
            if (npcEntity == null || transform == null) {
                debug("Skipping entity because required components were missing. ref=" + ref
                        + ", hasNpc=" + (npcEntity != null)
                        + ", hasTransform=" + (transform != null));
                return;
            }

            Vector3i targetBlock = transform.getPosition().toVector3i();
            EntityStore entityStore = store.getExternalData();
            World world = entityStore.getWorld();
            Set<Region> regions = RegionManager.instance.getApi().getRegionsAt(world, targetBlock.x, targetBlock.y, targetBlock.z);
            boolean canSpawn = RegionFlagResolver.resolve(regions, RegionFlag.MOB_SPAWN, true);
            boolean canIgnoreFrozen = RegionFlagResolver.resolve(regions, RegionFlag.MOB_SPAWN_IGNORE_FROZEN, true);
            boolean isFrozen = store.getComponent(ref, Frozen.getComponentType()) != null;
            String roleName = npcEntity.getRoleName();

            debug("Evaluating mob spawn. ref=" + ref
                    + ", role=" + roleName
                    + ", position=" + targetBlock
                    + ", regions=" + regions
                    + ", canSpawn=" + canSpawn
                    + ", canIgnoreFrozen=" + canIgnoreFrozen
                    + ", isFrozen=" + isFrozen);

            if (canIgnoreFrozen && isFrozen) {
                canSpawn = true;
                debug("Allowing spawn because entity is frozen and MOB_SPAWN_IGNORE_FROZEN is enabled. ref=" + ref);
            }

            if (roleName != null && (roleName.startsWith("HyCitizens") || roleName.startsWith("Citizens"))) {
                canSpawn = true;
                debug("Allowing spawn because entity role is exempt from mob spawn protection. ref=" + ref + ", role=" + roleName);
            }

            if (!canSpawn) {
                debug("Removing entity because mob spawn is blocked in this region. ref=" + ref
                        + ", role=" + roleName
                        + ", position=" + targetBlock);
                commandBuffer.removeEntity(ref, RemoveReason.REMOVE);
               /*RegionManager.instance.getMain().getExecutor().schedule(() ->
                                world.execute(() -> commandBuffer.removeEntity(ref, RemoveReason.REMOVE))
                        , 1000, TimeUnit.MILLISECONDS
                );
                */
                return;
            }

            debug("Allowing entity spawn after region checks. ref=" + ref
                    + ", role=" + roleName
                    + ", position=" + targetBlock);

        } catch (Exception e) {
            Logger.getLogger(MobSpawnProtectionFlagVTwo.class.getName())
                    .log(Level.SEVERE, "Error happened while checking mob spawn protection! ref=" + ref, e);
        }
    }

    @Override
    public void onEntityRemove(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

    }

}
