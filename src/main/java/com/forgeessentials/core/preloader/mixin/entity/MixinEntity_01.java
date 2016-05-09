package com.forgeessentials.core.preloader.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.world.PressurePlateEvent;

@Mixin(Entity.class)
public abstract class MixinEntity_01 {

	@Overwrite
	public boolean doesEntityNotTriggerPressurePlate() {
		return MinecraftForge.EVENT_BUS.post(new PressurePlateEvent((Entity) (Object) this));
	}

}