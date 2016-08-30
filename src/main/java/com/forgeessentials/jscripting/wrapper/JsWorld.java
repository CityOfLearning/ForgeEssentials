package com.forgeessentials.jscripting.wrapper;

import com.forgeessentials.util.MappedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class JsWorld<T extends World> extends JsWrapper<T> {

	public JsWorld(T that) {
		super(that);
	}

	public boolean blockExists(int x, int y, int z) {
		return !that.isBlockLoaded(new BlockPos(x, y, z));
	}

	public JsBlock getBlock(int x, int y, int z) {
		return JsBlock.get(that.getBlockState(new BlockPos(x, y, z)).getBlock());
	}

	public int getDifficulty() {
		return that.getDifficulty().ordinal();
	}

	public int getDimension() {
		return that.provider.getDimensionId();
	}

	@SuppressWarnings("unchecked")
	public MappedList<EntityPlayer, JsEntityPlayer> getPlayerEntities() {
		return new JsEntityPlayerList(that.playerEntities);
	}

	public void setBlock(int x, int y, int z, JsBlock block) {
		that.setBlockState(new BlockPos(x, y, z), block.getThat().getDefaultState());
	}

	public void setBlock(int x, int y, int z, JsBlock block, int meta) {
		that.setBlockState(new BlockPos(x, y, z), block.getThat().getStateFromMeta(meta));
	}

	// public void get() // tsgen ignore
	// {
	// return that.;
	// }

}
