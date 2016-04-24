package com.forgeessentials.util.events;

import com.forgeessentials.commons.selections.WarpPoint;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class PlayerMoveEvent extends FEPlayerEvent {
	public final WarpPoint before;
	public final WarpPoint after;

	public PlayerMoveEvent(EntityPlayer player, WarpPoint before, WarpPoint after) {
		super(player);
		this.before = before;
		this.after = after;
	}

	public boolean isBlockMove() {
		return (before.getBlockX() != after.getBlockX()) && (before.getBlockY() != after.getBlockY())
				&& (before.getBlockZ() != after.getBlockZ());
	}

	public boolean isCoordMove() {
		return (before.getX() != after.getX()) && (before.getY() != after.getY()) && (before.getZ() != after.getZ());
	}

	public boolean isViewMove() {
		return (before.getYaw() != after.getYaw()) && (before.getPitch() != after.getPitch());
	}

}
