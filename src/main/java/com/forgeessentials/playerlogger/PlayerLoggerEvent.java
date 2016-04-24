package com.forgeessentials.playerlogger;

import java.sql.Blob;
import java.util.Date;

import javax.persistence.EntityManager;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.playerlogger.entity.BlockData;
import com.forgeessentials.playerlogger.entity.PlayerData;
import com.forgeessentials.playerlogger.entity.WorldData;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class PlayerLoggerEvent<T extends Event> {
	public Date date;

	public T event;

	public PlayerLoggerEvent(T event) {
		this.event = event;
		this.date = new Date();
	}

	public BlockData getBlock(Block block) {
		return ModulePlayerLogger.getLogger().getBlock(block);
	}

	public PlayerData getPlayer(EntityPlayer player) {
		return ModulePlayerLogger.getLogger().getPlayer(player.getPersistentID(), player.getDisplayNameString());
	}

	public PlayerData getPlayer(UserIdent ident) {
		return ModulePlayerLogger.getLogger().getPlayer(ident.getUuid(), ident.getUsername());
	}

	public Blob getTileEntityBlob(TileEntity tileEntity) {
		return PlayerLogger.tileEntityToBlob(tileEntity);
	}

	public WorldData getWorld(int dimensionId) {
		return ModulePlayerLogger.getLogger().getWorld(dimensionId);
	}

	public abstract void process(EntityManager em);

}