package com.forgeessentials.core.preloader.mixin.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.entity.player.EntityPlayerMP;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayer_01 {

	@Overwrite
	public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_) {
		return true;
	}

}
