package com.forgeessentials.jscripting.wrapper;

import com.forgeessentials.commons.selections.WorldPoint;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;

public class JsEntityPlayer extends JsEntityLivingBase<EntityPlayer> {
	protected JsInventoryPlayer<?> inventory;

	private JsCommandSender commandSender;

	public JsEntityPlayer(EntityPlayer player) {
		super(player);
	}

	public JsEntityPlayer(EntityPlayer player, JsCommandSender commandSender) {
		super(player);
		this.commandSender = commandSender;
	}

	public JsPoint<?> getBedLocation(int dimension) {
		BlockPos coord = EntityPlayer.getBedSpawnLocation(that.worldObj, that.getBedLocation(dimension), false);
		return coord != null ? new JsWorldPoint<>(new WorldPoint(dimension, coord)) : null;
	}

	public JsCommandSender getCommandSender() {
		if ((commandSender != null) || !(that instanceof EntityPlayer)) {
			return commandSender;
		}
		return commandSender = new JsCommandSender(that, this);
	}

	public JsInventoryPlayer<?> getInventory() {
		if (inventory == null) {
			inventory = new JsInventoryPlayer<>(that.inventory);
		}
		return inventory;
	}

	public void setPosition(double x, double y, double z) {
		that.posX = x;
		that.posY = y;
		that.posZ = z;
		((EntityPlayerMP) that).playerNetServerHandler.setPlayerLocation(x, y, z, that.cameraYaw, that.cameraPitch);
	}

	public void setPosition(double x, double y, double z, float yaw, float pitch) {
		that.posX = x;
		that.posY = y;
		that.posZ = z;
		((EntityPlayerMP) that).playerNetServerHandler.setPlayerLocation(x, y, z, yaw, pitch);
	}

}
