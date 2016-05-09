package com.forgeessentials.core.preloader.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.forgeessentials.core.preloader.api.EntityTrackerHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.util.IntHashMap;

@Mixin(EntityTracker.class)
public abstract class MixinEntityTracker_01 implements EntityTrackerHelper {

	@Shadow
	private IntHashMap trackedEntityHashTable = new IntHashMap();

	@Override
	public EntityTrackerEntry getEntityTrackerEntry(Entity entity) {
		return (EntityTrackerEntry) trackedEntityHashTable.lookup(entity.getEntityId());
	}

}
