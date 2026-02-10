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
import com.mcodelogic.safeareas.utils.RegionFlagResolver;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.enums.RegionFlag;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Set;

public class MobSpawnProtectionFlag extends EntityTickingSystem<EntityStore> {
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

            boolean isFrozen = archetypeChunk.getComponent(index, Frozen.getComponentType()) != null;

            if (npcComponent == null || transformComponent == null) {
                return;
            }
            Vector3i targetBlock = transformComponent.getPosition().toVector3i();
            Set<Region> regions = RegionManager.instance.getApi().getRegionsAt(store.getExternalData().getWorld(), targetBlock.x, targetBlock.y, targetBlock.z);

            Boolean canSpawn = RegionFlagResolver.resolve(regions, RegionFlag.MOB_SPAWN, true);
            boolean canIgnoreFrozen = RegionFlagResolver.resolve(regions, RegionFlag.MOB_SPAWN_IGNORE_FROZEN, true);


            if (!canSpawn) {
                if (canIgnoreFrozen && isFrozen) return;
                if (entityRef.isValid()) {
                    commandBuffer.removeEntity(entityRef, RemoveReason.REMOVE);
//                String npcType = npcType(npcComponent.getRoleName());
//                HytaleLogger.getLogger().atInfo().log("Safe Areas> Blocked NPC(" + npcType + ":" + npcComponent.getRoleName() + ") spawn in region at xyz: " + targetBlock.toString());
                    return;
                }
                return;
            }
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
