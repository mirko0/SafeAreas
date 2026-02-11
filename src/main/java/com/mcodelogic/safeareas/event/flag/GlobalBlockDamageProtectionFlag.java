package com.mcodelogic.safeareas.event.flag;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.system.WorldEventSystem;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.mcodelogic.safeareas.KMain;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.enums.RegionFlag;
import com.mcodelogic.safeareas.utils.RegionFlagResolver;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Collections;
import java.util.Set;

public class GlobalBlockDamageProtectionFlag extends WorldEventSystem<EntityStore, DamageBlockEvent> {

    public GlobalBlockDamageProtectionFlag() {
        super(DamageBlockEvent.class);
    }

    @Override
    public void handle(@NonNullDecl Store<EntityStore> store,
                       @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
                       @NonNullDecl DamageBlockEvent event) {
        try {
            if (event.isCancelled()) return;
            Vector3i targetBlock = event.getTargetBlock();
            Set<Region> regions = RegionManager.instance.getApi().getRegionsAt(store.getExternalData().getWorld(), targetBlock.x, targetBlock.y, targetBlock.z);
            boolean canBreak = RegionFlagResolver.resolve(regions, RegionFlag.BLOCK_DAMAGE, true);
            if (!canBreak) {
                event.setCancelled(true);
                event.setDamage(0);
            }
        } catch (Exception e) {
            KMain.LOGGER.atWarning().log("Error happened while checking global block damage protection!");
            e.printStackTrace();
        }
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Collections.singleton(RootDependency.first());
    }
}
