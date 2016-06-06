package com.forgeessentials.core.preloader.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.forgeessentials.core.preloader.api.EntityTrackerHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.util.IntHashMap;

@Mixin(EntityTracker.class)
public class MixinEntityTracker_01 implements EntityTrackerHelper {

	@Shadow
	private IntHashMap<EntityTrackerEntry> trackedEntityHashTable = new IntHashMap<EntityTrackerEntry>();

	/**
	 * Provides support for vanish functionality.
	 *
	 * @param entity
	 *            the entity
	 * @return the entity tracker entry
	 */
	@Override
	public EntityTrackerEntry getEntityTrackerEntry(Entity entity) {
		return trackedEntityHashTable.lookup(entity.getEntityId());
	}

}