package com.mcodelogic.safeareas.event.flag;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.mcodelogic.safeareas.event.RegionFlagResolver;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.enums.RegionFlag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.logging.Logger;

public class DamageProtectionFlag extends EntityEventSystem<EntityStore, Damage> {

    public DamageProtectionFlag() {
        super(Damage.class);
    }

    public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage event) {
        if (event.isCancelled()) return;
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (player == null || playerRef == null) return;

        Set<Region> regions = RegionManager.instance.getTracker()
                .getState(playerRef.getUuid())
                .getCurrentRegions();
        boolean canPvp = RegionFlagResolver.resolve(regions, RegionFlag.PVP, true);
        boolean immortal = RegionFlagResolver.resolve(regions, RegionFlag.IMMORTAL, false);
        boolean fallDamage = RegionFlagResolver.resolve(regions, RegionFlag.FALL_DAMAGE, true);

        if (!fallDamage) {
            try {
                DamageCause cause = event.getCause();
                if (cause != null && cause.equals(DamageCause.FALL)) {
                    event.setCancelled(true);
                    return;
                }
            } catch (Exception e) {
                HytaleLogger.getLogger().atInfo().log("Safe Areas> Failed to check fall damage flag for region " + regions);
                e.printStackTrace();
            }
        }


        if (immortal) {
            event.setCancelled(true);
            return;
        }

        Damage.Source source = event.getSource();

        if (source instanceof Damage.EntitySource dmgSource) {
            Store<EntityStore> attackerStore = dmgSource.getRef().getStore();
            Ref<EntityStore> attackerRef = dmgSource.getRef();
            Player attacker = attackerStore.getComponent(attackerRef, Player.getComponentType());

            if (attackerRef.isValid() && commandBuffer.getComponent(attackerRef, Player.getComponentType()) != null && !canPvp) {
                event.setCancelled(true);
                return;
            }
        }
        if (source instanceof Damage.ProjectileSource dmgSource) {
            Store<EntityStore> attackerStore = dmgSource.getRef().getStore();
            Ref<EntityStore> attackerRef = dmgSource.getRef();
            Player attacker = attackerStore.getComponent(attackerRef, Player.getComponentType());
            if (attackerRef.isValid() && commandBuffer.getComponent(attackerRef, Player.getComponentType()) != null && !canPvp) {
                event.setCancelled(true);
                return;
            }
        }

    }

    @Nonnull
    private static final Query<EntityStore> QUERY = Player.getComponentType();

    @Nullable
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getFilterDamageGroup();
    }

    public Query<EntityStore> getQuery() {
        return QUERY;
    }

}
