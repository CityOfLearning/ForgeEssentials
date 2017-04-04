package com.forgeessentials.multiworld;

import java.io.File;
import java.io.FileInputStream;

import com.forgeessentials.commons.output.LoggingHandler;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.StartupQuery;

/**
 *
 * @author Olee
 */
public class MultiworldSaveHandler extends SaveHandler {

	public MultiworldSaveHandler(File base, Multiworld world) {
		super(base, "FEMultiworld/" + world.getName(), false);
	}

	@Override
	public IChunkLoader getChunkLoader(WorldProvider provider) {
		return new AnvilChunkLoader(getWorldDirectory());
	}

	@Override
	public WorldInfo loadWorldInfo() {
		File file1 = new File(getWorldDirectory(), "level.dat");
		if (file1.exists()) {
			LoggingHandler.felog.info("Loading World Info from: " + file1.getAbsolutePath());
			try {
				NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file1));
				NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
				WorldInfo worldInfo = new WorldInfo(nbttagcompound1);
				return worldInfo;
			} catch (StartupQuery.AbortedException e) {
				throw e;
			} catch (Exception exception1) {
				exception1.printStackTrace();
			}
		}

		file1 = new File(getWorldDirectory(), "level.dat_old");
		if (file1.exists()) {
			try {
				NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file1));
				NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
				WorldInfo worldInfo = new WorldInfo(nbttagcompound1);
				return worldInfo;
			} catch (StartupQuery.AbortedException e) {
				throw e;
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		return null;
	}

}