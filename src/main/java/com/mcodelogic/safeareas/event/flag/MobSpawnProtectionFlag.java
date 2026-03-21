package com.mcodelogic.safeareas.event.flag;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.entity.Frozen;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.mcodelogic.safeareas.KMain;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.enums.RegionFlag;
import com.mcodelogic.safeareas.utils.RegionFlagResolver;
import com.mcodelogic.safeareas.utils.SafeAreasDebug;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Set;

public class MobSpawnProtectionFlag extends EntityTickingSystem<EntityStore> {

    private static void debug(String message) {
        SafeAreasDebug.log("MobSpawnProtectionFlag", message);
    }

    @Override
    public void tick(float v,
                     int index,
                     @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNullDecl Store<EntityStore> store,
                     @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        try {
            Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(index);
            if (entityRef == null || !entityRef.isValid()) return;
            NPCEntity npcComponent = archetypeChunk.getComponent(index, NPCEntity.getComponentType());
            TransformComponent transformComponent = archetypeChunk.getComponent(index, TransformComponent.getComponentType());


            if (npcComponent == null || transformComponent == null) {
                debug("Skipping entity because required components were missing. ref=" + entityRef
                        + ", hasNpc=" + (npcComponent != null)
                        + ", hasTransform=" + (transformComponent != null));
                return;
            }
            Vector3i targetBlock = transformComponent.getPosition().toVector3i();
            Set<Region> regions = RegionManager.instance.getApi().getRegionsAt(store.getExternalData().getWorld(), targetBlock.x, targetBlock.y, targetBlock.z);

            Boolean canSpawn = RegionFlagResolver.resolve(regions, RegionFlag.MOB_SPAWN, true);
            boolean canIgnoreFrozen = RegionFlagResolver.resolve(regions, RegionFlag.MOB_SPAWN_IGNORE_FROZEN, true);
            boolean isFrozen = archetypeChunk.getComponent(index, Frozen.getComponentType()) != null;
            String roleName = npcComponent.getRoleName();

            debug("Evaluating mob spawn. ref=" + entityRef
                    + ", role=" + roleName
                    + ", position=" + targetBlock
                    + ", regions=" + regions
                    + ", canSpawn=" + canSpawn
                    + ", canIgnoreFrozen=" + canIgnoreFrozen
                    + ", isFrozen=" + isFrozen);

            if (canIgnoreFrozen && isFrozen) {
                canSpawn = true;
                debug("Allowing spawn because entity is frozen and MOB_SPAWN_IGNORE_FROZEN is enabled. ref=" + entityRef);
            }

            if (roleName != null && (roleName.startsWith("HyCitizens") || roleName.startsWith("Citizens"))) {
                canSpawn = true;
                debug("Allowing spawn because entity role is exempt from mob spawn protection. ref=" + entityRef + ", role=" + roleName);
            }

            if (!canSpawn) {
                debug("Removing entity because mob spawn is blocked in this region. ref=" + entityRef
                        + ", role=" + roleName
                        + ", position=" + targetBlock);
                if (entityRef.isValid()) {
                    commandBuffer.removeEntity(entityRef, RemoveReason.REMOVE);
                    return;
                }
                return;
            }

            debug("Allowing entity spawn after region checks. ref=" + entityRef
                    + ", role=" + roleName
                    + ", position=" + targetBlock);
        } catch (Exception e) {
            KMain.LOGGER.atWarning().log("Error happened while checking mob spawn protection!");
            e.printStackTrace();
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(NPCEntity.getComponentType(), TransformComponent.getComponentType());
    }

   /* public String npcType(String npcRole) {
        boolean isHostile = isNPCHostile(npcRole);
        boolean isFriendly = isNPCFriendly(npcRole);
        if (isHostile) return "hostile";
        if (isFriendly) return "friendly";
        return "unknown";
    }

    private List<String> hostileNpcRoles = List.of(
            "hostile",
            "aggressive",
            "monster",
            "enemy",
            "mob"
    );

    private List<String> friendlyNpcRoles = List.of(
            "neutral",
            "passive",
            "animal",
            "creature"
    );

    private boolean isNPCHostile(@Nullable String npcRole) {
        if (npcRole == null) return false;
        String npcRoleLower = npcRole.toLowerCase();
        return hostileNpcRoles.contains(npcRoleLower.trim());
    }

    private boolean isNPCFriendly(@Nullable String npcRole) {
        if (npcRole == null) return false;
        String npcRoleLower = npcRole.toLowerCase();
        return friendlyNpcRoles.contains(npcRoleLower.trim());
    }
*/
}
