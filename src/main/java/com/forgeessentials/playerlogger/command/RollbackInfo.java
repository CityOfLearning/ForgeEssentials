package com.forgeessentials.playerlogger.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.playerlogger.ModulePlayerLogger;
import com.forgeessentials.playerlogger.PlayerLogger;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.playerlogger.entity.Action01Block.ActionBlockType;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.registry.GameData;

public class RollbackInfo {

	public static class PlaybackTask extends TimerTask {
		private RollbackInfo rb;
		private int speed;

		public PlaybackTask(RollbackInfo rb, int speed) {
			this.rb = rb;
			this.speed = speed;
		}

		@Override
		@SuppressWarnings("deprecation")
		public void run() {
			rb.getTime().setSeconds(rb.getTime().getSeconds() - speed);
			rb.previewChanges();
		}

	}

	/**
	 * Send a faked block-update to a player
	 * 
	 * @param player
	 * @param change
	 * @param newBlock
	 * @param newMeta
	 */
	public static void sendBlockChange(EntityPlayerMP player, Action01Block change, IBlockState newState) {
		S23PacketBlockChange packet = new S23PacketBlockChange(DimensionManager.getWorld(change.world.id),
				change.getBlockPos());
		packet.blockState = newState;
		player.playerNetServerHandler.sendPacket(packet);
	}

	EntityPlayerMP player;

	private Selection area;

	private Date time;

	List<Action01Block> changes;

	private int timeStep = -60;

	public PlaybackTask task;

	public RollbackInfo(EntityPlayerMP player, Selection area) {
		this.player = player;
		this.area = area;
		setTime(new Date());
	}

	public void cancel() {
		if (task != null) {
			task.cancel();
		}
		for (Action01Block change : Lists.reverse(changes)) {
			player.playerNetServerHandler.sendPacket(
					new S23PacketBlockChange(DimensionManager.getWorld(change.world.id), change.getBlockPos()));
		}
	}

	public void confirm() {
		if (task != null) {
			task.cancel();
		}
		for (Action01Block change : changes) {
			if (change.type == ActionBlockType.PLACE) {
				WorldServer world = DimensionManager.getWorld(change.world.id);
				world.setBlockToAir(change.getBlockPos());
				System.out.println(change.time + " REMOVED " + change.block.name);
			} else if ((change.type == ActionBlockType.BREAK) || (change.type == ActionBlockType.DETONATE)) {
				WorldServer world = DimensionManager.getWorld(change.world.id);
				Block block = GameData.getBlockRegistry().getObject(new ResourceLocation(change.block.name));
				world.setBlockState(change.getBlockPos(), block.getStateFromMeta(change.metadata), 3);
				world.setTileEntity(change.getBlockPos(), PlayerLogger.blobToTileEntity(change.entity));
				System.out.println(change.time + " RESTORED " + change.block.name + ":" + change.metadata);
			}
		}
	}

	public Date getTime() {
		return time;
	}

	public void previewChanges() {
		List<Action01Block> lastChanges = changes;
		if (lastChanges == null) {
			lastChanges = new ArrayList<>();
		}

		changes = ModulePlayerLogger.getLogger().getLoggedBlockChanges(area, getTime(), null, 0);
		if (lastChanges.size() < changes.size()) {
			for (int i = lastChanges.size(); i < changes.size(); i++) {
				Action01Block change = changes.get(i);
				if (change.type == ActionBlockType.PLACE) {
					sendBlockChange(player, change, Blocks.air.getDefaultState());
					// System.out.println(FEConfig.FORMAT_DATE_TIME_SECONDS.format(change.time)
					// + " REMOVED " +
					// change.block.name);
				} else if ((change.type == ActionBlockType.BREAK) || (change.type == ActionBlockType.DETONATE)) {
					Block block = GameData.getBlockRegistry().getObject(new ResourceLocation(change.block.name));
					sendBlockChange(player, change, block.getStateFromMeta(change.metadata));
					// System.out.println(FEConfig.FORMAT_DATE_TIME_SECONDS.format(change.time)
					// + " RESTORED " +
					// change.block.name + ":" + change.metadata);
				}
			}
		} else if (lastChanges.size() > changes.size()) {
			for (int i = lastChanges.size() - 1; i >= changes.size(); i--) {
				Action01Block change = lastChanges.get(i);
				if (change.type == ActionBlockType.PLACE) {
					Block block = GameData.getBlockRegistry().getObject(new ResourceLocation(change.block.name));
					sendBlockChange(player, change, block.getStateFromMeta(change.metadata));
					// System.out.println(FEConfig.FORMAT_DATE_TIME_SECONDS.format(change.time)
					// + " REPLACED " +
					// change.block.name);
				} else if ((change.type == ActionBlockType.BREAK) || (change.type == ActionBlockType.DETONATE)) {
					sendBlockChange(player, change, Blocks.air.getDefaultState());
					// System.out.println(FEConfig.FORMAT_DATE_TIME_SECONDS.format(change.time)
					// + " REBROKE " +
					// change.block.name + ":" + change.metadata);
				}
			}
		}
	}

	public void setTime(Date time) {
		this.time = time;
	}

	@SuppressWarnings("deprecation")
	public void stepBackward() {
		timeStep *= timeStep < 0 ? 1.25 : -0.25;
		timeStep -= 1;
		getTime().setSeconds(getTime().getSeconds() + timeStep);
	}

	@SuppressWarnings("deprecation")
	public void stepForward() {
		timeStep *= timeStep > 0 ? 1.25 : -0.25;
		timeStep += 1;
		getTime().setSeconds(getTime().getSeconds() + timeStep);
	}

}