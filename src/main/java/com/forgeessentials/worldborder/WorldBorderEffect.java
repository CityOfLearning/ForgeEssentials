package com.forgeessentials.worldborder;

import com.forgeessentials.util.events.PlayerMoveEvent;

import net.minecraft.entity.player.EntityPlayerMP;

public abstract class WorldBorderEffect {

	protected int triggerDistance = 0;

	public WorldBorderEffect() {
	}

	public WorldBorderEffect(int triggerDistance) {
		this.triggerDistance = triggerDistance;
	}

	public void activate(WorldBorder border, EntityPlayerMP player) {
		/* do nothing */
	}

	public void deactivate(WorldBorder border, EntityPlayerMP player) {
		/* do nothing */
	}

	public double getTiggerDistance() {
		return triggerDistance;
	}

	public void playerMove(WorldBorder border, PlayerMoveEvent event) {
		/* do nothing */
	}

	public void tick(WorldBorder border, EntityPlayerMP player) {
		/* do nothing */
	}

}
