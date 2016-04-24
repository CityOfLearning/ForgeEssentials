package com.forgeessentials.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.forgeessentials.client.handler.ReachDistanceHandler;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.world.WorldSettings;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP {

	@Shadow
	private WorldSettings.GameType currentGameType;

	@Overwrite
	public float getBlockReachDistance() {
		if (ReachDistanceHandler.getReachDistance() > 0) {
			return ReachDistanceHandler.getReachDistance();
		}
		return currentGameType.isCreative() ? 5.0F : 4.5F;
	}

}
